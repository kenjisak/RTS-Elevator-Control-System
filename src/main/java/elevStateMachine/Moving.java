package elevStateMachine;

import allSystems.Elevator;
import otherResources.Message;
import otherResources.MessageType;
import otherResources.MovingDirection;
import static otherResources.Constants.*;/**
 * elevator Moving state
 * previous state: WaitingToMove
 * entry: receive MOVE message from scheduler
 * action:
 *  - wait for scheduler to send REQUEST_CURRENT_FLOOR_UPDATE;
 *  - respond with CURRENT_FLOOR_UPDATE to scheduler
 *  - each time a MOVE command is received, increment current floor and send a CURRENT_FLOOR_UPDATE back to scheduler
 *  - if a FAULT_STUCK command is received, simulate a fault and send back a REPORT_STUCK message
 * exit: receive STOP message from scheduler
 * next state: if there was a fault, then OutOfOrder; otherwise Stopped
 */
public class Moving extends ElevatorState{
    @Override
    public void setTimer(Elevator context) {
        //displayState(context," is Moving");
        elevOperation(context,"MOVING");
        boolean fault = false;
        while(true){
            if (context.isKill()){
                System.out.println("KILLING Moving");
                return;//kill thread, when interrupt flag is set
            }
            Message command = checkMsgs(context);
            if (command == null) {
                sleep(1);
                continue;//don't bother checking the message Type
            }
            if (command.getType() == MessageType.FAULT_STUCK){
                fault = true;
                sleep(STUCK_FAULT_TIME);
                Message response = new Message(command, MessageType.REPORT_STUCK, context.getCurrFloor());
                System.out.println(context.getCurrentSystemTime() + "Elevator " + context.getCarNum() + " is stuck at floor " + context.getCurrFloor());
                respondMsg(response, context);
                break;
            }
            else if (command.getType() == MessageType.STOP ) {//when elevator is on destination floor, scheduler sends back: STOP
                break;
            }

            else if (command.getType() == MessageType.MOVE){//the scheduler repeatedly sends MOVE in response to the CURRENT_FLOOR_UPDATE, so we keep moving
                if (command.getMovingDirection() == MovingDirection.UP){
                    elevOperation(context,"MOVE_UP");
                    context.moveUp(ELEVATOR_TRAVEL_TIME);
                } else if (command.getMovingDirection() == MovingDirection.DOWN) {
                    elevOperation(context,"MOVE_DOWN");
                    context.moveDown(ELEVATOR_TRAVEL_TIME);
                }
                Message response = new Message(command, MessageType.CURRENT_FLOOR_UPDATE, context.getCurrFloor());
                respondMsg(response, context);//we send the current floor
            }
        }
        timeout(context, fault);
    }

    public void timeout(Elevator context, boolean fault) {
        if(fault){
            context.setCurrentState(new OutOfOrder());
            context.getCurrentState().setTimer(context);
        }
        else{
            context.setCurrentState(new Stopped());
            context.getCurrentState().setTimer(context);
        }
        
    }
}
