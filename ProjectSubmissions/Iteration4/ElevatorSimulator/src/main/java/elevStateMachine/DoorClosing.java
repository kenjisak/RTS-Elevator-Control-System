package elevStateMachine;

import allSystems.Elevator;
import otherResources.Message;
import otherResources.MessageType;
import static otherResources.Constants.*;

/**
 * elevator closing doors state
 * previous state: Idle
 * entry: TRAVEL_TO_FLOOR message received from scheduler
 * action: wait for a CLOSE_DOOR message from scheduler; when door is closed, send a CONFIRM_DOOR_CLOSED message to scheduler
 * exit: doorToClosedTime has expired
 * next state: doorClosed
 */
public class DoorClosing extends ElevatorState{
    @Override
    public void setTimer(Elevator context) {
        //displayState(context," is closing doors");
        elevOperation(context,"DOORS_CLOSING");
        boolean fault = false;
        int secondsRunning = 0;
        while(true){//timeout when the mailbox has a request
            if (Thread.currentThread().isInterrupted()){
                return;//kill thread, when interrupt flag is set
            }
            sleep(1);//poll every second
            secondsRunning++;
            Message command = checkMsgs(context);//is set to true if no msgs, false if there is one
            if (command == null) {
                continue;
            }
            if (command.getType() == MessageType.CLOSE_DOOR){
                System.out.println(context.getCurrentSystemTime() + "Elevator " +context.getCarNum()+ " is closing doors for " + DOOR_TO_CLOSED_TIME + " seconds");
                sleep(DOOR_TO_CLOSED_TIME);//simulate time it takes to close the doors
                Message response = new Message(command, MessageType.CONFIRM_DOOR_CLOSED);
                respondMsg(response, context);
                break;
            }
            else if (command.getType() == MessageType.FAULT_DOOR){
                sleep(DOOR_FAULT_TIME);//simulate time it takes
                System.out.println(context.getCurrentSystemTime() + "Elevator" +context.getCarNum()+ " failed to close doors! Job " + command);
                Message response = new Message(command, MessageType.REPORT_DOOR_NOT_CLOSING);
                fault = true;
                respondMsg(response, context);
                break;
            }
        }
        timeout(context, fault);
    }
    public void timeout(Elevator context, boolean fault) {
        if (fault){
            context.setCurrentState(new DoorClosing());
            context.getCurrentState().setTimer(context);
        }
        else{
            context.setCurrentState(new WaitingToMove());
            context.getCurrentState().setTimer(context);
        }
    }
}
