package edu.hust.soict.huynv.network;

import edu.hust.soict.huynv.GenericSpaceShooter;
import edu.hust.soict.huynv.network.packets.Packet;
import edu.hust.soict.huynv.entities.PlayerMP;
import edu.hust.soict.huynv.network.packets.PacketControl;
import edu.hust.soict.huynv.network.packets.PacketDisconnect;
import edu.hust.soict.huynv.network.packets.PacketLogin;

import java.io.IOException;
import java.net.*;

public class GameClient extends Thread {

    private InetAddress ipAddress;
    private DatagramSocket socket;
    private GenericSpaceShooter gss;

    public GameClient(GenericSpaceShooter gss, String ipAddress) {
        this.gss = gss;
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
        while (true) {
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
//                packet = new PacketDisconnect(data);
//                System.out.println("[" + address.getHostAddress() + ":" + port + "] "
//                        + ((PacketDisconnect) packet).getUsername() + " has left the world...");
//                game.level.removePlayerMP(((PacketDisconnect) packet).getUsername());
                break;
            case CONTROL:
//                packet = new PacketControl(data);
//                handleMove((PacketControl) packet);
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
        System.out.println("[" + address.getHostAddress() + ":" + port + "] " + packet.getUsername()
                + " has joined the game...");
        PlayerMP player = new PlayerMP(packet.getX(), packet.getY(), this.gss, packet.getUsername(), address, port);
        GenericSpaceShooter.standardHandler.addEntity(player);
    }

//    private void handleMove(Packet02Move packet) {
//        this.game.level.movePlayer(packet.getUsername(), packet.getX(), packet.getY(), packet.getNumSteps(),
//                packet.isMoving(), packet.getMovingDir());
//    }

}
