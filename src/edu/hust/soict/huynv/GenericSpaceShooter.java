package edu.hust.soict.huynv;


import com.joshuacrotts.standards.*;
import edu.hust.soict.huynv.entities.PlayerMP;
import edu.hust.soict.huynv.entities.enemies.GreenBat;
import edu.hust.soict.huynv.network.GameClient;
import edu.hust.soict.huynv.network.GameServer;
import edu.hust.soict.huynv.network.packets.PacketEnemy;
import edu.hust.soict.huynv.network.packets.PacketLogin;
import edu.hust.soict.huynv.screens.StartScreen;
import edu.hust.soict.huynv.screens.WaitingScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GenericSpaceShooter extends StandardGame implements Runnable {

    public static GenericSpaceShooterHandler standardHandler;
    public static boolean isServer = false;
    public GameClient socketClient;
    public GameServer socketServer;
    public BufferedImage background = null;
    public int enemyCount = 0;
    public boolean isPlayed = false;
    public static boolean enterGame = false;
    public PlayerMP player;
    private Level level;

    public GenericSpaceShooter(int width, int height, String title) {
        super(width, height, title);

        this.background = StdOps.loadImage("res/bg.png");
        this.consoleFPS = false;
        standardHandler = new GenericSpaceShooterHandler(this);

        this.StartGame();
    }

    public static void main(String[] args) {
        new GenericSpaceShooter(500, 800, "Space Shooter");
    }

    @Override
    public void tick() {
        String endgameMessage = "";
        if(standardHandler.deadPlayers.size() == 2){
            for (PlayerMP player: standardHandler.playerList ) {
                System.out.println(player.getUsername() + " score: " + player.score);
                endgameMessage += "Player " + player.getUsername() + " : " + player.score + "\n";
            }
            JOptionPane.showMessageDialog(this, endgameMessage, "Result", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
        //only server generates enemy
        if (isServer) {
            System.out.println(GenericSpaceShooter.standardHandler.getEntities().size());
            if (GenericSpaceShooter.standardHandler.getEntities().size() < level.enemyNumber){
                enemyCount++;
                GreenBat greenBat = new GreenBat("enemy" + enemyCount, StdOps.rand(50, 450), StdOps.rand(-500, -50), level.enemyVelY, this);
                PacketEnemy enemyPacket = new PacketEnemy(greenBat.name, (int) greenBat.x, (int) greenBat.y, PacketEnemy.ADD, (int) greenBat.velY);
                enemyPacket.writeData(this.socketClient);
                GenericSpaceShooter.standardHandler.getEntities().add(greenBat);
            }
        }
        StandardHandler.Handler(standardHandler);
    }

    @Override
    public void render() {
        StandardDraw.image(this.background, 0, 0);
        StandardDraw.Handler(standardHandler);
    }


    @Override
    public void StartGame() {

        StartScreen startScreen = new StartScreen(this);

        while(!isPlayed){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        player = new PlayerMP(200, 800, this, JOptionPane.showInputDialog(this, "Please enter a username"));

        standardHandler.getEntities().add(player);
        standardHandler.playerList.add(player);
        standardHandler.playerName = player.getUsername();
        this.addListener(player);

        if (JOptionPane.showConfirmDialog(this, "Do you want to run the server ?") == 0) {
            isServer = true;
            socketServer = new GameServer(this);
            socketServer.start();

            String[] options = {"Easy", "Medium", "Hard"};
            int choice = JOptionPane.showOptionDialog(this, "Please choose a game level",
                    "Choose game difficulty",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            level = new Level(choice);
            level.start();
        }

        socketClient = new GameClient(this, "192.168.43.58");
        socketClient.start();

        PacketLogin loginPacket = new PacketLogin(player.getUsername(), (int) player.x, (int) player.y);
        if (socketServer != null) {
            socketServer.connectedPlayers.add(player);
            socketServer.handleConnection((PlayerMP) player, loginPacket);
        }
        loginPacket.writeData(socketClient);

        WaitingScreen waitingScreen = new WaitingScreen(this.window.getFrame());

        GenericSpaceShooter gss = this;
        this.window.getFrame().addComponentListener(new ComponentAdapter() {

            Point lastLocation = gss.window.getFrame().getLocation();;
            @Override
            public void componentMoved(ComponentEvent e) {
                if (lastLocation == null && gss.window.getFrame().isVisible()) {
                    lastLocation = gss.window.getFrame().getLocation();
                } else {
                    Point newLocation = gss.window.getFrame().getLocation();
                    int dx = newLocation.x - lastLocation.x;
                    int dy = newLocation.y - lastLocation.y;
                    waitingScreen.setLocation(waitingScreen.getX() + dx, waitingScreen.getY() + dy);
                    lastLocation = newLocation;
                }
            }
        });

        while (standardHandler.playerList.size() < 2) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(standardHandler.playerList.size()==1 && !isServer){
                waitingScreen.dispose();
                JOptionPane.showMessageDialog(this, "Number of players is full !");
                System.exit(0);
            }
        }

        waitingScreen.dispose();

        enterGame = true;
        super.StartGame();
    }
}
