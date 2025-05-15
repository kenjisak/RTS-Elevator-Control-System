package gui;

import javax.swing.*;
import java.awt.*;
import java.util.*;


public class ElevatorCell extends JFrame{
    private JPanel ElevatorCellMain;
    private JPanel ElevatorImageContainer;
    private JPanel ElevatorFloorInfo;
    private JLabel ElevatorFloorLabel;
    private JLabel ElevatorFloorNumber;
    private JPanel ElevatorPassengerInfo;
    private JLabel ElevatorPassengerLabel;
    private JLabel ElevatorPassengerCount;

    private JPanel ButtonsPressed;
    private JLabel ButtonText;
    private JLabel ButtonArray;
    private JPanel ElevatorName;
    private JLabel NameText;

    private String elevatorName;
    private Set<Integer> buttonsPressed;

    public ElevatorCell (String name) {
        this.elevatorName = name;
        this.buttonsPressed = new TreeSet<>() {};
        setElevatorName(this.elevatorName);
    }

    public JPanel getElevatorPanel() {
        return ElevatorCellMain;
    }
    public void setElevatorName(String s) { NameText.setText(s);}
    public void setFloorNumber(int n) {
        ElevatorFloorNumber.setText(String.valueOf(n));
    }
    public void setPassengerCount(int n) {
        ElevatorPassengerCount.setText(String.valueOf(n));
    }

    public void removeButtonPressed(int n){
        buttonsPressed.remove(n);
    }

    public void addButtonPressed(int n) {
        buttonsPressed.add(n);
    }

    public void renderButtonsPressed(){
        String buttons = String.valueOf(buttonsPressed);;
        Iterator<Integer> i = buttonsPressed.iterator();

        ButtonArray.setText(buttons);
    }

    public void setImageIcon(ImageIcon i){
        JLabel icon = new JLabel();
        icon.setIcon(i);
        ElevatorImageContainer.removeAll();
        ElevatorImageContainer.add(icon);
    }

    public String getElevatorName() {
        return elevatorName;
    }
}
