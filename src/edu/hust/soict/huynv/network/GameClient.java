package edu.hust.soict.huynv.network;

import edu.hust.soict.huynv.GenericSpaceShooter;
import edu.hust.soict.huynv.entities.PlayerMP;
import edu.hust.soict.huynv.network.packets.*;

import java.io.IOException;
import java.net.*;

public class GameClient extends Thread {

    private InetAddress ipAddress;
    public DatagramSocket socket;
    public boolean runnable;
    private GenericSpaceShooter gss;

    public GameClient(GenericSpaceShooter gss, String ipAddress) {
        this.gss = gss;
        this.runnable = true;
        try {
            this.socket = new DatagramSocket();
            this.ipAddress = InetAddress.getByName(ipAddress);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (runnable) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
        }
    }

    private void parsePacket(byte[] data, InetAddress address, int port) {
        String message = new String(data).trim();
        Packet.PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
        Packet packet = null;
        switch (type) {
            default:
            case INVALID:
                break;
            case LOGIN:
                packet = new PacketLogin(data);
                handleLogin((PacketLogin) packet, address, port);
                break;
            case DISCONNECT:
                packet = new PacketDisconnect(data);
                handleDisconnect((PacketDisconnect) packet);
                break;
            case PLAYER:
                packet = new PacketPlayer(data);
                handlePlayer((PacketPlayer) packet);
                break;
            case ENEMY:
                packet = new PacketEnemy(data);
                this.handleEnemy((PacketEnemy) packet);
                break;
            case BULLET:
                packet = new PacketBullet(data);
                this.handleBullet((PacketBullet) packet);
        }
    }


    public void sendData(byte[] data) {

        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, 1331);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleLogin(PacketLogin packet, InetAddress address, int port) {
        if(GenericSpaceShooter.standardHandler.playerList.get(0).getUsername().equals(packet.getUsername()))
            return;
        System.out.println("[" + address.getHostAddress() + ":" + port + "] " + packet.getUsername()
                + " has joined the game...");
        PlayerMP player = new PlayerMP(packet.getX(), packet.getY(), this.gss, packet.getUsername(), address, port);
        GenericSpaceShooter.standardHandler.getEntities().add(player);
        GenericSpaceShooter.standardHandler.playerList.add(player);
    }

    private void handlePlayer(PacketPlayer packet) {
        if(packet.getUsername().equals(GenericSpaceShooter.standardHandler.playerList.get(0).getUsername()) && packet.getX()!=0)
            return;
        GenericSpaceShooter.standardHandler.handlePlayer(packet);
    }

    private void handleEnemy(PacketEnemy packet){
        GenericSpaceShooter.standardHandler.handleEnemy(packet.name, packet.x, packet.y, packet.behaviour, packet.velY);
    }

    private void handleBullet(PacketBullet packet) {
        if(packet.getUsername().equals(GenericSpaceShooter.standardHandler.playerList.get(0).getUsername()))
            return;
        GenericSpaceShooter.standardHandler.handleBullet(packet);
    }

    private void handleDisconnect(PacketDisconnect packet){
        GenericSpaceShooter.standardHandler.handleDisconnect(packet.username);
    }
}
