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
    private int             indexRegister_1, indexRegister_2, programCounter, stackPointer;
    private int             interruptRegister, refreshRegister;
    private int             indexer;
    
    // Flags, Addresses, and Booleans -- Processor Control Variables
    private boolean         blocked;
    private boolean         isReset;
    private int             resetAddress;
    private boolean         interrupt_1, interrupt_2;
    private boolean         maskingInterrupts;
    private boolean         nonMaskableInterrupt;

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
        resetAddress = 0x0000;
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
    private static int count = 0;
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
                //return getBC();
            case DE:
                //return getDE();
            case HL:
                //return getHL();
            case GHOST_BC:
                //return getBC_ALT();
            case GHOST_DE:
                //return getDE_ALT();
            case GHOST_HL:
                //return getHL_ALT();
            case IX:
                //return reg_IX;
            case IY:
                //return reg_IY;
            case SP:
                //return getSP();
            case PC:
                //return reg_PC;
            case A:
                //return reg_A;
            case F:
                //return reg_F;
            case GHOST_A:
                //return reg_A_ALT;
            case GHOST_F:
                //return reg_F_ALT;
            case I:
                //return reg_I;
            case R:
                //return reg_R;
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
     * Runs the reset process of the CPU. First resets all of the Registers to their initial states, followed
     * by the 2 Index Registers and Stack Pointers. Resets the Interrupt and Refresh Registers, sets Interrupt
     * 1 and 2 to their initial states, and stop masking all interrupts. Finally, set the Program Counter to
     * the saved Reset Address, and set the initial CPU States.
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
        indexRegister_1 = indexRegister_2 = stackPointer = 0x00;
        
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
        int[] sendRegData = new int[16]; // A BC DE HL F     x 2
        int[] sendOtherData = new int[7]; // IX IY PC SP IR RR IDX
        
        sendRegData[0] = registers[0];
        sendRegData[1] = registers[1];
        sendRegData[2] = registers[2];
        sendRegData[3] = registers[3];
        sendRegData[4] = registers[4];
        sendRegData[5] = registers[5];
        sendRegData[6] = registers[6];
        sendRegData[7] = registers[7];
        sendRegData[8] = ghostRegisters[0];
        sendRegData[9] = ghostRegisters[1];
        sendRegData[10] = ghostRegisters[2];
        sendRegData[11] = ghostRegisters[3];
        sendRegData[12] = ghostRegisters[4];
        sendRegData[13] = ghostRegisters[5];
        sendRegData[14] = ghostRegisters[6];
        sendRegData[15] = ghostRegisters[7];
        
        
        sendOtherData[0] = indexRegister_1;
        sendOtherData[1] = indexRegister_2;
        sendOtherData[2] = programCounter;
        sendOtherData[3] = stackPointer;
        sendOtherData[4] = interruptRegister;
        sendOtherData[5] = refreshRegister;
        sendOtherData[6] = indexer;
        
        return new DataPack(sendRegData, sendOtherData);
    }
}
