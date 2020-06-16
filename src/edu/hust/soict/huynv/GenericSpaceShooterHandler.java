package edu.hust.soict.huynv;

import com.joshuacrotts.standards.StandardGameObject;
import com.joshuacrotts.standards.StandardHandler;
import com.joshuacrotts.standards.StandardID;
import edu.hust.soict.huynv.entities.Bullet;
import edu.hust.soict.huynv.entities.PlayerMP;
import edu.hust.soict.huynv.entities.enemies.GreenBat;
import edu.hust.soict.huynv.network.packets.PacketBullet;
import edu.hust.soict.huynv.network.packets.PacketDisconnect;
import edu.hust.soict.huynv.network.packets.PacketEnemy;
import edu.hust.soict.huynv.network.packets.PacketPlayer;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class GenericSpaceShooterHandler extends StandardHandler {

    public ArrayList<PlayerMP> playerList = new ArrayList<>();
    private GenericSpaceShooter gss;
    public String playerName;

    public GenericSpaceShooterHandler(GenericSpaceShooter gss) {
        this.gss = gss;
        this.entities = new ArrayList<StandardGameObject>();

        GenericSpaceShooterHandler me = this;
        this.gss.window.getFrame().addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                PacketDisconnect packetDisconnect = new PacketDisconnect(me.playerName);
                packetDisconnect.writeData(gss.socketClient);
            }
        });
    }

    public void tick() {
        for (int i = 0; i < this.getEntities().size(); i++) {

            if (gss.isServer) {
                //Player and Enemy collision
                if (this.getEntities().get(i).getId() == StandardID.Player) {

                    for (int j = 0; j < this.getEntities().size(); j++) {


                        if ((this.getEntities().get(j).getId() == StandardID.Obstacle || this.getEntities().get(j).getId() == StandardID.Enemy) &&
                                this.getEntities().get(j).getBounds().intersects(this.getEntities().get(i).getBounds())) {

                            PlayerMP player = (PlayerMP) this.getEntities().get(i);
                            player.health -= 20;
                            PacketPlayer packetPlayer = new PacketPlayer(player.getUsername(), player.score, (int) player.health);
                            packetPlayer.writeData(gss.socketClient);

                            GreenBat greenBat = (GreenBat) this.getEntities().get(j);
                            PacketEnemy packetEnemy = new PacketEnemy(greenBat.name, (int) greenBat.x, (int) greenBat.y, PacketEnemy.REMOVE);
                            packetEnemy.writeData(gss.socketClient);

                            this.getEntities().remove(j);

//                        j--;

                        }

                    }

                }

                //Player's bullet and Enemy collision
                if (this.getEntities().get(i).getId() == StandardID.Weapon) {

                    Bullet bullet = (Bullet) this.getEntities().get(i);

                    for (int j = 0; j < this.getEntities().size(); j++) {
                        if (this.getEntities().get(j).getId() == StandardID.Enemy &&
                                this.getEntities().get(j).getBounds().intersects(this.getEntities().get(i).getBounds())) {

                            GreenBat greenBat = (GreenBat) this.getEntities().get(j);
                            greenBat.health -= 20;

                            if (this.getEntities().get(j) == null)
                                continue;

                            if (this.getEntities().get(j).health <= 0) {
                                PlayerMP player = (PlayerMP) playerList.get(getPlayerMPIndex(bullet.getUsername()));
                                player.score++;
                                PacketPlayer packetPlayer = new PacketPlayer(player.getUsername(), player.score, (int) player.health);
                                packetPlayer.writeData(gss.socketClient);

                                PacketEnemy packetEnemy = new PacketEnemy(greenBat.name, (int) greenBat.x, (int) greenBat.y, PacketEnemy.REMOVE);
                                packetEnemy.writeData(gss.socketClient);
                            }
                        }


                    }
                }
            }
            this.getEntities().get(i).tick();
        }
    }

    public void render(Graphics2D graphics2D) {
        for (int i = 0; i < this.getEntities().size(); i++) {
            this.getEntities().get(i).render(graphics2D);
        }
    }

    private int getPlayerMPIndex(String username) {
        int index = 0;
        for (StandardGameObject e : getEntities()) {
            if (e instanceof PlayerMP && ((PlayerMP) e).getUsername().equals(username)) {
                break;
            }
            index++;
        }
        return index;
    }

    public void handlePlayer(PacketPlayer packet) {
        int index = getPlayerMPIndex(packet.getUsername());
        PlayerMP player = (PlayerMP) this.getEntities().get(index);
        if (packet.getScore() != 0) {
            player.score = packet.getScore();
        }
        if (packet.getHealth() != 0) {
            player.health = packet.getHealth();
        }
        if (packet.getX() != 0 && packet.getY() != 0) {
            player.x = packet.getX();
            player.y = packet.getY();
        }
    }

    private int getEnemyIndex(String name) {
        int index = 0;
        for (StandardGameObject e : getEntities()) {
            if (e instanceof GreenBat && ((GreenBat) e).name.equals(name)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public void handleEnemy(String enemyName, int x, int y, String behaviour) {
        if(behaviour.equals(PacketEnemy.ADD)){
            GreenBat greenBat = new GreenBat(enemyName, x, y, this.gss);
            this.getEntities().add(greenBat);
        }else if(behaviour.equals(PacketEnemy.REMOVE)){
            GreenBat greenBat = getEnemy(enemyName);
            this.getEntities().remove(greenBat);
        }

    }

    private GreenBat getEnemy(String name) {
        for (StandardGameObject e : getEntities()) {
            if (e instanceof GreenBat && ((GreenBat) e).name.equals(name)) {
                return (GreenBat) e;
            }
        }
        return null;
    }

    public synchronized ArrayList<StandardGameObject> getEntities() {
        return this.entities;
    }


    public void handleBullet(PacketBullet packet) {
        if (packet != null) {
            StandardID id = (packet.getId().equals("Player")) ? StandardID.Player : StandardID.Enemy;
            Bullet bullet = new Bullet(packet.getX(), packet.getY(), packet.getVelY(), id, packet.getUsername());
            this.getEntities().add(bullet);
        }
    }

    public void handleDisconnect(String username) {
        int disconnectedPlayerIndex = getPlayerMPIndex(username);
        System.out.println(username + " has disconnected.");
        this.getEntities().remove(disconnectedPlayerIndex);
    }
}
