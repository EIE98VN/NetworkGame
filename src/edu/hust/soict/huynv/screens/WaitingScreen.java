package edu.hust.soict.huynv.screens;

import javax.swing.*;

public class WaitingScreen extends JDialog{

    public WaitingScreen(JFrame frame) {
        JPanel panel = new JPanel();
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.setLocation(frame.getX() + 100, frame.getY() + 400);
        this.setTitle("Please wait");
        panel.add(new JLabel("Waiting for other players ...", SwingConstants.CENTER));
        this.add(panel);
        this.setSize(300, 100);
        this.setVisible(true);
        this.setAlwaysOnTop(true);
    }
}
