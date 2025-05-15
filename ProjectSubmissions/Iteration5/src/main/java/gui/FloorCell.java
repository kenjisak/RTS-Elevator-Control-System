package gui;

import javax.swing.*;
import java.awt.*;

public class FloorCell {
    private JPanel FloorCellMain;
    private JPanel FloorTitle;
    private JPanel FloorImageContainer;
    private JLabel FloorNumberLabel;
    private JLabel FloorNumber;
    private int floorNumber;

    public FloorCell(int floorNumber) {
        this.floorNumber = floorNumber;
        FloorNumber.setText(String.valueOf(floorNumber));
    }

    public JPanel getFloorPanel() {
        return FloorCellMain;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setImageIcon(ImageIcon i){
        Image image = i.getImage(); // transform it
        Image newimg = image.getScaledInstance(50, 50,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        i = new ImageIcon(newimg);  // transform it back

        JLabel icon = new JLabel();
        icon.setIcon(i);
        FloorImageContainer.removeAll();
        FloorImageContainer.add(icon);
    }
}
