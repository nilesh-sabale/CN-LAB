import java.io.*;
import java.net.*;

public class TCPReceiver {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(7000);
        System.out.println("TCP Receiver listening on port 7000...");
        Socket connectionSocket = serverSocket.accept();

        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

        String msg;
        while ((msg = inFromClient.readLine()) != null) {
            System.out.println(" Received: " + msg);
            outToClient.writeBytes("ACK for " + msg + "\n");
        }

        connectionSocket.close();
        serverSocket.close();
    }
}
