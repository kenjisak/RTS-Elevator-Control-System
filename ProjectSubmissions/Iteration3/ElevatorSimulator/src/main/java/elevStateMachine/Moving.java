package elevStateMachine;

import allSystems.Elevator;
import otherResources.Message;
import otherResources.MessageType;
import otherResources.MovingDirection; /**
 * elevator Moving state
 * previous state: WaitingToMove
 * entry: receive MOVE message from scheduler
 * action:
 *  - wait for scheduler to send send REQUEST_CURRENT_FLOOR_UPDATE;
 *  - respond with CURRENT_FLOOR_UPDATE to scheduler
 *  - each time a MOVE command is received, increment current floor and send a CURRENT_FLOOR_UPDATE back to scheduler
 * exit: receive STOP message from scheduler
 * next state: Stopped
 */
public class Moving extends ElevatorState{
    private final static int travelTime = 2;//rounded mean from measurements, exact is 1.64 between 1 floor
    @Override
    public void setTimer(Elevator context) {
        //displayState(context," is Moving");
        elevOperation(context,"MOVING");

        while(true){
            Message command = checkMsgs(context);
            if (command == null) {
                sleep(1);
                continue;//dont bother checking the message Type
            }
            if (command.getType() == MessageType.STOP ) {//when elevator is on destination floor, scheduler sends back: STOP
                break;
            }

            if (command.getType() == MessageType.MOVE){//the scheduler repeatedly sends MOVE in response to the CURRENT_FLOOR_UPDATE, so we keep moving
                if (command.getMovingDirection() == MovingDirection.UP){
                    elevOperation(context,"MOVE_UP");
                    context.moveUp(travelTime);
                } else if (command.getMovingDirection() == MovingDirection.DOWN) {
                    elevOperation(context,"MOVE_DOWN");
                    context.moveDown(travelTime);
                }
                Message response = new Message(command, MessageType.CURRENT_FLOOR_UPDATE, context.getCurrFloor());
                respondMsg(response, context);//we send the current floor
            }
        }
        timeout(context);
    }

    @Override
    public void timeout(Elevator context) {
        context.setCurrentState(new Stopped());
        context.getCurrentState().setTimer(context);
    }
}
