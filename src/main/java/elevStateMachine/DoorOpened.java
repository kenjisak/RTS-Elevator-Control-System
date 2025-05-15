package elevStateMachine;

import allSystems.Elevator;
import otherResources.Message;
import otherResources.MessageType;
import static otherResources.Constants.*;
/**
 * elevator door opened state
 * previous state: door opening
 * entry: receive REQUEST_UNLOADING_COMPLETE_CONFIRMATION message from scheduler
 * action: send CONFIRM_UNLOADING_COMPLETE message to scheduler
 * exit: doorStaysOpenTime timer expired
 * next state: Idle
 */
public class DoorOpened extends ElevatorState{
    @Override
    public void setTimer(Elevator context) {
        //displayState(context," Door is Opened");
        elevOperation(context,"DOOR_OPENED_UNLOADING_PASSENGERS");
        while(true) {
            if (context.isKill()){
                System.out.println("KILLING DoorOpened");
                return;//kill thread, when interrupt flag is set
            }
            Message command = checkMsgs(context);
            if (command == null) {
                sleep(1);
                continue;//don't bother checking the message Type
            }
            if (command.getType() == MessageType.REQUEST_UNLOADING_COMPLETE_CONFIRMATION) {
                System.out.println(context.getCurrentSystemTime() + "Elevator "+ context.getCarNum() + " loading/unloading passengers...");
                int sleepTimes = command.getSleepTimes();
                sleep(DOOR_STAYS_OPEN_TIME * sleepTimes);//we are keeping the door open for passengers to unload
                System.out.println(context.getCurrentSystemTime() + "Elevator "+ context.getCarNum() + " loading/unloading passengers complete.");
                Message response = new Message(command, MessageType.CONFIRM_LOADING_UNLOADING_COMPLETE);
                respondMsg(response, context);
                break;
            }
        }
        timeout(context);
    }
    @Override
    public void timeout(Elevator context) {
        context.setCurrentState(new Idle());
        context.getCurrentState().setTimer(context);
    }
}
