package primary;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FloorSubsystemSimulation implements Runnable{

    private ArrayList<Message> allEvents;
    private ArrayList<Message> msgsReceived;

    public FloorSubsystemSimulation(String filepath){
        this.allEvents = new ArrayList<>();
        readAllMessagesFromFile(filepath);
        msgsReceived = new ArrayList<>();
    }
    /**
     *Reads the input file, converts all lines into messages and adds them to allEvents list
     * @param filePath : string representing the filepath to the input file
     */
    private void readAllMessagesFromFile (String filePath){
        try {
            File myObj = new File(filePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                Message message = new Message(line);
                allEvents.add(message);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found.");
        }
        for (Message m : allEvents) {
            m.print();
        }
    }
    public ArrayList<Message> getAllEvents() {
        return allEvents;
    }

    @Override
    public void run() {

        System.out.println("The floor subsystem simulator has started.");
        //sending only the first message that was read from the file - proof of concept
        Message m;
        while (true){
            if(!allEvents.isEmpty()){
                m = new Message(allEvents.remove(0));
                m.setFromWho(EntityType.FLOOR);
                m.setToWho(EntityType.SCHED);
                m.print();
                System.out.println("Floor sending message to scheduler...");
                Messaging.getInstance().put(m);
            }

            //check if there are any messages in mailbox for the floor
            m = Messaging.getInstance().get(EntityType.FLOOR);
            if (m != null) {
                System.out.println("Floor received message from " + m.getFromWho()+ ", stopping thread");
                msgsReceived.add(m);
                return;
            }

        }
    }

    public void addToAllEvents(Message m){
        allEvents.add(m);
    }

    public ArrayList<Message> getMsgsReceived(){ return msgsReceived; }
}
