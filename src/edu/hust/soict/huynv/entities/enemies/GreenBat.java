package edu.hust.soict.huynv.entities.enemies;

import com.joshuacrotts.standards.StdOps;
import edu.hust.soict.huynv.GenericSpaceShooter;
import edu.hust.soict.huynv.entities.Bullet;
import edu.hust.soict.huynv.entities.enemies.Enemy;

import java.awt.*;

public class GreenBat extends Enemy {

    public GreenBat(double x, double y ){
        super(x, y);

        this.currentSprite = StdOps.loadImage("E:\\Term8\\Network Programming\\SpaceShooter01\\res\\greenbat.png");

        this.height = this.currentSprite.getHeight();
        this.width = this.currentSprite.getWidth();

        this.health = 40;
        this.velY = 2;
    }

    @Override
    public void tick() {

        if(this.health <= 0 || this.y >= 900){
            GenericSpaceShooter.standardHandler.removeEntity(this);
            if(this.health <= 0) GenericSpaceShooter.score++;
            return;
        }

        this.x += this.velX;
        this.y += this.velY;

//        this.fireBulletCheck();
//        this.fireBullet();
    }

    @Override
    public void render(Graphics2D graphics2D) {
        graphics2D.drawImage(this.currentSprite, (int) x, (int) y, null);
    }

    @Override
    public void fireBullet() {
        if(this.interval < 200){
            return;
        }else{
            this.interval = 0;
            GenericSpaceShooter.standardHandler.addEntity(new Bullet((this.x + this.width / 2), this.y, 2, this.getId()));
        }
    }

    private void fireBulletCheck(){
        this.interval ++;

        if(this.interval > 200){
            this.interval = 200;
        }
    }
}
