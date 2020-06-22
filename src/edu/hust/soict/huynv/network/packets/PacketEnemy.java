package edu.hust.soict.huynv.network.packets;

import edu.hust.soict.huynv.network.GameClient;
import edu.hust.soict.huynv.network.GameServer;

public class PacketEnemy extends Packet {

    public int x,y;
    public String name, behaviour;
    public int velY;
    public final static String REMOVE = "remove";
    public final static String ADD = "add";

    public PacketEnemy(byte[] data) {
        super(03);
        String[] dataArray = readData(data).split(",");
        this.name = dataArray[0];
        this.x = Integer.parseInt(dataArray[1]);
        this.y = Integer.parseInt(dataArray[2]);
        this.behaviour = dataArray[3];
        this.velY = Integer.parseInt(dataArray[4]);
    }

    public PacketEnemy(String name, int x, int y, String behaviour, int velY){
        super(03);
        this.name = name;
        this.x = x;
        this.y =y;
        this.behaviour = behaviour;
        this.velY = velY;
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
        return ("03" + this.name + "," + this.x + "," + this.y + "," + this.behaviour + "," + this.velY).getBytes();
    }
}
