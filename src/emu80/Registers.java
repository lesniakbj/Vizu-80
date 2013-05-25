package emu80;


public class Registers 
{
	// 8-Bit Registers
	// Note: char = 16-Bits (0 - 65,535)
	// 		 USE LOWER 8 BITS (0x00nn)
	public char A;
	public char B;
	public char C;
	public char D;
	public char E;
	public char H;
	
	// 16-Bit Registers
	public char programCounter;
	public char stackPointer;
	
	// Instruction Timers
	public char instructMClock;
	public char instructTClock;
	
	// Global Timers
	public char mClock;
	public char tClock;
	
	// Global Flags
	public boolean zeroFlag;
	public boolean subFlag;
	public boolean halfCarryFlag;
	public boolean carryFlag;
	
	public Registers()
	{
		initRegs();
		initTimers();
		initFlags();
	}
	
	private void initRegs()
	{
		this.A = 0x00;
		this.B = 0x00;
		this.C = 0x00;
		this.D = 0x00;
		this.E = 0x00;
		this.H = 0x00;
		
		this.programCounter = 0x0000;
		this.stackPointer = 0x0000;
	}
	
	private void initTimers()
	{
		this.instructMClock = 0x00;
		this.instructTClock = 0x00;
		
		this.mClock = 0x00;
		this.tClock = 0x00;
	}
	
	private void initFlags()
	{
		this.zeroFlag = false;
		this.subFlag = false;
		this.halfCarryFlag = false;
		this.carryFlag = false; 
	}
	
	public String toString()
	{
		String str = "";
		str += "Register: \t Value: \n";
		str += "--------- \t ------ \n";
		str += "A:        \t " + (int) this.A + " \n";
		
		return str;
	}
}