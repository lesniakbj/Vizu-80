package emu80;

public class Timers 
{	
	// Instruction Timers
	public char instructMClock;
	public char instructTClock;
	
	// Global Timers
	public char mClock;
	public char tClock;
	
	public Timers()
	{
		this.instructMClock = 0x00;
		this.instructTClock = 0x00;
		
		this.mClock = 0x00;
		this.tClock = 0x00;
	}
}
