package emu80;

public class CPUCore 
{
	private Registers registerBank;
	
	public CPUCore()
	{
		registerBank = new Registers();
	}
	
	public static void main(String[] args)
	{
		CPUCore cpu = new CPUCore();
		System.out.println("Register Bank State: ");
		System.out.println( cpu.registerBank.toString() );
	}
}
