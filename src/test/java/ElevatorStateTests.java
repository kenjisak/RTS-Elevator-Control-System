import allSystems.*;
import elevStateMachine.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import otherResources.Message;
import udp.UDP;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static otherResources.Constants.*;
import static otherResources.MessageType.*;
import static otherResources.MovingDirection.*;

public class ElevatorStateTests {
    int testPort = 50000;
    @AfterEach
    public void cleanUp() throws InterruptedException {
        Thread.sleep(500);
    }
    @Test
    @DisplayName("Elevator initializes with Idle state")
    void testElevInitIdle(){
        Elevator testElev = new Elevator(1);

        assertEquals(testElev.getCurrentState().getClass(), Idle.class);

        //clean up
        testElev.getUdp().closePorts();
    }
    @Test
    @DisplayName("Elevator transitions to closing doors")
    void testElevIdleTransition() throws InterruptedException {
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        UDP udpSched = new UDP(testPort);
        udpSched.register("Scheduler");

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message m = new Message("13:50:33.29 1 up 2 0");
        m.setToWho("Elevator1");
        m.setFromWho("Scheduler");
        m.setType(TRAVEL_TO_FLOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        assertEquals(DoorClosing.class, testElev.getCurrentState().getClass());

        Thread.sleep(1000);//timing to prevent runtime errors

        //clean up
        udpSched.closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();
    }
    @Test
    @DisplayName("Elevator transitions to waiting to move")
    void testElevCloseTransition() throws InterruptedException {
        DOOR_TO_CLOSED_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        UDP udpSched = new UDP(testPort);
        udpSched.register("Scheduler");

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message m = new Message("13:50:33.29 1 up 2 0");
        m.setToWho("Elevator1");
        m.setFromWho("Scheduler");
        m.setType(TRAVEL_TO_FLOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(CLOSE_DOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(DOOR_TO_CLOSED_TIME * 1000);//timing to wait for doors to close

        assertEquals(WaitingToMove.class, testElev.getCurrentState().getClass());

        Thread.sleep(1000);//timing to prevent runtime errors

        //clean up
        udpSched.closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();
    }
    @Test
    @DisplayName("Elevator transitions to moving")
    void testElevMoveTransition() throws InterruptedException {
        DOOR_TO_CLOSED_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        UDP udpSched = new UDP(testPort);
        udpSched.register("Scheduler");

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message m = new Message("13:50:33.29 1 up 2 0");
        m.setToWho("Elevator1");
        m.setFromWho("Scheduler");
        m.setType(TRAVEL_TO_FLOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(CLOSE_DOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(DOOR_TO_CLOSED_TIME * 1000);//timing to wait for doors to close

        m.setType(REQUEST_CURRENT_FLOOR_UPDATE);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        assertEquals(Moving.class, testElev.getCurrentState().getClass());

        Thread.sleep(1000);//timing to prevent runtime errors

        //clean up
        udpSched.closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();
    }
    @Test
    @DisplayName("Elevator state stays in moving state while moving up")
    void testElevMoveUp() throws InterruptedException {
        DOOR_TO_CLOSED_TIME = 1;
        ELEVATOR_TRAVEL_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        UDP udpSched = new UDP(testPort);
        udpSched.register("Scheduler");

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message m = new Message("13:50:33.29 1 up 2 0");
        m.setToWho("Elevator1");
        m.setFromWho("Scheduler");
        m.setType(TRAVEL_TO_FLOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(CLOSE_DOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(DOOR_TO_CLOSED_TIME * 1000);//timing to wait for doors to close

        m.setType(REQUEST_CURRENT_FLOOR_UPDATE);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        int startingFlr = testElev.getCurrFloor();
        assertEquals(startingFlr,testElev.getCurrFloor());
        assertEquals(Moving.class, testElev.getCurrentState().getClass());
        
        m.setType(MOVE);
        m.setMovingDirection(UP);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(ELEVATOR_TRAVEL_TIME * 1000);//timing to wait for elevator to travel

        assertEquals(startingFlr + 1,testElev.getCurrFloor());
        assertEquals(Moving.class, testElev.getCurrentState().getClass());

        Thread.sleep(1000);//timing to prevent runtime errors

        //clean up
        udpSched.closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();
    }
    @Test
    @DisplayName("Elevator state stays in moving state while moving down")
    void testElevMoveDown() throws InterruptedException {
        DOOR_TO_CLOSED_TIME = 1;
        ELEVATOR_TRAVEL_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        UDP udpSched = new UDP(testPort);
        udpSched.register("Scheduler");

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message m = new Message("13:50:33.29 1 up 2 0");
        m.setToWho("Elevator1");
        m.setFromWho("Scheduler");
        m.setType(TRAVEL_TO_FLOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(CLOSE_DOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(DOOR_TO_CLOSED_TIME * 1000);//timing to wait for doors to close

        m.setType(REQUEST_CURRENT_FLOOR_UPDATE);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        testElev.setCurrFloor(2);
        int startingFlr = testElev.getCurrFloor();
        assertEquals(startingFlr,testElev.getCurrFloor());
        assertEquals(Moving.class, testElev.getCurrentState().getClass());

        m.setType(MOVE);
        m.setMovingDirection(DOWN);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(ELEVATOR_TRAVEL_TIME * 1000);//timing to wait for elevator to travel

        assertEquals(startingFlr - 1,testElev.getCurrFloor());
        assertEquals(Moving.class, testElev.getCurrentState().getClass());

        Thread.sleep(1000);//timing to prevent runtime errors

        //clean up
        udpSched.closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();
    }
    @Test
    @DisplayName("Elevator transitions to stopping")
    void testElevStopTransition() throws InterruptedException {
        DOOR_TO_CLOSED_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        UDP udpSched = new UDP(testPort);
        udpSched.register("Scheduler");

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message m = new Message("13:50:33.29 1 up 2 0");
        m.setToWho("Elevator1");
        m.setFromWho("Scheduler");
        m.setType(TRAVEL_TO_FLOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(CLOSE_DOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(DOOR_TO_CLOSED_TIME * 1000);//timing to wait for doors to close

        m.setType(REQUEST_CURRENT_FLOOR_UPDATE);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(STOP);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        assertEquals(Stopped.class, testElev.getCurrentState().getClass());

        Thread.sleep(1000);//timing to prevent runtime errors

        //clean up
        udpSched.closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();
    }
    @Test
    @DisplayName("Elevator transitions to door opening")
    void testElevOpeningTransition() throws InterruptedException {
        DOOR_TO_CLOSED_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        UDP udpSched = new UDP(testPort);
        udpSched.register("Scheduler");

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message m = new Message("13:50:33.29 1 up 2 0");
        m.setToWho("Elevator1");
        m.setFromWho("Scheduler");
        m.setType(TRAVEL_TO_FLOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(CLOSE_DOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(DOOR_TO_CLOSED_TIME * 1000);//timing to wait for doors to close

        m.setType(REQUEST_CURRENT_FLOOR_UPDATE);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(STOP);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(REQUEST_STOPPED_CONFIRMATION);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        assertEquals(DoorOpening.class, testElev.getCurrentState().getClass());

        Thread.sleep(1000);//timing to prevent runtime errors

        //clean up
        udpSched.closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();
    }
    @Test
    @DisplayName("Elevator transitions to door opened")
    void testElevOpenedTransition() throws InterruptedException {
        DOOR_TO_CLOSED_TIME = 1;
        DOOR_TO_OPENED_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        UDP udpSched = new UDP(testPort);
        udpSched.register("Scheduler");

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message m = new Message("13:50:33.29 1 up 2 0");
        m.setToWho("Elevator1");
        m.setFromWho("Scheduler");
        m.setType(TRAVEL_TO_FLOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(CLOSE_DOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(DOOR_TO_CLOSED_TIME * 1000);//timing to wait for doors to close

        m.setType(REQUEST_CURRENT_FLOOR_UPDATE);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(STOP);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(REQUEST_STOPPED_CONFIRMATION);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(OPEN_DOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(DOOR_TO_OPENED_TIME * 1000);//timing to wait for doors to open

        assertEquals(DoorOpened.class, testElev.getCurrentState().getClass());

        Thread.sleep(1000);//timing to prevent runtime errors

        //clean up
        udpSched.closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();
    }
    @Test
    @DisplayName("Elevator transitions to idle at the end of a request")
    void testElevReIdleTransition() throws InterruptedException {
        DOOR_TO_CLOSED_TIME = 1;
        DOOR_TO_OPENED_TIME = 1;
        DOOR_STAYS_OPEN_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        UDP udpSched = new UDP(testPort);
        udpSched.register("Scheduler");

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message m = new Message("13:50:33.29 1 up 2 0");
        m.setToWho("Elevator1");
        m.setFromWho("Scheduler");
        m.setType(TRAVEL_TO_FLOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(CLOSE_DOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(DOOR_TO_CLOSED_TIME * 1000);//timing to wait for doors to close

        m.setType(REQUEST_CURRENT_FLOOR_UPDATE);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(STOP);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(REQUEST_STOPPED_CONFIRMATION);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(OPEN_DOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(DOOR_TO_OPENED_TIME * 1000);//timing to wait for doors to open

        m.setType(REQUEST_UNLOADING_COMPLETE_CONFIRMATION);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(DOOR_STAYS_OPEN_TIME * 1000);//timing to wait for doors to open

        assertEquals(Idle.class, testElev.getCurrentState().getClass());

        Thread.sleep(1000);//timing to prevent runtime errors

        //clean up
        udpSched.closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();
    }
    @Test
    @DisplayName("Elevator fault while doors closing")
    void testElevClosingFault() throws InterruptedException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        DOOR_FAULT_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        UDP udpSched = new UDP(testPort);
        udpSched.register("Scheduler");

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message m = new Message("13:50:33.29 1 up 2 0");
        m.setToWho("Elevator1");
        m.setFromWho("Scheduler");
        m.setType(TRAVEL_TO_FLOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(FAULT_DOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(DOOR_FAULT_TIME * 1000);//timing to wait for doors at fault

        assertTrue(outContent.toString().contains("Elevator1 failed to close doors!"));
        assertEquals(DoorClosing.class, testElev.getCurrentState().getClass());

        Thread.sleep(1000);//timing to prevent runtime errors

        //clean up
        udpSched.closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();

        System.setOut(originalOut);
        System.out.println(outContent);
    }
    @Test
    @DisplayName("Elevator fault while doors opening")
    void testElevOpeningFault() throws InterruptedException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        DOOR_FAULT_TIME = 1;
        DOOR_TO_CLOSED_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        UDP udpSched = new UDP(testPort);
        udpSched.register("Scheduler");

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message m = new Message("13:50:33.29 1 up 2 0");
        m.setToWho("Elevator1");
        m.setFromWho("Scheduler");
        m.setType(TRAVEL_TO_FLOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(CLOSE_DOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(DOOR_TO_CLOSED_TIME * 1000);//timing to wait for doors to close

        m.setType(REQUEST_CURRENT_FLOOR_UPDATE);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(STOP);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(REQUEST_STOPPED_CONFIRMATION);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(FAULT_DOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(2000);//timing to send message

        assertTrue(outContent.toString().contains("Elevator1 failed to open doors!"));
        assertEquals(DoorOpening.class, testElev.getCurrentState().getClass());

        Thread.sleep(1000);//timing to prevent runtime errors

        //clean up
        udpSched.closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();

        System.setOut(originalOut);
        System.out.println(outContent);
    }
    @Test
    @DisplayName("Elevator fault while moving")
    void testElevMovingFault() throws InterruptedException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        DOOR_TO_CLOSED_TIME = 1;
        ELEVATOR_TRAVEL_TIME = 1;
        STUCK_FAULT_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        UDP udpSched = new UDP(testPort);
        udpSched.register("Scheduler");

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message m = new Message("13:50:33.29 1 up 2 0");
        m.setToWho("Elevator1");
        m.setFromWho("Scheduler");
        m.setType(TRAVEL_TO_FLOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        m.setType(CLOSE_DOOR);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(DOOR_TO_CLOSED_TIME * 1000);//timing to wait for doors to close

        m.setType(REQUEST_CURRENT_FLOOR_UPDATE);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        int stuckFlr = testElev.getCurrFloor();

        m.setType(FAULT_STUCK);
        udpSched.sendMessage(m,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(STUCK_FAULT_TIME * 1000);//timing to wait for elevator to finish being stuck

        assertTrue(outContent.toString().contains("Elevator 1 is stuck at floor " + stuckFlr));
        assertEquals(OutOfOrder.class, testElev.getCurrentState().getClass());

        Thread.sleep(1000);//timing to prevent runtime errors

        //clean up
        udpSched.closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();

        System.setOut(originalOut);
        System.out.println(outContent);
    }
}
