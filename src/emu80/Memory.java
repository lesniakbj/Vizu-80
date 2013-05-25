package emu80;

public class Memory 
{
	private static char[] bios; 		// 0x0000 - 0x00FF -- Unmapped after use (256 Bytes)
	private static char[] rom; 			// 0x0000 - 0x7FFF -- Split (ROM0 = 0x3FFF, ROM1 = 0x7FFF) 
							   			// 					 ROM0 - 16k 	ROM1 - 16k
	private static char[] gpuRAM; 		// 0x8000 - 0x9FFF -- Graphics RAM 
	private static char[] externalRAM; 	// 0xA000 - 0xBFFF -- Cartridge RAM, Extends base RAM (8k)
	private static char[] workingRAM; 	// 0xC000 - 0xFDFF -- Main system RAM (16k)
									  	// 		 Main RAM - 8k		Shadow Ram - 8k
	private static char[] zeroPageRAM; 	// 0xFF80 - 0xFFFF -- Zero Page RAM 
	
	public Memory()
	{
		bios = new char[0x00FF];
		rom = new char[0x7FFF];
	}
	
	public char readByte(char address)
	{
		return (Character) null;
	}
}