package schedStateMachine;

import allSystems.Scheduler;
import otherResources.Algorithm;
import otherResources.Message;
import otherResources.MessageType;
import otherResources.MovingDirection;

import java.util.ArrayList;

/**
 *checks mailbox for messages from floor and elevator and handles them
 */
public class CheckAndHandleMessage extends SchedulerState{

    @Override
    public void setTimer(Scheduler context) {
        boolean fromFloor = false;
//        displayState(context," is Checking and Handling a Message");
        schedOperation(context,"CHECKING MESSAGES");
        Message currentMsg = null;
        while(currentMsg == null) {//timeout when the mailbox has a request
            currentMsg = checkMsgs(context);
        }

        schedOperation(context,"PROCESS REQUESTS");
        Message sendMsg;
        int currentFloor;
        String currentElevator;
        Algorithm currentElevatorAlgorithm;
        ArrayList<Message> elevatorCurrJobs;
        switch (currentMsg.getType()) {
            case INITIAL_READ_FROM_FILE:
                context.addMsgToInitialReadFromFile(currentMsg);
                String chosenElevator = context.getLeastBusyElevator();
                Algorithm chosenElevatorAlgorithm = context.getAlgorithm(chosenElevator);

                currentMsg.setAssignedTo(chosenElevator);
                System.out.println("Assigned job "+currentMsg.toString()+ " to "+ chosenElevator);

                int pickUp = currentMsg.getStartingFloor();
                int dropOff = currentMsg.getDestFloor();
                chosenElevatorAlgorithm.add(pickUp, dropOff);
                //TODO Step 1 @Toman add uid of this job into your algorithm so you can later track each stop to a uid. The uid can be obtained with currentMsg.getUid();

                String dir = (dropOff >pickUp) ? " up " : " down ";
                System.out.println("Scheduler: Successfully added requests (" + pickUp + dir + dropOff + ")");

                context.addMessageToCurrentJobs(currentMsg, chosenElevator);
                int chosenElevatorRequestsCount = chosenElevatorAlgorithm.getRequestsCount();
                if (chosenElevatorRequestsCount == 2) {
                    // the elevator is idle and it just got a request.
                    sendMsg = new Message(currentMsg, MessageType.TRAVEL_TO_FLOOR);
                    respondMsg(context, sendMsg, chosenElevator);
                }
                break;

            case ACK_TRAVEL_TO_FLOOR:
                sendMsg = new Message(currentMsg, MessageType.CLOSE_DOOR);
                respondMsg(context, sendMsg, currentMsg.getFromWho());
                break;

            case CONFIRM_DOOR_CLOSED:
                if (currentMsg.isFulfilledStartingFloor() && !currentMsg.isFulfilledDestFloor()){
                    sendMsg = new Message(currentMsg, MessageType.REQUEST_CURRENT_FLOOR_UPDATE);
                    respondMsg(context, sendMsg, currentMsg.getFromWho());
                } else {
                    sendMsg = new Message(currentMsg, MessageType.REQUEST_INITIAL_FLOOR_UPDATE);//DO NOT REMOVE THE REQUEST_INITIAL_FLOOR_UPDATE CASE it is needed to make the starting floor logic work
                    respondMsg(context, sendMsg, currentMsg.getFromWho());
                }
                break;

            //DO NOT REMOVE THE INITIAL_FLOOR_UPDATE CASE it is needed to make the starting floor logic work
            case INITIAL_FLOOR_UPDATE:
                currentElevator = currentMsg.getAssignedTo();
                currentFloor = currentMsg.getCurrentFloor();
                //handle the current message starting floor
                if (currentMsg.getStartingFloor() == currentFloor && !currentMsg.isFulfilledStartingFloor()) {
                    currentMsg.setFulfilledStartingFloor(true);
                }
                for (Message job : context.getCurrentJobs(currentElevator)) {
                    if (currentFloor == job.getStartingFloor() && !job.isFulfilledStartingFloor()) {
                        job.setFulfilledStartingFloor(true);
                        System.out.println(currentElevator + " is at starting floor " + currentMsg.getCurrentFloor() + " job " + currentMsg);
                    }
                }

                currentMsg.setType(MessageType.CURRENT_FLOOR_UPDATE);
                //break;//don't break need to use the next case to determine movement

            case CURRENT_FLOOR_UPDATE:
                currentFloor = currentMsg.getCurrentFloor();
                currentElevator = currentMsg.getFromWho();
                currentElevatorAlgorithm = context.getAlgorithm(currentElevator);
                currentElevatorAlgorithm.setCurrentFloor(currentFloor);
                if (currentElevatorAlgorithm.peek() == currentElevatorAlgorithm.getCurrentFloor()) { // Elevator has arrived
                    // "currentElevatorAlgorithm.pop()" pops the floor
                    //      and return the floor if elevator has arrived. This is
                    //      done to allow for testing
                    int arrivedAt = currentElevatorAlgorithm.pop();
                    //TODO @Toman we need a list of uid's associated with arrivedAt below:
//                    ArrayList <Integer> jobUids = new ArrayList<Integer>();
//                    jobUids = currentElevatorAlgorithm.getJobUids(arrivedAt); // getJobUids should return a list of job uids where either the starting floor or the dest floor is fulfilled by arrivedAt
                    sendMsg = new Message(currentMsg, MessageType.STOP);
                    respondMsg(context, sendMsg, currentElevator);
                    sendMsg = new Message(currentMsg, MessageType.REQUEST_STOPPED_CONFIRMATION);
                    respondMsg(context, sendMsg, currentElevator);
                } else {
                    int destinationFloor = currentElevatorAlgorithm.peek();
                    MovingDirection direction = context.determineMovingDirection(currentFloor, destinationFloor);
                    sendMsg = new Message(currentMsg, MessageType.MOVE, direction);
                    respondMsg(context, sendMsg, currentElevator);
                }
                break;

            case CONFIRM_STOPPED:
                sendMsg = new Message(currentMsg, MessageType.OPEN_DOOR);
                respondMsg(context, sendMsg,currentMsg.getFromWho());

                break;
            case CONFIRM_DOOR_OPENED:
                currentFloor = currentMsg.getCurrentFloor();
                currentElevator = currentMsg.getAssignedTo();

                if (currentMsg.getStartingFloor() == currentFloor && !currentMsg.isFulfilledStartingFloor() ){
                    currentMsg.setFulfilledStartingFloor(true);
                }
                for(Message job : context.getCurrentJobs(currentElevator)){
                    if (currentFloor == job.getStartingFloor() && !job.isFulfilledStartingFloor()){
                        job.setFulfilledStartingFloor(true);
                        //System.out.println(currentElevator + " is at starting floor " + currentMsg.getCurrentFloor() + " job " + currentMsg);
                        //TODO put print statement back once the job uid + algorithm integration is complete
                    }
                }

                sendMsg = new Message(currentMsg, MessageType.REQUEST_UNLOADING_COMPLETE_CONFIRMATION);
                respondMsg(context, sendMsg,currentMsg.getFromWho());


                break;
            case CONFIRM_LOADING_UNLOADING_COMPLETE:
                currentFloor = currentMsg.getCurrentFloor();
                currentElevator = currentMsg.getAssignedTo();
                //System.out.println(currentMsg.getAssignedTo() + " arrived at requested floor: " + currentFloor);

                if(currentFloor == currentMsg.getDestFloor() && !currentMsg.isFulfilledDestFloor() && currentMsg.isFulfilledStartingFloor()){
                    currentMsg.setFulfilledDestFloor(true);
                    System.out.println(currentMsg.getAssignedTo() + " arrived at dest floor "+currentMsg.getCurrentFloor()+" job " +currentMsg.toString());
                    System.out.println( currentMsg.getFromWho() +" completed this job:  "+ currentMsg.toString());
                    Message completedJob = context.removeMessageFromCurrentJobs(currentMsg, currentElevator);
                    context.addMsgToCompletedJobs(completedJob);
                }



                //check which jobs OTHER THAN THE CURRENT MESSAGE in the elevator's currentJobs are fulfilled
                //,output the completed jobs and remove them from the currentJobs
                context.setDestFloorFlagsForOtherJobs(currentFloor, currentElevator);
                ArrayList<Message> completedOtherJobs = context.getAllFulfilledJobsFromCurrentJobs(currentElevator);
                for(Message job : completedOtherJobs){
                    System.out.println( currentElevator + " arrived at dest floor " + currentFloor + "  for job " + job);
                    System.out.println( currentElevator +" completed this job:  " + job);
                    context.removeMessageFromCurrentJobs(job, currentElevator);
                    Message completedJob = context.removeMessageFromCurrentJobs(currentMsg, currentElevator);
                    context.addMsgToCompletedJobs(completedJob);
                }
                //TODO uncomment code once the job uid + algorithm integration is complete
//                if (context.getCompletedJobs().size() == context.getMsgsInitialReadFromFile().size()){
//                    System.out.println("All the jobs received from the input file were completed. Completed "+context.getCompletedJobs().size()+" jobs");
//                }

                currentElevator = currentMsg.getFromWho();
                currentElevatorAlgorithm = context.getAlgorithm(currentElevator);
                if (currentElevatorAlgorithm.getRequestsCount() != 0) {
                    if (currentElevatorAlgorithm.peek() == currentFloor) {
                        // When switching from one queue to another, the destination
                        //      floor on the first queue might be a pickup floor on
                        //      the next queue. With this we make sure to remove it
                        //      from the queue as the elevator is already there.
                        int floorNum = currentElevatorAlgorithm.pop(); // floor num could be used for testing
                        //TODO @Toman need to get the uid of the new job that is a pickup floor for floorNum
                    }
                    //TODO @Toman get the uid of the next job that corresponds to the next stop for this elevator
//                    int nextUid = currentElevatorAlgorithm.getNextJobUid(currentElevator);
//                    Message nextJob = context.getMessageFromCurrentJobsByUid(nextUid);

                    sendMsg = new Message(currentMsg, MessageType.TRAVEL_TO_FLOOR); //TODO send the nextJob once the job uid + algorithm integration is complete
                    respondMsg(context, sendMsg, currentElevator);
                }
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
