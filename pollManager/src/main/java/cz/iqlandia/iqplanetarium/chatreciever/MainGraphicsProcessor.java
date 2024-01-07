package cz.iqlandia.iqplanetarium.chatreciever;

import javax.swing.*;
import java.awt.*;

public class MainGraphicsProcessor extends JComponent {
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(1920, 1080);
    }

    @Override
    public Dimension getMaximumSize() {
        return getMinimumSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return getMaximumSize();
    }

    @Override
    public void paintComponent(Graphics g) {
        //Defining colors
        Color background = new Color(0, 0, 0);
        Color text = new Color(255, 255, 255);
        Color green = new Color(9, 145, 9);
        Color red = new Color(196, 0, 0);
        Color blue = new Color(35, 35, 173);
        Color orange = new Color(206, 113, 7);

        super.paintComponent(g);
        //Background
        g.setColor(background);
        g.fillRect(0, 0, getWidth(), getHeight());

        //Bars
        g.setColor(green);

        g.dispose();
    }
}
