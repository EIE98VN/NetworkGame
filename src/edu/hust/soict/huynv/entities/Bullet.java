package edu.hust.soict.huynv.entities;

import com.joshuacrotts.standards.StandardGameObject;
import com.joshuacrotts.standards.StandardID;
import com.joshuacrotts.standards.StdOps;
import edu.hust.soict.huynv.GenericSpaceShooter;

import java.awt.*;

public class Bullet extends StandardGameObject {
    private String username;

    public Bullet(double x, double y, double velY, StandardID id, String username){

        this.x = x ;
        this.y = y;
        this.id = (id == StandardID.Player) ? StandardID.Weapon : StandardID.Obstacle;
        this.username = username;

        this.currentSprite = StdOps.loadImage("res/bullet.png");

        this.width = this.currentSprite.getWidth();
        this.height = this.currentSprite.getHeight();

        this.velY = velY;
    }

    public Bullet(double x, double y, double velY, StandardID id){

        this.x = x ;
        this.y = y;
        this.id = (id == StandardID.Player) ? StandardID.Weapon : StandardID.Obstacle;

        this.currentSprite = StdOps.loadImage("res/bullet.png");

        this.width = this.currentSprite.getWidth();
        this.height = this.currentSprite.getHeight();

        this.velY = velY;
    }

    @Override
    public void tick() {

        if(this.y <= -20 || this.y >= 900){
            GenericSpaceShooter.standardHandler.getEntities().remove(this);
            return;
        }

        this.x += this.velX;
        this.y += this.velY;
    }

    @Override
    public void render(Graphics2D graphics2D) {
        if(Math.signum(this.velY) == -1){
            graphics2D.drawImage(this.currentSprite, (int) x - 2, (int) y, null);
        }else{
            graphics2D.drawImage(this.currentSprite, (int) x - 2, (int) y, width, -height, null);
        }
    }

    public String getUsername(){
        return this.username;
    }
}
