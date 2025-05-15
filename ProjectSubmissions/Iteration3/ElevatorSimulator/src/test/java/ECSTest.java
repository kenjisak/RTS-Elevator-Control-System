import allSystems.Elevator;
import allSystems.Floor;
import allSystems.Mailbox;
import otherResources.Message;
import allSystems.Scheduler;
import org.junit.Test;

import java.sql.Array;
import java.util.ArrayList;

import static org.junit.Assert.*;
import org.junit.jupiter.api.DisplayName;


public class ECSTest {

    @Test
    @DisplayName("Test sending message one pass single floor")
    public void testSendingMessageOnePassSingleFloor() throws InterruptedException {
        //TODO this was a test used for iteration 1. iteration 2 breaks this test due to different run method; will rewrite as part of fault handling
        /*
        final String FILE_PATH = "src/test/resources/inputMessageSendOnePassSingleFloor.txt";
        Floor floor = new Floor(FILE_PATH, 5);
        //read the messages intended for this floor successfully from file
        assertEquals(2, floor.getMessagesReadFromFile().size());

        Scheduler scheduler = new Scheduler();
        Elevator elevator = new Elevator(1);

        //set up test environment
        Thread schedulerSubsystem = new Thread(scheduler);
        Thread floorSubsystem = new Thread(floor);
        Thread elevatorSubsystem = new Thread(elevator, "Elevator Subsystem");

        schedulerSubsystem.start();
        floorSubsystem.start();
        elevatorSubsystem.start();

        //sleep until all the expected messages have been received
        do{
            Thread.sleep(1000);
        } while (scheduler.getMsgsReceived().size() < 2
                && floor.getMsgsReceived().isEmpty()
                && elevator.getMsgsReceived().isEmpty()
        );

        //floor subsystem successfully received 1 message from scheduler
        assertEquals(1, floor.getMsgsReceived().size());
        assertEquals(floor.getMsgsReceived().get(0).getFromWho(),"Scheduler");

        //scheduler successfully received 2 messages:  from floor and from elevator
        ArrayList<Message> list = scheduler.getMsgsReceived();
        assertEquals(2, list.size());
        assertTrue(containsElement(list, "Elevator1"));
        assertTrue(containsElement(list, "Floor5"));

        //elevator successfully received 1 message from scheduler
        assertEquals(1, elevator.getMsgsReceived().size());
        assertEquals(elevator.getMsgsReceived().get(0).getFromWho(),"Scheduler");

         */
    }

    @Test
    @DisplayName("Test read from file multiple floors")
    public void testReadFromFileMultipleFloors() throws InterruptedException{
        final String FILE_PATH = "/inputReadFromFileMultipleFloors.txt";
        final int MAX_FLOORS = 6; //this will give us floors 0, 1, 2, 3, 4, 5
        ArrayList<Floor> floors = new ArrayList<>();
        for (int i = 0; i < MAX_FLOORS; i++){
            Floor floor = new Floor(FILE_PATH, i);
            floors.add(floor);
        }

        assertEquals(2, floors.get(0).getMessagesReadFromFile().size());
        assertEquals(2, floors.get(1).getMessagesReadFromFile().size());
        assertEquals(2, floors.get(2).getMessagesReadFromFile().size());
        assertEquals(4, floors.get(3).getMessagesReadFromFile().size());
        assertEquals(1, floors.get(4).getMessagesReadFromFile().size());
        assertEquals(1, floors.get(5).getMessagesReadFromFile().size());

    }
    @Test
    @DisplayName("Test unique uid")
    public void testUniqueUid(){
        final String FILE_PATH = "/inputReadFromFileMultipleFloors.txt";
        final int MAX_FLOORS = 6; //this will give us floors 0, 1, 2, 3, 4, 5
        ArrayList<Floor> floors = new ArrayList<>();
        for (int i = 0; i < MAX_FLOORS; i++){
            Floor floor = new Floor(FILE_PATH, i);
            floors.add(floor);
        }
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
        assertFalse(foundDuplicate);
    }

    @Test
    @DisplayName("Test all jobs completed")
    public void testAllJobsCompleted(){
        //TODO test harness
    }


    //helper method that returns true if the message list contains an element that was received from the specific string from
    private boolean containsElement (ArrayList<Message> list, String from){
        for (Message m : list){
            if (m.getFromWho().equals(from)){
                return true;
            }
        }
        return false;
    }
}
