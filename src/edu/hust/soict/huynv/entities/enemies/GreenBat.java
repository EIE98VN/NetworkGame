package edu.hust.soict.huynv.entities.enemies;

import com.joshuacrotts.standards.StdOps;
import edu.hust.soict.huynv.GenericSpaceShooter;
import edu.hust.soict.huynv.network.packets.PacketEnemy;

import java.awt.*;

public class GreenBat extends Enemy {


    private GenericSpaceShooter gss;
    private boolean isCounted = false;


    public GreenBat(String name, double x, double y, double velY, GenericSpaceShooter gss) {
        super(name, x, y);
        this.gss = gss;
        this.currentSprite = StdOps.loadImage("res/greenbat.png");

        this.height = this.currentSprite.getHeight();
        this.width = this.currentSprite.getWidth();

        this.health = 40;
        this.velY = velY;
    }

    @Override
    public void tick() {

        if ((this.health <= 0 || this.y >= 800)) {
            if (this.health <= 0 && gss.isServer) {
                PacketEnemy packetEnemy = new PacketEnemy(this.name, (int) this.x, (int) this.y, PacketEnemy.REMOVE, (int) this.velY);
                packetEnemy.writeData(gss.socketClient);
            }
            GenericSpaceShooter.standardHandler.getEntities().remove(this);

            return;
        }

        this.x += this.velX;
        this.y += this.velY;
    }

    @Override
    public void render(Graphics2D graphics2D) {
        graphics2D.drawImage(this.currentSprite, (int) x, (int) y, null);
    }

}
