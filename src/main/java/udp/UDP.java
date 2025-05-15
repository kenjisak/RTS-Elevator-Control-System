package udp;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.SerializationUtils;
import otherResources.Message;

import java.io.IOException;
import java.net.*;

import static otherResources.Constants.*;
@Getter
@Setter
public class UDP {
    DatagramPacket sendPacket, receivePacket;
    DatagramSocket sendSocket, receiveSocket;

    public UDP(int receivePort){
        try {
            sendSocket = new DatagramSocket();
            receiveSocket = new DatagramSocket(receivePort);
            System.out.println("Listening on port " + receivePort);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    /** Registers the Subsystem into the Mailbox so there's a queue created for its messages to be placed in
     * @param subSystemKey: The Subsystem name that'll be used as its key for its mailbox
     */
    public void register(String subSystemKey){
        byte[] byteMsg = ("register" + subSystemKey).getBytes();
        sendBySocket(byteMsg,MAILPORT,MAILIP);
    }

    /**
     * Closes the receive and sending ports of the class that's using this UDP object
     */
    public void closePorts(){//for testing and unbinding ports
        System.out.println("Closed receive port: " + receiveSocket.getLocalPort());
        System.out.println("Closed send port: " + sendSocket.getLocalPort());
        receiveSocket.close();
        sendSocket.close();
    }

    /** Sends a GET request to the mailbox with the Subsystems key
     * @param subSystemKey: The Subsystem name that'll be used as its key for its mailbox
     */
    public void sendGetRequest(String subSystemKey){
//        System.out.println(subSystemKey + " sending GET request to Mailbox...");
        byte[] byteMsg = ("GET" + subSystemKey).getBytes();
        sendBySocket(byteMsg,MAILPORT,MAILIP);
    }

    /** Sends the Message depending on the port and ip for usability
     * @param message: The Message Object that needs to be sent
     * @param port: The port the packet needs to be sent to
     * @param ip: The IP address the packet needs to be sent to
     */
    public void sendMessage(Message message, int port, InetAddress ip){
//        System.out.println(message.getFromWho() + ": sending message to Mailbox...");
        byte[] byteMsg = SerializationUtils.serialize(message);
        sendBySocket(byteMsg,port,ip);
    }

    /** Creates the Datagram Packet and Sends it through its sendSocket
     * @param byteMsg: The byte array for the message that needs to be sent
     * @param port: The port the packet needs to be sent to
     * @param ip: The IP address the packet needs to be sent to
     */
    public void sendBySocket(byte[] byteMsg, int port, InetAddress ip){
        // Send the datagram packet to the server via the send/receive socket.
        sendPacket = new DatagramPacket(byteMsg, byteMsg.length,
                ip, port);
        try {
            sendSocket.send(sendPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Receives a Packet sent to its port and handles the Message
     * @return: A Message it receives, either a valid Message object or a null if it has none in its mailbox
     */
    public Message receiveMessage(){//is now its own thread and is basically a mailbox server which will receive and send back responses with info

        byte[] data = new byte[1000];
        receivePacket = new DatagramPacket(data, data.length);
//        System.out.println("Waiting for Packet.");

        // Block until a datagram packet is received from receiveSocket.

        try {
            receiveSocket.receive(receivePacket);
        } catch (IOException ignored) {}
//        System.out.println("Received Packet.");
        if(receivePacket.getLength() == 1000){
            //fixes empty stream for thread being interrupted
            return null;
        }
        return handleMessage(receivePacket);
    }

    /** Handles the given Datagram Packet that was received by checking if the data has a message object attribute, and returning either null or the deserialized message data
     * @param packetReceived: The Datagram Packet that was received
     * @return: A Message that is either a valid Message object or a null if the data in the Packet doesn't contain an attribute in the Message object
     */
    public Message handleMessage(DatagramPacket packetReceived){
        String message = new String(packetReceived.getData(),0,packetReceived.getLength());

        if (!message.contains("type")){
            return null;
        }

        byte[] trimmedData = new byte[packetReceived.getLength()];
        System.arraycopy(packetReceived.getData(), 0, trimmedData,0,packetReceived.getLength());
        Message messageObj = SerializationUtils.deserialize(trimmedData);

        //System.out.println("\tReceived Message:" + "\n\t\tFrom: " + messageObj.getFromWho() + "\n\t\tTo: " + messageObj.getToWho());
        //System.out.println("\t\tCommand: " + messageObj.getType());
        return messageObj;
    }
}
