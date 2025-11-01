import java.net.*;
import java.util.*;

public class GBNSenderPeer {
    public static void main(String[] args) throws Exception {
        DatagramSocket senderSocket = new DatagramSocket(3000);
        InetAddress receiverAddress = InetAddress.getByName("localhost");

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter total number of packets: ");
        int totalPackets = sc.nextInt();
        System.out.print("Enter window size: ");
        int windowSize = sc.nextInt();

        int base = 0;
        int nextSeqNum = 0;
        byte[] sendData;
        byte[] receiveData = new byte[1024];

        while (base < totalPackets) {
            while (nextSeqNum < base + windowSize && nextSeqNum < totalPackets) {
                String data = String.valueOf(nextSeqNum);
                sendData = data.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receiverAddress, 5000);
                senderSocket.send(sendPacket);
                System.out.println(" Sent packet: " + nextSeqNum);
                nextSeqNum++;
            }

            senderSocket.setSoTimeout(2000);
            try {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                senderSocket.receive(receivePacket);
                String ackStr = new String(receivePacket.getData(), 0, receivePacket.getLength());
                int ackNum = Integer.parseInt(ackStr);
                System.out.println(" Received ACK: " + ackNum);
                base = ackNum;
            } catch (SocketTimeoutException e) {
                System.out.println(" Timeout! Retransmitting window...");
                nextSeqNum = base;
            }
        }

        // End transmission
        sendData = "exit".getBytes();
        DatagramPacket endPacket = new DatagramPacket(sendData, sendData.length, receiverAddress, 5000);
        senderSocket.send(endPacket);
        System.out.println("Transmission completed successfully.");
        senderSocket.close();
    }
}
