package src.cpu;


/**
 * An Enumeration of all the Half Registers in the Z80 CPU. 
 * 
 * All are externally accessible to programmers
 * 
 * @author Brendan Lesniak
 */
public enum HalfRegisters
{
    /**
     * 8-Bit (1-Byte) Accumulator Register
     */
    A,
    
    /**
     * 8-Bit B Register
     */
    B,
    
    /**
     * 8-Bit C Register
     */
    C,
    
   /**
     * 8-Bit D Register
     */
    D,
    
    /**
     * 8-Bit E Register
     */
    E,
    
    /**
     * 8-Bit H Register
     */
    H,
    
    /**
     * 8-Bit L Register
     */
    L,
        
    /**
     * 8-Bit (1-Byte) Flags Register
     */
    F,
    
    /**
     * Ghost 8-Bit Accumulator Register
     */
    GHOST_A,
    
    /**
     * Ghost 8-Bit B Register
     */
    GHOST_B,
    
    /**
     * Ghost 8-Bit B Register
     */
    GHOST_C,
    
    /**
     * Ghost 8-Bit D Register
     */
    GHOST_D,
    
    /**
     * Ghost 8-Bit E Register
     */
    GHOST_E,
    
    /**
     * Ghost 8-Bit H Register
     */
    GHOST_H,
    
    /**
     * Ghost 8-Bit L Register
     */
    GHOST_L,
    
    /**
     * Ghost 8-Bit Flags Register
     */
    GHOST_F
}
