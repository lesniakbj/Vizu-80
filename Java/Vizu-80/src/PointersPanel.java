package src;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.Color;
import java.text.AttributedString;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class PointersPanel extends JPanel 
{

    public void paintComponent(Graphics g) 
    {        
        int width = getWidth();
        int height = getHeight();
        
        drawLabels(g, width, height);       
    }
    
    private void drawLabels(Graphics g, int width, int height)
    {     
        Graphics2D g2 = (Graphics2D) g;
    }
}