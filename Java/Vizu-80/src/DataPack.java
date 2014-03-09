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
    private int[] theRegisterData; // A BC DE HL F x 2
    private int[] theFullRegisterData; // BC DE HL x 2
    private int[] othersData; // IX IY PC SP IR RR IDX
    
    // [TO-DO]: ADD OTHER DATA TYPES THAT ARE SENT TO THE CONTROLLER... AKA OpCodes, Memory, etc...
    
    public DataPack(int[] halfData, int[] fullData, int[] otherData)
    {
        theRegisterData = halfData;
        theFullRegisterData = fullData;
        othersData = otherData;
    }
    
    public static DataPack emptyPack()
    {
        int[] regData, fullData, otherData;
        
        regData = new int[16];
        Utils.zero(regData);
        
        fullData = new int[6];
        Utils.zero(fullData);
        
        otherData = new int[7];
        Utils.zero(otherData);
        
        return new DataPack(regData, fullData, otherData);
    }
    
    public int[] getRegisterData()
    {
        return theRegisterData;
    }
    
    public int[] getFullRegisterData()
    {
        return theFullRegisterData;
    }
    
    public int[] getOtherData()
    {
        return othersData;
    }
}
