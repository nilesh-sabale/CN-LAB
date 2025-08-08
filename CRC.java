import java.util.*;

class CRC {    
    // Returns XOR of 'a' and 'b' (bitwise comparison)
    static String findXor(String a, String b) {
        int n = b.length();
        StringBuilder result = new StringBuilder();
        
        // Compare each bit (skip first bit as per original logic)
        for (int i = 1; i < n; i++) {
            if (a.charAt(i) == b.charAt(i)) {
                result.append('0');
            } else {
                result.append('1');
            }
        }
        return result.toString();
    }

    // Performs Modulo-2 division (CRC division)
    static String mod2div(String dividend, String divisor) {
        int n = dividend.length();
        int pick = divisor.length();
        String tmp = dividend.substring(0, pick);

        while (pick < n) {
            if (tmp.charAt(0) == '1') {
                // XOR with divisor and bring down next bit
                tmp = findXor(divisor, tmp) + dividend.charAt(pick);
            } else {
                // XOR with zeros and bring down next bit
                tmp = findXor(String.format("%0" + pick + "d", 0), tmp) 
                      + dividend.charAt(pick);
            }
            pick += 1;
        }

        // Final XOR step
        if (tmp.charAt(0) == '1') {
            tmp = findXor(divisor, tmp);
        } else {
            tmp = findXor(String.format("%0" + pick + "d", 0), tmp);
        }

        return tmp;
    }

    // Appends CRC remainder to original data
    public static String encodeData(String data, String key) {
        int n = key.length();
        String str = data + String.join("", Collections.nCopies(n - 1, "0"));
        String remainder = mod2div(str, key);
        return data + remainder;
    }

    // Checks if received data has errors
    public static int receiver(String code, String key) {
        String remainder = mod2div(code, key);
        return remainder.contains("1") ? 0 : 1;
    }

    public static void main(String[] args) {
        String data = "100100";
        String key = "1101";
        
        System.out.println("Data: " + data);
        System.out.println("Key: " + key);
        String code = encodeData(data, key);
        System.out.println("Encoded Data: " + code + "\n");

        System.out.println("Receiver Side");
        if (receiver(code, key) == 1) {
            System.out.println("Data is correct (No errors detected)");
        } else {
            System.out.println("Data is incorrect (Error detected)");
        }
    }
}