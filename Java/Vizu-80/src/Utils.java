package src;

import java.text.AttributedString;

import java.awt.font.TextAttribute;

public class Utils
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
    
    public static String toHex(int convert, boolean pad)
    {
        String str = "";
        if(pad)
        {
            if(convert <= 16)
                str += "0x0" + Integer.toHexString(convert).toUpperCase();
            else
                str += "0x" + Integer.toHexString(convert).toUpperCase();
        }
        else
        {
            return Integer.toHexString(convert).toUpperCase();
        }
        
        return str;
    }
    
    public static String padBinary(String binary, int maxLength)
    {
        String str = binary;
        while(str.length() < maxLength)
            str = "0" + str;
         
        return str;
    }
    
    public static String toBinary(int convert)
    {
        return Integer.toBinaryString(Integer.parseInt(toHex(convert, false), 16));
    }
    
    public static void printArray(int[] theArray)
    {
        for(int i = 0; i < theArray.length; i++)
            System.out.println(toHex(theArray[i], true));    
    }
}
