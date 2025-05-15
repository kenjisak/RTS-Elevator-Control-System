package udp;

import org.apache.commons.lang3.SerializationUtils;
import otherResources.Message;

import java.io.IOException;
import java.net.*;

import static otherResources.Constants.*;

public class UDP {
    DatagramPacket sendPacket, receivePacket;
    DatagramSocket sendSocket, receiveSocket;

    public UDP(int receivePort){
        try {
            receiveSocket = new DatagramSocket(receivePort);
            System.out.println("Listening on port " + receivePort);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }
    public void register(String subSystemKey){
        byte[] byteMsg = ("register" + subSystemKey).getBytes();
        sendBySocket(byteMsg,MAILPORT,MAILIP);
    }
    public void sendGetRequest(String subSystemKey){
//        System.out.println(subSystemKey + " sending GET request to Mailbox...");
        byte[] byteMsg = ("GET" + subSystemKey).getBytes();
        sendBySocket(byteMsg,MAILPORT,MAILIP);
    }
    public void sendMessage(Message message, int port, InetAddress ip){
//        System.out.println(message.getFromWho() + ": sending message to Mailbox...");
        byte[] byteMsg = SerializationUtils.serialize(message);
        sendBySocket(byteMsg,port,ip);
    }
    public void sendBySocket(byte[] byteMsg, int port, InetAddress ip){
        try {
            sendSocket = new DatagramSocket();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Send the datagram packet to the server via the send/receive socket.
        sendPacket = new DatagramPacket(byteMsg, byteMsg.length,
                ip, port);
        try {
            sendSocket.send(sendPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sendSocket.close();
    }

    public Message receiveMessage(){//is now its own thread and is basically a mailbox server which will receive and send back responses with info

        byte[] data = new byte[1000];
        receivePacket = new DatagramPacket(data, data.length);
//        System.out.println("Waiting for Packet.");

        // Block until a datagram packet is received from receiveSocket.
        try {
            receiveSocket.receive(receivePacket);
        } catch (IOException ignored) {}
//        System.out.println("Received Packet.");

        return handleMessage(receivePacket);
    }
    public Message handleMessage(DatagramPacket packetReceived){
        String message = new String(packetReceived.getData(),0,packetReceived.getLength());

        if (!message.contains("type")){
            return null;
        }

        byte[] trimmedData = new byte[packetReceived.getLength()];
        System.arraycopy(packetReceived.getData(), 0, trimmedData,0,packetReceived.getLength());
        Message messageObj = SerializationUtils.deserialize(trimmedData);

        //TODO uncomment these lines once done with algorithm
        //System.out.println("\tReceived Message:" + "\n\t\tFrom: " + messageObj.getFromWho() + "\n\t\tTo: " + messageObj.getToWho());
        //System.out.println("\t\tCommand: " + messageObj.getType());
        return messageObj;
    }
}
