package emu80;

public class CPUCore 
{
	// CPU Components (Opcodes, Registers & Flags, Clocks, Memory)
	private static Opcode[] opcodes;
	private static Registers registerBank;
	private static Timers timers;
	private static Memory memoryBank;
	
	private char programCounter;
	private char stackPointer;
	
	public CPUCore()
	{
		opcodes = Opcode.initOpcodes();
		registerBank = new Registers();
		timers = new Timers();
		memoryBank = new Memory();
		
		programCounter = 0;
		stackPointer = 0;
	}
	
}
