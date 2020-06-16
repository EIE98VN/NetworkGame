package edu.hust.soict.huynv.network.packets;

import edu.hust.soict.huynv.network.GameClient;
import edu.hust.soict.huynv.network.GameServer;

public class PacketPlayer extends Packet{

    private String username;
    private int x, y, score, health;

    public PacketPlayer(byte[] data) {
        super(02);
        String[] dataArray = readData(data).split(",");
        this.username = dataArray[0];
        this.x = Integer.parseInt(dataArray[1]);
        this.y = Integer.parseInt(dataArray[2]);
        this.score = Integer.parseInt(dataArray[3]);
        this.health = Integer.parseInt(dataArray[4]);
    }

    public PacketPlayer(int x, int y, String username) {
        super(02);
        this.username = username;
        this.x = x;
        this.y = y;
    }

    public PacketPlayer(String username, int score, int health){
        super(02);
        this.username = username;
        this.score = score;
        this.health = health;
    }

    @Override
    public void writeData(GameClient client) {
        client.sendData(getData());
    }

    @Override
    public void writeData(GameServer server) {
        server.sendDataToAllClients(getData());
    }

    @Override
    public byte[] getData() {
        return ("02" + this.username  + "," + this.x + "," + this.y + "," + this.score + "," + this.health).getBytes();
    }

    public String getUsername() {
        return username;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getScore(){
        return this.score;
    }

    public int getHealth() {
        return this.health;
    }
}
