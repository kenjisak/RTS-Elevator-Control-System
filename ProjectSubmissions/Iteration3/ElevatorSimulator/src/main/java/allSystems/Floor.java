package allSystems;

import lombok.Getter;
import lombok.Setter;
import otherResources.Message;
import udp.UDP;

import java.io.*;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;

import static otherResources.Constants.*;
@Getter
@Setter
public class Floor implements Runnable{
    private final int flrNum;
    private ArrayList<Message> messagesToSend;
    private ArrayList<Message> msgsReceived;
    private ArrayList<Message> messagesReadFromFile;

    private boolean interrupt = false;
    private static String filepath;
    private UDP udp;
    private int FLOORPORT;
    public Floor(String filepath, int flrNum ){
        this.flrNum = flrNum;
        this.messagesToSend = new ArrayList<>();
        this.messagesReadFromFile = new ArrayList<>();
        this.msgsReceived = new ArrayList<>();
        Floor.filepath = filepath;

        readMessagesForThisFloorFromFile(filepath);

        this.FLOORPORT = BASEFLOORPORT + this.flrNum;
        udp = new UDP(FLOORPORT);
        udp.register("Floor" + this.flrNum);
    }
    /**
     *Reads the input file, converts all lines into messages checks if the starting floor number == this floor number and adds them to allEvents list
     * @param filePath : string representing the filepath to the input file
     */
    private void readMessagesForThisFloorFromFile(String filePath){
        try {
            InputStream is = Floor.class.getResourceAsStream(filePath);
            BufferedReader reader  = new BufferedReader(new InputStreamReader(is));
            Scanner myReader = new Scanner(reader);
            int numberOfLines = 0;
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                Message message = new Message(line);
                message.setUid(numberOfLines);
                numberOfLines++;
                if (message.getStartingFloor() == this.flrNum){
                    messagesToSend.add(message);
                    messagesReadFromFile.add(message);
                }
            }
            myReader.close();
        } catch (Exception e) {
            System.out.println("Input file not found.");
        }
        System.out.println("Floor "+this.flrNum+" successfully read " + messagesReadFromFile.size() + " messages from file: ");
        for (Message m : messagesReadFromFile) {
            m.print();
        }
        //sort the messages to send by time
            Collections.sort(messagesToSend);
            System.out.println("Floor "+ flrNum + " messages after sorting:");
        for (Message m : messagesToSend) {
            m.print();
        }
    }
    public static Time lowestTimeInFile(){
        Time result;
        BufferedReader reader = null;
        //get the first line of the file
        Message minMessage;
        try {
            InputStream is = Floor.class.getResourceAsStream(filepath);
            reader = new BufferedReader(new InputStreamReader(is));
            Scanner myReader = new Scanner(reader);
            String line = myReader.nextLine();
            minMessage = new Message(line);
            reader.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try {
            InputStream is = Floor.class.getResourceAsStream(filepath);
            reader = new BufferedReader(new InputStreamReader(is));
            Scanner myReader = new Scanner(reader);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                Message currMessage = new Message(line);
                if(currMessage.getTimeAtReq().compareTo(minMessage.getTimeAtReq()) < 0){
                    minMessage = currMessage;
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return minMessage.getTimeAtReq();
    }

    private ArrayList<Long> scheduleToSend(){
        //System.out.println(m.getTimeAtReq() + " HERE " + this.flrNum);
        ArrayList<Long> waitTimes = new ArrayList<>();
        long waitTime = 0;
        boolean firstMessage = true;
        for(int i = 0; i < messagesToSend.size(); i++) {
            if (firstMessage) {
                waitTime = determineFirstInterval(messagesToSend.get(i));
                firstMessage = false;
                waitTimes.add(waitTime);
            } else {
                //time to wait until we send the next message - based on timestamp
                waitTime = Message.differenceInMs(messagesToSend.get(i-1), messagesToSend.get(i));
                waitTimes.add(waitTime);
            }
        }
        //System.out.println(this.flrNum + " WaitTimes size: " + waitTimes.size() + " MessagesSize: " + messagesToSend.size());
        return waitTimes;
    }

    private Long determineFirstInterval(Message firstMessage){
        Message m;
        String stringForBuilding = " 1 down 1";

        Time lowest = lowestTimeInFile();
        m = new Message(lowest + ".00" + stringForBuilding);

        return Message.differenceInMs(m,firstMessage);
    }

    public void waiting(int waitTime){
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        System.out.println("The floor subsystem for floor " + this.flrNum + " has started.");
        Message currMess;
        ArrayList<Long> waitTimes = scheduleToSend();
        while (true){
            if(!messagesToSend.isEmpty() && !waitTimes.isEmpty()){
                currMess = new Message(messagesToSend.remove(0));
                waiting(Math.toIntExact(waitTimes.remove(0)));
                currMess.setFromWho("Floor"+this.flrNum);
                currMess.setToWho("Scheduler");
                System.out.println("Floor " +flrNum+ " sending message to scheduler: " + currMess.toString());
                udp.sendMessage(currMess,MAILPORT,MAILIP);
            }

        }
    }
    public static void main(String[] args){
        ArrayList<Thread> floors = new ArrayList<>();
        //initialize floors, one thread per floor, floor 0 is the ground floor
        for(int i = 0; i<MAX_FLOORS; i++){
            Thread floorSubsystem = new Thread(new Floor(FILE_PATH, i), "Floor " + i);
            floors.add(floorSubsystem);
        }

        Time lowest = lowestTimeInFile();
        System.out.println("Lowest time in file is: " + lowest.toString());
        //start the threads
        for (int i = 0; i< MAX_FLOORS; i++){
            floors.get(i).start();
        }
    }
}
