package allSystems;

import gui.mainUI;
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

/**
 * Represents a Scheduler entity and will run as a thread
 * The scheduler receives messages from Floor and Elevators via the Mailbox,
 * and gives commands to the Elevators to tell them which floors to service,
 * when to move, when to stop, when to open/close doors, etc.
 */

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
    private mainUI frame;
    private ArrayList<ArrayList<Boolean>> floorButtons;
    private HashMap<String, Integer> passengerCount;
    private HashMap<Message, Long> waitingRequests;
    private boolean firstJobArrived = false;
    private LocalDateTime systemTimeFirstJobArrived;
    public Scheduler(){
        currentState = new CheckAndHandleMessage();
        completedJobs = new ArrayList<>();
        msgsInitialReadFromFile = new ArrayList<>();
        elevatorsSchedules = new HashMap<>(); //key = String elevator (e.g. "Elevator1"), value = Algorithm
        elevatorJobs = new HashMap<>();//key = String elevator (e.g. "Elevator1"), value = ArrayList<Message>
        floorButtons = new ArrayList<>(); //index=floor number, element = [isUpPressed, isDownPressed]
        passengerCount = new HashMap<>(); //key = String elevator (e.g. "Elevator1"), value = Passenger Count
        waitingRequests = new HashMap<>(); //key = Message waiting, value = time put to wait in ms
        initKeysElevatorsSchedules();
        initElevatorJobs();
        initFloorButtons();
        initPassengerCount();

        udp = new UDP(SCHEDPORT);
        udp.register("Scheduler");
        this.frame = new mainUI(this);
    }

    public synchronized HashMap<Message, Long> getWaitingRequests() {
        return waitingRequests;
    }

    /**
     * initializes the keys of the elevatorsSchedules map to have the names
     * of the elevators in the format "Elevator<i>", where i is the elevator's car number
     */
    private void initKeysElevatorsSchedules(){
        for (int i = 1; i <= MAX_ELEVATORS; i++) {
            String key = "Elevator" + i;
            elevatorsSchedules.put(key, new Algorithm());
        }
    }

    /**
     * initializes the keys of the elevatorJobs map to have the names
     * of the elevators in the format "Elevator<i>", where i is the elevator's car number
     */
    private void initElevatorJobs(){
        for (int i = 1; i <= MAX_ELEVATORS; i++) {
            String key = "Elevator" + i;
            ArrayList<Message> value = new ArrayList<>();
            this.elevatorJobs.put(key, value);
        }
    }

    private void initFloorButtons() {
        for (int i = 0; i < MAX_FLOORS; i++) {
            ArrayList<Boolean> array = new ArrayList<>(2);
            array.add(false);
            array.add(false);
            this.floorButtons.add(array);
        }
    }

    private void initPassengerCount(){
        for (int i = 1; i <= MAX_ELEVATORS; i++) {
            String key = "Elevator" + i;
            int value = 0;
            this.passengerCount.put(key, value);
        }
    }

    /***
     * checks and returns if elevators will have the capacity to take on a request given pick up and drop off
     * @param pickUp start floor of request
     * @param dropOff finish floor of request
     * @return a list of elevators that will be able to handle the requests
     */
    public ArrayList<String> canScheduleRequest(int pickUp, int dropOff) {
        ArrayList<String> possibleElevators = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Message>> entry : elevatorJobs.entrySet()) {
            String elevator = entry.getKey();
            ArrayList<Message> jobs = entry.getValue();

            ArrayList<Message> jobsFiltered = new ArrayList<>();
            for (Message job : jobs) {
                if (job.getStatus() != Message.MessageStatus.COMPLETED && getAlgorithm(elevator).whichQueue(pickUp, dropOff) == job.getQueueNumber()) {
                    jobsFiltered.add(job);
                }
            }

            ArrayList<Integer> passengersPerFloors = new ArrayList<>(MAX_FLOORS);
            for (int i = 0; i <= MAX_FLOORS; i++) { passengersPerFloors.add(0); }

            boolean notElevator = false;
            for (Message job : jobsFiltered) {
                int jobStartF = job.getStartingFloor();
                int jobDestF = job.getDestFloor();
                if (jobDestF > jobStartF) { // if going up
                    for (int i = jobStartF; i < jobDestF; i++) { // how many passengers there will be per floor
                        passengersPerFloors.set(i, passengersPerFloors.get(i) + 1);
                        if (passengersPerFloors.get(i) >= MAX_CAPACITY && i >= pickUp && i < dropOff) {
                            notElevator = true;
                            break;
                        }
                    }
                } else { // if going down
                    for (int i = jobStartF; i > jobDestF; i--) { // how many passengers there will be per floor
                        passengersPerFloors.set(i, passengersPerFloors.get(i) + 1);
                        if (passengersPerFloors.get(i) >= MAX_CAPACITY && i <= pickUp && i > dropOff) {
                            notElevator = true;
                            break;
                        }
                    }
                }
                if (notElevator) break;
            }
            if (!notElevator) possibleElevators.add(elevator);
        }
        return possibleElevators;
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

    /***
     * A function used to find which elevator has the lowest count of jobs
     * @param elevators list of elevators
     * @return elevator name that has the least number of requests
     */
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

    /***
     * A function that filters through all elevators and find the elevator that will
     * complete the request best, based on pre-determined rules.
     * @param pickUp pick up floor
     * @param dropOff drop off floor
     * @return returns elevator that is best suited to handle the request
     */
    public String getOptimalElevator(int pickUp, int dropOff) {
        ArrayList<String> NFElevators = canScheduleRequest(pickUp, dropOff);
        if (NFElevators.isEmpty()) return "";

        ArrayList<String> NOOSElevators = new ArrayList<>(); // Check for all elevators that are not out of service
        for (String elevator : NFElevators) {
            Algorithm algorithm = getAlgorithm(elevator);
            if (!algorithm.isOutOfService()) NOOSElevators.add(elevator);
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

    /***
     * A functioned that gets an algorithm from the hashmap using
     * and elevator name as a key.
     * @param key elevator name
     * @return algorithm that corresponds with the elevator
     */
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

    private void updateFloorButtons(boolean isNew, Message m) {
        int pickUp = m.getStartingFloor();
        int dropOff = m.getDestFloor();
        int dir = (dropOff > pickUp) ? 0 : 1;
        floorButtons.get(pickUp-1).set(dir, isNew);
    }

    
    /***
     * Adds a job to a specified elevator
     * @param elevator who is to handle the request
     * @param m message that contains the request
     * @param queueNumber the queue number where the request will be handled
     */
    public void addElevatorJob(String elevator, Message m, int queueNumber) {
        m.setQueueNumber(queueNumber);
        this.elevatorJobs.get(elevator).add(m);
        updateFloorButtons(true, m);
    }

    /***
     * Updates the jobs that have a boarding passenger on the floor specified, or has
     * a passenger getting off.
     * @param elevator elevator in which the request is contained
     * @param floor floor number the elevator is on
     * @param queueNumber the queue number on which the elevator is working on
     * @return returns a list of all jobs that have been updated
     */
    public HashMap<Message.MessageStatus, ArrayList<Message>> updateElevatorJob(String elevator, int floor, int queueNumber) {
        HashMap<Message.MessageStatus, ArrayList<Message>> main = new HashMap<>();
        ArrayList<Message> start = new ArrayList<>();
        ArrayList<Message> complete = new ArrayList<>();
        for(Message request : this.elevatorJobs.get(elevator)) { // iterate through all requests for elevator
            if (request.getQueueNumber() == queueNumber) { // all requests with specific queue number
                if (request.getStartingFloor() == floor) { // if the starting floor is the same as popped
                    request.setStatus(Message.MessageStatus.START_FLOOR_COMPLETED); // set status
                    int count  = passengerCount.get(elevator);
                    passengerCount.put(elevator, count+1);
                    start.add(request);
                    updateFloorButtons(false, request);
                } else if (request.getDestFloor() == floor) { // if the destination floor is the same as popped
                    request.setStatus(Message.MessageStatus.COMPLETED); // set status
                    int count  = passengerCount.get(elevator);
                    passengerCount.put(elevator, count-1);
                    complete.add(request);
                }
            }
//            this.frame.update();
        }
        main.put(Message.MessageStatus.START_FLOOR_COMPLETED, start);
        main.put(Message.MessageStatus.COMPLETED, complete);
        return main;
    }

    /***
     * @param elevator elevator name
     * @param floor floor number
     * @param queueNumber queue in which the request is stored in
     * @return returns all requests that are to be updated on the specified floor and queue number
     */
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

    /**
     * removes all the jobs from the elevatorJobs list for that elevator
     * @param elevator: the elevator who's jobs are to be removed
     */
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
        frame.log("Scheduler is ready");
        currentState.setTimer(this);
    }
    public static void main(String[] args){
        Thread schedulerSubsystem = new Thread(new Scheduler());
        schedulerSubsystem.start();
    }
}
