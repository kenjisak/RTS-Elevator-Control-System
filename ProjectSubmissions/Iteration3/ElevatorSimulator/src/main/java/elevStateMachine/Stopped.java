package elevStateMachine;

import allSystems.Elevator;
import otherResources.Message;
import otherResources.MessageType; /**
 * elevator Stopped state
 * previous state: Moving
 * entry: receive STOP message from scheduler
 * action: send CONFIRM_STOPPED message to scheduler
 * exit: receive OPEN_DOOR message from scheduler
 * next state: DoorOpening
 */
public class Stopped extends ElevatorState{
    @Override
    public void setTimer(Elevator context) {
        //displayState(context," is Stopped");
        elevOperation(context,"STOPPED");
        while(true) {
            Message command = checkMsgs(context);
            if (command == null) {
                sleep(1);
                continue;//don't bother checking the message Type
            }
            if (command.getType() == MessageType.REQUEST_STOPPED_CONFIRMATION) {
                Message response = new Message(command, MessageType.CONFIRM_STOPPED);
                respondMsg(response, context);
                break;
            }
        }
        timeout(context);
    }
    @Override
    public void timeout(Elevator context) {
        context.setCurrentState(new DoorOpening());
        context.getCurrentState().setTimer(context);
    }
}
