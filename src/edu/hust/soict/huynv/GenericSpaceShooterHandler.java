package edu.hust.soict.huynv;

import com.joshuacrotts.standards.StandardGameObject;
import com.joshuacrotts.standards.StandardHandler;
import com.joshuacrotts.standards.StandardID;

import java.awt.*;
import java.util.ArrayList;

public class GenericSpaceShooterHandler extends StandardHandler {

    public GenericSpaceShooterHandler() {
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

            //Player to Bullet collision
            if (this.entities.get(i).getId() == StandardID.Player) {

                for (int j = 0; j < this.entities.size(); j++) {


                    if ((this.entities.get(j).getId() == StandardID.Obstacle || this.entities.get(j).getId() == StandardID.Enemy) &&
                            this.entities.get(j).getBounds().intersects(this.entities.get(i).getBounds())) {

                        this.entities.get(i).health -= 20;
                        this.entities.remove(j);
                        j--;

                    }

                }

            }

            //Player bullet to Enemy
            if (this.entities.get(i).getId() == StandardID.Weapon) {

                for (int j = 0; j < this.entities.size(); j++) {

                    if (this.entities.get(j).getId() == StandardID.Enemy &&
                            this.entities.get(j).getBounds().intersects(this.entities.get(i).getBounds())) {

                        this.entities.get(j).health -= 20;

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
}
