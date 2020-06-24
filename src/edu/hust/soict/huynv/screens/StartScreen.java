package edu.hust.soict.huynv.screens;

import edu.hust.soict.huynv.GenericSpaceShooter;

import javax.swing.*;

public class StartScreen extends JDialog {

    public StartScreen(GenericSpaceShooter gss){
        this.setSize(300, 150);
        this.setLayout(null);
        this.setAlwaysOnTop(true);
        JButton playButton = new JButton("Play game");
        playButton.setVisible(true);
        playButton.setBounds(90, 10, 120, 30);
        playButton.addActionListener(actionEvent -> {
            this.setVisible(false);
            this.dispose();
            gss.isPlayed = true;
        });

        JButton exitButton = new JButton("Exit game");
        exitButton.setVisible(true);
        exitButton.setBounds(105, 50, 90, 30);
        exitButton.addActionListener(actionEvent -> System.exit(0));

        this.add(exitButton);
        this.add(playButton);
        this.setVisible(true);

        this.setLocation(gss.window.getFrame().getX() + 100 , gss.window.getFrame().getY() + 325);
    }

}
