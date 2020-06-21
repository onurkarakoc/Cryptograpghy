#include <sys/socket.h>  // create socket.
#include <arpa/inet.h> // ascii to network bits.
#include <netinet/in.h> // network to asii bit.
#include <sys/types.h>  // for data types.
#include <errno.h>  // We'll use error library to catch the errors.
#include <openssl/ssl.h> // used for openssl function's, certificates and configuring them.
#include <openssl/err.h> // find openssl errors
#include <stdlib.h> // for memory allocation operation.
#include <resolv.h>  // server to find out the runner's IP address.
#include <unistd.h> // We'll use it to fork at a time send and receive messages.
#include <string.h> // We'll use fgets function to get input from user.
#include <stdio.h>  // for input-output operations

#define BUFFER 1024  // size of the buffer to read messages

	
int CreateSocketListen(int port){
    int sd;
    struct sockaddr_in addr;   //use the struct while creating socket.
    sd = socket(PF_INET, SOCK_STREAM, 0); //created socket.
    bzero(&addr, sizeof(addr));    //free output the garbage space in memory
    addr.sin_family = AF_INET;    //Address family: IPv4.
    addr.sin_port = htons(port);  //converting host bit to network bit
    addr.sin_addr.s_addr = INADDR_ANY; // all local addresses
    
    if ( bind(sd, (struct sockaddr*)&addr, sizeof(addr)) != 0 ){  //bind the ip addres and port.
        perror("Can't bind port");    //errno.h library is used for detect the error.
        abort();      //if there is an error, then abort the process
    }

    if ( listen(sd, 5) != 0 ){    //for listening to max of 5 clients in the queue
        perror("Can't configure listening port");  //Report the error.
        abort();
    }
    
    return sd;

}


SSL_CTX* InitServerContext(void) {     //creating and setting up ssl context structure
    SSL_METHOD *method;
    SSL_CTX *ctx;
    OpenSSL_add_all_algorithms();       // load & register all crypto algorithms etc.
    SSL_load_error_strings();        // load all error messages.
    method = TLSv1_2_server_method();       // create new server-method instance, TLSv1 is the newest and is considered the safest.
    ctx = SSL_CTX_new(method);        // create new context from method

    if ( ctx == NULL ) {

        ERR_print_errors_fp(stderr);
        abort();

	}
    return ctx;
    }

	

void LoadCertificates(SSL_CTX* ctx, char* CertificateFile, char* KeyFile) { // to load a certificate into an SSL_CTX structure

	// set the local certificate from CertificateFile

    if ( SSL_CTX_use_certificate_file(ctx, CertificateFile, SSL_FILETYPE_PEM) <= 0 ) {

        ERR_print_errors_fp(stderr);
        abort();
    }

	// set the private key from KeyFile (may be the same as CertificateFile)

	if ( SSL_CTX_use_PrivateKey_file(ctx, KeyFile, SSL_FILETYPE_PEM) <= 0 ) {

        ERR_print_errors_fp(stderr);
        abort();
    }

	// verify private key

	if ( !SSL_CTX_check_private_key(ctx) ) {

        fprintf(stderr, "Private key does not match the public certificate\n");
        abort();
    }

}

	

void ShowCerts(SSL* ssl) {    //show the ceritficates to client and match them
    X509 *certificate;
    char *line;
    certificate = SSL_get_peer_certificate(ssl); // Get certificates (if available)

    if ( certificate != NULL ) {

        printf("Server certificates:\n");
        line = X509_NAME_oneline(X509_get_subject_name(certificate), 0, 0);
        printf("Server: %s\n", line);     // show server certifcates
        free(line);
        line = X509_NAME_oneline(X509_get_issuer_name(certificate), 0, 0);
        printf("client: %s\n", line);     // show client certificates
        free(line);
        X509_free(certificate);
    }

}

	

void Servlet(SSL* ssl) { // Serve the connection
    char buf[BUFFER];
    int sd, bytes;
    char input[BUFFER];

	pid_t cpid; 

	if ( SSL_accept(ssl) == -1 )     // do SSL-protocol accept

        ERR_print_errors_fp(stderr);

    else {

        ShowCerts(ssl);        // get any certificates

	//Fork system call is used to create a new process

	cpid=fork();

	if(cpid==0) {

        while(1){
            bytes = SSL_read(ssl, buf, sizeof(buf)); // get request and read message from server
            if ( bytes > 0 ) {
                buf[bytes] = 0;
                printf("\nMESSAGE FROM CLIENT:%s\n", buf);
		if(!strcmp(buf, "quit\n")){
			exit(0);
		}
            }
            else
                ERR_print_errors_fp(stderr);

        }
    }

	else {
        while(1){
            printf("\nMESSAGE TO CLIENT:");
            fgets(input, BUFFER, stdin);  // get request or message and reply to client
            SSL_write(ssl, input, strlen(input));
	    if(!strcmp(input, "quit\n")){
		break;
	    }
        }

	}

	}

	sd = SSL_get_fd(ssl);       // get socket connection
    SSL_free(ssl);         // release SSL state
    close(sd);          // close connection

}

	

int main(int argc, char *argv[]) {  // We'll get the port number from command line

    SSL_CTX *ctx;
    int server;
    char *portnum;

	if ( argc != 2 ){
        printf("Usage: %s \n", argv[0]);   //send the usage guide if syntax of setting port is different
        exit(0);
    }

	SSL_library_init();  //load encryption and hash algorithms in secure shell.
	portnum = argv[1];
    ctx = InitServerContext();  // initialize SSL
    LoadCertificates(ctx, "certificate.pem", "certificate.pem"); /* load certs */
    server = CreateSocketListen(atoi(portnum)); //create server socket in this function.
    
    struct sockaddr_in addr;      //socket for server
    socklen_t len = sizeof(addr);
    SSL *ssl;
    listen(server,5);      /*setting 5 clients at a time to queue*/

	int client = accept(server, (struct sockaddr*)&addr, &len);  // accept connection
    printf("Connection: %s:%d\n",inet_ntoa(addr.sin_addr), ntohs(addr.sin_port));  //printing connected client information

	ssl = SSL_new(ctx);              //get new SSL state with context
    SSL_set_fd(ssl, client);      //set connection socket to SSL state
    Servlet(ssl);     // call service function
    close(server);          // close server socket
    SSL_CTX_free(ctx);         // release context
    
}

