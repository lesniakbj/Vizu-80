package src;

import javax.swing.JMenuBar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class BackgroundMenuBar extends JMenuBar
{
    Color bgColor=Color.WHITE;

    public void setColor(Color color)
    {
        bgColor=color;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

    }
}