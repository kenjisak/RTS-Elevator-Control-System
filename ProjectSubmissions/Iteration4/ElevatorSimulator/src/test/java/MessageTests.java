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
     * Tests the Message class in java.
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

    @Test
    public void testConstructor_duplicateMessage() {
        Message initialMessage = new Message("13:50:15.29 5 down 4 0");
        Message duplicateMessage = new Message(initialMessage);

        assertEquals(initialMessage, duplicateMessage,"Initial and duplicate message should be identical");
    }

    @Test
    @DisplayName("Tests the overloaded Message constructor correctly sets the MessageType.")
    public void testConstructor_messageWithDifferentType() {
        Message msg = new Message("13:50:15.29 5 down 4 0");
        Message newMsg = new Message(msg, MessageType.MOVE);

        assertEquals(MessageType.MOVE, newMsg.getType(),"Type should be MOVE");
    }

    @Test
    @DisplayName("Tests that the overloaded Message constructor correctly sets the MessageType and Direction")
    public void testConstructor_messageWithTypeAndMovingDirection() {
        Message msg = new Message("13:50:15.29 5 down 4 0");
        Message newMsg = new Message(msg, MessageType.MOVE, MovingDirection.UP);

        assertEquals(MessageType.MOVE, newMsg.getType(),"Type should be MOVE");
        assertEquals(MovingDirection.UP, newMsg.getMovingDirection(),"Moving direction should be UP");
    }

    @Test
    @DisplayName("Tests that the overloaded Message class correctly sets the MessageType and currentFloor fields.")
    public void testConstructor_messageWithTypeAndCurrentFloor() {
        Message msg = new Message("13:50:15.29 5 down 4 0");
        Message newMsg = new Message(msg, MessageType.CURRENT_FLOOR_UPDATE, 6);

        assertEquals(MessageType.CURRENT_FLOOR_UPDATE, newMsg.getType(),"Type should be MOVE");
        assertEquals(6, newMsg.getCurrentFloor(),"Current floor should be 6");
    }

    @Test
    @DisplayName("Test that the job gets assigned to an Elevator")
    public void testSetAssignedTo() {
        Message msg = new Message("13:50:15.29 5 down 4 0");
        msg.setAssignedTo("Elevator1");
        assertEquals("Elevator1", msg.getAssignedTo(),"Assigned elevator should match");
    }

    @Test
    @DisplayName("Test that two Message instances with identical parameters are evaluated as equal by the equals method.")
    public void testEquals() {
        // Create two Message instances with identical parameters
        Message message1 = new Message("13:50:15.29 5 down 4 0");
        Message message2 = new Message("13:50:15.29 5 down 4 0");

        // Both instances should be evaluated as equal
        assertEquals(message1, message2, "Message instances with identical parameters should be evaluated as equal.");

    }

    @Test
    @DisplayName("Message1 will come before Message2, so the compareTo() method will return <0")
    public void testCompareTo() {
        Message msg1 = new Message("13:50:15.29 5 down 4 0");
        Message msg2 = new Message("13:50:16.29 5 down 4 0");

        assertTrue(msg1.compareTo(msg2) < 0,"Message 1 should be sooner than Message 2");
    }

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
    @Test
    @DisplayName("Test unique uid")
    public void testUniqueUid(){
        final String FILE_PATH = "/multipleFloors.txt";
        final int MAX_FLOORS = 6; //this will give us floors 0, 1, 2, 3, 4, 5
        ArrayList<Floor> floors = new ArrayList<>();
        for (int i = 0; i < MAX_FLOORS; i++){
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
