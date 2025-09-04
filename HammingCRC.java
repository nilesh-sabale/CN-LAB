import java.util.*;

public class HammingCRC {
    // CRC divisor polynomial (example: x^3 + x + 1 => 1011)
    static String divisor = "1011";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Input string
        System.out.println("Enter ASCII string (7/8 bit per char): ");
        String input = sc.nextLine();

        // Step 1: Convert ASCII to binary (8-bit)
        StringBuilder binary = new StringBuilder();
        for (char c : input.toCharArray()) {
            String b = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
            binary.append(b);
        }
        System.out.println("Binary (8-bit ASCII): " + binary);

        // Step 2: Encode using Hamming (7,4)
        StringBuilder hammingEncoded = new StringBuilder();
        for (int i = 0; i < binary.length(); i += 4) {
            String block = binary.substring(i, Math.min(i + 4, binary.length()));
            hammingEncoded.append(hammingEncode(block));
        }
        System.out.println("After Hamming encoding: " + hammingEncoded);

        // Step 3: Append CRC
        String codeword = appendCRC(hammingEncoded.toString());
        System.out.println("Final Codeword with CRC: " + codeword);

        // ===== Receiver Side =====
        System.out.println("\n--- Receiver Side ---");
        System.out.print("Do you want to flip a bit? (y/n): ");
        String ans = sc.nextLine();
        String received = codeword;
        if (ans.equalsIgnoreCase("y")) {
            System.out.print("Enter position to flip (0-based index): ");
            int pos = sc.nextInt();
            received = flipBit(codeword, pos);
            System.out.println("Received (with error): " + received);
        }

        // Step 4: CRC check
        String remainder = calculateCRC(received, false);
        if (!remainder.equals("0")) {
            System.out.println("CRC check FAILED: Multi-bit error detected.");
        } else {
            System.out.println("CRC check PASSED.");
        }

        // Step 5: Decode Hamming
        StringBuilder decodedBinary = new StringBuilder();
        for (int i = 0; i < received.length() - (divisor.length() - 1); i += 7) {
            if (i + 7 <= received.length() - (divisor.length() - 1)) {
                String block = received.substring(i, i + 7);
                decodedBinary.append(hammingDecode(block));
            }
        }

        // Step 6: Binary to ASCII
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < decodedBinary.length(); i += 8) {
            if (i + 8 <= decodedBinary.length()) {
                int val = Integer.parseInt(decodedBinary.substring(i, i + 8), 2);
                message.append((char) val);
            }
        }
        System.out.println("Decoded message: " + message);
    }

    // ===== Hamming Encode (7,4) =====
    public static String hammingEncode(String data) {
        while (data.length() < 4) data += "0"; // pad to 4 bits
        int d1 = data.charAt(0) - '0';
        int d2 = data.charAt(1) - '0';
        int d3 = data.charAt(2) - '0';
        int d4 = data.charAt(3) - '0';
        int p1 = d1 ^ d2 ^ d4;
        int p2 = d1 ^ d3 ^ d4;
        int p3 = d2 ^ d3 ^ d4;
        return "" + p1 + p2 + d1 + p3 + d2 + d3 + d4; // 7 bits
    }

    // ===== Hamming Decode =====
    public static String hammingDecode(String code) {
        int p1 = code.charAt(0) - '0';
        int p2 = code.charAt(1) - '0';
        int d1 = code.charAt(2) - '0';
        int p3 = code.charAt(3) - '0';
        int d2 = code.charAt(4) - '0';
        int d3 = code.charAt(5) - '0';
        int d4 = code.charAt(6) - '0';

        // syndrome
        int c1 = p1 ^ d1 ^ d2 ^ d4;
        int c2 = p2 ^ d1 ^ d3 ^ d4;
        int c3 = p3 ^ d2 ^ d3 ^ d4;
        int errorPos = c1 * 1 + c2 * 2 + c3 * 4;

        if (errorPos != 0) {
            System.out.println("Single-bit error detected at position: " + errorPos + " -> Corrected");
            char[] arr = code.toCharArray();
            arr[errorPos - 1] = (arr[errorPos - 1] == '0') ? '1' : '0';
            code = new String(arr);
        }
        return "" + code.charAt(2) + code.charAt(4) + code.charAt(5) + code.charAt(6);
    }

    // ===== CRC Functions =====
    public static String appendCRC(String data) {
        return data + calculateCRC(data, true);
    }

    public static String calculateCRC(String data, boolean sender) {
        String user = data;
        if (sender) {
            for (int i = 0; i < divisor.length() - 1; i++) user += "0";
        }
        int counter = divisor.length();
        String rem = user.substring(0, divisor.length());
        while (counter <= user.length()) {
            String ans = "";
            String div = (rem.charAt(0) == '0') ? "0".repeat(divisor.length()) : divisor;
            for (int j = 0; j < divisor.length(); j++) {
                ans += (Integer.parseInt("" + rem.charAt(j)) ^ Integer.parseInt("" + div.charAt(j)));
            }
            rem = "";
            boolean zero = true;
            for (int k = 0; k < ans.length(); k++) {
                if (ans.charAt(k) == '1') zero = false;
                if (!zero) rem += ans.charAt(k);
            }
            while (rem.length() < divisor.length() && counter < user.length()) {
                rem += user.charAt(counter++);
            }
            if (counter >= user.length() && rem.length() < divisor.length()) break;
        }
        if (rem.equals("")) rem = "0";
        return rem;
    }

    // ===== Flip bit (for error simulation) =====
    public static String flipBit(String s, int pos) {
        char[] arr = s.toCharArray();
        arr[pos] = (arr[pos] == '0') ? '1' : '0';
        return new String(arr);
    }
}
