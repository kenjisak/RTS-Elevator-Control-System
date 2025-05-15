package elevStateMachine;

import allSystems.Elevator;
import otherResources.Message;
import otherResources.MessageType;
import otherResources.MovingDirection;
import static otherResources.Constants.*;
/**
 * elevator Out of Order state
 * previous state: Moving
 * entry: receive OUT_OF_ORDER message from scheduler
 * action: none
 * exit: none
 * next state: none
 */
public class OutOfOrder extends ElevatorState{
    @Override
    public void setTimer(Elevator context) {
        elevOperation(context,"OUT OF ORDER");
        while(true) {
            if (Thread.currentThread().isInterrupted()){
                return;//kill thread, when interrupt flag is set
            }
            Message command = checkMsgs(context);
            if (command == null) {
                sleep(1);
                continue;
            }
            if (command.getType() == MessageType.OUT_OF_ORDER){
                sleep(DOOR_TO_OPENED_TIME);
                System.out.println("\n"+context.getCurrentSystemTime() + "Elevator " + context.getCarNum() + " is out of order ");
            }
        }
    }
}
