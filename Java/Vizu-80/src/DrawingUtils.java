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
    
    public static String pad(String toPad, int totalLength)
    {
        if(toPad.length() >= totalLength)
            return toPad;
        
        String str = toPad;
        
        while(str.length() < totalLength)
            str += " ";
        
        return str;
    }
    
    public static String toHex(int convert)
    {
        return Integer.toHexString(convert);
    }
}
