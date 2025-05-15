import allSystems.Floor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import otherResources.Message;
import otherResources.MessageType;
import otherResources.MovingDirection;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class MessageTests {
    /**
     * Tests the constructor of the Message class for properly parsing a message line.
     * It verifies that the parsed properties of the constructed Message object match the expected values.
     */
    @Test
    @DisplayName("Test that the Message constructor properly parses a job")
    public void testConstructor_line() {
        Message msg = new Message("13:50:15.29 5 down 4 0");
        assertEquals("13:50:15", msg.getTimeAtReq().toString(),"Time should match");
        assertEquals(5, msg.getStartingFloor(),"Starting floor should match");
        assertEquals("down", msg.getDirection(),"Direction should match");
        assertEquals(4, msg.getDestFloor(),"Destination floor should match");
    }

    /**
     * Tests the constructor of the Message class when creating a duplicate message.
     * It verifies that the initial message and the duplicate message are identical.
     */
    @Test
    public void testConstructor_duplicateMessage() {
        Message initialMessage = new Message("13:50:15.29 5 down 4 0");
        Message duplicateMessage = new Message(initialMessage);

        assertEquals(initialMessage, duplicateMessage,"Initial and duplicate message should be identical");
    }

    /**
     * Tests the overloaded Message constructor for setting the MessageType correctly.
     *
     * The constructor takes an existing Message object and a MessageType parameter.
     * It creates a new Message object with the provided MessageType, copying all other properties from the existing Message.
     * It then asserts that the MessageType of the new Message matches the provided MessageType.
     */
    @Test
    @DisplayName("Tests the overloaded Message constructor correctly sets the MessageType.")
    public void testConstructor_messageWithDifferentType() {
        Message msg = new Message("13:50:15.29 5 down 4 0");
        Message newMsg = new Message(msg, MessageType.MOVE);

        assertEquals(MessageType.MOVE, newMsg.getType(),"Type should be MOVE");
    }

    /**
     * Tests that the overloaded Message constructor correctly sets the MessageType and MovingDirection.
     *
     * The method creates a new Message object by passing an existing Message object, MessageType,
     * and MovingDirection as parameters. It then asserts that the MessageType of the new Message
     * object matches the provided MessageType, and the MovingDirection matches the provided
     * MovingDirection.
     */
    @Test
    @DisplayName("Tests that the overloaded Message constructor correctly sets the MessageType and Direction")
    public void testConstructor_messageWithTypeAndMovingDirection() {
        Message msg = new Message("13:50:15.29 5 down 4 0");
        Message newMsg = new Message(msg, MessageType.MOVE, MovingDirection.UP);

        assertEquals(MessageType.MOVE, newMsg.getType(),"Type should be MOVE");
        assertEquals(MovingDirection.UP, newMsg.getMovingDirection(),"Moving direction should be UP");
    }

    /**
     * This method is a test case that verifies whether the overloaded constructor of the Message class correctly sets the MessageType and currentFloor fields.
     *
     * It creates a Message object with the provided message line "13:50:15.29 5 down 4 0", and then creates a new Message object using the overloaded constructor, passing the original
     * Message object, MessageType.CURRENT_FLOOR_UPDATE, and 6 as parameters.
     *
     * The test asserts that the MessageType of the new Message object is set to MessageType.CURRENT_FLOOR_UPDATE and the currentFloor is set to 6.
     */
    @Test
    @DisplayName("Tests that the overloaded Message class correctly sets the MessageType and currentFloor fields.")
    public void testConstructor_messageWithTypeAndCurrentFloor() {
        Message msg = new Message("13:50:15.29 5 down 4 0");
        Message newMsg = new Message(msg, MessageType.CURRENT_FLOOR_UPDATE, 6);

        assertEquals(MessageType.CURRENT_FLOOR_UPDATE, newMsg.getType(),"Type should be MOVE");
        assertEquals(6, newMsg.getCurrentFloor(),"Current floor should be 6");
    }

    /**
     * This method is a test case that verifies whether the assignedTo field of the Message object
     * is properly set.
     *
     * It creates a new Message object using the provided message line "13:50:15.29 5 down 4 0".
     * Then it sets the assignedTo field to the provided string "Elevator1".
     * Finally, it asserts that the assignedTo field of the Message object matches the provided string.
     */
    @Test
    @DisplayName("Test that the job gets assigned to an Elevator")
    public void testSetAssignedTo() {
        Message msg = new Message("13:50:15.29 5 down 4 0");
        msg.setAssignedTo("Elevator1");
        assertEquals("Elevator1", msg.getAssignedTo(),"Assigned elevator should match");
    }

    /**
     * This method is a test case that verifies whether two Message instances with identical parameters are evaluated as equal by the equals method.
     *
     * It creates two Message instances with identical parameters by passing the same message line "13:50:15.29 5 down 4 0" to the constructor.
     * Then it asserts that the two instances are equal using the assertEquals method. The message argument is set to "Message instances with identical parameters should be evaluated
     * as equal."
     *
     */
    @Test
    @DisplayName("Test that two Message instances with identical parameters are evaluated as equal by the equals method.")
    public void testEquals() {
        // Create two Message instances with identical parameters
        Message message1 = new Message("13:50:15.29 5 down 4 0");
        Message message2 = new Message("13:50:15.29 5 down 4 0");

        // Both instances should be evaluated as equal
        assertEquals(message1, message2, "Message instances with identical parameters should be evaluated as equal.");

    }

    /**
     * Test case to verify the behavior of the compareTo() method.
     * It compares two Message objects and ensures that the expected result is returned.
     */
    @Test
    @DisplayName("Message1 will come before Message2, so the compareTo() method will return <0")
    public void testCompareTo() {
        Message msg1 = new Message("13:50:15.29 5 down 4 0");
        Message msg2 = new Message("13:50:16.29 5 down 4 0");

        assertTrue(msg1.compareTo(msg2) < 0,"Message 1 should be sooner than Message 2");
    }

    /**
     * Calculates the time difference in milliseconds between two Message objects.
     *
     * @return The time difference in milliseconds between the two Message objects.
     * @throws InterruptedException If the thread is interrupted while sleeping.
     */
    @Test
    @DisplayName("Test if differenceInMs calculates the correct time difference.")
    public void testDifferenceInMs() throws InterruptedException {
        // Creating two messages with a specific time gap
        Message msg1 = new Message("13:50:15.29 5 down 4 0");
        Thread.sleep(1000); // waiting for 1 second
        Message msg2 = new Message("13:50:16.29 5 down 4 0");

        // Calculate time difference using the method
        long difference = Message.differenceInMs(msg1, msg2);

        // The difference should be approximately 1000 milliseconds.
        assertTrue(difference >= 950 && difference <= 1050, "Time difference should be approximately 1000 milliseconds");
    }

    /**
     * This method tests for unique UID among the messages read from multiple floors.
     * It creates a list of floors and reads messages from each floor.
     * Then it checks if any duplicate UIDs are found among the messages.
     * Finally, it asserts that no duplicate UIDs are found.
     */
    @Test
    @DisplayName("Test unique uid")
    public void testUniqueUid(){
        final String FILE_PATH = "/multipleFloors.txt";
        final int MAX_FLOORS = 6; //this will give us floors 1, 2, 3, 4, 5, 6
        ArrayList<Floor> floors = new ArrayList<>();
        for (int i = 1; i <= MAX_FLOORS; i++){
            Floor floor = new Floor(FILE_PATH, i);
            floors.add(floor);
        }//set up

        boolean foundDuplicate = false;
        for (Floor floor : floors){
            ArrayList<Message> list = floor.getMessagesReadFromFile();
            int id = -1;
            for (Message m : list){
                if (m.getUid() == id){
                    foundDuplicate = true;
                    break;
                }
            }
        }

        for (int i = 0; i < MAX_FLOORS; i++){
            floors.get(i).getUdp().closePorts();
        }//cleanup

        assertFalse(foundDuplicate);
    }
}
