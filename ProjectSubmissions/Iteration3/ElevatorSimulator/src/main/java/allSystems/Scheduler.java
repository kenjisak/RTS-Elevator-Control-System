package allSystems;

import lombok.Getter;
import lombok.Setter;
import otherResources.Algorithm;
import otherResources.Message;
import otherResources.MovingDirection;
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

    private HashMap<String, ArrayList<Message>> pendingJobsMap;//requests assigned to each elevator

    private SchedulerState currentState;

    private HashMap<String, ArrayList<Message>> currentJobsMap; // map of the current message we are working on for that elevator
    // key = elevator name (e.g. "Elevator1") , value = its current list of message

    private HashMap<String, Algorithm> elevatorsSchedules;

    public Scheduler(){
        currentState = new CheckAndHandleMessage();
        completedJobs = new ArrayList<>();
        msgsInitialReadFromFile = new ArrayList<>();
        pendingJobsMap = new HashMap<>();//key = String elevator (e.g. "Elevator1"), value = ArrayList<Message>
        currentJobsMap = new HashMap<>();//key = String elevator (e.g. "Elevator1"), value = ArrayList<Message>
        elevatorsSchedules = new HashMap<>(); //key = String elevator (e.g. "Elevator1"), value = Algorithm
        initKeysElevatorsSchedules();
        initKeysCurrentJobsMap();

        udp = new UDP(SCHEDPORT);
        udp.register("Scheduler");
    }

    private void initKeysElevatorsSchedules(){
        for (int i = 1; i <= MAX_ELEVATORS; i++) {
            String key = "Elevator" + i;
            elevatorsSchedules.put(key, new Algorithm());
        }
    }

    public String getLeastBusyElevator() {
        String leastRequestsElevator = "Elevator1";
        int leastRequestsCount = elevatorsSchedules.get("Elevator1").getRequestsCount();
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

    public Algorithm getAlgorithm(String key) {
        return elevatorsSchedules.get(key);
    }


    private void initKeysCurrentJobsMap(){
        for (int i = 1; i <= MAX_ELEVATORS; i++) {
            String key = "Elevator" + i;
            currentJobsMap.put(key, new ArrayList<Message>());
        }
    }


    /**
     * Removes and returns the first message from the corresponding elevator pending jobs list
     * @param elevator
     * @return
     */
    public Message getFirstMessageFromPendingJobs(String elevator){
        //find list by which elevator the message is assigned to
        ArrayList <Message> l = pendingJobsMap.get(elevator);
        if(l != null){
            if (!l.isEmpty()) return l.remove(0);
        }
        return null;
    }


    public void addMessageToCurrentJobs(Message m, String elevator){
        //find elevator's queue by key
        ArrayList<Message> l = currentJobsMap.get(elevator);
        if (l != null){
            //check if message assignment is correct
            if (!m.getAssignedTo().equals(elevator)){
                m.setAssignedTo(elevator);
            }
            l.add(m);
        }
    }


/**
     * Finds and removes the message from the corresponding elevator current jobs
     * according to which elevator the message is assigned to
     * @param toRemove the message to be removed
     * @param elevator the key at which the pending jobs list is found
     */
    public synchronized Message removeMessageFromCurrentJobs(Message toRemove, String elevator){
        //find list by which elevator the message is assigned to
        ArrayList<Message> jobs = currentJobsMap.get(elevator);
        if( jobs != null && !jobs.isEmpty()){
            //find the message by timestamp because the object may be different now
            for(Iterator<Message> it = jobs.iterator(); it.hasNext(); ) {
                Message m = it.next();
                if(m.getUid() == toRemove.getUid()){
                    jobs.remove(m);
                    return m;
                }
            }
        }
        return null;
    }

    public Message findMessageByTimestampInCurrentJobs(Message needle){
        ArrayList<Message> haystack = currentJobsMap.get(needle.getAssignedTo());
        for (Message job : haystack){
            if (job.compareTo(needle) == 0){
                return job;
            }
        }
        return null;
    }

    public Message getMessageFromCurrentJobsByUid(int uid, String elevator){
        ArrayList<Message> list = currentJobsMap.get(elevator);
        for (Message job : list){
            if (job.getUid() == uid){
                return job;
            }
        }
        return null;
    }
    /**
     * returns all the jobs from this elevator's currentJobs that have both the
     * startingFloor and the destFloor fulfilled
     * does not remove the jobs from the list
     * @param elevator: the name of the elevator (e.g. "Elevator1")
     * @return: an array list of the jobs that meet the criteria
     */
    public synchronized ArrayList<Message> getAllFulfilledJobsFromCurrentJobs(String elevator){
        ArrayList<Message> result = new ArrayList<Message>();
        //find list by which elevator the message is assigned to
        ArrayList<Message> jobs = currentJobsMap.get(elevator);
        if( jobs != null && !jobs.isEmpty()){
            for(Iterator<Message> it = jobs.iterator(); it.hasNext(); ) {
                Message job = it.next();
                if(job.isFulfilledStartingFloor() && job.isFulfilledDestFloor()){
                    result.add(job);
                }
            }
        }
        return result;
    }

    public Message getFirstMessageFromCurrentJobs(String elevator){
        //find list by which elevator the message is assigned to
        ArrayList <Message> l = currentJobsMap.get(elevator);
        if(l != null){
            if (!l.isEmpty()) return l.get(0);
        }
        return null;
    }

    public ArrayList <Message> getCurrentJobs(String elevator) {
        return currentJobsMap.get(elevator);
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

    public void addMsgToInitialReadFromFile(Message m){
        msgsInitialReadFromFile.add(m);
    }

    /**
     * iterates through the currentJobs of this elevator and sets the flags of these jobs to true
     * if the stop is equal to the startingFloor of that job
     * @param stop: the floor number that is to be checked against these other jobs
     * @param elevator: the name of the elevator (e.g. "Elevator1")
     */
    public void setStartingFloorFlagsForOtherJobs(int stop, String elevator){
        //get this elevator's current jobs
        ArrayList<Message> jobs = getCurrentJobs(elevator);
        for (Message job : jobs){
            if (job.getStartingFloor() == stop){
                job.setFulfilledStartingFloor(true);
            }
        }
    }

    /**
     * iterates through the currentJobs of this elevator and sets the flags of these jobs to true
     * if the stop is equal to the destFloor of that job
     * @param stop: the floor number that is to be checked against these other jobs
     * @param elevator: the name of the elevator (e.g. "Elevator1")
     */
    public void setDestFloorFlagsForOtherJobs(int stop, String elevator){
        //get this elevator's current jobs
        ArrayList<Message> jobs = getCurrentJobs(elevator);
        for (Message job : jobs){
            if (job.getDestFloor() == stop){
                job.setFulfilledDestFloor(true);
            }
        }
    }

    /**
     * iterates through the currentJobs of this elevator and sets the flags of these jobs to true
     * if the stop is equal to the startFloor or destFloor of that job
     * @param stop: the floor number that is to be checked against these other jobs
     * @param elevator: the name of the elevator (e.g. "Elevator1")
     */
    public void setFlagsForOtherJobs(int stop, String elevator){
        //get this elevator's current jobs
        ArrayList<Message> jobs = getCurrentJobs(elevator);
        for (Message job : jobs){
            if (job.getStartingFloor() == stop){
                job.setFulfilledStartingFloor(true);
            }
            if (job.getDestFloor() == stop){
                job.setFulfilledDestFloor(true);
            }
        }
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
