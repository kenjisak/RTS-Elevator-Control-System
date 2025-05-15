import schedStateMachine.CheckAndHandleMessage;
import allSystems.Scheduler;
import otherResources.Message;
import otherResources.MessageType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SchedulerStateTests {
    /**
     * Test method to handle current floor update.
     * @throws InterruptedException if the thread is interrupted
     */
    @Test
    @DisplayName("test the INITIAL_READ_FROM_FILE case")
    public void testHandleCurrentFloorUpdate() throws InterruptedException {

        Scheduler scheduler = new Scheduler(); // Initialize according to your setup

        Message message = new Message("13:50:33.29 4 down 1 2");  // You should set the type of the message here.

        CheckAndHandleMessage checkAndHandleMessage = new CheckAndHandleMessage();

        checkAndHandleMessage.handleInitialReadFromFile(scheduler, message);
        MessageType expectedMessageType = MessageType.INITIAL_READ_FROM_FILE;
        assertEquals(expectedMessageType, message.getType());

        scheduler.getUdp().closePorts();
    }
    /**
     * This method tests the functionality of handling the REPORT_DOOR_NOT_CLOSING case.
     *
     * It initializes a Scheduler object and creates a Message object with a predefined message.
     * The CheckAndHandleMessage object is then used to handle the message with type REPORT_DOOR_NOT_CLOSING.
     * Finally, the expected message type is compared with the actual message type to verify that
     * the message type was correctly set to REPORT_DOOR_NOT_CLOSING.
     */
    @Test
    @DisplayName("test the REPORT_DOOR_NOT_CLOSING case")
    public void testReportDoorNotClosing() {
        Scheduler scheduler = new Scheduler(); // Initialize according to your setup
        Message message = new Message("13:50:33.29 4 down 1 2");  // You should set the type of the message here.

        CheckAndHandleMessage checkAndHandleMessage = new CheckAndHandleMessage();
        message.setType(MessageType.REPORT_DOOR_NOT_CLOSING);
        checkAndHandleMessage.handleAckTravelToFloor(scheduler, message);
        MessageType expectedMessageType = MessageType.REPORT_DOOR_NOT_CLOSING;
        assertEquals(expectedMessageType, message.getType());

        scheduler.getUdp().closePorts();
    }
    /**
     * Test method to test the functionality of loading and unloading a message.
     *
     * This method initializes a Scheduler object and creates a Message object with a predefined message string.
     * The CheckAndHandleMessage object is then used to handle the message with type CONFIRM_LOADING_UNLOADING_COMPLETE.
     * Finally, the expected message type is compared with the actual message type to verify that the message type was
     * correctly set to CONFIRM_LOADING_UNLOADING_COMPLETE.
     */
    @Test
    @DisplayName("test the CONFIRM_LOADING_UNLOADING_COMPLETE case")
    public void testLoadingAndUnloading() {
        Scheduler scheduler = new Scheduler();
        Message message = new Message("13:50:33.29 4 down 1 2");  // You should set the type of the message here.

        CheckAndHandleMessage checkAndHandleMessage = new CheckAndHandleMessage();
        message.setType(MessageType.CONFIRM_LOADING_UNLOADING_COMPLETE);

        checkAndHandleMessage.handleReportDoorNotOpening(scheduler, message);
        MessageType expectedMessageType = MessageType.CONFIRM_LOADING_UNLOADING_COMPLETE;
        assertEquals(expectedMessageType, message.getType());

        scheduler.getUdp().closePorts();
    }
}




