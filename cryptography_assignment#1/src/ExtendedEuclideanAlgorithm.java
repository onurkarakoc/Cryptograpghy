import java.math.BigInteger;
import java.util.Scanner;

public class ExtendedEuclideanAlgorithm {

    /**
     *
     * @param a
     * @param b
     * @return resultArray {x, y, gcd}
     */
    public BigInteger[] findGcdXAndY(BigInteger a, BigInteger b){
        //We are defining the constants in order to find x_j and y_j.
        BigInteger x_0 = BigInteger.ZERO, x_1 = BigInteger.ONE, y_0 = BigInteger.ONE, y_1 = BigInteger.ZERO, temp, quotient, remainder, copyOfA, copyOfB;
        //We'll use these copies(original values) at the end of the function in order to find gcd according to Extended Euclidean Algorithm.
        copyOfA = a;
        copyOfB = b;
        BigInteger[] resultArray = new BigInteger[3];
        // if b is 0
        if(b.equals(BigInteger.ZERO))
            throw new IllegalArgumentException("Argument b cannot be 0, dividing by zero is not possible in mathematical operation");
        // means that if b is equal to 0, we can stop.
        while (b.compareTo(BigInteger.ZERO) != 0){
            quotient = a.divide(b);
            remainder = a.mod(b);
            a = b;
            b = remainder;

            temp = x_0;
            x_0 = x_1.subtract(quotient.multiply(x_0));
            x_1 = temp;

            temp = y_0;
            y_0 = y_1.subtract(quotient.multiply(y_0));
            y_1 = temp;
        }
        resultArray[0] = x_1;
        resultArray[1] = y_1;
        resultArray[2] = copyOfA.multiply(x_1).add(copyOfB.multiply(y_1));
        return resultArray;
    }

    // TODO: 18.11.2019 We'll implement the multiplicative inverse of a number in mod p.
    // return -1 means that a and b are not relatively prime; so we cannot use the rule here.
    public BigInteger findMultiplicativeInverse(BigInteger a, BigInteger b){
        BigInteger[] resultArray = findGcdXAndY(a, b);
        if(!resultArray[2].equals(BigInteger.ONE))
            return new BigInteger("-1");
        return resultArray[0].mod(b).add(b).mod(b);
    }

    //We are computing the GCD using  Euclidean Algorithm
    public BigInteger findGcdOfTwoNumber(BigInteger a, BigInteger b){
        BigInteger temp;
        while(b.compareTo(BigInteger.ZERO) != 0){
            temp = b;
            b = a.mod(b);
            a = temp;
        }
        return a;
    }

    // GCD is 1 means that two numbers are relatively prime.
     public boolean areRelativelyPrime(BigInteger a, BigInteger b){
        BigInteger result = findGcdOfTwoNumber(a, b);
        if (result.compareTo(BigInteger.ONE) == 0)
                return true;
        return false;
    }
}
