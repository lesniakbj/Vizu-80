package src.cpu;


/**
 * Interface - Describes an I/O Device
 * 
 * @author Brendan Lesniak
 */
public interface IDevice
{
    /**
     * Read data from the indicated address
     * 
     * @param address
     *              The address that is read from
     * @return 8-bits, packed into an int
     */
    public int read(int address);
    
    /**
     * Write data to the indicated address
     * 
     * @param address
     *              The address that data is to be written to
     * @param data
     *              The 8-bit value to be written to the address
     */
    public void write(int address, int data);
}
