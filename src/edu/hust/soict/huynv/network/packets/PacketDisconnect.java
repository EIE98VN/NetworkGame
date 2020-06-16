package edu.hust.soict.huynv.network.packets;

import edu.hust.soict.huynv.network.GameClient;
import edu.hust.soict.huynv.network.GameServer;

public class PacketDisconnect extends Packet{
    public String username;

    public PacketDisconnect(byte[] data) {
        super(01);
        String[] dataArray = readData(data).split(",");
        this.username = dataArray[0];
    }

    public PacketDisconnect(String username) {
        super(01);
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
        return ("01" + this.username).getBytes();
    }
}
