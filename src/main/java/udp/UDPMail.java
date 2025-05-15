package udp;

import allSystems.Mailbox;
import org.apache.commons.lang3.SerializationUtils;
import otherResources.Message;

import java.net.DatagramPacket;

import static otherResources.Constants.*;
import static otherResources.Constants.ELEVIP;

public class UDPMail extends UDP{
    Mailbox instance;
    public UDPMail(int receivePort, Mailbox instance) {
        super(receivePort);
        this.instance = instance;
    }

    /** Handles the given Datagram Packet that was received by either handling the:
     * GET request: Performs a get() for the subsystem and responds back with a message object or null
     * REGISTER request: Registers the subsystem and responds back it was successful
     * A Message: That needs to be put() into the Subsystems mailbox
     * @param packetReceived: The Datagram Packet that was received
     * @return: Always returns null
     */
    @Override
    public Message handleMessage(DatagramPacket packetReceived) {
        String message = new String(packetReceived.getData(),0,packetReceived.getLength());

        if (message.contains("GET")){
            String receiver = message.replaceAll("GET","");
            Message res = instance.get(receiver);//null possible

//            System.out.println("Mailbox sending message to " + receiver + "...");
            if (receiver.equals("Scheduler")){
                sendMessage(res,SCHEDPORT,SCHEDIP);
            } else if (receiver.contains("Elevator")) {
                String carNum = receiver.replaceAll("Elevator","");
                int elevPort = BASEELEVPORT + Integer.parseInt(carNum);

                sendMessage(res,elevPort,ELEVIP);
            }
        }else if (message.contains("register")){
            message = message.replaceAll("register","");
            instance.register(message);
            System.out.println(message + " registered successfully");
        } else {
            //received of Message type, and not a command
            byte[] trimmedData = new byte[packetReceived.getLength()];
            System.arraycopy(packetReceived.getData(), 0, trimmedData,0,packetReceived.getLength());
            Message messageObj = SerializationUtils.deserialize(trimmedData);

            System.out.println("Received Message:" + "\n\tFrom: " + messageObj.getFromWho() + "\n\tTo: " + messageObj.getToWho());
            System.out.println("\tCommand: " + messageObj.getType());

            instance.put(messageObj);
        }

        return null;
    }
}
