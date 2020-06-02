package edu.hust.soict.huynv.entities;

import edu.hust.soict.huynv.GenericSpaceShooter;
import edu.hust.soict.huynv.entities.Player;

import java.net.InetAddress;

public class PlayerMP extends Player {

    public InetAddress ipAddress;
    public int port;

    public PlayerMP(double x, double y, GenericSpaceShooter gss, String username, InetAddress ipAddress, int port) {
        super(x, y, gss, username);
        this.ipAddress = ipAddress;
        this.port = port;
    }


    public PlayerMP(int x, int y, GenericSpaceShooter gss, String username) {
        super(x, y, gss, username);
    }
}
