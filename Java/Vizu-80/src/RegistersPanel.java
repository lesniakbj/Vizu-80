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

public class RegistersPanel extends JPanel 
{
    private static final AttributedString regA = DrawingUtils.underline("A:");
    private static final AttributedString flags = DrawingUtils.underline("Flags:");
    private static final AttributedString regB = DrawingUtils.underline("B:");
    private static final AttributedString regC = DrawingUtils.underline("C:");
    private static final AttributedString regD = DrawingUtils.underline("D:");
    private static final AttributedString regE = DrawingUtils.underline("E:");
    private static final AttributedString regH = DrawingUtils.underline("H:");
    private static final AttributedString regL = DrawingUtils.underline("L:");
    private static final AttributedString ghostRegA = DrawingUtils.underline("A' :");
    private static final AttributedString ghostFlags = DrawingUtils.underline("Flags' :");
    private static final AttributedString ghostRegB = DrawingUtils.underline("B' :");    
    private static final AttributedString ghostRegC = DrawingUtils.underline("C' :");
    private static final AttributedString ghostRegD = DrawingUtils.underline("D' :");
    private static final AttributedString ghostRegE = DrawingUtils.underline("E' :");
    private static final AttributedString ghostRegH = DrawingUtils.underline("H' :");
    private static final AttributedString ghostRegL = DrawingUtils.underline("L' :");
    
    private static final int HEIGHT_PADDING = 30;
    private static final int WIDTH_PADDING = 10;

    public void paintComponent(Graphics g) 
    {        
        int width = getWidth();
        int height = getHeight();
        
        drawLabels(g, width, height);       
    }
    
    private void drawLabels(Graphics g, int width, int height)
    {     
        int layoutWidth = (width / 8) - 20;
        int layoutHeight = height / 5;
        
        Graphics2D g2 = (Graphics2D) g;
        
        // Strings
        g2.setColor(Color.black);
        g2.drawString(regA.getIterator(), layoutWidth, layoutHeight - HEIGHT_PADDING);
        g2.drawString(flags.getIterator(), (layoutWidth * 3), layoutHeight - HEIGHT_PADDING);
        g2.drawString(regB.getIterator(), layoutWidth, (layoutHeight * 2) - HEIGHT_PADDING);
        g2.drawString(regC.getIterator(), (layoutWidth * 3) + WIDTH_PADDING, (layoutHeight * 2) - HEIGHT_PADDING);
        g2.drawString(regD.getIterator(), layoutWidth, (layoutHeight * 3) - HEIGHT_PADDING);
        g2.drawString(regE.getIterator(), (layoutWidth * 3) + WIDTH_PADDING, (layoutHeight * 3) - HEIGHT_PADDING);
        g2.drawString(regH.getIterator(), layoutWidth, (layoutHeight * 4) - HEIGHT_PADDING);
        g2.drawString(regL.getIterator(), (layoutWidth * 3) + WIDTH_PADDING, (layoutHeight * 4) - HEIGHT_PADDING);
        g2.drawString(ghostRegA.getIterator(), layoutWidth * 7, layoutHeight - HEIGHT_PADDING);
        g2.drawString(ghostFlags.getIterator(), (layoutWidth * WIDTH_PADDING), layoutHeight - HEIGHT_PADDING);
        g2.drawString(ghostRegB.getIterator(), layoutWidth * 7, (layoutHeight * 2) - HEIGHT_PADDING);
        g2.drawString(ghostRegC.getIterator(), (layoutWidth * WIDTH_PADDING) + WIDTH_PADDING, (layoutHeight * 2) - HEIGHT_PADDING);
        g2.drawString(ghostRegD.getIterator(), layoutWidth * 7, (layoutHeight * 3) - HEIGHT_PADDING);
        g2.drawString(ghostRegE.getIterator(), (layoutWidth * WIDTH_PADDING) + WIDTH_PADDING, (layoutHeight * 3) - HEIGHT_PADDING);
        g2.drawString(ghostRegH.getIterator(), layoutWidth * 7, (layoutHeight * 4) - HEIGHT_PADDING);
        g2.drawString(ghostRegL.getIterator(), (layoutWidth * WIDTH_PADDING) + WIDTH_PADDING, (layoutHeight * 4) - HEIGHT_PADDING);
    }
}