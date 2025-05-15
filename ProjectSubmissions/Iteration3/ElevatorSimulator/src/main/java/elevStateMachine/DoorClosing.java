package elevStateMachine;

import allSystems.Elevator;
import otherResources.Message;
import otherResources.MessageType; /**
 * elevator closing doors state
 * previous state: Idle
 * entry: TRAVEL_TO_FLOOR message received from scheduler
 * action: wait for a CLOSE_DOOR message from scheduler; when door is closed, send a CONFIRM_DOOR_CLOSED message to scheduler
 * exit: doorToClosedTime has expired
 * next state: doorClosed
 */
public class DoorClosing extends ElevatorState{
    private final static int doorToClosedTime = 5;//rounded mean from measurements, exact is 4.92s
    @Override
    public void setTimer(Elevator context) {
        //displayState(context," is closing doors");
        elevOperation(context,"DOORS_CLOSING");

        int secondsRunning = 0;
        while(true){//timeout when the mailbox has a request
            sleep(1);//poll every second
            secondsRunning++;
            Message command = checkMsgs(context);//is set to true if no msgs, false if there is one
            if (command == null) {
                continue;
            }
            if (command.getType() == MessageType.CLOSE_DOOR){
                System.out.println("Elevator " +context.getCarNum()+ " is closing doors for " + doorToClosedTime + " seconds");
                sleep(doorToClosedTime);//simulate time it takes to close the doors
                Message response = new Message(command, MessageType.CONFIRM_DOOR_CLOSED);
                respondMsg(response, context);
                break;
            }
        }
        timeout(context);
    }
    @Override
    public void timeout(Elevator context) {
        context.setCurrentState(new WaitingToMove());
        context.getCurrentState().setTimer(context);
    }
}
