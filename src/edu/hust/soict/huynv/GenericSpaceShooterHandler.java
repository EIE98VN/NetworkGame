package edu.hust.soict.huynv;

import com.joshuacrotts.standards.StandardGameObject;
import com.joshuacrotts.standards.StandardHandler;
import com.joshuacrotts.standards.StandardID;
import edu.hust.soict.huynv.entities.Bullet;
import edu.hust.soict.huynv.entities.PlayerMP;
import edu.hust.soict.huynv.entities.enemies.GreenBat;
import edu.hust.soict.huynv.network.packets.PacketBullet;
import edu.hust.soict.huynv.network.packets.PacketEnemy;

import java.awt.*;
import java.util.ArrayList;

public class GenericSpaceShooterHandler extends StandardHandler {

    private GenericSpaceShooter gss;
    public ArrayList<PlayerMP> playerList = new ArrayList<>();

    public GenericSpaceShooterHandler(GenericSpaceShooter gss) {
        this.gss = gss;
        this.entities = new ArrayList<StandardGameObject>();
    }

    public void tick() {
        for (int i = 0; i < this.entities.size(); i++) {

//            if((this.entities.get(i) instanceof PlayerMP) && !(((PlayerMP) this.entities.get(i)).getUsername().equals(playerList.get(0).getUsername())))
//                    continue;

            //Player and Enemy collision
            if (this.entities.get(i).getId() == StandardID.Player) {

                for (int j = 0; j < this.entities.size(); j++) {


                    if ((this.entities.get(j).getId() == StandardID.Obstacle || this.entities.get(j).getId() == StandardID.Enemy) &&
                            this.entities.get(j).getBounds().intersects(this.entities.get(i).getBounds())) {

                        this.entities.get(i).health -= 20;

                        GreenBat greenBat = (GreenBat) this.entities.get(j);
                        PacketEnemy packetEnemy = new PacketEnemy(greenBat.name, (int) greenBat.x, (int) greenBat.y);
                        packetEnemy.writeData(gss.socketClient);

                        if(gss.isServer){
                            this.getEntities().remove(j);
                        }

                        j--;

                    }

                }

            }

            //Player's bullet and Enemy collision
            if (this.entities.get(i).getId() == StandardID.Weapon) {

                Bullet bullet = (Bullet) this.getEntities().get(i);
                if(bullet.getUsername().equals(this.playerList.get(0).getUsername())){
                    for (int j = 0; j < this.entities.size(); j++) {
                        if (this.entities.get(j).getId() == StandardID.Enemy &&
                                this.entities.get(j).getBounds().intersects(this.entities.get(i).getBounds())) {

                            this.entities.get(j).health -= 20;

                            if(this.entities.get(j).health <= 0 ){
                                playerList.get(0).score++;
                            }
                        }

                    }
                }

            }


            this.entities.get(i).tick();
        }
    }

    public void render(Graphics2D graphics2D) {
        for (int i = 0; i < this.entities.size(); i++) {
            this.entities.get(i).render(graphics2D);
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

    public void handlePlayer(String username, int score, int x, int y) {
        int index = getPlayerMPIndex(username);
        PlayerMP player = (PlayerMP) this.getEntities().get(index);
        player.score = score;
        player.x = x;
        player.y = y;
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

    public  void handleEnemy(String enemyName, int x, int y){
        if(getEnemyIndex(enemyName)==-1){
            GreenBat greenBat = new GreenBat(enemyName, x, y, this.gss);
            this.getEntities().add(greenBat);
        }else{
            this.getEntities().remove(getEnemy(enemyName));
        }

    }

    private  GreenBat getEnemy(String name){
        for (StandardGameObject e : getEntities()) {
            if (e instanceof GreenBat && ((GreenBat) e).name.equals(name)) {
                return (GreenBat) e;
            }
        }
        return null;
    }

    public synchronized ArrayList<StandardGameObject> getEntities(){

        return this.entities;
    }


    public void handleBullet(PacketBullet packet) {
        if(packet != null){
            StandardID id = (packet.getId().equals("Player")) ? StandardID.Player:StandardID.Enemy;
            Bullet bullet = new Bullet(packet.getX(), packet.getY(), packet.getVelY(), id, packet.getUsername());
            this.getEntities().add(bullet);
        }
    }
}
