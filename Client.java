import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            Socket s = new Socket("127.0.0.1", 5000);
            DataInputStream din = new DataInputStream(s.getInputStream());
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());

            System.out.println("Select Operation:");
            System.out.println("1. Say Hello");
            System.out.println("2. File Transfer");
            System.out.println("3. Arithmetic Calculator");
            System.out.println("4. Trigonometric Calculator");
            System.out.print("Enter your choice: ");
            String choice = sc.nextLine();

            // Send choice to server
            dout.writeUTF(choice);
            dout.flush();

            switch (choice) {
                case "1":
                    sayHello(din, dout);
                    break;
                case "2":
                    sendFile(s);
                    break;
                case "3":
                    arithmeticCalc(din, dout, sc);
                    break;
                case "4":
                    trigonometricCalc(din, dout, sc);
                    break;
                default:
                    System.out.println("Invalid option!");
                    break;
            }

            din.close();
            dout.close();
            s.close();
            System.out.println("Client closed.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // (a) Say Hello
    static void sayHello(DataInputStream din, DataOutputStream dout) throws IOException {
        dout.writeUTF("Hello Server, this is Client!");
        dout.flush();
        String reply = din.readUTF();
        System.out.println("Server says: " + reply);
    }

    // (b) File Transfer
    static void sendFile(Socket s) throws IOException {
        File file = new File("send.txt");
        if (!file.exists()) {
            System.out.println("File 'send.txt' not found! Please create it in this folder first.");
            return;
        }

        FileInputStream fis = new FileInputStream(file);
        OutputStream out = s.getOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;

        System.out.println("Sending file...");
        while ((bytesRead = fis.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        fis.close();
        out.close();
        System.out.println("File sent successfully!");
    }

    // (c) Arithmetic Calculator
    static void arithmeticCalc(DataInputStream din, DataOutputStream dout, Scanner sc) throws IOException {
        System.out.print("Enter first number: ");
        double a = sc.nextDouble();
        System.out.print("Enter second number: ");
        double b = sc.nextDouble();
        System.out.print("Enter operator (+, -, *, /): ");
        String op = sc.next();

        dout.writeDouble(a);
        dout.writeDouble(b);
        dout.writeUTF(op);
        dout.flush();

        String result = din.readUTF();
        System.out.println(result);
    }

    // (d) Trigonometric Calculator
    static void trigonometricCalc(DataInputStream din, DataOutputStream dout, Scanner sc) throws IOException {
        System.out.print("Enter function (sin/cos/tan): ");
        String func = sc.next();
        System.out.print("Enter angle (in degrees): ");
        double angle = sc.nextDouble();

        dout.writeUTF(func);
        dout.writeDouble(angle);
        dout.flush();

        String result = din.readUTF();
        System.out.println(result);
    }
}
