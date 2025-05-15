import allSystems.Elevator;
import allSystems.Floor;
import allSystems.Mailbox;
import allSystems.Scheduler;
import org.junit.jupiter.api.*;
import otherResources.Message;
import udp.UDP;

import static org.junit.jupiter.api.Assertions.*;
import static otherResources.Constants.*;
import static otherResources.Constants.MAILIP;

public class MailboxTests {
    int testPort = 50000;
    /*
    Mailbox mailbox;
    Thread mail;
    String testFilePath = "../input1Line.txt";
    @BeforeEach
    public void setUp() throws InterruptedException {
        mailbox = new Mailbox();
        mail = new Thread(mailbox);

        mail.start();
        Thread.sleep(10);//Wait for Mailbox to spawn properly
    }
    @AfterEach
    public void cleanUp() throws InterruptedException {
        mail.interrupt();
        mailbox.getUdp().closePorts();
    }
    @Test
    @DisplayName("Mailbox receives Message from Floor")
    public void testFloorMsg() throws InterruptedException {
//        UDP udp = new UDP(3000);
//        udp.register("Scheduler");
        Floor floor = new Floor(testFilePath,4);
        Scheduler scheduler = new Scheduler();
        Elevator elevator = new Elevator(1);

        Thread flrThread = new Thread(floor,"Floor 4");
        Thread schedulerThread = new Thread(scheduler,"Scheduler");
        Thread elevThread = new Thread(elevator,"Elevator 1");


        schedulerThread.start();
        elevThread.start();
        flrThread.start();

        Thread.sleep(100);//timing allowing messages to send between systems

        assertEquals(0,0);


        //clean up after test
        schedulerThread.interrupt();
        Thread.sleep(5);//let thread die, before closing ports
        scheduler.getUdp().closePorts();

        elevThread.interrupt();
        Thread.sleep(5);//let thread die, before closing ports
        elevator.getUdp().closePorts();

        flrThread.interrupt();
        Thread.sleep(5);//let thread die, before closing ports
        floor.getUdp().closePorts();


//        udp.closePort();
    }

    @Test
    @DisplayName("Mailbox receives Message from Floor")
    public void testFlojjorMsg() throws InterruptedException {
//        UDP udp = new UDP(3000);
//        udp.register("Scheduler");
        Floor floor = new Floor(testFilePath,4);
        Scheduler scheduler = new Scheduler();
        Elevator elevator = new Elevator(1);

        Thread flrThread = new Thread(floor,"Floor 4");
        Thread schedulerThread = new Thread(scheduler,"Scheduler");
        Thread elevThread = new Thread(elevator,"Elevator 1");


        schedulerThread.start();
        elevThread.start();
        flrThread.start();

        Thread.sleep(100);//timing allowing messages to send between systems

        assertEquals(0,0);


        //clean up after test
        schedulerThread.interrupt();
        Thread.sleep(5);//let thread die, before closing ports
        scheduler.getUdp().closePorts();

        elevThread.interrupt();
        Thread.sleep(5);//let thread die, before closing ports
        elevator.getUdp().closePorts();

        flrThread.interrupt();
        Thread.sleep(5);//let thread die, before closing ports
        floor.getUdp().closePorts();

//        udp.closePort();
    }*/
    @AfterEach
    public void cleanUp() throws InterruptedException {
        Thread.sleep(500);
    }
    @Test
    @DisplayName("Registers with Mailbox: Elevator")
    void testRegisterElev() throws InterruptedException {
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        UDP udp = new UDP(testPort);
        udp.register("Elevator1");
        Thread.sleep(50);//timing to wait for registering

        assertTrue(mailbox.getMessageQueueMap().containsKey("Elevator1"));
        mail.interrupt();
        mailbox.getUdp().closePorts();
        udp.closePorts();
    }

    @Test
    @DisplayName("Registers with Mailbox: Scheduler")
    void testRegisterScheduler() throws InterruptedException {
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        UDP udp = new UDP(testPort);
        udp.register("Scheduler");
        Thread.sleep(50);//timing to wait for registering

        assertTrue(mailbox.getMessageQueueMap().containsKey("Scheduler"));
        mail.interrupt();
        mailbox.getUdp().closePorts();
        udp.closePorts();
    }
    @Test
    @DisplayName("Registers with Mailbox: Floor")
    void testRegisterFloor() throws InterruptedException {
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();

        UDP udp = new UDP(testPort);
        udp.register("Floor1");
        Thread.sleep(50);//timing to wait for registering

        assertTrue(mailbox.getMessageQueueMap().containsKey("Floor1"));
        mail.interrupt();
        mailbox.getUdp().closePorts();
        udp.closePorts();
    }
    @Test
    @DisplayName("Message placed correctly in recipient Mailbox: Scheduler")
    void testMessageSendingScheduler() throws InterruptedException {
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();
        Thread.sleep(5);//Wait for Mailbox to spawn properly

        UDP udp = new UDP(testPort);
        udp.register("Scheduler");
        Thread.sleep(50);//timing to wait for registering

        Floor floor = new Floor("/testinput1Line.txt",4);
        Thread flrThread = new Thread(floor,"Floor 4");
        flrThread.start();
        Thread.sleep(100);//timing to send floor

        assertEquals(1, mailbox.getMessageQueueMap().get("Scheduler").size());


        mail.interrupt();
        mailbox.getUdp().closePorts();

        flrThread.interrupt();
        Thread.sleep(5);//let thread die, before closing ports
        floor.getUdp().closePorts();

        udp.closePorts();
    }
    @Test
    @DisplayName("Message placed correctly in recipient Mailbox: Elevator")
    void testMessageSendingElevator() throws InterruptedException {
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();
        Thread.sleep(5);//Wait for Mailbox to spawn properly

        UDP schedTest = new UDP(SCHEDPORT);
        schedTest.register("Scheduler");
        UDP elevTest= new UDP(BASEELEVPORT + 1);
        elevTest.register("Elevator1");
        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message m = new Message("13:50:33.29 1 up 1 1");
        m.setToWho("Elevator1");
        m.setFromWho("Scheduler");
        schedTest.sendMessage(m,MAILPORT,MAILIP);

        Thread.sleep(50);//timing to send message
        System.out.println(mailbox.getMessageQueueMap());
        assertEquals(1, mailbox.getMessageQueueMap().get("Elevator1").size());

        //clean up
        mail.interrupt();
        mailbox.getUdp().closePorts();

        schedTest.closePorts();
        elevTest.closePorts();
    }
    @Test
    @DisplayName("Mailbox Get message returns a real message or null")
    void testMailboxGet() throws InterruptedException {
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();
        Thread.sleep(5);//Wait for Mailbox to spawn properly

        UDP schedTest = new UDP(SCHEDPORT);
        schedTest.register("Scheduler");
        Thread.sleep(50);//timing to wait for registering

        //rig and test
        Message m = new Message("13:50:33.29 1 up 1 1");
        m.setToWho("Scheduler");
        m.setFromWho("Scheduler");
        schedTest.sendMessage(m,MAILPORT,MAILIP);

        Thread.sleep(50);//timing to send message
        System.out.println(mailbox.getMessageQueueMap());
        Message testMessage = mailbox.get("Scheduler");
        assertEquals(m,testMessage);

        Message testMessageNull = mailbox.get("Scheduler");
        assertNull(testMessageNull);

        //clean up
        mail.interrupt();
        mailbox.getUdp().closePorts();

        schedTest.closePorts();
    }
    @Test
    @DisplayName("Mailbox Put message places message in correct recipient")
    void testMailboxPut() throws InterruptedException {
        //set up
        Mailbox mailbox = new Mailbox();
        Thread mail = new Thread(mailbox,"Mail");
        mail.start();
        Thread.sleep(5);//Wait for Mailbox to spawn properly

        mailbox.register("Elevator1");
        Thread.sleep(50);//timing to wait for registering
        //rig and test

        Message m = new Message("13:50:33.29 1 up 1 1");
        m.setToWho("Elevator1");
        m.setFromWho("Scheduler");
        mailbox.put(m);

        Thread.sleep(50);//timing to put message
        System.out.println(mailbox.getMessageQueueMap());
        Message testMessage = mailbox.get("Elevator1");
        assertEquals(m,testMessage);

        //clean up
        mail.interrupt();
        mailbox.getUdp().closePorts();
    }
}
