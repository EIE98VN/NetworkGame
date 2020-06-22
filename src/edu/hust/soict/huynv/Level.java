package edu.hust.soict.huynv;

import java.time.Duration;
import java.time.Instant;

public class Level extends Thread{

    private int level;
    public double enemyVelY;
    public int enemyNumber;
    public Instant start;
    private int levelChangeSeconds;

    public Level(int level){
        levelChangeSeconds = 20;
        start = Instant.now();
        this.level = level;
        if(this.level == 0){
            this.enemyVelY = 2;
            this.enemyNumber = 10;
        }else if(this.level == 1){
            this.enemyVelY = 3;
            this.enemyNumber = 12;
        }else{
            this.enemyVelY = 5;
            this.enemyNumber = 15;
        }
    }

    @Override
    public void run() {
        while(true){
            if(GenericSpaceShooter.enterGame){
                Duration duration = Duration.between(start, Instant.now());
                if(duration.toSeconds() > levelChangeSeconds){
                    this.enemyVelY += 1;
                    this.enemyNumber +=5;
                    start = Instant.now();
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
