package schedStateMachine;

import allSystems.Scheduler;
import otherResources.Algorithm;
import otherResources.Message;
import otherResources.MessageType;
import otherResources.MovingDirection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static otherResources.Constants.*;

/**
 *checks mailbox for messages from floor and elevator and handles them
 */
public class CheckAndHandleMessage extends SchedulerState{


    /**
     * Handles messages of the type INITIAL_READ_FROM_FILE that were received from the floors:
     * - adds the message to the Scheduler initialReadFromFile list
     * - picks an elevator according to Algorithm and assigns the job to it
     * - adds that job to the elevator's jobs list
     * - if that elevator is currently idle with no jobs to do, it sends a TRAVEL_TO_FLOOR command
     * @param context: the scheduler context
     * @param currentMsg: the message received by the scheduler
     */
    public void handleInitialReadFromFile(Scheduler context, Message currentMsg){
        Message sendMsg = null;
        int queueNumber;
        if (currentMsg.getType() == MessageType.INITIAL_READ_FROM_FILE){
            context.addMsgToInitialReadFromFile(currentMsg);
            int pickUp = currentMsg.getStartingFloor();
            int dropOff = currentMsg.getDestFloor();

            String chosenElevator = context.getOptimalElevator(pickUp, dropOff);
            if (chosenElevator.trim().isEmpty()) {
                System.out.println(context.getCurrentSystemTime() + "Scheduler: All elevators are Out Of Service - EXITING");
                return;
            }
            Algorithm chosenElevatorAlgorithm = context.getAlgorithm(chosenElevator);

            currentMsg.setAssignedTo(chosenElevator);

            queueNumber = chosenElevatorAlgorithm.add(pickUp, dropOff);
            /*
            String dir = (dropOff >pickUp) ? " up " : " down ";
            System.out.println(context.getCurrentSystemTime() + "Scheduler: Added " + currentMsg.getUid() + "(" + pickUp + dir + dropOff + ") -> " + chosenElevator + "(Q" + queueNumber + ")");
            chosenElevatorAlgorithm.printQueues();
             */
            System.out.println(context.getCurrentSystemTime()+"Assigned job: " + currentMsg + " to "+chosenElevator);
            int chosenElevatorRequestsCount = chosenElevatorAlgorithm.getRequestsCount();
            if (chosenElevatorRequestsCount == 2) {
                // the elevator is idle, and it just got a request.
                sendMsg = new Message(currentMsg, MessageType.TRAVEL_TO_FLOOR);
                respondMsg(context, sendMsg, chosenElevator);
            }
            context.addElevatorJob(chosenElevator, currentMsg, queueNumber);

        }
    }

    /**
     * Handles ACK_TRAVEL_TO_FLOOR message received from an elevator and inserts a fault if applicable:
     * - if the job has a fault that has not already been fulfilled and the random coin toss returns true, then a FAULT_DOOR message will be sent
     * - otherwise, a CLOSE_DOOR message will be sent
     * @param context: the scheduler context
     * @param currentMsg: the message received by the scheduler
     */
    public void handleAckTravelToFloor(Scheduler context, Message currentMsg){
        String currentElevator = currentMsg.getFromWho();
        int currentFloor = currentMsg.getCurrentFloor();
        Message sendMsg = null;
        if(currentMsg.getType() == MessageType.ACK_TRAVEL_TO_FLOOR){
            if(currentMsg.getFault() == SOFT_FAULT && !currentMsg.isFulfilledFault()){
                if(context.tossCoinForFault() && currentMsg.getCurrentFloor() != -1){
                    currentMsg.setFulfilledFault(true);
                    System.out.println(context.getCurrentSystemTime() + "Injecting soft fault to " +currentElevator+ " at floor " +currentFloor+ " for job: "+currentMsg);
                    sendMsg = new Message(currentMsg, MessageType.FAULT_DOOR);
                }
                else {
                    sendMsg = new Message(currentMsg, MessageType.CLOSE_DOOR);
                }
            }
            else{
                sendMsg = new Message(currentMsg, MessageType.CLOSE_DOOR);
            }
            respondMsg(context, sendMsg, currentElevator);
        }
    }

    /**
     * Handles messages of the type REPORT_DOOR_NOT_CLOSING that were received from the elevator:
     * - sends a new message to the elevator to close the door
     * @param context: the scheduler context
     * @param currentMsg: the message received by the scheduler
     */
    public void handleReportDoorNotClosing(Scheduler context, Message currentMsg) {
        Message sendMsg = null;
        int currentFloor;
        String currentElevator;
        if(currentMsg.getType() == MessageType.REPORT_DOOR_NOT_CLOSING){
            currentElevator = currentMsg.getFromWho();
            currentFloor = currentMsg.getCurrentFloor();
            System.out.println(context.getCurrentSystemTime() + currentElevator + " has a door closing fault at floor " + currentFloor + " job " + currentMsg);
            sendMsg = new Message(currentMsg, MessageType.CLOSE_DOOR);
            respondMsg(context, sendMsg,currentElevator);
        }
    }

    /**
     * Handles messages of the type CONFIRM_DOOR_CLOSED that were received from the elevator:
     * - sends a new message to the elevator of the type REQUEST_CURRENT_FLOOR_UPDATE
     * @param context: the scheduler context
     * @param currentMsg: the message received by the scheduler
     */
    public void handleConfirmDoorClosed(Scheduler context, Message currentMsg){
        Message sendMsg = null;
        if(currentMsg.getType() == MessageType.CONFIRM_DOOR_CLOSED){
            sendMsg = new Message(currentMsg, MessageType.REQUEST_CURRENT_FLOOR_UPDATE);
            respondMsg(context, sendMsg, currentMsg.getFromWho());
        }
    }

    /**
     * Handles messages of the type CURRENT_FLOOR_UPDATE that were received from the elevator and inserts a STUCK fault if applicable:
     * - if the message has a HARD_FAULT that has not already been fulfilled, the scheduler will insert a fault at this time if either:
     *        - the random coin toss returns true OR
     *        - this is the last chance to insert a fault for this job (we are at the floor right before the destination floor)
     *  - if a fault is to be inserted, the scheduler sends out a FAULT_STUCK message to the elevator
     *  - otherwise, the scheduler sends out either:
     *        - a STOP message + a REQUEST_STOPPED_CONFIRMATION message, if the elevator has arrived at a scheduled stop OR
     *        - a MOVE message if the elevator should keep travelling
     * @param context: the scheduler context
     * @param currentMsg: the message received by the scheduler
     */
    public void handleCurrentFloorUpdate(Scheduler context, Message currentMsg){
        Message sendMsg = null;
        int currentFloor;
        String currentElevator;
        Algorithm currentElevatorAlgorithm;
        if(currentMsg.getType() == MessageType.CURRENT_FLOOR_UPDATE){
            currentFloor = currentMsg.getCurrentFloor();
            currentElevator = currentMsg.getFromWho();
            currentElevatorAlgorithm = context.getAlgorithm(currentElevator);
            currentElevatorAlgorithm.setCurrentFloor(currentFloor);
            //if current job has a hard fault, insert fault here if random coin toss says yes
            if (currentMsg.getFault() == HARD_FAULT && !currentMsg.isFulfilledFault() && currentFloor != -1){
                if (context.tossCoinForFault() || (currentFloor == currentMsg.getDestFloor() - 1)){ // If the current floor is destFloor-1 and there was no fault yet, send one now (last chance)
                    currentMsg.setFulfilledFault(true);
                    System.out.println(context.getCurrentSystemTime() + "Injecting hard fault to " +currentElevator+ " at floor " +currentFloor+ " for job: "+currentMsg);
                    sendMsg = new Message(currentMsg, MessageType.FAULT_STUCK);
                    respondMsg(context, sendMsg, currentElevator);
                }
                else {
                    sendMsg = context.moveOrStop(currentElevatorAlgorithm, currentMsg);
                    if (sendMsg.getType() == MessageType.STOP){
                        respondMsg(context, sendMsg, currentElevator);
                        sendMsg = new Message(currentMsg, MessageType.REQUEST_STOPPED_CONFIRMATION);
                        respondMsg(context, sendMsg, currentElevator);
                    }
                    else if (sendMsg.getType() == MessageType.MOVE) respondMsg(context, sendMsg, currentElevator);
                }
            }
            else {
                sendMsg = context.moveOrStop(currentElevatorAlgorithm, currentMsg);
                if (sendMsg.getType() == MessageType.STOP){
                    respondMsg(context, sendMsg, currentElevator);
                    sendMsg = new Message(currentMsg, MessageType.REQUEST_STOPPED_CONFIRMATION);
                    respondMsg(context, sendMsg, currentElevator);
                }
                else if (sendMsg.getType() == MessageType.MOVE) respondMsg(context, sendMsg, currentElevator);
            }
        }
    }

    /**
     * Handles messages of the type REPORT_STUCK that were received from the elevator:
     * - send the elevator an OUT_OF_ORDER message and an OPEN_DOOR message
     * - resend the elevator's current jobs as INITIAL_READ_FROM_FILE messages to the Scheduler so that they can be reprocessed and be assigned to a different elevator
     * - jobs are modified as follows:
     *         - if the job has the starting floor fulfilled, set its starting floor to the current floor (irl the passengers that are already in the elevator get out and call another elevator at their current floor)
     *         - otherwise, send the job unchanged
     * @param context: the scheduler context
     * @param currentMsg: the message received by the scheduler
     */
    public void handleReportStuck(Scheduler context, Message currentMsg){
        if (currentMsg.getType() == MessageType.REPORT_STUCK){
            String currentElevator = currentMsg.getFromWho();
            int currentFloor = currentMsg.getCurrentFloor();
            System.out.println(context.getCurrentSystemTime()+currentElevator+" REPORT_STUCK at floor " +currentFloor);
            //we handle the hard fault by getting all the messages from the currentJobQueue of that elevator
            //and resending them as INITIAL_READ_FROM_FILE so that the scheduler can reassign them
            Message sendMsg = new Message(currentMsg, MessageType.OUT_OF_ORDER);
            respondMsg(context, sendMsg, currentElevator);
            sendMsg = new Message(currentMsg, MessageType.OPEN_DOOR);
            respondMsg(context, sendMsg, currentElevator);
            Algorithm currentElevatorAlgorithm = context.getAlgorithm(currentElevator);
            currentElevatorAlgorithm.setOutOfService(true);
            System.out.println("\n"+context.getCurrentSystemTime()+"Taking "+currentElevator+" out of service, will modify and reassign the following jobs:");
            for (Message job:context.getElevatorJobs().get(currentElevator)){
                //if this is the job that just had a fault, we reset the fault and change the starting floor if needed
                if(currentMsg.getUid() == job.getUid()){
                    int start = job.getStartingFloor();
                    int dest = job.getDestFloor();
                    if( (start < currentFloor && currentFloor < dest)
                            || (dest < currentFloor && currentFloor < start)){
                        job.setStartingFloor(currentFloor);
                    }
                    job.setFault(NO_FAULT);
                    //job.setFulfilledFault(false);
                }
                //for the jobs that have already been started, we change the starting floor
                else if(job.isFulfilledStartingFloor() && !job.isFulfilledDestFloor()){
                    job.setStartingFloor(currentFloor);
                }
                if (job.getStatus() != Message.MessageStatus.COMPLETED){ //only send out jobs that are not completed, to prevent duplicates
                    String jobString = job.getTimeAtReq().toString()+".00" + " " + job.getStartingFloor() + " " + job.getDirection() + " "+ job.getDestFloor() + " " + job.getFault();
                    sendMsg = new Message(jobString); //send these as INITIAL_START_FROM_FILE
                    sendMsg.setUid(job.getUid());
                    System.out.println("\t"+sendMsg);
                    respondMsg(context, sendMsg, "Scheduler", "Floor"+sendMsg.getStartingFloor());
                }

            }
            //empty the current jobs queue
            context.removeAllJobsFromElevatorJobs(currentElevator);
        }
    }

    /**
     * Handles messages of the type CONFIRM_STOPPED that were received from the elevator:
     * - if the job has a SOFT_FAULT that has not been fulfilled yet:
     *         - we send a FAULT_DOOR message if the random coin toss is true or we are already at the destination floor (last chance to send a fault)
     *         - otherwise we send an OPEN_DOOR message
     * - if the job has no fault, we send an OPEN_DOOR message
     * @param context: the scheduler context
     * @param currentMsg: the message received by the scheduler
     */
    public void handleConfirmStopped(Scheduler context, Message currentMsg){
        String currentElevator = currentMsg.getFromWho();
        int currentFloor = currentMsg.getCurrentFloor();
        Message sendMsg = null;
        if(currentMsg.getType() == MessageType.CONFIRM_STOPPED){
            if(currentMsg.getFault() == SOFT_FAULT && !currentMsg.isFulfilledFault()) {
                if(context.tossCoinForFault() || currentFloor == currentMsg.getDestFloor()) {
                    currentMsg.setFulfilledFault(true);
                    System.out.println(context.getCurrentSystemTime() + "Injecting soft fault to " +currentElevator+ " at floor " +currentFloor+ " for job: "+currentMsg);
                    sendMsg = new Message(currentMsg, MessageType.FAULT_DOOR);
                }
                else sendMsg = new Message(currentMsg, MessageType.OPEN_DOOR);
            }
            else{
                sendMsg = new Message(currentMsg, MessageType.OPEN_DOOR);
            }
            respondMsg(context,sendMsg,currentMsg.getFromWho());
        }
    }

    /**
     * Handles messages of the type REPORT_DOOR_NOT_OPENING that were received from the elevator:
     *  - report that the elevator has a door opening fault
     *  - send the elevator another OPEN_DOOR
     * @param context: the scheduler context
     * @param currentMsg: the message received by the scheduler
     */
    public void handleReportDoorNotOpening(Scheduler context, Message currentMsg) {
        if (currentMsg.getType() == MessageType.REPORT_DOOR_NOT_OPENING){
            int currentFloor = currentMsg.getCurrentFloor();
            String currentElevator = currentMsg.getFromWho();
            System.out.println(context.getCurrentSystemTime() + currentElevator+" has a door opening fault at floor " +currentFloor + " job " + currentMsg);
            System.out.println("Sending " + currentElevator + "a new OPEN_DOOR command...");
            Message sendMsg = new Message(currentMsg, MessageType.OPEN_DOOR);
            respondMsg(context, sendMsg, currentElevator);
        }
    }


    /**
     * Handles messages of the type CONFIRM_DOOR_OPENED that were received from the elevator:
     * - send back a REQUEST_UNLOADING_COMPLETE_CONFIRMATION message
     * @param context: the scheduler context
     * @param currentMsg: the message received by the scheduler
     */
    public void handleConfirmDoorOpened(Scheduler context, Message currentMsg){
        if (currentMsg.getType() == MessageType.CONFIRM_DOOR_OPENED){
            String currentElevator = currentMsg.getFromWho();
            System.out.println(context.getCurrentSystemTime() + currentElevator+" opened the door job: " + currentMsg);
            Message sendMsg = new Message(currentMsg, MessageType.REQUEST_UNLOADING_COMPLETE_CONFIRMATION);
            respondMsg(context, sendMsg,currentMsg.getFromWho());
        }
    }

    /**
     * Handles messages of the type CONFIRM_LOADING_UNLOADING_COMPLETE that were received from the elevator:
     * - check which jobs are COMPLETED in this elevator's jobs list, and add them to the Scheduler completedJobs list
     * - check which jobs in this elevator's jobs list have the START_FLOOR_COMPLETED status and set their isFulfilledStartingFloor flag
     * - if this elevator has more jobs assigned to it, find the next job that correlates with the next stop for this elevator:
     *          - if there is more than 1 job, pick one according to rules
     *          - if there are none, we do not send any messages out
     *  - send out the next job if there is one as a TRAVEL_TO_FLOOR message
     * @param context: the scheduler context
     * @param currentMsg: the message received by the scheduler
     */
    public void handleConfirmLoadingUnloadingComplete(Scheduler context, Message currentMsg){
        if (currentMsg.getType() == MessageType.CONFIRM_LOADING_UNLOADING_COMPLETE){
            String currentElevator = currentMsg.getFromWho();
            Algorithm currentElevatorAlgorithm = context.getAlgorithm(currentElevator);
            int currentFloor = currentElevatorAlgorithm.getCurrentFloor();
            int queueNumber = currentElevatorAlgorithm.getQueueNumber();
            int poppedFloor = currentElevatorAlgorithm.pop();
            HashMap<Message.MessageStatus, ArrayList<Message>> requests = context.updateElevatorJob(currentElevator, poppedFloor, queueNumber);
            for (Message m : requests.get(Message.MessageStatus.COMPLETED)) {
                m.setFulfilledDestFloor(true);
                m.setStatus(Message.MessageStatus.COMPLETED);
                context.addMsgToCompletedJobs(m);
                System.out.println(context.getCurrentSystemTime() + currentElevator + " completed job: " +m);
                        //System.out.println(context.getCurrentSystemTime() + "Scheduler: (" + currentElevator + ") " + m + " status: " + m.getStatus() );
                // + "startFloor = " + m.isFulfilledStartingFloor() + ", destFloor = " + m.isFulfilledDestFloor());
                System.out.println("\nCompleted Jobs: ");
                ArrayList<Message> completedJobs = context.getCompletedJobs();
                if(!completedJobs.isEmpty()){
                    for(Message job:completedJobs){
                        System.out.println(job);
                    }
                }
            }
            for (Message m : requests.get(Message.MessageStatus.START_FLOOR_COMPLETED)) {
                m.setFulfilledStartingFloor(true);
                m.setStatus(Message.MessageStatus.START_FLOOR_COMPLETED);
                System.out.println(context.getCurrentSystemTime() + currentElevator + " completed starting floor for job: " +m);
                //System.out.println(context.getCurrentSystemTime() + "Scheduler: (" + currentElevator + ") " + m + " status: " + m.getStatus()  );
                //    + "startFloor = " + m.isFulfilledStartingFloor() + ", destFloor = " + m.isFulfilledDestFloor());
            }

            if (currentElevatorAlgorithm.peek() == currentFloor) {
                // When switching from one queue to another, the destination
                //      floor on the first queue might be a pickup floor on
                //      the next queue. With this we make sure to remove it
                //      from the queue as the elevator is already there.
                queueNumber = currentElevatorAlgorithm.getQueueNumber();
                poppedFloor = currentElevatorAlgorithm.pop();
                requests = context.updateElevatorJob(currentElevator, poppedFloor, queueNumber);
                for (Message m : requests.get(Message.MessageStatus.START_FLOOR_COMPLETED)) {
                    m.setFulfilledStartingFloor(true);
                    m.setStatus(Message.MessageStatus.START_FLOOR_COMPLETED);
                    System.out.println(context.getCurrentSystemTime() + currentElevator + " completed starting floor for job: " +m);
                    //System.out.println(context.getCurrentSystemTime() + "Scheduler: (" + currentElevator + ") " + m + " status: " + m.getStatus() );
                    // + " startFloor = " + m.isFulfilledStartingFloor() + ", destFloor = " + m.isFulfilledDestFloor());
                }
            }

            if (currentElevatorAlgorithm.getRequestsCount() != 0) {
                queueNumber = currentElevatorAlgorithm.getQueueNumber();
                int nextFloor = currentElevatorAlgorithm.peek();
                ArrayList<Message> nextJobs = context.getFloorMessages(currentElevator, nextFloor, queueNumber);
                System.out.println("\nNext jobs for "+currentElevator+": ");
                for (Message job: nextJobs){
                    System.out.println(job);
                }
                Message nextJob = context.getNextJob(nextJobs);
                System.out.println("Picked next job: " + nextJob +"\n");
                Message sendMsg = new Message(nextJob, MessageType.TRAVEL_TO_FLOOR);
                respondMsg(context, sendMsg, currentElevator);
            }
        }
    }

    @Override
    public void setTimer(Scheduler context) {
        schedOperation(context,"CHECKING MESSAGES");
        Message currentMsg = null;
        while(currentMsg == null) {//timeout when the mailbox has a request
            if (Thread.currentThread().isInterrupted()){
                return;//kill thread, when interrupt flag is set
            }
            currentMsg = checkMsgs(context);
        }

        schedOperation(context,"PROCESS REQUESTS");

        switch (currentMsg.getType()) {
            case INITIAL_READ_FROM_FILE:
                handleInitialReadFromFile(context, currentMsg);
                break;

            case ACK_TRAVEL_TO_FLOOR:
                handleAckTravelToFloor(context, currentMsg);
                break;

            case REPORT_DOOR_NOT_CLOSING:
                handleReportDoorNotClosing(context, currentMsg);
                break;

            case CONFIRM_DOOR_CLOSED:
                handleConfirmDoorClosed(context, currentMsg);
                break;

            case CURRENT_FLOOR_UPDATE:
                handleCurrentFloorUpdate(context, currentMsg);
                break;

            case REPORT_STUCK:
                handleReportStuck(context, currentMsg);
                break;

            case CONFIRM_STOPPED:
                handleConfirmStopped(context, currentMsg);
                break;

            case REPORT_DOOR_NOT_OPENING:
                handleReportDoorNotOpening(context, currentMsg);
                break;

            case CONFIRM_DOOR_OPENED:
                handleConfirmDoorOpened(context, currentMsg);
                break;

            case CONFIRM_LOADING_UNLOADING_COMPLETE:
                handleConfirmLoadingUnloadingComplete(context, currentMsg);
                break;

            default:
                break;
        }
        timeout(context);
    }


    public void timeout(Scheduler context) {
        context.setCurrentState(new CheckAndHandleMessage());
        context.getCurrentState().setTimer(context);
    }
}
