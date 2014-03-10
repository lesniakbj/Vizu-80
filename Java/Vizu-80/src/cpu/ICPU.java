package src.cpu;


/**
 * Interface describing a CPU, and its external facing processes
 * 
 * @author Brendan Lesniak 
 */
public interface ICPU
{
    public void resetCPU();
    
    public void executeInstruction() throws CPUException;
    
    public boolean isHalted(); 
       
    public long getCycles();
    
    public void resetCycles();
}