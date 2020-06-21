import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AES {
    //Initial Vector is for the cipher block chaining.
    private static final String initVector = "encryptionIntVec";
    private SecretKey secretKey;

    public String encrypt(String data) throws Exception{
        try{
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKey secretKey = randomKeyGenerator();
            //We have to keep the key in global so that we can decrypt.
            this.secretKey = secretKey;
            //AES is algorithm, CBC is cipher block chaining and PKCS5PADDING is block size mechanism.
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            byte[] encodedValue = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encodedValue);
        }
        catch (Exception e){
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public String decrypt(String encrypted){
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initVector.getBytes("UTF-8"));
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, this.secretKey, ivParameterSpec);
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(original);
        }
        catch (Exception e){
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    //We're generating a key randomly and key size is 256 bit.
    private SecretKey randomKeyGenerator() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey;

    }

    public static String getInitVector() {
        return initVector;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }
}
