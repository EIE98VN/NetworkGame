package edu.hust.soict.huynv.network.packets;

import edu.hust.soict.huynv.network.GameClient;
import edu.hust.soict.huynv.network.GameServer;

public class PacketControl extends Packet{

    private String username;
    private int x, y;

    public PacketControl(byte[] data) {
        super(02);
        String[] dataArray = readData(data).split(",");
        this.username = dataArray[0];
        this.x = Integer.parseInt(dataArray[1]);
        this.y = Integer.parseInt(dataArray[2]);
    }

    public PacketControl(String username, int x, int y) {
        super(02);
        this.username = username;
        this.x = x;
        this.y = y;
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
        return ("02" + this.username + "," + this.x + "," + this.y).getBytes();
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
}
