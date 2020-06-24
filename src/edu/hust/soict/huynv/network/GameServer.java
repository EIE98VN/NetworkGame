package edu.hust.soict.huynv.network;

import com.joshuacrotts.standards.StandardGameObject;
import edu.hust.soict.huynv.GenericSpaceShooter;
import edu.hust.soict.huynv.entities.PlayerMP;
import edu.hust.soict.huynv.entities.enemies.GreenBat;
import edu.hust.soict.huynv.network.packets.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

public class GameServer extends Thread {

    public List<PlayerMP> connectedPlayers = new ArrayList<>();
    public DatagramSocket socket;
    public boolean runnable;
    private GenericSpaceShooter gss;

    public GameServer(GenericSpaceShooter gss) {
        this.gss = gss;
        this.runnable = true;
        try {
            this.socket = new DatagramSocket(1331);
        } catch (SocketException e) {
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
                if(this.connectedPlayers.size() == 3)
                    break;
                packet = new PacketLogin(data);
                System.out.println("[" + address.getHostAddress() + ":" + port + "] "
                        + ((PacketLogin) packet).getUsername() + " has connected...");
                PlayerMP player = new PlayerMP(300, 800, gss, ((PacketLogin) packet).getUsername(), address, port);
                this.handleConnection(player, (PacketLogin) packet);
                break;
            case DISCONNECT:
                packet = new PacketDisconnect(data);
                handleDisconnect((PacketDisconnect) packet);
//                System.out.println("[" + address.getHostAddress() + ":" + port + "] "
//                        + ((Packet01Disconnect) packet).getUsername() + " has left...");
//                this.removeConnection((Packet01Disconnect) packet);
                break;
            case PLAYER:
                packet = new PacketPlayer(data);
                this.handlePlayer((PacketPlayer) packet);
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

    private void handleEnemy(PacketEnemy packet) {
        packet.writeData(this);
        if (packet.name != null) {
            GreenBat greenBat = getEnemy(packet.name);
            if (greenBat != null) {
                greenBat.health = 0;
            }
        }
    }

    public void handleConnection(PlayerMP player, PacketLogin packet) {
        boolean alreadyConnected = false;
        for (PlayerMP p : this.connectedPlayers) {
            if (player.getUsername().equalsIgnoreCase(p.getUsername())) {
                if (p.ipAddress == null) {
                    p.ipAddress = player.ipAddress;
                }
                if (p.port == -1) {
                    p.port = player.port;
                }
                alreadyConnected = true;
            } else {
                // relay to the current connected player that there is a new player
                System.out.println("Send " + packet.getUsername() + " to " + p.getUsername());
                sendData(packet.getData(), p.ipAddress, p.port);

                // relay to the new player that the currently connect player exists
                PacketLogin currentPlayerPacket = new PacketLogin(p.getUsername(), (int) p.x, (int) p.y);
                sendData(currentPlayerPacket.getData(), player.ipAddress, player.port);
            }
        }
        if (!alreadyConnected) {
            this.connectedPlayers.add(player);
            GenericSpaceShooter.standardHandler.getEntities().add(player);
            GenericSpaceShooter.standardHandler.playerList.add(player);
        }
    }

//    public void removeConnection(Packet01Disconnect packet) {
//        this.connectedPlayers.remove(getPlayerMPIndex(packet.getUsername()));
//        packet.writeData(this);
//    }

    public PlayerMP getPlayerMP(String username) {
        for (PlayerMP player : this.connectedPlayers) {
            if (player.getUsername().equals(username)) {
                return player;
            }
        }
        return null;
    }

    public int getPlayerMPIndex(String username) {
        int index = 0;
        for (PlayerMP player : this.connectedPlayers) {
            if (player.getUsername().equals(username)) {
                break;
            }
            index++;
        }
        return index;
    }

    public void sendData(byte[] data, InetAddress ipAddress, int port) {

        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
        try {
            this.socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendDataToAllClients(byte[] data) {
        for (PlayerMP p : connectedPlayers) {
            sendData(data, p.ipAddress, p.port);
        }
    }

    private void handlePlayer(PacketPlayer packet) {
        if (getPlayerMP(packet.getUsername()) != null) {
            if (!packet.getUsername().equals(GenericSpaceShooter.standardHandler.playerList.get(0).getUsername())) {
                GenericSpaceShooter.standardHandler.handlePlayer(packet);
            }
            packet.writeData(this);
        }
    }


    private GreenBat getEnemy(String name) {
        Iterator<StandardGameObject> iterator =  GenericSpaceShooter.standardHandler.getEntities().iterator();

        try {
            while (iterator.hasNext()) {
                StandardGameObject gameObject = iterator.next();
                if (gameObject instanceof GreenBat && ((GreenBat) gameObject).name.equals(name)) {
                    return (GreenBat) gameObject;
                }
            }
        }catch (ConcurrentModificationException ex){
            System.out.println("ConcurrentModificationException on getEnemy");
        }

        return null;
    }

    private void handleBullet(PacketBullet packet) {
        GenericSpaceShooter.standardHandler.handleBullet(packet);
        packet.writeData(this);
    }

    private void handleDisconnect(PacketDisconnect packet){
        GenericSpaceShooter.standardHandler.handleDisconnect(packet.username);
        packet.writeData(this);
    }
}
