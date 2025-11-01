    import java.net.*;

    public class GBNReceivePeer {
        public static void main(String[] args) throws Exception {
            DatagramSocket receiverSocket = new DatagramSocket(5000);
            byte[] receiveData = new byte[1024];
            byte[] sendData;

            int expectedSeqNum = 0;
            System.out.println("Receiver ready on port 5000...");

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                receiverSocket.receive(receivePacket);
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                InetAddress senderIP = receivePacket.getAddress();
                int senderPort = 3000;

                if (message.equalsIgnoreCase("exit")) {
                    System.out.println("Transmission ended by sender.");
                    break;
                }

                int seqNum = Integer.parseInt(message);
                if (seqNum == expectedSeqNum) {
                    System.out.println("Received packet: " + seqNum);
                    expectedSeqNum++;
                } else {
                    System.out.println(" Out of order packet: " + seqNum + " | Expected: " + expectedSeqNum);
                }

                // Send ACK
                sendData = String.valueOf(expectedSeqNum).getBytes();
                DatagramPacket ackPacket = new DatagramPacket(sendData, sendData.length, senderIP, senderPort);
                receiverSocket.send(ackPacket);
                System.out.println(" Sent ACK: " + expectedSeqNum);
            }
            receiverSocket.close();
        }
    }
