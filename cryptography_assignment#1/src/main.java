import java.math.BigInteger;

public class main {

    public static void main(String[] args) throws Exception {
        ExtendedEuclideanAlgorithm extendedEuclideanAlgorithm = new ExtendedEuclideanAlgorithm();
        FermatLittleTheorem fermatLittleTheorem = new FermatLittleTheorem();
        FastModularExponentiation fastModularExponentiation = new FastModularExponentiation();
        AES aes = new AES();

        BigInteger[] resultXYAndGcd = extendedEuclideanAlgorithm.findGcdXAndY(new BigInteger("482"), new BigInteger("1180"));
        System.out.println("==================EXTENDED EUCLIDEAN MODULE===================");
        System.out.println("For value a = 482 and value b = 1180 x is " + resultXYAndGcd[0] + ", y is " + resultXYAndGcd[1] + " and gcd is " + resultXYAndGcd[2]);
        System.out.println("482 and 1180 are relatively prime: " + extendedEuclideanAlgorithm.areRelativelyPrime(new BigInteger("482"), new BigInteger("1180")));
        System.out.println("The multiplicative inverse of 5 in mod(11): " + extendedEuclideanAlgorithm.findMultiplicativeInverse(new BigInteger("5"), new BigInteger("11")));
        System.out.println("Gcd of values 12345 and 11111: " + extendedEuclideanAlgorithm.findGcdOfTwoNumber(new BigInteger("12345"), new BigInteger("11111")));
        System.out.println("==================FERMAT'S LITTLE THEOREM=====================");
        System.out.println("1076017 is a prime number. I checked it on the internet and we're testing with Fermat's Little Theorem: " + fermatLittleTheorem.fermatMethod(new BigInteger("1076017")));
        System.out.println("1002 is not prime number. Test result according to Fermat's Little Theorem: " + fermatLittleTheorem.fermatMethod(new BigInteger("1002")));
        System.out.println("===================FAST MODULAR EXPONENTIATION=================");
        System.out.println("5^121242653 in mod(11) is equal to: " + fastModularExponentiation.getMod(new BigInteger("5"), new BigInteger("121242653"), new BigInteger("11")));
        System.out.println("===================AES ENCRYPTION AND DECRYPTION===============");
        System.out.println("AES plaintext is 'Cryptography homework #1' and cipher text is: " + aes.encrypt("Cryptography homework #1"));
        System.out.println("Randomly generated key is: " + aes.getSecretKey());
        System.out.println("Now we're testing the decryption plaintext is: " + aes.decrypt(aes.encrypt("Cryptography homework #1")));
    }
}
