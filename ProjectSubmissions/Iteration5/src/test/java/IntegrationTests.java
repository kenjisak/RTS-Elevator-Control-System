import allSystems.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import otherResources.Message;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static otherResources.Constants.*;
import static otherResources.Message.MessageStatus.*;
import static otherResources.MessageType.*;

public class IntegrationTests {
    @AfterEach
    public void cleanUp() throws InterruptedException {
        Thread.sleep(500);
    }
    @Test
    @DisplayName("Elevator travels one floor up")
    void testElevReqUp() throws InterruptedException {
        MAX_ELEVATORS = 1;
        ELEVATOR_TRAVEL_TIME = 1;
        DOOR_TO_CLOSED_TIME = 1;
        DOOR_TO_OPENED_TIME = 1;
        DOOR_STAYS_OPEN_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        Scheduler testSched = new Scheduler();
        Thread sched = new Thread(testSched, "Scheduler");
        sched.start();

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message testRequest = new Message("13:50:33.29 1 up 2 0");
        testRequest.setToWho("Scheduler");
        testRequest.setFromWho("Floor1");
        testRequest.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(25 * 1000);//timing to let scenario run


        Message completedReq = testSched.getCompletedJobs().get(0);
        assertEquals(COMPLETED, completedReq.getStatus());
        assertTrue(completedReq.isFulfilledStartingFloor());
        assertTrue(completedReq.isFulfilledDestFloor());

        //clean up
        sched.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        testSched.getUdp().closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();
    }
    @Test
    @DisplayName("Elevator travels one floor down")
    void testElevReqDown() throws InterruptedException {
        MAX_ELEVATORS = 1;
        ELEVATOR_TRAVEL_TIME = 1;
        DOOR_TO_CLOSED_TIME = 1;
        DOOR_TO_OPENED_TIME = 1;
        DOOR_STAYS_OPEN_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        Scheduler testSched = new Scheduler();
        Thread sched = new Thread(testSched, "Scheduler");
        sched.start();

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message testRequest = new Message("13:50:33.29 2 down 1 0");
        testRequest.setToWho("Scheduler");
        testRequest.setFromWho("Floor2");
        testRequest.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(25 * 1000);//timing to let scenario run


        Message completedReq = testSched.getCompletedJobs().get(0);
        assertEquals(COMPLETED, completedReq.getStatus());
        assertTrue(completedReq.isFulfilledStartingFloor());
        assertTrue(completedReq.isFulfilledDestFloor());

        //clean up
        sched.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        testSched.getUdp().closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();
    }
    @Test
    @DisplayName("Elevator travels multiple floors up")
    void testElevReqUpMultiple() throws InterruptedException {
        MAX_ELEVATORS = 1;
        ELEVATOR_TRAVEL_TIME = 1;
        DOOR_TO_CLOSED_TIME = 1;
        DOOR_TO_OPENED_TIME = 1;
        DOOR_STAYS_OPEN_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        Scheduler testSched = new Scheduler();
        Thread sched = new Thread(testSched, "Scheduler");
        sched.start();

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message testRequest = new Message("13:50:33.29 1 up 5 0");
        testRequest.setToWho("Scheduler");
        testRequest.setFromWho("Floor1");
        testRequest.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(30 * 1000);//timing to let scenario run


        Message completedReq = testSched.getCompletedJobs().get(0);
        assertEquals(COMPLETED, completedReq.getStatus());
        assertTrue(completedReq.isFulfilledStartingFloor());
        assertTrue(completedReq.isFulfilledDestFloor());

        //clean up
        sched.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        testSched.getUdp().closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();
    }
    @Test
    @DisplayName("Elevator travels multiple floors down")
    void testElevReqDownMultiple() throws InterruptedException {
        MAX_ELEVATORS = 1;
        ELEVATOR_TRAVEL_TIME = 1;
        DOOR_TO_CLOSED_TIME = 1;
        DOOR_TO_OPENED_TIME = 1;
        DOOR_STAYS_OPEN_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        Scheduler testSched = new Scheduler();
        Thread sched = new Thread(testSched, "Scheduler");
        sched.start();

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message testRequest = new Message("13:50:33.29 5 down 2 0");
        testRequest.setToWho("Scheduler");
        testRequest.setFromWho("Floor5");
        testRequest.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(35 * 1000);//timing to let scenario run


        Message completedReq = testSched.getCompletedJobs().get(0);
        assertEquals(COMPLETED, completedReq.getStatus());
        assertTrue(completedReq.isFulfilledStartingFloor());
        assertTrue(completedReq.isFulfilledDestFloor());

        //clean up
        sched.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        testSched.getUdp().closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();
    }

    @Test
    @DisplayName("Elevator servicing multiple requests")
    void testElevReqMultipleReqs() throws InterruptedException {
        MAX_ELEVATORS = 1;
        ELEVATOR_TRAVEL_TIME = 1;
        DOOR_TO_CLOSED_TIME = 1;
        DOOR_TO_OPENED_TIME = 1;
        DOOR_STAYS_OPEN_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        Scheduler testSched = new Scheduler();
        Thread sched = new Thread(testSched, "Scheduler");
        sched.start();

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message testRequest = new Message("13:50:33.29 1 up 2 0");
        testRequest.setToWho("Scheduler");
        testRequest.setFromWho("Floor1");
        testRequest.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Message testRequest2 = new Message("13:50:34.29 4 down 3 0");
        testRequest2.setToWho("Scheduler");
        testRequest2.setFromWho("Floor4");
        testRequest2.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest2,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(45 * 1000);//timing to let scenario run


        Message completedReq = testSched.getCompletedJobs().get(0);
        assertEquals(COMPLETED, completedReq.getStatus());
        assertTrue(completedReq.isFulfilledStartingFloor());
        assertTrue(completedReq.isFulfilledDestFloor());

        Message completedReq2 = testSched.getCompletedJobs().get(1);
        assertEquals(COMPLETED, completedReq2.getStatus());
        assertTrue(completedReq2.isFulfilledStartingFloor());
        assertTrue(completedReq2.isFulfilledDestFloor());

        //clean up
        sched.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        testSched.getUdp().closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();
    }
    @Test
    @DisplayName("Elevator servicing on the way requests")
    void testElevOtwReqs() throws InterruptedException {
        MAX_ELEVATORS = 2;
        ELEVATOR_TRAVEL_TIME = 1;
        DOOR_TO_CLOSED_TIME = 1;
        DOOR_TO_OPENED_TIME = 1;
        DOOR_STAYS_OPEN_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        Scheduler testSched = new Scheduler();
        Thread sched = new Thread(testSched, "Scheduler");
        sched.start();

        Elevator testElev = new Elevator(2);
        Thread elev = new Thread(testElev, "Elevator2");
        elev.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message testRequest = new Message("13:50:33.29 1 up 2 0");
        testRequest.setToWho("Scheduler");
        testRequest.setFromWho("Floor1");
        testRequest.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Message testRequest2 = new Message("13:50:34.29 2 up 3 0");
        testRequest2.setToWho("Scheduler");
        testRequest2.setFromWho("Floor2");
        testRequest2.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest2,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(35 * 1000);//timing to let scenario run


        Message completedReq = testSched.getCompletedJobs().get(0);
        assertEquals(COMPLETED, completedReq.getStatus());
        assertTrue(completedReq.isFulfilledStartingFloor());
        assertTrue(completedReq.isFulfilledDestFloor());

        Message completedReq2 = testSched.getCompletedJobs().get(1);
        assertEquals(COMPLETED, completedReq2.getStatus());
        assertTrue(completedReq2.isFulfilledStartingFloor());
        assertTrue(completedReq2.isFulfilledDestFloor());

        assertEquals(completedReq.getAssignedTo(),completedReq2.getAssignedTo());
        //clean up
        sched.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        testSched.getUdp().closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();
    }
    @Test
    @DisplayName("Two Elevators servicing requests that are not on the way at the same time")
    void testElevBusyReqs() throws InterruptedException {
        MAX_ELEVATORS = 2;
        ELEVATOR_TRAVEL_TIME = 1;
        DOOR_TO_CLOSED_TIME = 1;
        DOOR_TO_OPENED_TIME = 1;
        DOOR_STAYS_OPEN_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        Scheduler testSched = new Scheduler();
        Thread sched = new Thread(testSched, "Scheduler");
        sched.start();

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Elevator testElev2 = new Elevator(2);
        Thread elev2 = new Thread(testElev2, "Elevator2");
        elev2.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message testRequest = new Message("13:50:33.29 1 up 2 0");
        testRequest.setToWho("Scheduler");
        testRequest.setFromWho("Floor1");
        testRequest.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Message testRequest2 = new Message("13:50:34.29 3 down 1 0");
        testRequest2.setToWho("Scheduler");
        testRequest2.setFromWho("Floor3");
        testRequest2.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest2,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(30 * 1000);//timing to let scenario run


        Message completedReq = testSched.getCompletedJobs().get(0);
        assertEquals(COMPLETED, completedReq.getStatus());
        assertTrue(completedReq.isFulfilledStartingFloor());
        assertTrue(completedReq.isFulfilledDestFloor());

        Message completedReq2 = testSched.getCompletedJobs().get(1);
        assertEquals(COMPLETED, completedReq2.getStatus());
        assertTrue(completedReq2.isFulfilledStartingFloor());
        assertTrue(completedReq2.isFulfilledDestFloor());

        assertNotEquals(completedReq.getAssignedTo(),completedReq2.getAssignedTo());
        //clean up
        sched.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        testSched.getUdp().closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        testElev2.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev2.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();
    }
    @Test
    @DisplayName("Elevator has a soft fault")
    void testElevSoftFault() throws InterruptedException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        MAX_ELEVATORS = 1;
        ELEVATOR_TRAVEL_TIME = 1;
        DOOR_TO_CLOSED_TIME = 1;
        DOOR_TO_OPENED_TIME = 1;
        DOOR_STAYS_OPEN_TIME = 1;
        DOOR_FAULT_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        Scheduler testSched = new Scheduler();
        Thread sched = new Thread(testSched, "Scheduler");
        sched.start();

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message testRequest = new Message("13:50:33.29 1 up 2 1");
        testRequest.setToWho("Scheduler");
        testRequest.setFromWho("Floor1");
        testRequest.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(25 * 1000);//timing to let scenario run


        Message completedReq = testSched.getCompletedJobs().get(0);
        assertEquals(COMPLETED, completedReq.getStatus());
        assertTrue(completedReq.isFulfilledStartingFloor());
        assertTrue(completedReq.isFulfilledDestFloor());

        boolean softFaultInjected = outContent.toString().contains("Elevator1 failed to close doors!") || outContent.toString().contains("Elevator1 failed to open doors!");

        assertTrue(softFaultInjected);

        //clean up
        sched.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        testSched.getUdp().closePorts();

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
    @DisplayName("Elevator has a hard fault with no other elevators able to service the request")
    void testElevHardFault() throws InterruptedException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        MAX_ELEVATORS = 1;
        ELEVATOR_TRAVEL_TIME = 1;
        DOOR_TO_CLOSED_TIME = 1;
        DOOR_TO_OPENED_TIME = 1;
        DOOR_STAYS_OPEN_TIME = 1;
        STUCK_FAULT_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        Scheduler testSched = new Scheduler();
        Thread sched = new Thread(testSched, "Scheduler");
        sched.start();

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message testRequest = new Message("13:50:33.29 1 up 2 2");
        testRequest.setToWho("Scheduler");
        testRequest.setFromWho("Floor1");
        testRequest.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(10 * 1000);//timing to let scenario run

        assertEquals(0,testSched.getCompletedJobs().size());

        assertTrue(outContent.toString().contains("Taking Elevator1 out of service"));
        assertTrue(outContent.toString().contains("Scheduler: All elevators are Full"));
        assertTrue(outContent.toString().contains("Elevator 1 is out of order"));

        //clean up
        sched.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        testSched.getUdp().closePorts();

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
    @DisplayName("Elevator has a hard fault and sends another elevator to service")
    void testElevHardFaultReService() throws InterruptedException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        MAX_ELEVATORS = 2;
        ELEVATOR_TRAVEL_TIME = 1;
        DOOR_TO_CLOSED_TIME = 1;
        DOOR_TO_OPENED_TIME = 1;
        DOOR_STAYS_OPEN_TIME = 1;
        STUCK_FAULT_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        Scheduler testSched = new Scheduler();
        Thread sched = new Thread(testSched, "Scheduler");
        sched.start();

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Elevator testElev2 = new Elevator(2);
        Thread elev2 = new Thread(testElev2, "Elevator2");
        elev2.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message testRequest = new Message("13:50:33.29 1 up 2 2");
        testRequest.setToWho("Scheduler");
        testRequest.setFromWho("Floor1");
        testRequest.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(30 * 1000);//timing to let scenario run

        Message completedReq = testSched.getCompletedJobs().get(0);
        assertEquals(COMPLETED, completedReq.getStatus());
        assertTrue(completedReq.isFulfilledStartingFloor());
        assertTrue(completedReq.isFulfilledDestFloor());

        assertTrue(outContent.toString().contains("Taking Elevator2 out of service"));
        assertFalse(outContent.toString().contains("Scheduler: All elevators are Full"));
        assertTrue(outContent.toString().contains("Elevator 2 is out of order"));

        //clean up
        sched.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        testSched.getUdp().closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        testElev2.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev2.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();

        System.setOut(originalOut);
        System.out.println(outContent);
    }
    @Test
    @DisplayName("Elevator has 6 OTW requests and at capacity with 5 requests, so it sends another elevator to service")
    void testElevCapacity() throws InterruptedException {
        MAX_ELEVATORS = 2;
        ELEVATOR_TRAVEL_TIME = 1;
        DOOR_TO_CLOSED_TIME = 1;
        DOOR_TO_OPENED_TIME = 1;
        DOOR_STAYS_OPEN_TIME = 1;
        STUCK_FAULT_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        Scheduler testSched = new Scheduler();
        Thread sched = new Thread(testSched, "Scheduler");
        sched.start();

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Elevator testElev2 = new Elevator(2);
        Thread elev2 = new Thread(testElev2, "Elevator2");
        elev2.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message testRequest = new Message("13:50:33.29 1 up 8 0");
        testRequest.setToWho("Scheduler");
        testRequest.setFromWho("Floor1");
        testRequest.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Message testRequest2 = new Message("13:50:34.29 2 up 8 0");
        testRequest2.setToWho("Scheduler");
        testRequest2.setFromWho("Floor2");
        testRequest2.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest2,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Message testRequest3 = new Message("13:50:35.29 3 up 8 0");
        testRequest3.setToWho("Scheduler");
        testRequest3.setFromWho("Floor3");
        testRequest3.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest3,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Message testRequest4 = new Message("13:50:36.29 4 up 8 0");
        testRequest4.setToWho("Scheduler");
        testRequest4.setFromWho("Floor4");
        testRequest4.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest4,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Message testRequest5 = new Message("13:50:37.29 5 up 8 0");
        testRequest5.setToWho("Scheduler");
        testRequest5.setFromWho("Floor5");
        testRequest5.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest5,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Message testRequest6 = new Message("13:50:38.29 6 up 8 0");
        testRequest6.setToWho("Scheduler");
        testRequest6.setFromWho("Floor6");
        testRequest6.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest6,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(70 * 1000);//timing to let scenario run

        ArrayList<Message> completedJobs = testSched.getCompletedJobs();
        int elev1PassCount = 0;
        int elev2PassCount = 0;
        for (Message completedJob : completedJobs) {
            if (Objects.equals(completedJob.getAssignedTo(), "Elevator1")) {
                elev1PassCount++;
            }
            if (Objects.equals(completedJob.getAssignedTo(), "Elevator2")) {
                elev2PassCount++;
            }
        }
        assertEquals(MAX_CAPACITY, elev2PassCount);
        assertEquals(1, elev1PassCount);

        //clean up
        sched.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        testSched.getUdp().closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        testElev2.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev2.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();
    }
    @Test
    @DisplayName("Elevator has 6 OTW requests and at capacity with 5 requests, so it sends another elevator to service")
    void testElevCapacitySameFloors() throws InterruptedException {
        MAX_ELEVATORS = 2;
        ELEVATOR_TRAVEL_TIME = 1;
        DOOR_TO_CLOSED_TIME = 1;
        DOOR_TO_OPENED_TIME = 1;
        DOOR_STAYS_OPEN_TIME = 1;
        STUCK_FAULT_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        Scheduler testSched = new Scheduler();
        Thread sched = new Thread(testSched, "Scheduler");
        sched.start();

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Elevator testElev2 = new Elevator(2);
        Thread elev2 = new Thread(testElev2, "Elevator2");
        elev2.start();

        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message testRequest = new Message("13:50:33.29 7 up 8 0");
        testRequest.setToWho("Scheduler");
        testRequest.setFromWho("Floor7");
        testRequest.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest,MAILPORT,MAILIP);
//        Thread.sleep(1000);//timing to send message

        Message testRequest2 = new Message("13:50:33.29 7 up 8 0");
        testRequest2.setToWho("Scheduler");
        testRequest2.setFromWho("Floor7");
        testRequest2.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest2,MAILPORT,MAILIP);
//        Thread.sleep(1000);//timing to send message

        Message testRequest3 = new Message("13:50:33.29 7 up 8 0");
        testRequest3.setToWho("Scheduler");
        testRequest3.setFromWho("Floor7");
        testRequest3.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest3,MAILPORT,MAILIP);
//        Thread.sleep(1000);//timing to send message

        Message testRequest4 = new Message("13:50:33.29 7 up 8 0");
        testRequest4.setToWho("Scheduler");
        testRequest4.setFromWho("Floor7");
        testRequest4.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest4,MAILPORT,MAILIP);
//        Thread.sleep(1000);//timing to send message

        Message testRequest5 = new Message("13:50:33.29 7 up 8 0");
        testRequest5.setToWho("Scheduler");
        testRequest5.setFromWho("Floor7");
        testRequest5.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest5,MAILPORT,MAILIP);
//        Thread.sleep(1000);//timing to send message

        Message testRequest6 = new Message("13:50:33.29 7 up 8 0");
        testRequest6.setToWho("Scheduler");
        testRequest6.setFromWho("Floor7");
        testRequest6.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest6,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(50 * 1000);//timing to let scenario run

        ArrayList<Message> completedJobs = testSched.getCompletedJobs();
        int elev1PassCount = 0;
        int elev2PassCount = 0;
        for (Message completedJob : completedJobs) {
            if (Objects.equals(completedJob.getAssignedTo(), "Elevator1")) {
                elev1PassCount++;
            }
            if (Objects.equals(completedJob.getAssignedTo(), "Elevator2")) {
                elev2PassCount++;
            }
        }
        assertEquals(MAX_CAPACITY, elev2PassCount);
        assertEquals(1, elev1PassCount);

        //clean up
        sched.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        testSched.getUdp().closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        testElev2.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev2.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();
    }
    @Test
    @DisplayName("Elevator has 10 OTW requests, which should reroute due to capacity limits")
    void testElevCapacitySameFloorsBusy() throws InterruptedException {
        MAX_ELEVATORS = 2;
        ELEVATOR_TRAVEL_TIME = 1;
        DOOR_TO_CLOSED_TIME = 1;
        DOOR_TO_OPENED_TIME = 1;
        DOOR_STAYS_OPEN_TIME = 1;
        STUCK_FAULT_TIME = 1;
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        Scheduler testSched = new Scheduler();
        Thread sched = new Thread(testSched, "Scheduler");
        sched.start();

        Elevator testElev = new Elevator(1);
        Thread elev = new Thread(testElev, "Elevator1");
        elev.start();

        Elevator testElev2 = new Elevator(2);
        Thread elev2 = new Thread(testElev2, "Elevator2");
        elev2.start();

        Thread.sleep(50);//timing to wait for registering
        //rig and test
        Message testRequestBusy = new Message("13:50:33.29 4 down 2 0");
        testRequestBusy.setToWho("Scheduler");
        testRequestBusy.setFromWho("Floor4");
        testRequestBusy.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequestBusy,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        //rig and test
        Message testRequest = new Message("13:50:33.29 2 down 1 0");
        testRequest.setToWho("Scheduler");
        testRequest.setFromWho("Floor2");
        testRequest.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest,MAILPORT,MAILIP);
//        Thread.sleep(1000);//timing to send message

        Message testRequest2 = new Message("13:50:33.29 2 down 1 0");
        testRequest2.setToWho("Scheduler");
        testRequest2.setFromWho("Floor2");
        testRequest2.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest2,MAILPORT,MAILIP);
//        Thread.sleep(1000);//timing to send message

        Message testRequest3 = new Message("13:50:33.29 2 down 1 0");
        testRequest3.setToWho("Scheduler");
        testRequest3.setFromWho("Floor2");
        testRequest3.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest3,MAILPORT,MAILIP);
//        Thread.sleep(1000);//timing to send message

        Message testRequest4 = new Message("13:50:33.29 2 down 1 0");
        testRequest4.setToWho("Scheduler");
        testRequest4.setFromWho("Floor2");
        testRequest4.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest4,MAILPORT,MAILIP);
//        Thread.sleep(1000);//timing to send message

        Message testRequest5 = new Message("13:50:33.29 3 down 1 0");
        testRequest5.setToWho("Scheduler");
        testRequest5.setFromWho("Floor3");
        testRequest5.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest5,MAILPORT,MAILIP);
//        Thread.sleep(1000);//timing to send message

        Message testRequest6 = new Message("13:50:33.29 3 down 1 0");
        testRequest6.setToWho("Scheduler");
        testRequest6.setFromWho("Floor3");
        testRequest6.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest6,MAILPORT,MAILIP);
//        Thread.sleep(1000);//timing to send message

        Message testRequest7 = new Message("13:50:33.29 3 down 1 0");
        testRequest7.setToWho("Scheduler");
        testRequest7.setFromWho("Floor3");
        testRequest7.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest7,MAILPORT,MAILIP);
//        Thread.sleep(1000);//timing to send message

        Message testRequest8 = new Message("13:50:33.29 3 down 1 0");
        testRequest8.setToWho("Scheduler");
        testRequest8.setFromWho("Floor3");
        testRequest8.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest8,MAILPORT,MAILIP);
//        Thread.sleep(1000);//timing to send message

        Message testRequest9 = new Message("13:50:33.29 3 down 1 0");
        testRequest9.setToWho("Scheduler");
        testRequest9.setFromWho("Floor3");
        testRequest9.setType(INITIAL_READ_FROM_FILE);
        testSched.getUdp().sendMessage(testRequest9,MAILPORT,MAILIP);
        Thread.sleep(1000);//timing to send message

        Thread.sleep(70 * 1000);//timing to let scenario run

        //clean up
        sched.interrupt();
        Thread.sleep(1500);//let thread die, before closing ports
        testSched.getUdp().closePorts();

        testElev.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev.getUdp().closePorts();

        testElev2.setKill(true);
        Thread.sleep(1500);//let thread die, before closing ports
        testElev2.getUdp().closePorts();

        mail.interrupt();
        Thread.sleep(50);//let thread die, before closing ports
        mailbox.getUdp().closePorts();

        ArrayList<Message> completedJobs = testSched.getCompletedJobs();
        int elev1PassCount = 0;
        int elev2PassCount = 0;
        for (Message completedJob : completedJobs) {
            if (Objects.equals(completedJob.getAssignedTo(), "Elevator1")) {
                elev1PassCount++;
            }
            if (Objects.equals(completedJob.getAssignedTo(), "Elevator2")) {
                elev2PassCount++;
            }
        }
        assertEquals(MAX_CAPACITY + 1, elev2PassCount);
        assertEquals(MAX_CAPACITY - 1, elev1PassCount);
    }
}
