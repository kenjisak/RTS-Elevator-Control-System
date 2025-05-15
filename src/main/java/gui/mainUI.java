package gui;

import allSystems.Scheduler;
import otherResources.Algorithm;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static gui.mainUI.iconName.*;
import static otherResources.Constants.*;

public class mainUI extends JFrame{
    private JPanel MainPanel;
    private JPanel SchedulerPanel;
    private JPanel ElevatorPanel;
    private JPanel FloorPanel;
    private JLabel SchedulerLabel;
    private JScrollPane SchedulerOutput;
    private JTextArea textArea;

    private ArrayList<ElevatorCell> elevators;
    private ArrayList<FloorCell> floors;
    private Scheduler scheduler;
    private final int elevatorCount;
    private final int floorCount;

    private HashMap<iconName,ImageIcon> icons;


    enum iconName{
        ELEVATOR_BASE("src/main/resources/Icons/elevator_icon.png"),
        ELEVATOR_DOOR_OPEN("src/main/resources/Icons/elevator_icon_doorOpen.png"),
        ELEVATOR_DOOR_OPENING("src/main/resources/Icons/elevator_icon_opening.png"),
        ELEVATOR_MOVING_DOWN("src/main/resources/Icons/elevator_icon_down.png"),
        ELEVATOR_MOVING_UP("src/main/resources/Icons/elevator_icon_up.png"),
        ELEVATOR_LAMP_ON("src/main/resources/Icons/elevator_icon_lampOn.png"),
        ELEVATOR_OUT_OF_ORDER("src/main/resources/Icons/elevator_icon_outOfOrder.png"),

        FLOOR_BUTTON_BASE("src/main/resources/Icons/floor_buttons.png"),
        FLOOR_BUTTON_UP("src/main/resources/Icons/floor_buttons_up_pressed.png"),
        FLOOR_BUTTON_DOWN("src/main/resources/Icons/floor_buttons_down_pressed.png"),
        FLOOR_BUTTON_UP_DOWN("src/main/resources/Icons/floor_buttons_up&down_pressed.png"),
        FLOOR_FIRST_BASE("src/main/resources/Icons/floor_first_button.png"),
        FLOOR_FIRST_UP("src/main/resources/Icons/floor_first_up_pressed.png"),
        FLOOR_TOP_BASE("src/main/resources/Icons/floor_top_button.png"),
        FLOOR_TOP_DOWN("src/main/resources/Icons/floor_top_button_pressed.png"),

        BELL_ICON_BASE("src/main/resources/Icons/bell_icon.png"),
        BELL_ICON_RING("src/main/resources/Icons/bell_icon_ring.png"),

        DEFAULT("src/main/resources/Icons/default.jpg");

        String pathName;
        iconName(String s) {
            this.pathName = s;
        }
    }

    public mainUI (Scheduler app) {
        this.elevatorCount = MAX_ELEVATORS;
        this.floorCount = MAX_FLOORS;
        this.scheduler = app;
        this.icons = new HashMap<>();
        this.elevators = new ArrayList<>();
        this.floors = new ArrayList<>();
        this.FloorPanel.setLayout(new GridLayout((int) (double) (floorCount / 8),2));

        setupFrame();
        loadImage();

    }

    private void setupFrame(){
        setTitle("SYSC 3303 Project: Elevator Simulator");
        setContentPane(MainPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(800,650);
        setResizable(true);
        setVisible(true);

        for (int i = 0; i < elevatorCount; i++) {
            String elevatorName = "Elevator" + (i + 1);
            ElevatorCell elevatorCell = new ElevatorCell(elevatorName);
            elevatorCell.setFloorNumber(START_FLOOR);
            elevatorCell.setPassengerCount(0);
            this.elevators.add(elevatorCell);
            JPanel elevatorPanel = elevatorCell.getElevatorPanel();
            this.ElevatorPanel.add(elevatorPanel);
            elevatorCell.renderButtonsPressed();
        }

        for (int i = 0; i < floorCount; i++) {
            FloorCell floorCell = new FloorCell(i+1);
            this.floors.add(floorCell);
            JPanel floorPanel = floorCell.getFloorPanel();
            this.FloorPanel.add(floorPanel);
        }

    }

    private void loadImage(){
        for(iconName i : iconName.values()){
            BufferedImage image = null;
            //System.out.println(String.valueOf(ELEVATOR_BASE.pathName));
            try {
                image = ImageIO.read(new File(i.pathName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ImageIcon icon = new ImageIcon(image.getScaledInstance(100, 100, Image.SCALE_DEFAULT));
            icons.put(i,icon);
        }

        setDefault();
        update();
    }

    private void setDefault(){
        for(ElevatorCell current: elevators){
            current.setImageIcon(icons.get(ELEVATOR_BASE));
        }

        int counter = 1;
        for(FloorCell current: floors){
            if(counter == START_FLOOR){
                current.setImageIcon(icons.get(FLOOR_FIRST_BASE));
            } else if (counter == MAX_FLOORS){
                current.setImageIcon(icons.get(FLOOR_TOP_BASE));
            } else {
                current.setImageIcon(icons.get(FLOOR_BUTTON_BASE));
            }
            counter++;
        }
    }

    public void update(){
        HashMap<String, Algorithm> algorithmHashMap =  this.scheduler.getElevatorsSchedules();
        ArrayList<ArrayList<Boolean>> floorButtons = this.scheduler.getFloorButtons();
        HashMap<String, Integer> passengerCount = this.scheduler.getPassengerCount();
        for (ElevatorCell current : elevators) {
            Algorithm currentElevatorAlgorithm = algorithmHashMap.get(current.getElevatorName());
            int currentFloor = currentElevatorAlgorithm.getCurrentFloor();
            boolean isOutOfService = currentElevatorAlgorithm.isOutOfService();
            boolean isUp = currentElevatorAlgorithm.isUp();
            boolean DoorsOpen = currentElevatorAlgorithm.isDoorsOpen();
            boolean ChangingDoors = currentElevatorAlgorithm.isDoorsChanging();
            boolean isIdle = currentElevatorAlgorithm.isIdle();
            current.setPassengerCount(passengerCount.get(current.getElevatorName()));

            if (isUp){
//                System.out.println("UI: Up");
                current.setImageIcon(icons.get(ELEVATOR_MOVING_UP));
            } else {
//                System.out.println("UI: Down");
                current.setImageIcon(icons.get(ELEVATOR_MOVING_DOWN));
            }

            if (DoorsOpen){
//                System.out.println("UI: DoorsOpen");
                current.setImageIcon(icons.get(ELEVATOR_DOOR_OPEN));
            }

            if (ChangingDoors){
//                System.out.println("UI: ChangingDoors");
                current.setImageIcon(icons.get(ELEVATOR_DOOR_OPENING));
            }

            if (isIdle){
//                System.out.println("UI: Idle");
                current.setImageIcon(icons.get(ELEVATOR_DOOR_OPEN));
            }

            if (isOutOfService){
//                System.out.println("UI: OutOfService");
                current.setImageIcon(icons.get(ELEVATOR_OUT_OF_ORDER));
            }
            current.setFloorNumber(currentFloor);
        }

        for (FloorCell current : floors) {
            int floorNumber = current.getFloorNumber()-1;
            boolean isUp = floorButtons.get(floorNumber).get(0);
            boolean isDown = floorButtons.get(floorNumber).get(1);

            if (floorNumber == START_FLOOR-1){
                if (isUp){
                    current.setImageIcon(icons.get(FLOOR_FIRST_UP));
                } else {
                    current.setImageIcon(icons.get(FLOOR_FIRST_BASE));
                }
            } else if (floorNumber == MAX_FLOORS-1){
                if (isDown){
                    current.setImageIcon(icons.get(FLOOR_TOP_DOWN));
                } else {
                    current.setImageIcon(icons.get(FLOOR_TOP_BASE));
                }
            } else {
                if (isUp && isDown) {
                    current.setImageIcon(icons.get(FLOOR_BUTTON_UP_DOWN));
                } else if (isDown) {
                    current.setImageIcon(icons.get(FLOOR_BUTTON_DOWN));
                } else if (isUp) {
                    current.setImageIcon(icons.get(FLOOR_BUTTON_UP));
                } else {
                    current.setImageIcon(icons.get(FLOOR_BUTTON_BASE));
                }
            }
        }

        this.revalidate();
        this.repaint();
    }

    public void updateButtonsPressed(boolean b, int n, String current){
        for(ElevatorCell e : elevators){
            if(e.getElevatorName().equals(current)){
                if(b){
                    e.addButtonPressed(n);
                } else {
                    e.removeButtonPressed(n);
                }
                e.renderButtonsPressed();
                break;
            }
        }
        this.revalidate();
        this.repaint();
    }

    public void log(String text){
        textArea.append(" " + text + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}
