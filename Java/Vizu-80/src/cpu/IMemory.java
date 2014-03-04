package src.cpu;


/**
 * Interface defining read and write operations from memory
 * 
 * @author Brendan Lesniak
 */
public interface IMemory
{
    public int readByte(int address);
    
    public int readWord(int address);
    
    public int writeByte(int address, int data);
    
    public int writeWord(int address, int data);
}
