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
    @AfterEach
    public void cleanUp() throws InterruptedException {
        Thread.sleep(500);
    }
    /**
     * Tests if an Elevator is registering with the Mailbox successfully
     */
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
    /**
     * Tests if the Scheduler is registering with the Mailbox successfully
     */
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
    /**
     * Tests if a Floor is registering with the Mailbox successfully
     */
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
    /**
     * Tests if a Message is put() correctly in the intended recipient's(Scheduler) Mailbox
     */
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
    /**
     * Tests if a Message is put() correctly in the intended recipient's(Elevator) Mailbox
     */
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
    /**
     * Tests if a Message is get() properly, either it returns a null if the queue is empty or a Message object
     */
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
    /**
     * Tests if a Message is put() correctly in the intended recipient's Mailbox
     */
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
