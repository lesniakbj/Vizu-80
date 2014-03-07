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
    private int[] theRegisterData; // A BC DE HL F
    private int[] othersData; // IX IY PC SP IR RR IDK
    
    // [TO-DO]: ADD OTHER DATA TYPES THAT ARE SENT TO THE CONTROLLER... AKA OpCodes, Memory, etc...
    
    public DataPack(int[] regData, int[] otherData)
    {
        theRegisterData = regData;
        othersData = otherData;
    }
    
    public int[] getRegisterData()
    {
        return theRegisterData;
    }
    
    public int[] getOtherData()
    {
        return othersData;
    }
}