package primary;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Floor implements Runnable{
    private final int flrNum;
    private ArrayList<Message> messagesToSend;
    private ArrayList<Message> msgsReceived;
    private ArrayList<Message> messagesReadFromFile;

    public Floor(String filepath, int flrNum ){
        this.flrNum = flrNum;
        this.messagesToSend = new ArrayList<>();
        this.messagesReadFromFile = new ArrayList<>();
        this.msgsReceived = new ArrayList<>();

        readMessagesForThisFloorFromFile(filepath);
        Mailbox.getInstance().register("Floor" + this.flrNum);
    }
    /**
     *Reads the input file, converts all lines into messages checks if the starting floor number == this floor number and adds them to allEvents list
     * @param filePath : string representing the filepath to the input file
     */
    private void readMessagesForThisFloorFromFile(String filePath){
        try {
            File myObj = new File(filePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                Message message = new Message(line);
                if (message.getStartingFloor() == this.flrNum){
                    messagesToSend.add(message);
                    messagesReadFromFile.add(message);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found.");
        }
        System.out.println("Floor "+this.flrNum+" successfully read these messages from file: ");
        for (Message m : messagesReadFromFile) {
            m.print();
        }
    }
    public ArrayList<Message> getMessagesToSend() {
        return messagesToSend;
    }
    public ArrayList<Message> getMessagesReadFromFile() {
        return messagesReadFromFile;
    }

    @Override
    public void run() {

        System.out.println("The floor subsystem for floor " + this.flrNum + " has started.");
        //sending only the first message that was read from the file - proof of concept
        Message m;
        while (true){
            if(!messagesToSend.isEmpty()){
                m = new Message(messagesToSend.remove(0));
                m.sentFromWho("Floor"+this.flrNum);
                m.sentToWho("Scheduler");
                m.print();
                System.out.println("Floor sending message to scheduler...");
                Mailbox.getInstance().put(m);
            }

            //check if there are any messages in mailbox for the floor
            m = Mailbox.getInstance().get("Floor"+this.flrNum);
            if (m != null) {
                System.out.println("Floor received message from " + m.getFromWho()+ ", stopping thread");
                msgsReceived.add(m);
                return;
            }

        }
    }

    public void addToAllEvents(Message m){
        messagesToSend.add(m);
    }

    public ArrayList<Message> getMsgsReceived(){ return msgsReceived; }
}
