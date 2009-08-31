/**
 * Instruction.java
 * Stores the parts of each instruction for use in execution.
 *
 * @author Christopher Erickson
 */

public class Instruction
{
	/* Class Variables */
	private String label;
	private String opcode;
	private String operand;
	private String offset;
	private String comment;
	
    /**
     * Creates an instance of an Instruction.
     * Initializes class variables to their default values.
     */
	public Instruction()
	{
		label = "";
		opcode = "";
		operand = "";
		offset = "";
		comment = "";
	}
	
    /**
     * Creates an instance of an Instruction.
     * Initializes class variables to the passed in values.
     * 
     * @param inLabel Label on this instruction's line.
     * @param inOpcode 3-character opcode name.
     * @param inOperand String representing the operand's value/address.
     * @param inOffset String containing "x", "y", "x)", or "y" representing the offset, if any.
     * @param inComment Comment at end of instruction's line.
     */
	public Instruction( String inLabel, String inOpcode, String inOperand, String inOffset, String inComment )
	{
		label = inLabel;
		opcode = inOpcode;
		operand = inOperand;
		comment = inComment;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public String getOpcode()
	{
		return opcode;
	}
	
	public String getOperand()
	{
		return operand;
	}
	
	public String getOffset()
	{
		return offset;
	}
	
	public String getComment()
	{
		return comment;
	}
}
