package elevStateMachine;

import allSystems.Elevator;
import otherResources.Message;
import otherResources.MessageType;
import static otherResources.Constants.*;
/**
 * elevator door opening state
 * previous state: Stopped
 * entry: receive OPEN_DOOR message from scheduler
 * action: send CONFIRM_DOOR_OPENED message to scheduler
 * exit: doorToOpenedTime timer expired
 * next state: door opened
 */
public class DoorOpening extends ElevatorState{
    @Override
    public void setTimer(Elevator context) {
        //displayState(context," is Opening Doors");
        elevOperation(context,"DOOR_OPENING");
        boolean fault = false;
        while(true) {
            if (Thread.currentThread().isInterrupted()){
                return;//kill thread, when interrupt flag is set
            }
            Message command = checkMsgs(context);
            if (command == null) {
                sleep(1);
                continue;//don't bother checking the message Type
            }
            if (command.getType() == MessageType.OPEN_DOOR) {
                System.out.println(context.getCurrentSystemTime() + "Elevator" +context.getCarNum()+ " is opening doors...");
                sleep(DOOR_TO_OPENED_TIME);//we are opening the door
                System.out.println(context.getCurrentSystemTime() + "Elevator" +context.getCarNum()+ " doors are opened.");
                Message response = new Message(command, MessageType.CONFIRM_DOOR_OPENED);
                respondMsg(response, context);
                break;
            }
            else if (command.getType() == MessageType.FAULT_DOOR){
                sleep(DOOR_FAULT_TIME);//simulate time it takes
                System.out.println(context.getCurrentSystemTime() + "Elevator" +context.getCarNum()+ " failed to open doors! Job " + command);
                Message response = new Message(command, MessageType.REPORT_DOOR_NOT_OPENING);
                fault = true;
                respondMsg(response, context);
                break;
            }
        }

        timeout(context, fault);
    }
    public void timeout(Elevator context, boolean fault) {
        if (fault) {
            context.setCurrentState(new DoorOpening());
            context.getCurrentState().setTimer(context);
        }
        else{
            context.setCurrentState(new DoorOpened());
            context.getCurrentState().setTimer(context);
        }

    }
}
