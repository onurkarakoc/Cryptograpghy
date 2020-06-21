import java.math.BigInteger;
import java.util.Random;

public class FermatLittleTheorem {

   private final static Random random = new Random();

   public boolean fermatMethod(BigInteger testForPrime){
       // 1 is not prime.
       if(testForPrime.equals(BigInteger.ONE))
           return false;
       //becomes more certain as number of iterations are increased.
       for(int i = 0; i<10000; i++){
            BigInteger randomCoPrime = generateRandomCoPrimeNumber(testForPrime);
            randomCoPrime = FastModularExponentiation.getMod(randomCoPrime, testForPrime.subtract(BigInteger.ONE), testForPrime);
            // Theorem says that a(n-1) = 1 (mod n).
            if(!randomCoPrime.equals(BigInteger.ONE))
                return false;
       }
       //if passes the test for every iteration probably it is prime.
       return true;
   }

    private BigInteger generateRandomCoPrimeNumber(BigInteger n){
        while(true){
            //We generated random coprime number here. RandomCoPrime has same bit length with n. This is final so that nobody can change it.
            final BigInteger randomCoPrime = new BigInteger(n.bitLength(), random);
            //We tested it 1<= randomCoPrime < n , where n is the number to be tested for primality.
            if(randomCoPrime.compareTo(BigInteger.ONE) >= 0 && n.compareTo(randomCoPrime) > 0)
                return randomCoPrime;
        }
    }

}
