package src;

import java.util.ArrayList;
/**
 * A CPU Data pack; contains all of the register data, packed into an ArrayList<int>
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class DataPack
{
    private ArrayList<Integer> theData;
    
    
    
    public int[] getData()
    {
        int[] data = new int[theData.size()];
        
        for(int i = 0; i < theData.size(); i++)
            data[i] = theData.get(i);
            
        return data;
    }
}
