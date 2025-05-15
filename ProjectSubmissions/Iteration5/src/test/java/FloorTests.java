import allSystems.Floor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import otherResources.Message;

import java.sql.Time;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class FloorTests {
    Floor floor;
    @BeforeEach
    public void setUp(){
        floor = new Floor("/multipleFloors.txt", 1);
    }
    @AfterEach
    public void cleanUp(){
        floor.getUdp().closePorts();
    }

    /**
     * Testing the Floor constructor.
     * It checks if the floor object is not null and the floor number is initialized correctly.
     */
    @Test
    @DisplayName("testing the Floor constructor")
    public void testFloorConstructor() {
        assertNotNull(floor);
        assertEquals(1, floor.getFlrNum());
    }

    /**
     * Reads the input file, converts all lines into messages, checks if the starting floor number == this
     * floor number, and adds them to the messagesReadFromFile list. Sorts the messages to send by time.
     */
    @Test
    @DisplayName("test reading messages from file for a floor")
    public void testReadMessagesForThisFloorFromFile() {
        floor.readMessagesForThisFloorFromFile("/multipleFloors.txt");
        assertNotNull(floor.getMessagesReadFromFile());
    }

    /**
     * This method is used to test the functionality of reading messages from a file for multiple floors.
     * It initializes multiple Floor objects with different floor numbers and reads messages for each floor
     * from a file specified by FILE_PATH constant.
     * It asserts the number of messages read for each floor.
     */
    @Test
    @DisplayName("Test read from file multiple floors")
    public void testReadFromFileMultipleFloors(){
        floor.getUdp().closePorts();//clean up BeforeEach Setup


        final String FILE_PATH = "/multipleFloors.txt";
        final int MAX_FLOORS = 6; //this will give us floors 1, 2, 3, 4, 5, 6
        ArrayList<Floor> floors = new ArrayList<>();
        for (int i = 1; i <= MAX_FLOORS; i++){
            Floor floor = new Floor(FILE_PATH, i);
            floors.add(floor);
        }//set up
        for (int i = 0; i < MAX_FLOORS; i++){
            floors.get(i).getUdp().closePorts();
        }//cleanup

        assertEquals(4, floors.get(0).getMessagesReadFromFile().size());//floor 1
        assertEquals(0, floors.get(1).getMessagesReadFromFile().size());//floor 2
        assertEquals(1, floors.get(2).getMessagesReadFromFile().size());//floor 3
        assertEquals(3, floors.get(3).getMessagesReadFromFile().size());//floor 4
        assertEquals(2, floors.get(4).getMessagesReadFromFile().size());//floor 5
        assertEquals(0, floors.get(5).getMessagesReadFromFile().size());//floor 6
    }

    /**
     * Finds the lowest time in the input file.
     * @return The lowest time as a Time object.
     * @throws RuntimeException If the file is not found or if there is an input/output error.
     */
    @Test
    public void testLowestTimeInFile() {
        Time lowestTime = Floor.lowestTimeInFile();
        // Test as per the expected "lowest" time in your test input file
        System.out.println(lowestTime);
        //13:50:15.29 in long units
        assertEquals(new Time(67815029).getTime(), lowestTime.getTime());
    }

    /**
     * Asserts that the schedule of jobs exists and is populated.
     */
    @Test
    @DisplayName("assert that the schedule of jobs exists and is populated")
    public void testScheduleToSend() {
        ArrayList<Long> scheduleList = floor.scheduleToSend();
        // Validate as per your implementation. If it returns the schedule of sending messages in Long
        assertNotEquals(0, scheduleList.size());
        assertNotNull(scheduleList);
    }

    /**
     * Determines the first interval between two messages.
     * @return The first interval in milliseconds as a Long value.
     * @throws RuntimeException If there is an error in determining the first interval.
     */
    @Test
    @DisplayName("assert FirstInterval() method returns a numeric value.")
    public void testDetermineFirstInterval() {
        Long firstInterval = floor.determineFirstInterval(new Message("13:50:15.29 5 down 4 0\n"));
        assertNotNull(firstInterval);
    }

    /**
     * Method for testing the waiting method in Floor. It asserts that the wait()
     * method works as it should by measuring the elapsed time after calling wait()
     * and asserting that it is greater than or equal to the given wait time.
     */
    @Test
    @DisplayName("assert wait() method works as it should.")
    public void testWaiting() {
        int waitTimeMs = 1000; // 1 second

        long startTime = System.currentTimeMillis();
        floor.waiting(waitTimeMs);
        long elapsedTime = System.currentTimeMillis() - startTime;

        assertTrue(elapsedTime >= waitTimeMs);
    }
}