import java.io.*;
import java.net.*;

public class TCPSender {
    public static void main(String[] args) throws Exception {
        Socket clientSocket = new Socket("localhost", 7000);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        for (int i = 0; i < 10; i++) {
            String msg = "Packet " + i;
            outToServer.writeBytes(msg + "\n");
            System.out.println(" Sent: " + msg);
            String ack = inFromServer.readLine();
            System.out.println(" Received: " + ack);
            Thread.sleep(300); // simulate delay
        }

        clientSocket.close();
    }
}
