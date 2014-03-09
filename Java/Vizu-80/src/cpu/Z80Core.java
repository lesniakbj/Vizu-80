package src.cpu;

import src.DataPack;

/*
 * 
 * VERSION 0.1.a MILESTONES:
 * --------------------------
 * - Decoding of all opcodes (unimplemented methods for actions of opcodes)
 * - Complete getRegisterContents()
 * 
 */

/**
 * Concrete implementation of the ZiLOG z80 CPU Core,emulating all of the known instruction set.
 * Attempts to implement all of the undocumented functionality of the processor as well.
 * 
 * 
 * @author Brendan Lesniak
 * @version 0.0.a
 */
public class Z80Core implements ICPU
{
    private static final String name = "ZiLOG z80 Visual Emulator";
    private static final int version = 0;
    private static final int versionMinor = 0;
    private static final String versionPatch = "a";
    
    // Externalized parts of the CPU, defined when the z80 CPU is constructed, interfaces (need to be implemented)
    private IMemory         systemRAM;
    private IDevice         systemIO;
    
    // Internalized CPU Flags and State
    private int             currentOpcode;
    private boolean         halted;
    private long            states;
    
    // CPU Registers
    // [TO-DO]: Externalize, create a dedicated registers object
    private int[]           registers; // A, B, C, D, E, H, L, F
    private int[]           ghostRegisters; // A', B', C', D', E', H', L', F'
    private int             index_x, index_y, programCounter, stackPointer;
    private int             interruptRegister, refreshRegister;
    private int             indexer;
    
    // Flags, Addresses, and Booleans -- Processor Control Variables
    private boolean         blocked;
    private boolean         isReset;
    private int             resetAddress;
    private boolean         interrupt_1, interrupt_2;
    private boolean         maskingInterrupts;
    private boolean         nonMaskableInterrupt;
    
    int[] sendHalfRegData = new int[16]; // A BC DE HL F     x 2
    int[] sendFullRegData = new int[6]; // BC DE HL         x 2
    int[] sendOtherData = new int[7]; // IX IY PC SP IR RR IDX

    /**
     * Empty Z80 Constructor, does not specify System RAM nor System IO; TESTING ONLY - DO NOT USE!
     * 
     * @since 0.0.a
     * @see IMemory
     * @see IDevice
     * @deprecated
     */
    @Deprecated
    public Z80Core()
    {
        systemRAM = null;
        systemIO = null;
        states = 0;
        
        registers = new int[8];
        ghostRegisters = new int[8];
        
        blocked = false;
        isReset = false;
        resetAddress = 0x1000;
    }
    
    /**
     * Constructor for the Z80 Core, does not completely initialize all CPU Registers and Components.
     * 
     * @param ram
     *          Specifies the System RAM Controller (and Bank) being used by the CPU
     * @param device
     *          Specifies the System IO Controller used by the CPU
     * 
     * @since 0.0.a
     * @see src.cpu.IMemory
     * @see src.cpu.IDevice
     */    
    public Z80Core(IMemory ram, IDevice device)
    {
        systemRAM = ram;
        systemIO = device;
        states = 0;
        
        registers = new int[8];
        ghostRegisters = new int[8];
        
        blocked = false;
        isReset = false;
        resetAddress = 0x0000;
    }
    
    /**
     * Public Facing, executes one instruction during the cycle. First checks for Non-Maskable Interrupts, 
     * ones that force the CPU to fetch a new opcode. If there was one, check to see if there was just a 
     * Maskable Interrupt, [DOCUMENT]. Fetch the opcode from System RAM at the location specified by 
     * the Program Counter,incrementing after. Finally, decode the opcode, and check to see 
     * if the CPU needs to be reset. If so, decrement the Program Counter, and run {@link #resetCPU()}. 
     * 
     * @throws CPUException
     * 
     * @since 0.0.a
     * @see src.cpu.IMemory
     * @see src.cpu.IDevice
     * @see src.cpu.CPUException
     */  
    public void executeInstruction() throws CPUException
    {
        halted = false;
        
        if(nonMaskableInterrupt)
        {
            // No Interrupts directly after a maskable interrupt
            // [TO-DO]: FIGURE THIS OUT AND DOCUMENT IT
            if(maskingInterrupts)
            {
                maskingInterrupts = false;
            }
            else
            {
                maskingInterrupts = false; // Interrupt has been accepted by the CPU
                interrupt_2 = interrupt_1; // Store current interrupt state in #2
                //decrementStackPointer_2();
                //systemRAM.writeWord(stackPointer, programCounter);
                programCounter = 0x0066;
            }
            
            //currentOpcode = systemRAM.readByte(programCounter);
            //incrementProgramCounter();
            
            try
            {
                decodeOpcode(currentOpcode);
            }
            catch(CPUException e)
            {
                //decrementProgramCounter();
                throw e;
            }
            
            if(isReset)
            {
                isReset = false;
                resetCPU();
            }            
        }
    }
    
    /**
     * Public facing method to reset the CPU. Calls private method {@link runResetProcess()} to reset 
     * the CPU to its initial state. 
     * 
     * @since 0.0.a
     */
    public void resetCPU()
    {
        runResetProcess();
    }
    
    /**
     * Public facing method to find if the CPU has Halted. 
     * 
     * @return Whether the CPU has Halted or not
     * 
     * @since 0.0.a
     */
    public boolean isHalted()
    {
        return halted;
    }
    
    /**
     * Public facing method to find if the CPU is currently Blocked.
     * 
     * @return Whether the CPU is currently Blocked or not
     * 
     * @since 0.0.a
     */
    public boolean isCurrentlyBlocking()
    {
        return blocked;
    }
        
    /**
     * Public facing method to find the current States of the CPU.
     * 
     * @return [TO-DO]: WTF IS THE STATES VARIABLE?!
     * 
     * @since 0.0.a
     */
    public long getStates()
    {
        return states;
    }
    
    /**
     * Public facing method to reset CPU States back to their initial state.
     * 
     * @since 0.0.a
     */
    public void resetStates()
    {
        states = 0;
    }
    
    /**
     * Public facing method to indicate that an external interrupt has occured.
     * 
     * @since 0.0.a
     */
    public void setInterrupt()
    {
        nonMaskableInterrupt = true;
    }
    
    /**
     * Public facing method to get the CPU Program Counter.
     * 
     * @return Current location (in System RAM) of the Program, held by the Program Counter
     * 
     * @since 0.0.a
     */
    public int getProgramCounter()
    {
        return programCounter;
    }
    
    /**
     * Public facing method to set the CPU Program Counter to an address in System Memory.
     * 
     * @param add 
     *          The Address that Program Counter is being set to
     *          
     * @since 0.0.a
     */
    public void setProgramCounter(int add)
    {
        programCounter = add;
    }
    
    /**
     * Public facing method to set the CPU Reset Address to an address in System Memory.
     * Used to resume a program when it has been halted.
     * 
     * @param add 
     *          The Address that Reset Address is being set to
     *          
     * @since 0.0.a        
     */
    public void setResetAddress(int add)
    {
        resetAddress = add;
    }
    
    /**
     * Public facing method to retrieve the contents of any CPU Register pair. 
     * 
     * @param reg
     *          An Enumeration Constant specifying which register to retrieve the contents from
     * @return 
     *          The Content of the Register specified by reg
     *          
     * @since 0.0.a
     * @see src.cpu.FullRegisters
     */
    public int getRegisterContents(FullRegisters reg)
    {
        switch (reg)
        {
            case BC:
                return (registers[1] << 8) + registers[2]; // Move B to the upper half (8-Bits), and add C
            case DE:
                return (registers[3] << 8) + registers[4]; // repeat...
            case HL:
                return (registers[5] << 8) + registers[6];
            case GHOST_BC:
                return (ghostRegisters[1] << 8) + ghostRegisters[2];
            case GHOST_DE:
                return (ghostRegisters[3] << 8) + ghostRegisters[4];
            case GHOST_HL:
                return (ghostRegisters[5] << 8) + ghostRegisters[6];
            case IX:
                return index_x;
            case IY:
                return index_y;
            case SP:
                return stackPointer;
            case PC:
                return programCounter;
            case A:
                return registers[0];
            case F:
                return registers[7];
            case GHOST_A:
                return ghostRegisters[0];
            case GHOST_F:
                return ghostRegisters[7];
            case I:
                return interruptRegister;
            case R:
                return refreshRegister;
            default:
                return -1;
            
        }
    }
    
    public int getHalfRegisterContents(HalfRegisters reg)
    {
        switch (reg)
        {
            case A:
                return registers[0];
            case B:
                return registers[1];
            case C:
                return registers[2];
            case D:
                return registers[3];
            case E:
                return registers[4];
            case H:
                return registers[5];
            case L:
                return registers[6];
            case F:
                return registers[7];
            case GHOST_A:
                return ghostRegisters[0];
            case GHOST_B:
                return ghostRegisters[1];
            case GHOST_C:
                return ghostRegisters[2];
            case GHOST_D:
                return ghostRegisters[3];
            case GHOST_E:
                return ghostRegisters[4];
            case GHOST_H:
                return ghostRegisters[5];
            case GHOST_L:
                return ghostRegisters[6];
            case GHOST_F:
                return ghostRegisters[7];
            default:
                return -1;
            
        }
    }


    /**
     * Public facing method that returns the location specified by the CPU Stack Pointer.
     * 
     * @return The location the Stack Pointer is holding
     * 
     * @since 0.0.a
     */
    public int getStackPointer()
    {
        return stackPointer;
    }    
   
    /**
     * Public facing toString() method
     * 
     * @return
     *      The name of the CPU, along with revision history
     *      
     * @since 0.0.a
     */
    public String toString()
    {
        return name + " -- Current Revision: " + version + "." + versionMinor + "." + versionPatch;
    }
    
    /**
     * Runs the reset process of the CPU. <li>First resets all of the Registers to their initial states, followed
     * by the 2 Index Registers and Stack Pointers.</li> <li>Resets the Interrupt and Refresh Registers, sets Interrupt
     * 1 and 2 to their initial states, and stop masking all interrupts.</li> <li>Finally, set the Program Counter to
     * the saved Reset Address, and set the initial CPU States.</li>
     * 
     * @since 0.0.a
     */
    private void runResetProcess()
    {
        // Processor is not currently halted
        halted = false;
        
        // Reset all Registers and Ghost Registers back to their initial states
        for(int i = 0; i < registers.length; i++)
            registers[i] = 0x00;
            
        for(int i = 0; i < ghostRegisters.length; i++)
            ghostRegisters[i] = 0x00;
        
        // Reset all Index and Stack Pointers back to their intial states
        index_x = index_y = stackPointer = 0x0000;
        
        // Reset Interrupt and Refresh registers to their initial states
        interruptRegister = refreshRegister = 0x00;
        
        // Set Interrupt Flag 1 & 2 and Interrupt Mask to initial states
        interrupt_1 = interrupt_2 = false;
        maskingInterrupts = false;
        nonMaskableInterrupt = false;
        
        // Set the Program Counter to the initial program address and set initial State
        programCounter = resetAddress;
        states = 0;
        
    }
    
    /**
     * Decode the current opcode
     * 
     * @param opcode
     *          The Opcode that needs to be decoded
     *          
     * @throws CPUException
     */
    private void decodeOpcode(int opcode) throws CPUException
    {
        states = states + OpcodeStateTables.getOpcodeTState(opcode);
    }
    
    public DataPack getDataPack()
    {        
        sendHalfRegData[0] = getHalfRegisterContents(HalfRegisters.A);
        sendHalfRegData[1] = getHalfRegisterContents(HalfRegisters.B);
        sendHalfRegData[2] = getHalfRegisterContents(HalfRegisters.C);
        sendHalfRegData[3] = getHalfRegisterContents(HalfRegisters.D);
        sendHalfRegData[4] = getHalfRegisterContents(HalfRegisters.E);
        sendHalfRegData[5] = getHalfRegisterContents(HalfRegisters.H);
        sendHalfRegData[6] = getHalfRegisterContents(HalfRegisters.L);
        sendHalfRegData[7] = getHalfRegisterContents(HalfRegisters.F);
        sendHalfRegData[8] = getHalfRegisterContents(HalfRegisters.GHOST_A);
        sendHalfRegData[9] = getHalfRegisterContents(HalfRegisters.GHOST_B);
        sendHalfRegData[10] = getHalfRegisterContents(HalfRegisters.GHOST_C);
        sendHalfRegData[11] = getHalfRegisterContents(HalfRegisters.GHOST_D);
        sendHalfRegData[12] = getHalfRegisterContents(HalfRegisters.GHOST_E);
        sendHalfRegData[13] = getHalfRegisterContents(HalfRegisters.GHOST_H);
        sendHalfRegData[14] = getHalfRegisterContents(HalfRegisters.GHOST_L);
        sendHalfRegData[15] = getHalfRegisterContents(HalfRegisters.GHOST_F);
        
        sendFullRegData[0] = getRegisterContents(FullRegisters.BC);
        sendFullRegData[1] = getRegisterContents(FullRegisters.DE);
        sendFullRegData[2] = getRegisterContents(FullRegisters.HL);
        sendFullRegData[3] = getRegisterContents(FullRegisters.GHOST_BC);
        sendFullRegData[4] = getRegisterContents(FullRegisters.GHOST_DE);
        sendFullRegData[5] = getRegisterContents(FullRegisters.GHOST_HL);
        
        sendOtherData[0] = getRegisterContents(FullRegisters.IX);
        sendOtherData[1] = getRegisterContents(FullRegisters.IY);
        sendOtherData[2] = getRegisterContents(FullRegisters.PC);
        sendOtherData[3] = getRegisterContents(FullRegisters.SP);
        sendOtherData[4] = getRegisterContents(FullRegisters.I);
        sendOtherData[5] = getRegisterContents(FullRegisters.R);
        sendOtherData[6] = indexer;
        
        return new DataPack(sendHalfRegData, sendFullRegData, sendOtherData);
    }
}
