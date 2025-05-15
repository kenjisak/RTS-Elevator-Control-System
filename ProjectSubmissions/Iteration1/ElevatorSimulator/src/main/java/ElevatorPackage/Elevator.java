package ElevatorPackage;

import ButtonPackage.CarFloorNumberButton;
import ButtonPackage.DoorInterruptButton;
import primary.EntityType;
import primary.Message;
import primary.Messaging;

import java.util.ArrayList;

public class Elevator implements Runnable{
    private final int carNum;
    private DoorInterruptButton openDoor,closeDoor;
    private CarFloorNumberButton[] flrBtns;

    private ArrayList<Message> msgsReceived;
    //TODO: Door object? with states opened, closing(for interrupt), closed
    public Elevator(int carNum){
        this.carNum = carNum;

        this.openDoor = new DoorInterruptButton("open");
        this.closeDoor = new DoorInterruptButton("close");

        msgsReceived = new ArrayList<>();

    }


    @Override
    public void run(){
        System.out.println(Thread.currentThread().getName() + " has started.");
           while (true){
               Message m = Messaging.getInstance().get(EntityType.ELEVATOR);
               if (m != null) {
                   msgsReceived.add(m);
                   System.out.println("Elevator received message from " + m.getFromWho() + ", sending message elevator -> scheduler");
                   m.print();
                //make a new object so that the metadata fromWho and toWho is not overwritten   
                   Message elMessage = new Message(m);
                   elMessage.setFromWho(EntityType.ELEVATOR);
                   elMessage.setToWho(EntityType.SCHED);
                   Messaging.getInstance().put(elMessage);

                   System.out.println(msgsReceived.size());
                   return;
               }
           }
        }
    public ArrayList<Message> getMsgsReceived(){ return msgsReceived; }
}
