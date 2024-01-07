package cz.iqlandia.iqplanetarium.chatreciever;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static MainGraphicsProcessor mgp = new MainGraphicsProcessor();

    public static void main(String[] args) {
        JFrame frame = new JFrame("Hlasovani | PollManager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(mgp);
        frame.setMinimumSize(mgp.getMinimumSize());
        frame.setPreferredSize(mgp.getPreferredSize());
        frame.setMaximumSize(mgp.getMaximumSize());

        java.util.Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mgp.repaint();
            }
        }, 1000 / 25, 1000 / 25);

        frame.setSize(1920, 1080);
        frame.setVisible(true);
    }
}