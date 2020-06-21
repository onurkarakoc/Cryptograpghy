import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FastModularExponentiation {


    public static BigInteger getMod(BigInteger base, BigInteger exponent, BigInteger modulus){
        List<BigInteger> listOfPowersOfExponent = getPowersOfTwos(exponent);
        BigInteger mult = BigInteger.ONE;

        for(BigInteger exp : listOfPowersOfExponent){
            //We're calculating the mod for every exponent in the list according to Fast Exponentiation Algorithm.
            BigInteger mod = getModPrivate(base, exp, modulus);
            mult = mult.multiply(mod);
        }
        return mult.mod(modulus);
    }
    // Thanks to this function, we will calculate the mod for every exponent in the above function
    private static BigInteger getModPrivate(BigInteger base, BigInteger exponent, BigInteger modulus){
        while(! exponent.equals(BigInteger.ONE)){
            if(base.equals(BigInteger.ONE)){
                return BigInteger.ONE;
            }
            base = getNegativeModOfNumber(base, modulus);
            exponent = exponent.divide(BigInteger.TWO);

            base = base.pow(2);

        }
        return base.mod(modulus);
    }

    //This function will give us the negative mod of the number. For example 14 is equal to 3 in mod 11. And also -8 is also equal to 3 in mod 11.
    private static BigInteger getNegativeModOfNumber(BigInteger n, BigInteger modulus){
        return n.mod(modulus).subtract(modulus);
    }


    private static List<BigInteger> getPowersOfTwos(BigInteger n){
        //binaryRepresentation is the reverse binary form of n
        List<BigInteger> binaryRepresentation = new ArrayList<BigInteger>();
        List<BigInteger> powersOfTwosList = new ArrayList<BigInteger>();
        BigInteger remainder, quotient;

        while (true){
            quotient = n.divide(BigInteger.TWO);
            remainder = n.mod(BigInteger.TWO);
            if(quotient.equals(BigInteger.ONE)){
                binaryRepresentation.add(remainder);
                binaryRepresentation.add(quotient);
                break;
            }
            else{
                binaryRepresentation.add(remainder);
                n = quotient;
            }
        }
        // Here we're calculating the powers and collections.reverse will give to us normal order. For example 28 is 16 + 8 + 4
        for(int i = 0; i<binaryRepresentation.size(); i++) {
            if (binaryRepresentation.get(i).equals(BigInteger.ONE))
                powersOfTwosList.add(BigInteger.TWO.pow(i));
        }
        Collections.reverse(powersOfTwosList);
        return powersOfTwosList;

    }

}
