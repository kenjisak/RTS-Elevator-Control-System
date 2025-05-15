package elevStateMachine;

import allSystems.Elevator;
import otherResources.Message;
import otherResources.MessageType; /**
 * elevator door opening state
 * previous state: Stopped
 * entry: receive OPEN_DOOR message from scheduler
 * action: send CONFIRM_DOOR_OPENED message to scheduler
 * exit: doorToOpenedTime timer expired
 * next state: door opened
 */
public class DoorOpening extends ElevatorState{
    private final static int doorToOpenedTime = 4;//rounded mean from measurements, exact is 4.02s
    @Override
    public void setTimer(Elevator context) {
        //displayState(context," is Opening Doors");
        elevOperation(context,"DOOR_OPENING");
        while(true) {
            Message command = checkMsgs(context);
            if (command == null) {
                sleep(1);
                continue;//don't bother checking the message Type
            }
            if (command.getType() == MessageType.OPEN_DOOR) {
                sleep(doorToOpenedTime);//we are opening the door
                Message response = new Message(command, MessageType.CONFIRM_DOOR_OPENED);
                respondMsg(response, context);
                break;
            }
        }

        timeout(context);
    }
    @Override
    public void timeout(Elevator context) {
        context.setCurrentState(new DoorOpened());
        context.getCurrentState().setTimer(context);
    }
}
