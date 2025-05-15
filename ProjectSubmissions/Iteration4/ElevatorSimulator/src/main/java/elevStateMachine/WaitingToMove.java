package elevStateMachine;

import allSystems.Elevator;
import otherResources.Message;
import otherResources.MessageType; /**
 * elevator waiting to move state
 * previous state: doorsClosing
 * entry: door is closed
 * action: send CONFIRM_MOVING message to scheduler
 * exit: receive MOVE message from scheduler
 * next state: Moving
 */
public class WaitingToMove extends ElevatorState{
    @Override
    public void setTimer(Elevator context) {
        //displayState(context," doors are closed, waiting to Move");
        elevOperation(context,"WAITING_TO_MOVE");

        int secondsRunning = 0;
        while(true){//timeout when the mailbox has the correct command
            if (Thread.currentThread().isInterrupted()){
                return;//kill thread, when interrupt flag is set
            }
            secondsRunning++;
            sleep(1000);//polling rate
            Message command = checkMsgs(context);

            if (command == null) {
                continue;
            }
            if (command.getType() == MessageType.REQUEST_CURRENT_FLOOR_UPDATE){
                Message response = new Message(command, MessageType.CURRENT_FLOOR_UPDATE,context.getCurrFloor());
                respondMsg(response, context);
                break;
            }
        }
        timeout(context);
    }
    public void timeout(Elevator context) {
        context.setCurrentState(new Moving());
        context.getCurrentState().setTimer(context);
    }
    @Override
    public void sleep(int seconds) {
        try {
            Thread.sleep(1000);
            //System.out.println(seconds);
        } catch (InterruptedException ignored) {}
    }
}
