package src.cpu;


/**
 * An Enumeration of all the Full Registers in the Z80 CPU. 
 * 
 * All are externally accessible to programmers
 * 
 * @author Brendan Lesniak
 */
public enum FullRegisters
{
    /**
     * 8-Bit (1-Byte) Accumulator Register
     */
    A,
    
    /**
     * 16-Bit (2-Bytes) BC Register pair; 2 8-Bit Registers
     */
    BC,
    
    /**
     * 16-Bit (2-Bytes) DE Register pair; 2 8-Bit Registers
     */
    DE,
    
    /**
     * 16-Bit (2-Bytes) HL Register pair; 2 8-Bit Registers
     */
    HL,
        
    /**
     * 8-Bit (1-Byte) Flags Register
     */
    F,
    
    /**
     * Ghost 8-Bit Accumulator Register
     */
    GHOST_A,
    
    /**
     * Ghost 16-Bit (2-Bytes) BC Register pair; 2 8-Bit Registers
     */
    GHOST_BC,
    
    /**
     * Ghost 16-Bit (2-Bytes) DE Register pair; 2 8-Bit Registers
     */
    GHOST_DE,
    
    /**
     * Ghost 16-Bit (2-Bytes) HL Register pair; 2 8-Bit Registers
     */
    GHOST_HL,
    
    /**
     * Ghost 8-Bit Flags Register
     */
    GHOST_F,
    
    /**
     * 16-Bit Index Register; Indexes certain processes (Array / Vector operations, etc.)
     */
    IX,
    
    /**
     * 2nd 16-Bit Index Register; Indexes certain processes (Array / Vector operations, etc.)
     */
    IY,
    
    /**
     * CPU Stack Pointer
     */
    SP,
    
    /**
     * CPU Program Counter
     */
    PC,
    
    /**
     * 8-Bit Interrupt Register; Used for handling interrupts
     */
    I,
    
    /**
     * 7-Bit Refresh Register
     */
    R
}
