package primary;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static java.lang.Integer.parseInt;

/**
 * This class builds a message from one line of input
 * The message has timeAtReq, startingFloor, direction and destFloor
 * Metadata includes sender and intended receiver of this message
 * Metadata will change as the message is being passed around to different threads
 */
public class Message {
    private Time timeAtReq; //this is the Time in the input line, first segment of string
    private Integer startingFloor; //this is the Floor in the input line, second segment of the string
    private String direction; //this is the Floor Button in the input line, third segment in the string
    private int destFloor; //this is the Car Button in the input line, fourth segment in the string

    private EntityType fromWho;//metadata, sender of this message
    private EntityType toWho;//metadata, intended receiver of this message


    public Message(String line){
        this.timeAtReq = null;
        processString(line);
    }

    /**
     * Overloaded constructor to make a duplicate object from an existing message
     * This is used so that we have distinct objects when passing an identical message for Iteration 1
     * //TODO may be removed in future iterations
     * @param m: message that is to be copied into a new object
     */
    public Message(Message m){
        this.timeAtReq = m.getTimeAtReq();
        this.startingFloor = m.getStartingFloor();
        this.direction = m.getDirection();
        this.destFloor = m.getDestFloor();
        this.fromWho = m.getFromWho();
        this.toWho = m.getToWho();
    }

    /**
     * Processes one line of input in format "hh:mm:ss.mmm integer1 Up/Down integer2" by splitting the words by space and
     * saving the values in timeAtRequest, startingFloor, direction and destFloor
     * All strings are changed to lower case and any trailing \n is removed
     * @param line: a string in the format "hh:mm:ss.mmm integer1 Up/Down integer2" , may end in \n
     */
    private void processString(String line){
        String[] words = line.toLowerCase().replace("\n","").split(" ");

        //time
        DateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss.SSS");
        try {
            this.timeAtReq = new Time(dateFormatter.parse(words[0]).getTime());
        } catch (ParseException e) {
            System.out.println("Invalid time "+ e);
        }

        //starting floor
        try{
            this.startingFloor = Integer.parseInt(words[1]);
        } catch (NumberFormatException e){
            System.out.println("Invalid integer "+e);
        }

        //direction
        this.direction = words[2].toLowerCase();

        //destination floor
        try{
            this.destFloor = parseInt(words[3]);
        } catch (NumberFormatException e){
            System.out.println("Invalid integer "+e);
        }
    }

    public void print(){
        System.out.println("Time processed: " + timeAtReq.toString() + "\tStarting floor: " + startingFloor + "\tDirection: " + direction + "\tDestination floor: " + destFloor);
    }

    public Time getTimeAtReq() {
        return timeAtReq;
    }

    public int getStartingFloor() {
        return startingFloor;
    }

    public int getDestFloor(){
        return destFloor;
    }

    public String getDirection(){
        return direction;
    }

    public EntityType getFromWho(){ return fromWho; }
    public void setFromWho(EntityType frmWho){
        this.fromWho = frmWho;
    }

    public EntityType getToWho(){ return toWho; }
    public void setToWho(EntityType toWho){
        this.toWho = toWho;
    }

}
