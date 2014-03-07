package src;

import src.cpu.Z80Core;
import java.util.ArrayList;
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
    private ArrayList<DataPack> dataPacks;
    
    private int currentDataPack;
    
    private static boolean firstStart;
    
    public DataController()
    {
        this(new Z80Core());       
    }
    
    public DataController(Z80Core theCore)
    {
        cpuCore = theCore;
        currentDataPack = 0;
        dataPacks = new ArrayList<DataPack>(0);
        firstStart = true;
    }
    
    public DataPack nextStep()
    {
        if(firstStart)
        {
            cpuCore.resetCPU();
            firstStart = false;
        }
        
        
        try
        {
            cpuCore.executeInstruction();
            dataPacks.add(cpuCore.getDataPack());
        }
        catch(CPUException e)
        {
            System.out.println(e);
        }
        
        System.out.println(dataPacks.size());
        return cpuCore.getDataPack();
    }
}
