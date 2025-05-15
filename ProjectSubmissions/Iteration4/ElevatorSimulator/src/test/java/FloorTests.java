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
     * Floor.java tests
     */
    @Test
    @DisplayName("testing the Floor constructor")
    public void testFloorConstructor() {
        assertNotNull(floor);
        assertEquals(1, floor.getFlrNum());
    }


    @Test
    @DisplayName("test reading messages from file for a floor")
    public void testReadMessagesForThisFloorFromFile() {
        floor.readMessagesForThisFloorFromFile("/multipleFloors.txt");
        assertNotNull(floor.getMessagesReadFromFile());
    }
    @Test
    @DisplayName("Test read from file multiple floors")
    public void testReadFromFileMultipleFloors(){
        floor.getUdp().closePorts();//clean up BeforeEach Setup


        final String FILE_PATH = "/multipleFloors.txt";
        final int MAX_FLOORS = 6; //this will give us floors 0, 1, 2, 3, 4, 5
        ArrayList<Floor> floors = new ArrayList<>();
        for (int i = 0; i < MAX_FLOORS; i++){
            Floor floor = new Floor(FILE_PATH, i);
            floors.add(floor);
        }//set up
        for (int i = 0; i < MAX_FLOORS; i++){
            floors.get(i).getUdp().closePorts();
        }//cleanup

        assertEquals(0, floors.get(0).getMessagesReadFromFile().size());
        assertEquals(4, floors.get(1).getMessagesReadFromFile().size());
        assertEquals(0, floors.get(2).getMessagesReadFromFile().size());
        assertEquals(1, floors.get(3).getMessagesReadFromFile().size());
        assertEquals(3, floors.get(4).getMessagesReadFromFile().size());
        assertEquals(2, floors.get(5).getMessagesReadFromFile().size());
    }

    @Test
    public void testLowestTimeInFile() {
        Time lowestTime = Floor.lowestTimeInFile();
        // Test as per the expected "lowest" time in your test input file
        System.out.println(lowestTime);
        //13:50:15.29 in long units
        assertEquals(new Time(67815029).getTime(), lowestTime.getTime());
    }

    @Test
    @DisplayName("assert that the schedule of jobs exists and is populated")
    public void testScheduleToSend() {
        ArrayList<Long> scheduleList = floor.scheduleToSend();
        // Validate as per your implementation. If it returns the schedule of sending messages in Long
        assertNotEquals(0, scheduleList.size());
        assertNotNull(scheduleList);
    }

    @Test
    @DisplayName("assert FirstInterval() method returns a numeric value.")
    public void testDetermineFirstInterval() {
        Long firstInterval = floor.determineFirstInterval(new Message("13:50:15.29 5 down 4 0\n"));
        assertNotNull(firstInterval);
    }

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