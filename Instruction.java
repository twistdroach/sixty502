/**
 * Instruction.java
 * Stores the parts of each instruction for use in execution.
 *
 * @author Christopher Erickson
 */

public abstract class Instruction
{
	private int opcode;
	private String instr;
	private String[] addrModes;
	
	public String[] getAddrModes()
	{
		return addrModes;
	}
	
	public abstract void run();
}
