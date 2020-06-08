package edu.hust.soict.huynv.network.packets;

import com.joshuacrotts.standards.StandardID;
import edu.hust.soict.huynv.network.GameClient;
import edu.hust.soict.huynv.network.GameServer;

public class PacketBullet extends Packet {

    private int x, y, velY;
    private String id, username;

    public PacketBullet(byte[] data) {
        super(04);
        String[] dataArray = readData(data).split(",");
        this.x = Integer.parseInt(dataArray[0]);
        this.y = Integer.parseInt(dataArray[1]);
        this.velY = Integer.parseInt(dataArray[2]);
        this.id = dataArray[3];
        this.username = dataArray[4];
    }

    public PacketBullet(int x, int y, int velY, String id, String username){
        super(04);
        this.x = x;
        this.y = y;
        this.velY = velY;
        this.id = id;
        this.username = username;
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
        return ("04" + this.x + "," + this.y + "," + this.velY + "," + this.id + "," + this.username).getBytes();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getVelY() {
        return velY;
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }
}
