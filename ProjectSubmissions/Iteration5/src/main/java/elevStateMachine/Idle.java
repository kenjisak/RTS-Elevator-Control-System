package elevStateMachine;

import allSystems.Elevator;
import otherResources.Message;
import otherResources.MessageType; /**
 * elevator Idle state
 * entry: thread start
 * action: send an ACK_TRAVEL_TO_FLOOR message to scheduler
 * exit: received a TRAVEL_TO_FLOOR message from scheduler
 * next state: DoorClosing
 */
public class Idle extends ElevatorState{
    //Listens for packets from Scheduler
    @Override
    public void setTimer(Elevator context) {
//        displayState(context," is Idle");
        elevOperation(context,"IDLING");

        int secondsRunning = 0;
        while(true){//timeout when the mailbox has a request
            if (context.isKill()){
                System.out.println("KILLING Idle");
                return;//kill thread, when interrupt flag is set
            }
            secondsRunning++;
            sleep(secondsRunning);
            Message command = checkMsgs(context);//is set to true if no msgs, false if there is one
            if (command == null) {
                continue;
            }
            if (command.getType() == MessageType.TRAVEL_TO_FLOOR){
                Message response = new Message(command, MessageType.ACK_TRAVEL_TO_FLOOR);
                respondMsg(response, context);
                break;//received a service request
            }
        }
        timeout(context);
    }
    @Override
    public void timeout(Elevator context) {
        context.setCurrentState(new DoorClosing());
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
