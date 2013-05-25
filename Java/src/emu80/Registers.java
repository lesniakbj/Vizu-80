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
	
	// Global Flags
	public boolean zeroFlag;
	public boolean subFlag;
	public boolean halfCarryFlag;
	public boolean carryFlag;
	
	public Registers()
	{
		initRegs();
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
	}
	
	private void initFlags()
	{
		this.zeroFlag = false;
		this.subFlag = false;
		this.halfCarryFlag = false;
		this.carryFlag = false; 
	}
}