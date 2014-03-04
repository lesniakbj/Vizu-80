package src;

import java.text.AttributedString;

import java.awt.font.TextAttribute;

public class DrawingUtils
{
    public static AttributedString underline(String str)
    {
        AttributedString string = new AttributedString(str);
        
        string.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, 0, str.length());
        string.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        return string;
    }
}
