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
    private int destFloor; //this is the Car Button in the input line, fourth segment in the string, final destination

    private String fromWho;//metadata, sender of this message
    private String toWho;//metadata, intended receiver of this message
    private MessageType type;//metadata, what type of message it is
    private int currentFloor; //the current floor of the elevator, sent as an update to scheduler when MessageType type == CURRENT_FLOOR_UPDATE

    private MovingDirection movingDirection;//this is the direction in which the elevator is requested to move when MessageType type == MOVE
    private boolean fulfilledStartingFloor;
    private boolean fulfilledDestFloor;

    /**
     * Overloaded constructor to be used by floor
     * Builds a message from a line as read from file by each floor
     * @param line: one line read from the input file for example: 13:50:15.29 5 down 4
     */
    public Message(String line){
        this.timeAtReq = null;
        processString(line);
        this.type = MessageType.INITIAL_READ_FROM_FILE;
        this.movingDirection = MovingDirection.NONE;
        this.currentFloor = -1;//default value
        this.fulfilledStartingFloor = false;
        this.fulfilledDestFloor = false;

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
        this.type = m.getType();
        this.movingDirection = m.getMovingDirection();
        this.currentFloor = m.getCurrentFloor();
        this.fulfilledStartingFloor = m.fulfilledStartingFloor;
        this.fulfilledDestFloor = m.fulfilledDestFloor;
    }


    /**
     * Overloaded constructor to make a new message from an existing message m with a different type
     * @param m: message that is to be copied into a new object
     * @param type: the new type to be given to the object
     */
    public Message(Message m, MessageType type){
        this.timeAtReq = m.getTimeAtReq();
        this.startingFloor = m.getStartingFloor();
        this.direction = m.getDirection();
        this.destFloor = m.getDestFloor();
        this.fromWho = m.getFromWho();
        this.toWho = m.getToWho();
        this.type = type;
        this.movingDirection = m.getMovingDirection();
        this.currentFloor = m.getCurrentFloor();
        this.fulfilledStartingFloor = m.fulfilledStartingFloor;
        this.fulfilledDestFloor = m.fulfilledDestFloor;
    }

    /**
     * Overloaded constructor to be used by scheduler when sending a MOVE message
     * builds a new message from an existing message m and changes its type and moving direction
     * The moving direction will change only if the type == MessageType.MOVE
     * @param m: message that is to be copied into a new object
     * @param type: the new type to be given to the object
     * @param dir: the moving direction in which the elevator should travel
     */
    public Message(Message m, MessageType type, MovingDirection dir){
        this.timeAtReq = m.getTimeAtReq();
        this.startingFloor = m.getStartingFloor();
        this.direction = m.getDirection();
        this.destFloor = m.getDestFloor();
        this.fromWho = m.getFromWho();
        this.toWho = m.getToWho();
        this.type = type;
        if (type == MessageType.MOVE){
            this.movingDirection = dir;
        }else{
            this.movingDirection = m.getMovingDirection();
        }
        this.fulfilledStartingFloor = m.fulfilledStartingFloor;
        this.fulfilledDestFloor = m.fulfilledDestFloor;
    }

    /**
     * Overloaded constructor to be used by floor when sending a CURRENT_FLOOR_UPDATE message
     * makes a new message from an existing message m
     * the current floor will only change if the type == MessageType.CURRENT_FLOOR_UPDATE
     * @param m: message that is to be copied into a new object
     * @param type: the new type to be given to the object
     * @param currentFloor: the current floor at which the elevator is located
     */
    public Message(Message m, MessageType type, int currentFloor){
        this.timeAtReq = m.getTimeAtReq();
        this.startingFloor = m.getStartingFloor();
        this.direction = m.getDirection();
        this.destFloor = m.getDestFloor();
        this.fromWho = m.getFromWho();
        this.toWho = m.getToWho();
        this.type = type;
        if (type == MessageType.CURRENT_FLOOR_UPDATE){
            this.currentFloor = currentFloor;
        }
        this.movingDirection = m.getMovingDirection();
        this.fulfilledStartingFloor = m.fulfilledStartingFloor;
        this.fulfilledDestFloor = m.fulfilledDestFloor;
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
        System.out.println("Time processed: " + timeAtReq.toString() + "\tStarting floor: " + startingFloor + "\tMovingDirection: " + direction + "\tDestination floor: " + destFloor);
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

    public String getFromWho(){ return fromWho; }
    public void sentFromWho(String frmWho){
        this.fromWho = frmWho;
    }

    public String getToWho(){ return toWho; }
    public void sentToWho(String toWho){
        this.toWho = toWho;
    }

    public MessageType getType() {
        return this.type;
    }
    public void setType( MessageType type) {
        this.type = type;
    }

    public MovingDirection getMovingDirection() { return this.movingDirection; }

    public int getCurrentFloor() { return this.currentFloor; }
    public boolean isFulfilledStartingFloor() { return this.fulfilledStartingFloor; }
    public void setFulfilledStartingFloor(boolean val) {
        fulfilledStartingFloor = val;
    }

    public boolean isFulfilledDestFloor() { return this.fulfilledDestFloor; }
    public void setFulfilledDestFloor(boolean val) {
        fulfilledDestFloor = val;
    }

}