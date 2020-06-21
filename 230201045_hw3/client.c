#include <sys/socket.h>  // create socket.
#include <arpa/inet.h> // ascii to network bits.
#include <netinet/in.h> // network to asii bit.
#include <sys/types.h>  // for data types.
#include <errno.h>  // We'll use error library to catch the errors.
#include "openssl/ssl.h" // used for openssl function's, certificates and configuring them.
#include "openssl/err.h" // find openssl errors
#include <malloc.h> // for memory allocation operation.
#include <resolv.h>  // server to find out the runner's IP address.
#include <unistd.h> // We'll use it to fork at a time send and receive messages.
#include <string.h> // We'll use fgets function to get input from user.
#include <stdio.h>  // for input-output operations
#include <netdb.h> //definitions for network database operations

#define BUFFER 1024  // size of the buffer to read messages



int OpenConnection(const char *hostname, int port) {
    int sd;
    struct hostent *host;
    struct sockaddr_in addr;

	if ( (host = gethostbyname(hostname)) == NULL ) {
        perror(hostname);
        abort();
    }

	sd = socket(PF_INET, SOCK_STREAM, 0);   // setting the connection TCP.
    bzero(&addr, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    addr.sin_addr.s_addr = *(long*)(host->h_addr);

    if ( connect(sd, (struct sockaddr*)&addr, sizeof(addr)) != 0 ) { //initiate a connection.
        close(sd);
        perror(hostname);
        abort();
    }
    return sd;
}

	

SSL_CTX* InitContext(void) {    //creating and setting up ssl context structure
    SSL_METHOD *method;
    SSL_CTX *ctx;
    OpenSSL_add_all_algorithms(); //load crpto algorithms etc.
    SSL_load_error_strings();  // load all error messages.
    method = TLSv1_2_client_method();
    ctx = SSL_CTX_new(method);   // Create new context

	if ( ctx == NULL ) {
        ERR_print_errors_fp(stderr);
        abort();
    }
    return ctx;
}

	

void ShowCertificates(SSL* ssl)  { //show the ceritficates to server and match them but here we are not using any client certificate
    X509 *certificate;
    char *line;
    certificate = SSL_get_peer_certificate(ssl); // get the server's certificate

	if ( certificate != NULL ) {
        printf("Server certificates:\n");
        line = X509_NAME_oneline(X509_get_subject_name(certificate), 0, 0);
        printf("Subject: %s\n", line);
        free(line);
        line = X509_NAME_oneline(X509_get_issuer_name(certificate), 0, 0);
        printf("Issuer: %s\n", line);
        free(line);
        X509_free(certificate);     // free the malloc'ed certificate copy
	}
    else
        printf("Info: No client certificates configured.\n");
    }

	
int main(int argc, char *argv[]) {   // get the port number and ip address as arguments
    SSL_CTX *ctx;
    int server;
    SSL *ssl;
    char buf[BUFFER];
    char input[BUFFER];
    int bytes;
    char *hostname, *portnum;

	pid_t cpid;    // for forking.

	if ( argc != 3 ) {

        printf("usage: %s  \n", argv[0]);
        exit(0);
    }

	SSL_library_init();   //load encryption and hash algorithms in secure shell.
    hostname=argv[1];
    portnum=argv[2];
    ctx = InitContext(); //call the function.

	server = OpenConnection(hostname, atoi(portnum));   // convert port ascii port to integer.
    ssl = SSL_new(ctx);      //get new SSL state with context

	SSL_set_fd(ssl, server);    /* attach the socket descriptor */
    if ( SSL_connect(ssl) == -1 )   // perform the connection
        ERR_print_errors_fp(stderr);
    else {
        printf("Connected with %s encryption\n", SSL_get_cipher(ssl));
        ShowCertificates(ssl);
        cpid=fork();  //Fork system call is used to create a new process
        if(cpid==0)  {
            while(1){
                printf("\nMESSAGE TO SERVER:");
                fgets(input, BUFFER, stdin);
                SSL_write(ssl, input, strlen(input));   // encrypt and send a message.
		if(!strcmp(input, "quit\n")){
			exit(0);
		}
                
            }
        }
        else {
            while(1) {
                bytes = SSL_read(ssl, buf, sizeof(buf)); // get request or message
                if ( bytes > 0 ) {
                    buf[bytes] = 0;
			printf("\nMESSAGE FROM SERVER: %s\n", buf);
		    if(!strcmp(buf, "quit\n")){
			break;
		    }
                    
                }
            }
            
        }
        SSL_free(ssl);        // release connection state
    }
    close(server);         // close socket
    SSL_CTX_free(ctx);        // release context
    return 0; // success!

}
