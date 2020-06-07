package edu.hust.soict.huynv.entities.enemies;

import com.joshuacrotts.standards.StdOps;
import edu.hust.soict.huynv.GenericSpaceShooter;
import edu.hust.soict.huynv.entities.Bullet;
import edu.hust.soict.huynv.network.packets.PacketEnemy;

import java.awt.*;

public class GreenBat extends Enemy {


    private GenericSpaceShooter gss;
    private boolean isCounted = false;


    public GreenBat(String name, double x, double y, GenericSpaceShooter gss) {
        super(name, x, y);
        this.gss = gss;
        this.currentSprite = StdOps.loadImage("res/greenbat.png");

        this.height = this.currentSprite.getHeight();
        this.width = this.currentSprite.getWidth();

        this.health = 40;
        this.velY = 2;
    }

    @Override
    public void tick() {

        if (this.health <= 0 || this.y >= 800) {
            if (this.health <= 0) {
                PacketEnemy packetEnemy = new PacketEnemy(this.name, (int) this.x, (int) this.y);
                packetEnemy.writeData(gss.socketClient);
            }
            GenericSpaceShooter.standardHandler.removeEntity(this);

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
        if (this.interval < 200) {
            return;
        } else {
            this.interval = 0;
            GenericSpaceShooter.standardHandler.addEntity(new Bullet((this.x + this.width / 2), this.y, 2, this.getId()));
        }
    }

    private void fireBulletCheck() {
        this.interval++;

        if (this.interval > 200) {
            this.interval = 200;
        }
    }
}
