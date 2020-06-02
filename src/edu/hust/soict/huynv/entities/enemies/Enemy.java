package edu.hust.soict.huynv.entities.enemies;

import com.joshuacrotts.standards.StandardGameObject;
import com.joshuacrotts.standards.StandardID;
import com.joshuacrotts.standards.StdOps;

import java.awt.*;

public abstract class Enemy extends StandardGameObject {

    protected int interval = StdOps.rand(0, 120);

    public String name;

    public Enemy(String name, double x, double y){
        super(x, y, StandardID.Enemy);
        this.name = name;
    }

    @Override
    public abstract void tick();

    @Override
    public abstract void render(Graphics2D graphics2D);

    public abstract void fireBullet();
}
