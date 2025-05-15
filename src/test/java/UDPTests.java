import allSystems.*;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import otherResources.Message;
import udp.UDP;

import java.net.DatagramPacket;

import static org.junit.jupiter.api.Assertions.*;
import static otherResources.Constants.*;

public class UDPTests {
    int testPort = 5000;
    /**
     * Tests if the port for elevators listen to upon initialization are created correctly
     */
    @Test
    @DisplayName("Receive Port Initializing for Elevators")
    void testingPortInitElev(){
        int maxElevs = 10;

        for (int i = 1; i <= maxElevs; i++) {
            Elevator elevator = new Elevator(i);
            int elevPort = elevator.getUdp().getReceiveSocket().getLocalPort();
            assertEquals(BASEELEVPORT + i, elevPort);

            //clean up after test
            elevator.getUdp().closePorts();
        }
    }
    /**
     * Tests if the port for floors listen to upon initialization are created correctly
     */
    @Test
    @DisplayName("Receive Port Initializing for Floors")
    void testingPortInitFloor(){
        int maxFlrs = 10;

        for (int i = 1; i <= maxFlrs; i++) {
            Floor floor = new Floor("/multipleFloors.txt",i);
            int floorPort = floor.getUdp().getReceiveSocket().getLocalPort();
            assertEquals(BASEFLOORPORT + i, floorPort);

            //clean up after test
            floor.getUdp().closePorts();
        }
    }
    /**
     * Tests if the port for the scheduler to listen to upon initialization are created correctly
     */
    @Test
    @DisplayName("Receive Port Initializing for Scheduler")
    void testingPortInitSched(){
        Scheduler scheduler = new Scheduler();
        int schedPort = scheduler.getUdp().getReceiveSocket().getLocalPort();
        assertEquals(SCHEDPORT,schedPort);

        //clean up after test
        scheduler.getUdp().closePorts();
    }
    /**
     * Tests if the port for the mailbox to listen to upon initialization are created correctly
     */
    @Test
    @DisplayName("Receive Port Initializing for Mailbox")
    void testingPortInitMail(){
        Mailbox mailbox = new Mailbox();
        int mailPort = mailbox.getUdp().getReceiveSocket().getLocalPort();
        assertEquals(MAILPORT,mailPort);

        //clean up after test
        mailbox.getUdp().closePorts();
    }
    /**
     * Tests if the ports for the Elevators close correctly
     */
    @Test
    @DisplayName("Receive Port Cleanup for Elevators")
    void testingPortClose(){
        int maxElevs = 10;

        for (int i = 1; i <= maxElevs; i++) {
            Elevator elevator = new Elevator(i);
            int elevPort = elevator.getUdp().getReceiveSocket().getLocalPort();

            assertEquals(BASEELEVPORT + i, elevPort);
            elevator.getUdp().closePorts();

            elevPort = elevator.getUdp().getReceiveSocket().getLocalPort();
            assertEquals(-1, elevPort);

            ///////////////////FLOORS/////////////////////
            Floor floor = new Floor("",i);
            int floorPort = floor.getUdp().getReceiveSocket().getLocalPort();

            assertEquals(BASEFLOORPORT + i, floorPort);
            floor.getUdp().closePorts();

            floorPort = floor.getUdp().getReceiveSocket().getLocalPort();
            assertEquals(-1, floorPort);
        }

        Scheduler scheduler = new Scheduler();
        int schedPort = scheduler.getUdp().getReceiveSocket().getLocalPort();
        assertEquals(SCHEDPORT,schedPort);

        scheduler.getUdp().closePorts();
        schedPort = scheduler.getUdp().getReceiveSocket().getLocalPort();
        assertEquals(-1,schedPort);

        Mailbox mailbox = new Mailbox();
        int mailPort = mailbox.getUdp().getReceiveSocket().getLocalPort();
        assertEquals(MAILPORT,mailPort);

        mailbox.getUdp().closePorts();
        mailPort = mailbox.getUdp().getReceiveSocket().getLocalPort();
        assertEquals(-1,mailPort);
    }
    /**
     * Tests if the ports for the floors close correctly
     */
    @Test
    @DisplayName("Receive Port Cleanup for Floors")
    void testingPortCloseFlr(){
        int maxFlrs = 10;

        for (int i = 1; i <= maxFlrs; i++) {
            Floor floor = new Floor("",i);
            floor.getUdp().closePorts();

            int floorPort = floor.getUdp().getReceiveSocket().getLocalPort();
            assertEquals(-1, floorPort);
        }
    }
    /**
     * Tests if the ports for the scheduler close correctly
     */
    @Test
    @DisplayName("Receive Port Cleanup for Scheduler")
    void testingPortCloseSched(){
        Scheduler scheduler = new Scheduler();
        scheduler.getUdp().closePorts();

        int schedPort = scheduler.getUdp().getReceiveSocket().getLocalPort();
        assertEquals(-1,schedPort);
    }
    /**
     * Tests if the ports for the mailbox close correctly
     */
    @Test
    @DisplayName("Receive Port Cleanup for Mailbox")
    void testingPortCloseMail(){
        Mailbox mailbox = new Mailbox();
        mailbox.getUdp().closePorts();

        int mailPort = mailbox.getUdp().getReceiveSocket().getLocalPort();
        assertEquals(-1,mailPort);
    }
    /**
     * Tests if the handleMessage() method in the UDP class returns correctly, either null if there's no message in the receivers mailbox or the message object if there is one
     */
    @Test
    @DisplayName("Handle Message Verification")
    void testingHandleMessage(){
        UDP testUDP = new UDP(testPort);

        Message m = new Message("13:50:33.29 1 up 1 1");
        m.setToWho("testToWho");
        m.setFromWho("testFromWho");

        byte[] testData = SerializationUtils.serialize(m);
        DatagramPacket testPacket = new DatagramPacket(testData, testData.length);

        Message testMessage = testUDP.handleMessage(testPacket);
        assertNotNull(testMessage);

        //null return test
        byte[] testDataNull = SerializationUtils.serialize(null);
        testPacket = new DatagramPacket(testDataNull, testDataNull.length);

        Message testMessageNull = testUDP.handleMessage(testPacket);
        assertNull(testMessageNull);

        //clean up
        testUDP.closePorts();
    }
}
