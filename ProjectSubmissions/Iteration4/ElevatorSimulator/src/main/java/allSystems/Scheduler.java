package allSystems;

import lombok.Getter;
import lombok.Setter;
import otherResources.Algorithm;
import otherResources.Message;
import otherResources.MessageType;
import otherResources.MovingDirection;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import schedStateMachine.CheckAndHandleMessage;
import schedStateMachine.SchedulerState;
import udp.UDP;
import static otherResources.Constants.*;

@Getter
@Setter
public class Scheduler implements Runnable{
    private UDP udp;
    private ArrayList<Message> completedJobs;
    private ArrayList<Message> msgsInitialReadFromFile; //these are the initial messages from the floors

    private HashMap<String, ArrayList<Message>> elevatorJobs;  // Each Elevator with their own request
                                                                        //     Each request contains the starting floor,
                                                                        //     destination floor, queue number in the
                                                                        //     algorithm, and status.

    private SchedulerState currentState;


    private HashMap<String, Algorithm> elevatorsSchedules;

    public Scheduler(){
        currentState = new CheckAndHandleMessage();
        completedJobs = new ArrayList<>();
        msgsInitialReadFromFile = new ArrayList<>();
        elevatorsSchedules = new HashMap<>(); //key = String elevator (e.g. "Elevator1"), value = Algorithm
        elevatorJobs = new HashMap<>();//key = String elevator (e.g. "Elevator1"), value = ArrayList<Message>
        initKeysElevatorsSchedules();
        initElevatorJobs();

        udp = new UDP(SCHEDPORT);
        udp.register("Scheduler");
    }

    private void initKeysElevatorsSchedules(){
        for (int i = 1; i <= MAX_ELEVATORS; i++) {
            String key = "Elevator" + i;
            elevatorsSchedules.put(key, new Algorithm());
        }
    }

    private void initElevatorJobs(){
        for (int i = 1; i <= MAX_ELEVATORS; i++) {
            String key = "Elevator" + i;
            ArrayList<Message> value = new ArrayList<>();
            this.elevatorJobs.put(key, value);
        }
    }

    public String getLeastTravelElevator(ArrayList<String> elevators, int picUp, int dropOff) {
        String leastTravelElevator = elevators.get(0);
        if (elevators.size() == 1) return leastTravelElevator;
        int leastTravel = elevatorsSchedules.get(leastTravelElevator).theoTravel(picUp, dropOff);

        for (String elevator : elevators) {
            if(this.getAlgorithm(elevator).theoTravel(picUp, dropOff) < leastTravel) {
                leastTravel = this.getAlgorithm(elevator).theoTravel(picUp, dropOff);
                leastTravelElevator = elevator;
            }
        }
        return leastTravelElevator;
    }

    public String getLeastBusyElevator(ArrayList<String> elevators) {
        String leastRequestsElevator = elevators.get(0);
        if (elevators.size() == 1) return leastRequestsElevator;
        int leastRequestsCount = elevatorsSchedules.get(leastRequestsElevator).getRequestsCount();

        for (Map.Entry<String, Algorithm> entry : elevatorsSchedules.entrySet()) {
            String key = entry.getKey();
            Algorithm value = entry.getValue();
            if(value.getRequestsCount() < leastRequestsCount) {
                leastRequestsCount = value.getRequestsCount();
                leastRequestsElevator = key;
            }
        }
        return leastRequestsElevator;
    }

    public String getOptimalElevator(int pickUp, int dropOff) {
        ArrayList<String> NOOSElevators = new ArrayList<>(); // Check for all elevators that are not out of service
        for (Map.Entry<String, Algorithm> entry : elevatorsSchedules.entrySet()) {
            if (!entry.getValue().isOutOfService()) NOOSElevators.add(entry.getKey());
        }
        if (NOOSElevators.isEmpty()) return "";

        ArrayList<String> OTWElevators = new ArrayList<>(); // Check for all elevators that are on the way
        for (String elevator : NOOSElevators) {
            Algorithm algorithm = getAlgorithm(elevator);
            if (algorithm.peek() != -1 && algorithm.onTheWay(pickUp, dropOff)) OTWElevators.add(elevator);
        }
        if (OTWElevators.isEmpty()) return getLeastBusyElevator(NOOSElevators);

        ArrayList<String> COElevators = new ArrayList<>(); // Check for elevators that already stops at one
        for (String elevator : OTWElevators) {
            if (this.getAlgorithm(elevator).containsOne(pickUp, dropOff)) COElevators.add(elevator);
        }
        if (COElevators.isEmpty()) return getLeastTravelElevator(OTWElevators, pickUp, dropOff);

        ArrayList<String> CBElevators = new ArrayList<>(); // Check for elevators that already stops at both
        for (String elevator: COElevators) {
            if (this.getAlgorithm(elevator).containsBoth(pickUp, dropOff)) CBElevators.add(elevator);
        }
        if (CBElevators.isEmpty()) return getLeastTravelElevator(COElevators, pickUp, dropOff);

        return getLeastTravelElevator(CBElevators, pickUp, dropOff);
    }

    public Algorithm getAlgorithm(String key) {
        return elevatorsSchedules.get(key);
    }

    public MovingDirection determineMovingDirection(int currFloor, int destFloor){
        MovingDirection direction;

        if(currFloor < destFloor){
            direction = MovingDirection.UP;
        } else {
            direction = MovingDirection.DOWN;
        }

        return direction;

    }
    public void addMsgToCompletedJobs(Message m){
        completedJobs.add(m);
    }

    /**
     * adds a message to initial read from file list
     * if there is already a message in the list with the same uid, m replaces that message
     * @param m: the message to be added
     */
    public void addMsgToInitialReadFromFile(Message m){
        //if there is already a message with the same uid as m, replace the old message with m
        //this is needed for fault handling when we reassign the jobs of an elevator that is now out of service
        //instead of reassigning, we resend a new message with INITIAL_READ_FROM_FILE type
        for(int i=0; i<msgsInitialReadFromFile.size(); i++){
            if (msgsInitialReadFromFile.get(i).getUid() == m.getUid()){
                msgsInitialReadFromFile.set(i, m);
                return;
            }
        }
        msgsInitialReadFromFile.add(m);
    }

    public void addElevatorJob(String elevator, Message m, int queueNumber) {
        m.setQueueNumber(queueNumber);
        this.elevatorJobs.get(elevator).add(m);
    }
    public HashMap<Message.MessageStatus, ArrayList<Message>> updateElevatorJob(String elevator, int floor, int queueNumber) {
        HashMap<Message.MessageStatus, ArrayList<Message>> main = new HashMap<>();
        ArrayList<Message> start = new ArrayList<>();
        ArrayList<Message> complete = new ArrayList<>();
        for(Message request : this.elevatorJobs.get(elevator)) { // iterate through all requests for elevator
            if (request.getQueueNumber() == queueNumber) { // all requests with specific queue number
                if (request.getStartingFloor() == floor) { // if the starting floor is the same as poped
                    request.setStatus(Message.MessageStatus.START_FLOOR_COMPLETED); // set status
                    start.add(request);
                } else if (request.getDestFloor() == floor) { // if the destination floor is the same as poped
                    request.setStatus(Message.MessageStatus.COMPLETED); // set status
                    complete.add(request);
                }
            }
        }
        main.put(Message.MessageStatus.START_FLOOR_COMPLETED, start);
        main.put(Message.MessageStatus.COMPLETED, complete);
        return main;
    }

    public ArrayList<Message> getFloorMessages(String elevator, int floor, int queueNumber) {
        ArrayList<Message> lst = new ArrayList<>();
        for (Message request : this.elevatorJobs.get(elevator)) {
            if (request.getQueueNumber() == queueNumber && (request.getStartingFloor() == floor || request.getDestFloor() == floor)) {
                lst.add(request);
            }
        }
        return lst;
    }

    /**
     * returns the next job to send as a TRAVEL_TO_FLOOR message to the elevator from a list of that
     * elevator's jobs that will fulfil the next stop
     * @param jobs: the list of messages selected from jobs assigned to that elevator that are related to its next stop
     * @return : a single message to send as its next job
     */
    public Message getNextJob(ArrayList<Message> jobs){
        if(!jobs.isEmpty()){

            //choose the first message found that has a fault and that fault has not been fulfilled
            for (Message job : jobs){
                if (job.getFault() != NO_FAULT  && !job.isFulfilledFault()){
                    return job;
                }
            }
            // send the first job that has the starting floor fulfilled
            for (Message job : jobs){
                if (job.isFulfilledStartingFloor() && !job.isFulfilledDestFloor()){
                    return job;
                }
            }
            //if no such jobs, send the first job, making sure it is not an already completed job
            for (Message job : jobs){
                if (!job.isFulfilledDestFloor()) return job;
            }
        }
        return null;
    }

    /**
     * coin toss method
     * @return: true 50% probability, false with 50% probability
     */
    public boolean tossCoinForFault(){
        Random random = new Random(System.currentTimeMillis());
        return random.nextBoolean();
        //return Math.random() < 0.5;
    }
    public void removeAllJobsFromElevatorJobs(String elevator){
        ArrayList<Message> jobs = elevatorJobs.get(elevator);
        jobs.clear();
    }

    /**
     * decides if the elevator should stop at this floor or keep moving to the next floor depending on the algorithm
     * @param currentElevatorAlgorithm: the algorithm of the elevator
     * @param currentMsg: the current message
     * @return: if the elevator should keep moving to the next floor, return a MOVE message, otherwise return a STOP message
     */
    public Message moveOrStop(Algorithm currentElevatorAlgorithm, Message currentMsg){
        if (currentElevatorAlgorithm.peek() == currentElevatorAlgorithm.getCurrentFloor()) { // Elevator has arrived
            return new Message(currentMsg, MessageType.STOP);
        }
        else {
            int destinationFloor = currentElevatorAlgorithm.peek();
            MovingDirection direction = determineMovingDirection(currentMsg.getCurrentFloor(), destinationFloor);
            return new Message(currentMsg, MessageType.MOVE, direction);
        }
    }

    /**
     * gives the current system time in HH:mm:ss
     * @return: the current time
     */
    public String getCurrentSystemTime(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        LocalDateTime now = LocalDateTime.now();
        return "(" + dtf.format(now) + ")";
    }

    @Override
    public void run() {
        System.out.println("Scheduler is ready");
        currentState.setTimer(this);
    }
    public static void main(String[] args){
        Thread schedulerSubsystem = new Thread(new Scheduler());
        schedulerSubsystem.start();
    }
}
