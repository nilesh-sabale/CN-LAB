import java.io.*;
import java.net.*;
import java.lang.Math;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(5000);
            System.out.println("Server started on port 5000");
            System.out.println("Waiting for client connection...");

            Socket s = ss.accept();
            System.out.println("Client connected!");

            DataInputStream din = new DataInputStream(s.getInputStream());
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());

            // Receive option from client
            String option = din.readUTF();
            System.out.println("Client selected option: " + option);

            switch (option) {
                case "1":
                    sayHello(din, dout);
                    break;
                case "2":
                    receiveFile(s);
                    break;
                case "3":
                    arithmeticCalculator(din, dout);
                    break;
                case "4":
                    trigonometricCalculator(din, dout);
                    break;
                default:
                    System.out.println("Invalid option received.");
            }

            din.close();
            dout.close();
            s.close();
            ss.close();
            System.out.println("Server closed.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // (a) Say Hello
    static void sayHello(DataInputStream din, DataOutputStream dout) throws IOException {
        String msg = din.readUTF();
        System.out.println("Client says: " + msg);
        dout.writeUTF("Hello Client, this is Server!");
        dout.flush();
    }

    // (b) File Transfer
    static void receiveFile(Socket s) throws IOException {
        InputStream in = s.getInputStream();
        FileOutputStream fos = new FileOutputStream("received.txt");
        byte[] buffer = new byte[4096];
        int bytesRead;
        System.out.println("Receiving file...");
        while ((bytesRead = in.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
        System.out.println("File received successfully! (saved as received.txt)");
        fos.close();
    }

    // (c) Arithmetic Calculator
    static void arithmeticCalculator(DataInputStream din, DataOutputStream dout) throws IOException {
        double num1 = din.readDouble();
        double num2 = din.readDouble();
        String op = din.readUTF();

        double result = 0;
        switch (op) {
            case "+": result = num1 + num2; break;
            case "-": result = num1 - num2; break;
            case "*": result = num1 * num2; break;
            case "/": result = (num2 != 0) ? num1 / num2 : 0; break;
            default: dout.writeUTF("Invalid operator!"); return;
        }

        dout.writeUTF("Result: " + result);
        dout.flush();
    }

    // (d) Trigonometric Calculator
    static void trigonometricCalculator(DataInputStream din, DataOutputStream dout) throws IOException {
        String func = din.readUTF();
        double angle = din.readDouble();
        double result = 0;

        switch (func.toLowerCase()) {
            case "sin": result = Math.sin(Math.toRadians(angle)); break;
            case "cos": result = Math.cos(Math.toRadians(angle)); break;
            case "tan": result = Math.tan(Math.toRadians(angle)); break;
            default: dout.writeUTF("Invalid function!"); return;
        }

        dout.writeUTF(func + "(" + angle + ") = " + result);
        dout.flush();
    }
}
