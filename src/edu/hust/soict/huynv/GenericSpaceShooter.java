package edu.hust.soict.huynv;


import com.joshuacrotts.standards.StandardDraw;
import com.joshuacrotts.standards.StandardGame;
import com.joshuacrotts.standards.StandardHandler;
import com.joshuacrotts.standards.StdOps;
import edu.hust.soict.huynv.entities.PlayerMP;
import edu.hust.soict.huynv.entities.enemies.GreenBat;
import edu.hust.soict.huynv.network.GameClient;
import edu.hust.soict.huynv.network.GameServer;
import edu.hust.soict.huynv.network.packets.PacketEnemy;
import edu.hust.soict.huynv.network.packets.PacketLogin;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class GenericSpaceShooter extends StandardGame implements Runnable {

    public static GenericSpaceShooterHandler standardHandler;
    public static int score = 0;
    public static boolean isServer = false;
    public GameClient socketClient;
    public GameServer socketServer;
    public BufferedImage background = null;
    public int enemyCount = 0;

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
//        System.out.println(standardHandler.deadPlayers.size());
        if(standardHandler.deadPlayers.size() == 3){
            for (PlayerMP player: standardHandler.playerList ) {
                System.out.println(player.getUsername() + " score: " + player.score);
                endgameMessage += "Player " + player.getUsername() + " : " + player.score + "\n";
            }
            JOptionPane.showMessageDialog(this, endgameMessage);
            System.exit(0);
        }
        //only server generates enemy
        if ((GenericSpaceShooter.standardHandler.getEntities().size() < 10) && isServer) {

            enemyCount++;
//            System.out.println("Send enemy"+enemyCount);
            GreenBat greenBat = new GreenBat("enemy" + enemyCount, StdOps.rand(50, 450), StdOps.rand(-500, -50), this);
            PacketEnemy enemyPacket = new PacketEnemy(greenBat.name, (int) greenBat.x, (int) greenBat.y, PacketEnemy.ADD);
            enemyPacket.writeData(this.socketClient);
            GenericSpaceShooter.standardHandler.getEntities().add(greenBat);
        }
        StandardHandler.Handler(standardHandler);
    }

    @Override
    public void render() {
        StandardDraw.image(this.background, 0, 0);
        StandardDraw.Handler(standardHandler);
    }


    @Override
    public synchronized void StartGame() {
        PlayerMP player = new PlayerMP(200, 800, this, JOptionPane.showInputDialog(this, "Please enter a username"));

        standardHandler.getEntities().add(player);
        standardHandler.playerList.add(player);
        standardHandler.playerName = player.getUsername();
        this.addListener(player);

        if (JOptionPane.showConfirmDialog(this, "Do you want to run the server ?") == 0) {
            isServer = true;
            socketServer = new GameServer(this);
            socketServer.start();
        }

        socketClient = new GameClient(this, "localhost");
        socketClient.start();

        PacketLogin loginPacket = new PacketLogin(player.getUsername(), (int) player.x, (int) player.y);
        if (socketServer != null) {
            socketServer.connectedPlayers.add(player);
            socketServer.handleConnection((PlayerMP) player, loginPacket);
        }
        loginPacket.writeData(socketClient);

        JDialog waitingDialog = new JDialog(this.window.getFrame());
        waitingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        waitingDialog.setLocationRelativeTo(this.window.getFrame());
        waitingDialog.add(new JLabel("Waiting for other player"));
        waitingDialog.setSize(300, 100);
        waitingDialog.setVisible(true);
        while (standardHandler.playerList.size() < 3) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        waitingDialog.dispose();

        super.StartGame();
    }

}
