package edu.hust.soict.huynv;


import com.joshuacrotts.standards.StandardDraw;
import com.joshuacrotts.standards.StandardGame;
import com.joshuacrotts.standards.StandardHandler;
import com.joshuacrotts.standards.StdOps;
import edu.hust.soict.huynv.entities.PlayerMP;
import edu.hust.soict.huynv.entities.enemies.GreenBat;
import edu.hust.soict.huynv.entities.Player;
import edu.hust.soict.huynv.network.GameClient;
import edu.hust.soict.huynv.network.GameServer;
import edu.hust.soict.huynv.network.packets.PacketLogin;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class GenericSpaceShooter extends StandardGame implements Runnable{

    public static StandardHandler standardHandler;

    public GameClient socketClient;
    public GameServer socketServer;

    public BufferedImage background = null ;

    public static int score = 0;

    public GenericSpaceShooter(int width, int height, String title) {
        super(width, height, title);

        this.background = StdOps.loadImage("E:\\Term8\\Network Programming\\SpaceShooter01\\res\\bg.png");
        this.consoleFPS = false;
        standardHandler = new GenericSpaceShooterHandler();


        this.StartGame();
    }

    @Override
    public void tick() {
        if(GenericSpaceShooter.standardHandler.size() < 20)
            GenericSpaceShooter.standardHandler.addEntity(new GreenBat(StdOps.rand(0, 1200), StdOps.rand(-500, -50)));
        StandardHandler.Handler(standardHandler);
//        GenericSpaceShooter.score++;
    }

    @Override
    public void render() {
        StandardDraw.image(this.background, 0,0);
        StandardDraw.Handler(standardHandler);
    }

    public static void main(String[] args) {
        new GenericSpaceShooter(1200 , 800, "Space Shooter");
    }

    @Override
    public synchronized void StartGame() {
        Player player = new PlayerMP(300, 800, this,  JOptionPane.showInputDialog(this, "Please enter a username"));

        standardHandler.addEntity(player);
        this.addListener(player);

        if (JOptionPane.showConfirmDialog(this, "Do you want to run the server") == 0) {
            socketServer = new GameServer(this);
            socketServer.start();
        }

        socketClient = new GameClient(this, "localhost");
        socketClient.start();

        super.StartGame();

        PacketLogin loginPacket = new PacketLogin(player.getUsername(), (int) player.x, (int) player.y);
        if (socketServer != null) {
            socketServer.addConnection((PlayerMP) player, loginPacket);
        }
        loginPacket.writeData(socketClient);
    }

}
