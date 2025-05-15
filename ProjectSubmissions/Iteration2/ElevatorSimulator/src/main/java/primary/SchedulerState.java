package primary;

public class SchedulerState {

    public void SetTimer(Scheduler context){}
    public void timeout(Scheduler context){}
    public void schedOperation(Scheduler context, String action){
        //System.out.println("Scheduler" + " Operation: " + action);
    }
    public void displayState(Scheduler context){}
    public void sleep(int seconds) {
        while(seconds != 0){
            try {
                Thread.sleep(1000);
                System.out.println(seconds);
                seconds -= 1;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public Message checkMsgs(Scheduler context){
        Message m = Mailbox.getInstance().get("Scheduler");
        if (m == null){
            return null;
        }
        context.getMsgsReceived().add(m);
        //System.out.println("Scheduler " + ", Received Request Below: ");
        //m.print();
        return m;
    }
    //TODO: ELEVATOR IS CURRENTLY HARDCODED, pass param (send to who)
    public void respondMsg(Message m){
        m.sentFromWho("Scheduler");
        m.sentToWho("Elevator1");
        Mailbox.getInstance().put(m);
    }
}

class checkingMessages extends SchedulerState{

    @Override
    public void SetTimer(Scheduler context) {
        displayState(context);
        schedOperation(context,"CHECKING MESSAGES");
        String type = "";
        while(true){//timeout when the mailbox has a request
            Message command = checkMsgs(context);//is set to true if no msgs, false if there is one
            if (command == null) {
                continue;
            }
            if (command.getType() == MessageType.INITIAL_READ_FROM_FILE){
                //System.out.println("Message received from floor");
                //command.print();
                type = "Floor";
                context.currentMsg = command;
                break;//received a service request a floor
            }
            if(command.getFromWho().contains("Elevator")){
                //System.out.println("Message received from elevator");
                type = "Elevator";
                context.currentMsg = command;
                break;
            }
        }
        timeout(context, type);
    }
    public void timeout(Scheduler context, String type) {
        if(type.equals("Floor")){
            context.setState(new handleFloorRequest());
        }
        else if(type.equals("Elevator")){
            context.setState(new handleElevatorRequest());
        } else {
            //System.out.println("message not properly understood");
        }
        context.getCurrentState().SetTimer(context);
    }
    @Override
    public void displayState(Scheduler context) {
        //System.out.println("State: Scheduler is Checking Messages");
    }
}

class handleFloorRequest extends SchedulerState{
    @Override
    public void SetTimer(Scheduler context) {
        displayState(context);
        schedOperation(context,"Handling Floor Request");
        timeout(context);
    }

    @Override
    public void schedOperation(Scheduler context, String action) {
         //System.out.println("Queuing starting floor: " + context.currentMsg.getStartingFloor());
         //System.out.println("Queuing destination floor: " + context.currentMsg.getDestFloor());
    }

    @Override
    public void timeout(Scheduler context) {
        context.setState(new processMessage());
        context.getCurrentState().SetTimer(context);
    }
    @Override
    public void displayState(Scheduler context) {
        //System.out.println("State: Scheduler is handleFloorRequest");
    }
}

class handleElevatorRequest extends SchedulerState{

    @Override
    public void SetTimer(Scheduler context) {
        displayState(context);
        schedOperation(context,"Handling Elevator Request");
        timeout(context);
    }

    @Override
    public void schedOperation(Scheduler context, String action) {
        switch (context.currentMsg.getType()) {
            case ACK_TRAVEL_TO_FLOOR:
                //System.out.println("Updating Elevator Model: Elevator destination updated");
                break;
            case CONFIRM_MOVING:
                //System.out.println("Updating Elevator Model: Elevator is moving");
                break;
            case CONFIRM_STOPPED:
                //System.out.println("Updating Elevator Model: Elevator has stopped'");
                break;
            case CONFIRM_DOOR_OPENED:
                //System.out.println("Updating Elevator Model: Elevator's doors are opened");
                break;
            case CONFIRM_DOOR_CLOSED:
                //System.out.println("Updating Elevator Model: Elevator's doors are closed");
                break;
            case CURRENT_FLOOR_UPDATE:
                //System.out.println("Updating Elevator Model: Elevator's current floor updated'");
                break;
            case CONFIRM_LOADING_UNLOADING_COMPLETE:
                //System.out.println("Updating Elevator Model: Elevator has finished unloading");
                break;
            default:
                break;
        }
    }

    @Override
    public void timeout(Scheduler context) {
        context.setState(new processMessage());
        context.getCurrentState().SetTimer(context);
    }
    
    @Override
    public void displayState(Scheduler context) {
        //System.out.println("State: Scheduler is handleElevatorRequest");
    }
}

class processMessage extends SchedulerState{

    @Override
    public void SetTimer(Scheduler context) {
        displayState(context);
        schedOperation(context,"PROCESS REQUESTS");

        Message sendMsg;
        //timeout when the mailbox has a request
        switch (context.currentMsg.getType()) {
            case INITIAL_READ_FROM_FILE:
                sendMsg = new Message(context.currentMsg, MessageType.TRAVEL_TO_FLOOR);
                respondMsg(sendMsg);

                break;
            case ACK_TRAVEL_TO_FLOOR:
                sendMsg = new Message(context.currentMsg, MessageType.CLOSE_DOOR);
                respondMsg(sendMsg);

                break;
            case CONFIRM_DOOR_CLOSED:
                if (context.currentMsg.isFulfilledStartingFloor()){//if it already was at starting floor
                    sendMsg = new Message(context.currentMsg, MessageType.REQUEST_CURRENT_FLOOR_UPDATE);
                    respondMsg(sendMsg);
                } else {
                    sendMsg = new Message(context.currentMsg, MessageType.REQUEST_INITIAL_FLOOR_UPDATE);
                    respondMsg(sendMsg);
                }

                break;
            case INITIAL_FLOOR_UPDATE:
                if (context.currentMsg.getStartingFloor() == context.currentMsg.getCurrentFloor()) {
                    context.currentMsg.setFulfilledStartingFloor(true);
                } else {
                    context.currentMsg.setFulfilledStartingFloor(false);
                }

                context.currentMsg.setType(MessageType.CURRENT_FLOOR_UPDATE);
                //break;//dont break need to use the next case to determine movement
            case CURRENT_FLOOR_UPDATE:
                //we send the elevator at the starting floor
                if (!context.currentMsg.isFulfilledStartingFloor()){
                    if (context.currentMsg.getStartingFloor() == context.currentMsg.getCurrentFloor()) {
                        sendMsg = new Message(context.currentMsg, MessageType.STOP);
                        respondMsg(sendMsg);
                        sendMsg = new Message(context.currentMsg, MessageType.REQUEST_STOPPED_CONFIRMATION);
                        respondMsg(sendMsg);
                        context.currentMsg.setFulfilledStartingFloor(true);
                    }
                    else {
                        sendMsg = new Message(context.currentMsg, MessageType.MOVE, context.determineMovingDirection(context.currentMsg.getCurrentFloor(), context.currentMsg.getStartingFloor()));
                        respondMsg(sendMsg);
                    }
                }

                //we have already sent the elevator to the starting floor, now we send it to the destination floor
                else if (!context.currentMsg.isFulfilledDestFloor()){
                    if(context.currentMsg.getDestFloor() == context.currentMsg.getCurrentFloor()){
                        sendMsg = new Message(context.currentMsg, MessageType.STOP);
                        respondMsg(sendMsg);
                        sendMsg = new Message(context.currentMsg, MessageType.REQUEST_STOPPED_CONFIRMATION);
                        respondMsg(sendMsg);
                        context.currentMsg.setFulfilledDestFloor(true);
                    }
                    else {
                        sendMsg = new Message(context.currentMsg, MessageType.MOVE, context.determineMovingDirection(context.currentMsg.getCurrentFloor(), context.currentMsg.getDestFloor()));
                        respondMsg(sendMsg);
                    }
                }

                break;
            case CONFIRM_STOPPED:
                sendMsg = new Message(context.currentMsg, MessageType.OPEN_DOOR);
                respondMsg(sendMsg);

                break;
            case CONFIRM_DOOR_OPENED:
                sendMsg = new Message(context.currentMsg, MessageType.REQUEST_UNLOADING_COMPLETE_CONFIRMATION);
                respondMsg(sendMsg);

                break;
            case CONFIRM_LOADING_UNLOADING_COMPLETE:
                System.out.println("Elevator arrived at requested floor: " + context.currentMsg.getCurrentFloor());
                if(!context.currentMsg.isFulfilledDestFloor() && context.currentMsg.getCurrentFloor() == context.currentMsg.getStartingFloor()){
                    context.currentMsg.setFulfilledStartingFloor(true);
                    sendMsg = new Message(context.currentMsg, MessageType.TRAVEL_TO_FLOOR);
                    respondMsg(sendMsg);
                }
                else {
                    System.out.println("Elevator completed this request:  ");
                    context.currentMsg.print();
                }
                break;

            default:
                break;

        }
        timeout(context);
    }
    @Override
    public void timeout(Scheduler context) {
        context.setState(new checkingMessages());
        context.getCurrentState().SetTimer(context);
    }
    @Override
    public void displayState(Scheduler context) {
        //System.out.println("State: Scheduler is Processing Event/Message");
    }
}