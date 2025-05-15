import ElevatorPackage.Elevator;
import primary.EntityType;
import primary.FloorSubsystemSimulation;
import primary.Message;
import primary.Scheduler;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ECSTest {

    @Test
    public void testMessageSending() throws InterruptedException {
        final String FILE_PATH = "src/main/resources/input.txt";
        FloorSubsystemSimulation floorSubsystemSimulation = new FloorSubsystemSimulation(FILE_PATH);
        //read successfully from file
        assertEquals(10, floorSubsystemSimulation.getAllEvents().size());

        Scheduler scheduler = new Scheduler();
        Elevator elevator = new Elevator(0);

        //set up test environment
        Thread schedulerSubsystem = new Thread(scheduler);
        Thread floorSubsystem = new Thread(floorSubsystemSimulation);
        Thread elevatorSubsystem = new Thread(elevator, "Elevator Subsystem");

        schedulerSubsystem.start();
        floorSubsystem.start();
        elevatorSubsystem.start();

        //sleep until all the expected messages have been received
        do{
            Thread.sleep(1000);
        } while (scheduler.getMsgsReceived().size() < 2
                && floorSubsystemSimulation.getMsgsReceived().isEmpty()
                && elevator.getMsgsReceived().isEmpty()
        );

        //floor subsystem successfully received 1 message from scheduler
        assertEquals(1, floorSubsystemSimulation.getMsgsReceived().size());
        assertEquals(floorSubsystemSimulation.getMsgsReceived().get(0).getFromWho(),EntityType.SCHED);

        //scheduler successfully received 2 messages:  from floor subsystem and from
        ArrayList<Message> list = scheduler.getMsgsReceived();
        assertEquals(2, list.size());
        assertTrue(containsElement(list, EntityType.ELEVATOR));
        assertTrue(containsElement(list, EntityType.FLOOR));

        //elevator successfully received 1 message from scheduler
        assertEquals(1, elevator.getMsgsReceived().size());
        assertEquals(elevator.getMsgsReceived().get(0).getFromWho(),EntityType.SCHED);

    }
    //helper method that returns true if the message list contains an element was received from the specific entity type
    private boolean containsElement (ArrayList<Message> list, EntityType from){
        for (Message m : list){
            if (m.getFromWho() == from){
                return true;
            }
        }
        return false;
    }
}
