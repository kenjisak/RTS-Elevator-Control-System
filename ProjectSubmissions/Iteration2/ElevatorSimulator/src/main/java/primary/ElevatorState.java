package primary;
/**
 * Interface representing the states of an Elevator in a state machine.
 */
public class ElevatorState {
    public void SetTimer(Elevator context){}
    public void timeout(Elevator context){}
    public void elevOperation(Elevator context, String action){
        System.out.println("Elevator " + context.getCarNum() + " Operation: " + action);
    }
    public void displayState(Elevator context){}
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
    public Message checkMsgs(Elevator context){
        Message m = Mailbox.getInstance().get("Elevator" + context.getCarNum());
        if (m == null){
            return null;
        }
        context.getMsgsReceived().add(m);
        //System.out.println("Elevator " + context.getCarNum() + ", Received Request Below: ");
        //m.print();
        return m;//cant return null, thread in wait set until there's a message to return
    }

    public void respondMsg(Message m, Elevator context){
        m.sentFromWho("Elevator" + context.getCarNum());
        m.sentToWho("Scheduler");
        Mailbox.getInstance().put(m);
    }
}


/**
 * elevator idle state
 * entry: thread start
 * action: send an ACK_TRAVEL_TO_FLOOR message to scheduler
 * exit: received a TRAVEL_TO_FLOOR message from scheduler
 * next state: doorClosing
 */
class idle extends ElevatorState{
    //Listens for packets from Scheduler
    @Override
    public void SetTimer(Elevator context) {
        displayState(context);
        elevOperation(context,"IDLING");

        int secondsRunning = 0;
        while(true){//timeout when the mailbox has a request
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
        context.setState(new doorClosing());
        context.getCurrentState().SetTimer(context);
    }
    @Override
    public void displayState(Elevator context) {
        System.out.println("State: Elevator " + context.getCarNum() + " is Idle");
    }
    @Override
    public void sleep(int seconds) {
        try {
            Thread.sleep(1000);
            System.out.println(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/**
 * elevator closing doors state
 * previous state: idle
 * entry: TRAVEL_TO_FLOOR message received from scheduler
 * action: wait for a CLOSE_DOOR message from scheduler; when door is closed, send a CONFIRM_DOOR_CLOSED message to scheduler
 * exit: doorToClosedTime has expired
 * next state: doorClosed
 */
class doorClosing extends ElevatorState{
    private final static int doorToClosedTime = 5;//rounded mean from measurements, exact is 4.92s
    @Override
    public void SetTimer(Elevator context) {
        displayState(context);
        elevOperation(context,"DOORS_CLOSING");

        int secondsRunning = 0;
        while(true){//timeout when the mailbox has a request
            secondsRunning++;
            sleep(secondsRunning);
            Message command = checkMsgs(context);//is set to true if no msgs, false if there is one
            if (command == null) {
                continue;
            }
            if (command.getType() == MessageType.CLOSE_DOOR){
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
        context.setState(new waitingToMove());
        context.getCurrentState().SetTimer(context);
    }
    @Override
    public void displayState(Elevator context) {
        System.out.println("State: Elevator " + context.getCarNum() + " is closing doors");
    }
}


/**
 * elevator waiting to move state
 * previous state: doorsClosing
 * entry: door is closed
 * action: send CONFIRM_MOVING message to scheduler
 * exit: receive MOVE message from scheduler
 * next state: moving
 */
class waitingToMove extends ElevatorState{
    @Override
    public void SetTimer(Elevator context) {
        displayState(context);
        elevOperation(context,"WAITING_TO_MOVE");

        int secondsRunning = 0;
        while(true){//timeout when the mailbox has the correct command
            secondsRunning++;
            sleep(secondsRunning);//polling rate
            Message command = checkMsgs(context);

            if (command == null) {
                continue;
            }
            if (command.getType() == MessageType.REQUEST_INITIAL_FLOOR_UPDATE){
                Message response = new Message(command, MessageType.INITIAL_FLOOR_UPDATE,context.getCurrFloor());
                respondMsg(response, context);
                break;
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
        context.setState(new moving());
        context.getCurrentState().SetTimer(context);
    }
    @Override
    public void displayState(Elevator context) {
        System.out.println("State: Elevator " + context.getCarNum() + " doors are closed, waiting to Move");
    }
    @Override
    public void sleep(int seconds) {
        try {
            Thread.sleep(1000);
            System.out.println(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


/**
 * elevator moving state
 * previous state: waitingToMove
 * entry: receive MOVE message from scheduler
 * action:
 *  - wait for scheduler to send send REQUEST_CURRENT_FLOOR_UPDATE;
 *  - respond with CURRENT_FLOOR_UPDATE to scheduler
 *  - each time a MOVE command is received, increment current floor and send a CURRENT_FLOOR_UPDATE back to scheduler
 * exit: receive STOP message from scheduler
 * next state: stopped
 */
class moving extends ElevatorState{
    private final static int travelTime = 2;//rounded mean from measurements, exact is 1.64 between 1 floor
    @Override
    public void SetTimer(Elevator context) {
        displayState(context);
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
        context.setState(new stopped());
        context.getCurrentState().SetTimer(context);
    }
    @Override
    public void displayState(Elevator context) {
        System.out.println("State: Elevator " + context.getCarNum() + " is Moving");
    }
}

/**
 * elevator stopped state
 * previous state: moving
 * entry: receive STOP message from scheduler
 * action: send CONFIRM_STOPPED message to scheduler
 * exit: receive OPEN_DOOR message from scheduler
 * next state: doorOpening
 */
class stopped extends ElevatorState{
    @Override
    public void SetTimer(Elevator context) {
        displayState(context);
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
        context.setState(new doorOpening());
        context.getCurrentState().SetTimer(context);
    }
    @Override
    public void displayState(Elevator context) {
        System.out.println("State: Elevator " + context.getCarNum() + " is Stopped");
    }
}

/**
 * elevator door opening state
 * previous state: stopped
 * entry: receive OPEN_DOOR message from scheduler
 * action: send CONFIRM_DOOR_OPENED message to scheduler
 * exit: doorToOpenedTime timer expired
 * next state: door opened
 */
class doorOpening extends ElevatorState{
    private final static int doorToOpenedTime = 4;//rounded mean from measurements, exact is 4.02s
    @Override
    public void SetTimer(Elevator context) {
        displayState(context);
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
        context.setState(new doorOpened());
        context.getCurrentState().SetTimer(context);
    }
    @Override
    public void displayState(Elevator context) {
        System.out.println("State: Elevator " + context.getCarNum() + " is Opening Doors");
    }
}

/**
 * elevator door opened state
 * previous state: door opening
 * entry: receive REQUEST_UNLOADING_COMPLETE_CONFIRMATION message from scheduler
 * action: send CONFIRM_UNLOADING_COMPLETE message to scheduler
 * exit: doorStaysOpenTime timer expired
 * next state: idle
 */
class doorOpened extends ElevatorState{
    private final static int doorStaysOpenTime = 5;//this is the passengers unloading time
    @Override
    public void SetTimer(Elevator context) {
        displayState(context);
        elevOperation(context,"DOOR_OPENED_UNLOADING_PASSENGERS");
        while(true) {
            Message command = checkMsgs(context);
            if (command == null) {
                sleep(1);
                continue;//don't bother checking the message Type
            }
            if (command.getType() == MessageType.REQUEST_UNLOADING_COMPLETE_CONFIRMATION) {
                sleep(doorStaysOpenTime);//we are keeping the door open for passengers to unload
                Message response = new Message(command, MessageType.CONFIRM_LOADING_UNLOADING_COMPLETE);
                respondMsg(response, context);
                break;
            }
        }
        timeout(context);
    }
    @Override
    public void timeout(Elevator context) {
        context.setState(new idle());
        context.getCurrentState().SetTimer(context);
    }
    @Override
    public void displayState(Elevator context) {
        System.out.println("State: Elevator " + context.getCarNum() + " Door is Opened");
    }
}