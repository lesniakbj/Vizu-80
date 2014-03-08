package src;

import src.cpu.Z80Core;
import java.util.ArrayDeque;
import java.util.Deque;
import src.cpu.IMemory;
import src.cpu.IDevice;
import src.cpu.CPUException;

/**
 * Contols data coming in from the CPU / Calls CPU methods for updating. <br><br>
 *      This is the Conroller between our Model (Z80Core) and our view (Vizu80GUI), fetching a DataPack <br>
 *      from the CPU and adding it to its own internal cache of DataPacks. Is also the intermediate between <br>
 *      the view and the CPU. This means it is the controller, controlling all aspects of CPU operations.
 * 
 * @author Brendan Lesniak 
 * @version 0.1.a
 * @since 0.1.a
 */
public class DataController
{
    private Z80Core cpuCore;
    private Deque<DataPack> dataPacks; 
    private Deque<DataPack> rewindPacks;
    
    private int numPacks;
    
    private static boolean firstStart;
    private static boolean firstClick;
    
    public DataController()
    {
        this(new Z80Core());       
    }
    
    public DataController(Z80Core theCore)
    {
        cpuCore = theCore;
        numPacks = 0;
        dataPacks = new ArrayDeque<DataPack>(0);
        rewindPacks = new ArrayDeque<DataPack>(0);
        firstStart = true;
    }
    
    public DataPack nextStep()
    {
        DataPack dat = null;
        
        if(rewindPacks.size() > 0)
        {
            dat = rewindPacks.pop();
            dataPacks.push(dat);
            return dat;
        }
            
        if(firstStart)
        {
            cpuCore.resetCPU();
            firstStart = false;
        }        
        
        try
        {
            cpuCore.executeInstruction();
            dataPacks.push(cpuCore.getDataPack());
            numPacks++;
        }
        catch(CPUException e)
        {
            System.out.println(e);
        }
        
        firstClick = true;
        return cpuCore.getDataPack();
    }
    
    public DataPack backStep()
    {     
        DataPack dat = null;
        
        if(firstStart)
            return null;
        
        if(dataPacks.peek() != null)
        {       
            numPacks--;           
            dat = dataPacks.pop();
            rewindPacks.push(dat);
        }
            
        return dat;
    }
    
    public String toString()
    {
        return "Data Size: " + dataPacks.size() + "/n" + "Rewind Size: " + rewindPacks.size();
    }
}
