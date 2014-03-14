package src.cpu;

import src.DataPack;

/**
 * Concrete implementation of the ZiLOG z80 CPU Core,emulating all of the known instruction set.
 * Attempts to implement all of the undocumented functionality of the processor as well.
 * 
 * 
 * @author Brendan Lesniak
 * @version 0.1.a
 */
public class Z80Core implements ICPU
{
    private static final String name = "ZiLOG z80 Visual Emulator";
    private static final int version = 0;
    private static final int versionMinor = 0;
    private static final String versionPatch = "a";
    
    private static final int SIGN_MASK          = 0b10000000;                       // 1 0 0 0 0 0 0 0 -- 128                                                                
    private static final int RESET_SIGN_MASK    = ~SIGN_MASK;           // ~10000000 = 0 1 1 1 1 1 1 1 
                    
    private static final int ZERO_MASK          = 0b01000000;                       // 0 1 0 0 0 0 0 0 -- 64
    private static final int RESET_ZERO_MASK    = ~ZERO_MASK;           // ~01000000 = 1 0 1 1 1 1 1 1
                                                                            
    private static final int HALF_CARRY_MASK    = 0b00010000;                       // 0 0 0 1 0 0 0 0 -- 16
    private static final int RESET_HALF_MASK    = ~HALF_CARRY_MASK;     // ~00010000 = 1 1 1 0 1 1 1 1 
                                                                            
    private static final int OFLOW_PARITY_MASK  = 0b00000100;                       // 0 0 0 0 0 1 0 0 -- 4
    private static final int RESET_OFLOW_MASK   = ~OFLOW_PARITY_MASK;   // ~00000100 = 1 1 1 1 1 0 1 1
    
    private static final int SUBTRACT_MASK      = 0b00000010;                       // 0 0 0 0 0 0 1 0 -- 2
    private static final int RESET_SUB_MASK     = ~SUBTRACT_MASK;       // ~00000010 = 1 1 1 1 1 1 0 1
    
    private static final int CARRY_MASK         = 0b00000001;                       // 0 0 0 0 0 0 0 1 -- 1
    private static final int RESET_CARRY_MASK   = ~CARRY_MASK;          // ~00000001 = 1 1 1 1 1 1 1 0 
    
    // Externalized parts of the CPU, defined when the z80 CPU is constructed, interfaces (need to be implemented)
    private IMemory         systemRAM;
    private IDevice         systemIO;
    private static final int maxAddressSpace    = 0xFFFF;   // 64 KB
    
    // Internalized CPU Flags and State
    private int             currentOpcode;
    private boolean         halted;
    private long            cycles;
    
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
    
    // DATA! -- DATA! -- DATA!
    int[] sendHalfRegData       = new int[16];  // A BC DE HL F     x 2
    int[] sendFullRegData       = new int[6];   // BC DE HL         x 2
    int[] sendOtherData         = new int[7];   // IX IY PC SP IR RR IDX

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
        cycles = 0;
        
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
        cycles = 0;
        
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
     * Public facing method to find the current cycle count of the CPU.
     * 
     * @since 0.0.a
     */
    public long getCycles()
    {
        return cycles;
    }
    
    /**
     * Public facing method to reset CPU cycles back to their initial state.
     * 
     * @since 0.0.a
     */
    public void resetCycles()
    {
        cycles = 0;
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
     * Runs the reset process of the CPU. <li>First resets all of the Registers to their initial cycles, followed
     * by the 2 Index Registers and Stack Pointers.</li> <li>Resets the Interrupt and Refresh Registers, sets Interrupt
     * 1 and 2 to their initial cycles, and stop masking all interrupts.</li> <li>Finally, set the Program Counter to
     * the saved Reset Address, and set the initial CPU cycles.</li>
     * 
     * @since 0.0.a
     */
    private void runResetProcess()
    {
        // Processor is not currently halted
        halted = false;
        
        // Reset all Registers and Ghost Registers back to their initial cycles
        for(int i = 0; i < registers.length; i++)
            registers[i] = 0x00;
            
        for(int i = 0; i < ghostRegisters.length; i++)
            ghostRegisters[i] = 0x00;
        
        // TEST DATA
        registers[0] = 0x0A;
        registers[1] = 0xBB;
        registers[2] = 0xF7;
        registers[7] = 0x06;
        // Reset all Index and Stack Pointers back to their intial cycles
        index_x = index_y = stackPointer = 0x0390;
        
        // Reset Interrupt and Refresh registers to their initial cycles
        interruptRegister = refreshRegister = 0x28;
        
        // Set Interrupt Flag 1 & 2 and Interrupt Mask to initial cycles
        interrupt_1 = interrupt_2 = false;
        maskingInterrupts = false;
        nonMaskableInterrupt = false;
        
        // Set the Program Counter to the initial program address and set initial State
        programCounter = resetAddress;
        cycles = 0;
        
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
        cycles = cycles + OpcodeStateTables.getOpcodeCycleTimeState(opcode);
        
        switch(opcode)
        {
            case 0x00:  // NOP
            {
                break;  // Program Counter already incremented in executeInstruction()
            }
                
            case 0x01:  // LD BC, nn -- Load BC with word nn from system RAM, pointed to by the program counter
            {
                loadFull(FullRegisters.BC, systemRAM.readWord(programCounter));
                programCounter += 2;
                programCounter = programCounter & maxAddressSpace;
                break;
            }
            
            case 0x02: // LD (BC), a -- Stores a into the system RAM location pointed to by BC
            {
                systemRAM.writeByte(getRegisterContents(FullRegisters.BC), registers[0]);
                break;
            }
            
            case 0x03: // INC BC -- Adds one to BC
            {
                loadFull(FullRegisters.BC, ALU_incWord(getRegisterContents(FullRegisters.BC)));
                break;
            }
            
            case 0x04: // INC B -- Adds one to B
            {
                loadHalf(HalfRegisters.B, ALU_incByte(getHalfRegisterContents(HalfRegisters.B)));
                break;
            }
            
            case 0x05: // DEC B -- Decrement B by 1
            {
                loadHalf(HalfRegisters.B, ALU_decByte(getHalfRegisterContents(HalfRegisters.B)));
                break;
            }
            
            case 0x06: // LD B, n -- Loads n from system RAM into Byte Register B
            {
                loadHalf(HalfRegisters.B, systemRAM.readByte(programCounter));
                programCounter += 1;
                programCounter = programCounter & maxAddressSpace;
            }
            
            case 0x11: // LD DE, nn -- Load DE with word nn from system RAM, pointed to by the program counter
            {
                loadFull(FullRegisters.DE, systemRAM.readWord(programCounter));
                programCounter += 2;
                programCounter = programCounter & maxAddressSpace;
                break;
            }
            
            case 0x14: // INC D -- Adds one to D
            {
                loadHalf(HalfRegisters.D, ALU_incByte(getHalfRegisterContents(HalfRegisters.D)));
                break;
            }
            
            case 0x21: // LD HL, nn -- Load HL with word nn from system RAM, pointed to by the program counter
            {
                loadFull(FullRegisters.HL, systemRAM.readWord(programCounter));
                programCounter += 2;
                programCounter = programCounter & maxAddressSpace;
                break;
            }
                        
            case 0x24: // INC H -- Adds one to H
            {
                loadHalf(HalfRegisters.H, ALU_incByte(getHalfRegisterContents(HalfRegisters.H)));
                break;
            }
            
            case 0x31: // LD SP, nn -- Load stack pointer with word nn from system RAM, pointed to by the program counter
            {
                loadFull(FullRegisters.SP, systemRAM.readWord(programCounter));
                programCounter += 2;
                programCounter = programCounter & maxAddressSpace;
                break;
            }
        }
    }
    
    // Upper Register ... 11111111 00000000     (0xFF00) & 
    //                    10011010 00010110     (data)
    //                    =================
    //                    10011010 00000000     >> 8 bits right
    //                    =================
    //                             10011010     (Upper Register = Upper byte)
    //
    //
    // Lower Register ... 00000000 11111111     (0x00FF) & 
    //                    10011010 00010110     (data)
    //                    =================
    //                    00000000 00010110     No Shift, drop top 8 bits
    //                    =================
    //                             00010110     (Lower Register = Lower byte)
    private void loadFull(FullRegisters reg, int data)
    {
        switch(reg)
        {
            case BC: // Upper - Lower
            {
                registers[1] = (data & 0xFF00) >> 8;    // Register B ... 11111111 00000000 (0xFF00) & 
                                                        //                10011010 00010110 (data)
                                                        //                =================
                                                        //                10011010 00000000
                                                        //                =================
                                                        //                         10011010 (Upper byte)
                
                registers[2] = data & 0x00FF;           // Register C  
            }
            
            case DE:
            {
                registers[3] = (data & 0xFF00) >> 8;
                registers[4] = data & 0x00FF;
            }
            
            case HL:
            {
                registers[5] = (data & 0xFF00) >> 8;
                registers[6] = data & 0x00FF;
            }
            
            case SP:
            {
                stackPointer = data;
            }
        }
    }
    
    private void loadHalf(HalfRegisters reg, int data)
    {
        switch(reg)
        {
            case B:
            {
                registers[1] = data & 0xFF;
            }
            
            case D:
            {
                registers[3] = data & 0xFF;
            }
            
            case H:
            {
                registers[5] = data & 0xFF;
            }
        }
    }
    
    private int ALU_incWord(int data)
    {
        data++;
        return (data & 0x0000FFFF);
    }
    
    private int ALU_incByte(int data)
    {
        if(getFlag(Flags.CARRY))                // Was there a carry?
            registers[7] = 0x01;                // Set the initial state of the Flags 
        else
            registers[7] = 0x00;
           
        setHalfCarryFlagAddition(data, 0x01);   // Set if there will be a half carry, will the bottom 4 bits overflow
        setOverflowFlag(data == 0x7F);          // Set if the data will overflow to the upper 8 bits
        
        data++;                                 // Incrememnt the data
        setSignFlag((data & SIGN_MASK) != 0);   // Set if there was a change in sign due to the incrememnt
        
        data = data & 0xFF;                     // Mask the data back down to one byte
        setZeroFlag(data == 0);                 // Set if the data is == to 0
        
        unsetFlag(Flags.SUBTRACT);
        //setOtherFlags(data);                  // Set the undocumented flags
            
        return data;
    }
    
    private int ALU_decByte(int data)
    {
        if(getFlag(Flags.CARRY))
            registers[7] = 0x01;
        else
            registers[7] = 0x00;
            
        setHalfCarryFlagSubtraction(data, 0x01);
        setOverflowFlag(data == 0x80);              // Set if the data will underflow to the lower 7 bits
        
        data--;
        setSignFlag((data & SIGN_MASK) != 0);
        
        data = data & 0xFF;
        setZeroFlag(data == 0);
        
        setFlag(Flags.SUBTRACT);
        //setOthe
        
        return data;
    }
    
     private void setFlag(Flags flag)
    {
        switch(flag)
        {
            case SIGN:
            {
                registers[7] = registers[7] | SIGN_MASK;
                break;
            } 
            
            case ZERO:
            {
                registers[7] = registers[7] | ZERO_MASK;
                break;
            }
            
            case HALF_CARRY:
            {
                registers[7] = registers[7] | HALF_CARRY_MASK;
                break;
            }
            
            case PARITY_OFLOW:
            {
                registers[7] = registers[7] | OFLOW_PARITY_MASK;
                break;
            }
            
            case SUBTRACT:
            {
                registers[7] = registers[7] | SUBTRACT_MASK;
                break;
            }
            
            case CARRY:
            {
                registers[7] = registers[7] | CARRY_MASK;
                break;
            }
        }
    }
            
    private void unsetFlag(Flags flag)
    {
        switch(flag)
        {
            case SIGN:
            {
                registers[7] = registers[7] & RESET_SIGN_MASK;
                break;
            } 
            
            case ZERO:
            {
                registers[7] = registers[7] & RESET_ZERO_MASK;
                break;
            }
            
            case HALF_CARRY:
            {
                registers[7] = registers[7] & RESET_HALF_MASK;
                break;
            }
            
            case PARITY_OFLOW:
            {
                registers[7] = registers[7] & RESET_OFLOW_MASK;
                break;
            }
            
            case SUBTRACT:
            {
                registers[7] = registers[7] & RESET_SUB_MASK;
                break;
            }
            
            case CARRY:
            {
                registers[7] = registers[7] & RESET_CARRY_MASK;
                break;
            }
        }
    }
    
    private boolean getFlag(Flags flag)
    {
        switch(flag)
        {
            case SIGN:
            {
                return (registers[7] & SIGN_MASK) != 0;         // 00110100 (data) & 10000000 (mask) == 1 if bit 0 set, 0 if not
            } 
            
            case ZERO:
            {
                return (registers[7] & ZERO_MASK) != 0;         // 00110100 (data) & 01000000 (mask) == 1 if bit 0 set, 0 if not
            }
            
            case HALF_CARRY:
            {
                return (registers[7] & HALF_CARRY_MASK) != 0;   // 00110100 (data) & 00010000 (mask) == 1 if bit 0 set, 0 if not
            }
            
            case PARITY_OFLOW:
            {
                return (registers[7] & OFLOW_PARITY_MASK) != 0; // 00110100 (data) & 00000100 (mask) == 1 if bit 0 set, 0 if not
            }
            
            case SUBTRACT:
            {
                return (registers[7] & SUBTRACT_MASK) != 0;     // 00110100 (data) & 00000010 (mask) == 1 if bit 0 set, 0 if not
            }
            
            case CARRY:
            {
                return (registers[7] & CARRY_MASK) != 0;        // 00110100 (data) & 00000001 (mask) == 1 if bit 0 set, 0 if not
            }
        }
        
        return false;
    }
    
    private void setHalfCarryFlagAddition(int data, int dataAdded)
    {
        data = data & 0x0F;             // Clear upper 4 bits of data to 0
        dataAdded = dataAdded & 0x0F;   // Clear upper 4 bits of dataAdded to 0
        
        setHalfCarryFlag((data + dataAdded) > 0x0F);   // If there was a half cary, there will be a bit present at bit 0:
                                                        // (data + dataAdded >> 0x0F) == 1
    }
    
    private void setHalfCarryFlagSubtraction(int data, int dataHalf)
    {
        data = data & 0x0F;
        dataHalf = dataHalf & 0x0F;
        
        setHalfCarryFlag(data < dataHalf);
    }

    private void setOverflowFlag(boolean overflow)
    {
        if(overflow)
            setFlag(Flags.PARITY_OFLOW);
        else
            unsetFlag(Flags.PARITY_OFLOW);
    }
    
    private void setZeroFlag(boolean zero)
    {
        if(zero)
            setFlag(Flags.ZERO);
        else
            unsetFlag(Flags.ZERO);
    }
    
    private void setCarryFlag(boolean carried)
    {
        if(carried)
            setFlag(Flags.CARRY);
        else
            setFlag(Flags.CARRY);
    }
    
    private void setSignFlag(boolean signBit)
    {
        if(signBit)
            setFlag(Flags.SIGN);
        else
            unsetFlag(Flags.SIGN);
    }
    
    private void setHalfCarryFlag(boolean carried)
    {
        if(carried)
            setFlag(Flags.HALF_CARRY);
        else
            unsetFlag(Flags.HALF_CARRY);
    }
    
    public static String getVersion()
    {
        return version + "." + versionMinor + "." + versionPatch;
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
