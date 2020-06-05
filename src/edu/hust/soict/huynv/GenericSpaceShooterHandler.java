package edu.hust.soict.huynv;

import com.joshuacrotts.standards.StandardGameObject;
import com.joshuacrotts.standards.StandardHandler;
import com.joshuacrotts.standards.StandardID;
import edu.hust.soict.huynv.entities.PlayerMP;
import edu.hust.soict.huynv.entities.enemies.GreenBat;
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

//            if (this.entities.get(i).getId() == StandardID.Enemy) {
//                ArrayList<Integer> removes = new ArrayList<Integer>();
//                int size = this.entities.size();
//                for (int j = 0; j < size; j++) {
//                    for (int k = 0; k < removes.size(); k++) {
//                        System.out.println("here");
//
//                        if (j == removes.get(k)) break;
//                        if (this.entities.get(j).getId() == StandardID.Enemy) {
//                            double distance = Math.abs(this.entities.get(i).getX() - this.entities.get(j).getX());
//                            System.out.println(distance);
//                            if (distance > 0 && distance < 60) {
//                                this.entities.remove(j);
//                                removes.add(j);
//                            }
//                        }
//                    }
//                }
//            }

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
                            this.entities.remove(j);
                        }

                        j--;

                    }

                }

            }

            //Player's bullet and Enemy collision
            if (this.entities.get(i).getId() == StandardID.Weapon) {

                for (int j = 0; j < this.entities.size(); j++) {
                    if (this.entities.get(j).getId() == StandardID.Enemy &&
                            this.entities.get(j).getBounds().intersects(this.entities.get(i).getBounds())) {

                        this.entities.get(j).health -= 20;

                        if(this.entities.get(j).health <= 0 ){
                            playerList.get(0).score ++;
                        }
                    }

                }

            }


            //Score
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

    public void movePlayer(String username, int x, int y) {
        int index = getPlayerMPIndex(username);
        PlayerMP player = (PlayerMP) this.getEntities().get(index);
        player.x = x;
        player.y = y;
    }

    private synchronized int getEnemyIndex(String name) {
        int index = 0;
        for (StandardGameObject e : getEntities()) {
            if (e instanceof GreenBat && ((GreenBat) e).name.equals(name)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public synchronized void handleEnemy(String enemyName, int x, int y){
        if(getEnemyIndex(enemyName)==-1){
            GreenBat greenBat = new GreenBat(enemyName, x, y, this.gss);
            this.addEntity(greenBat);
        }else{
            this.entities.remove(getEnemy(enemyName));
        }

    }

    private synchronized GreenBat getEnemy(String name){
        for (StandardGameObject e : getEntities()) {
            if (e instanceof GreenBat && ((GreenBat) e).name.equals(name)) {
                return (GreenBat) e;
            }
        }
        return null;
    }
}
