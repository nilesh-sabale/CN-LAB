import java.util.Scanner;

public class CRC {

    // Function to perform XOR division
    public static String xorDivision(String dividend, String divisor) {
        int pick = divisor.length();
        String temp = dividend.substring(0, pick);

        while (pick < dividend.length()) {
            if (temp.charAt(0) == '1') {
                // Perform XOR and bring down the next bit
                temp = xor(divisor, temp) + dividend.charAt(pick);
            } else {
                // If leftmost bit is 0, replace with zeros
                temp = xor("0".repeat(pick), temp) + dividend.charAt(pick);
            }
            pick++;
        }

        // Last XOR step
        if (temp.charAt(0) == '1') {
            temp = xor(divisor, temp);
        } else {
            temp = xor("0".repeat(pick), temp);
        }

        return temp.substring(1); // remainder
    }

    // XOR operation between two binary strings
    public static String xor(String a, String b) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < b.length(); i++) {
            result.append(a.charAt(i) == b.charAt(i) ? '0' : '1');
        }
        return result.toString();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Input ASCII message (7 or 8 bits)
        System.out.print("Enter binary message (7/8 bits): ");
        String message = sc.next();

        System.out.print("Enter generator polynomial (binary): ");
        String generator = sc.next();

        // Append zeros (degree of generator - 1)
        String appendedData = message + "0".repeat(generator.length() - 1);

        // Get remainder
        String remainder = xorDivision(appendedData, generator);

        // Codeword = original message + remainder
        String codeword = message + remainder;

        System.out.println("Remainder (CRC bits): " + remainder);
        System.out.println("Transmitted codeword: " + codeword);

        // Receiver side
        System.out.print("Enter received codeword: ");
        String received = sc.next();

        String checkRemainder = xorDivision(received, generator);

        if (checkRemainder.contains("1")) {
            System.out.println("Error detected in received message!");
        } else {
            System.out.println("No error detected.");
        }

        sc.close();
    }
}
