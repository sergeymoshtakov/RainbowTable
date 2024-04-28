import java.security.*;
import java.math.BigInteger;
import java.util.*;

public class RainbowTable {

    // possible password symbols
    static final String Z = "0123456789abcdefghijklmnopqrstuvwxyz";

    // password length
    static final int L = 7;

    // chain length
    static final int CHAIN_LENGTH = 2000;

    public static void main(String[] args) {
        List<String> rainbowTable = createRainbowTable();

        for (int i = 0; i < rainbowTable.size(); i += 2) {
            String password = rainbowTable.get(i);
            String hash = rainbowTable.get(i + 1);
            System.out.println("Password: " + password);
            System.out.println("Hash value: " + hash);
        }

        // hash value to check
        String targetHash = "1d56a37fb6b08aa709fe90e12ca59e12";

        String plaintext = findCleartext(targetHash, rainbowTable);
        if (plaintext != null) {
            System.out.println("Clear text founded: " + plaintext);
        } else {
            System.out.println("Clear text not founded.");
        }
    }

    // Rainbow Table
    static List<String> createRainbowTable() {
        List<String> rainbowTable = new ArrayList<>();
        String password = "0000000";
        for (int i = 0; i < CHAIN_LENGTH; i++) {
            String hash = hash(password);
            rainbowTable.add(password);
            rainbowTable.add(hash);
            password = reduce(hash, i);
        }
        return rainbowTable;
    }

    // MD5 hash function
    static String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Reduce function from 3.27
    static String reduce(String hash, int step) {
        BigInteger H = new BigInteger(hash, 16);
        H = H.add(BigInteger.valueOf(step));
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < L; i++) {
            BigInteger[] divMod = H.divideAndRemainder(BigInteger.valueOf(Z.length()));
            int index = divMod[1].intValue();
            result.insert(0, Z.charAt(index));
            H = divMod[0];
        }
        return result.toString();
    }

    // function to find Clear Text
    static String findCleartext(String targetHash, List<String> rainbowTable) {
        for (int i = 0; i < rainbowTable.size(); i += 2) {
            String hash = rainbowTable.get(i + 1);
            if (hash.equals(targetHash)) {
                return rainbowTable.get(i);
            }
        }
        return null;
    }
}
