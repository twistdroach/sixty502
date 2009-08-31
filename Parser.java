/**
 * Parser.java
 * Static class that handles all regex and parsing.
 *
 * @author Christopher Erickson
 */

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser
{
	/* Class Variables */
	// Instruction classification                            Label       Inst              Operand                  Offset         Comment
	private static Pattern instPattern = Pattern.compile( "^(\\w+:)?\\s*(\\w{3})\\s*(\\(?[\\w#<>%\\$]+\\)?)?[\\s,]*([xXyY]\\)?)?\\s*(;.*)?$" );
	
    /**
     * Parses instructions from program data.
     * 
     * @param program A Vector containing strings to be parsed for instructions.
     * @return A Vector of Instructions.
     */
    public static Vector<Instruction> getInstructions( Vector<String> program )
    {
    	Vector<Instruction> instructions = new Vector<Instruction>();
    	String line;
    	for ( int i = 0; i < program.size(); ++i )
    	{
    		line = program.get( i );
    		
    		String label;
    		String opcode;
    		String operand;
    		String offset;
    		String comment;

    		// Compare this line of the program with instruction syntax
            Matcher matched = instPattern.matcher( line );
            if ( matched.find() )
            {              
                // Create and append the instruction to the list
                label = matched.group( 1 );
                opcode = matched.group( 2 );
                operand = matched.group( 3 );
                offset = matched.group( 4 );
                comment = matched.group( 5 );
                
                instructions.add( new Instruction( label, opcode, operand, offset, comment ) );
            }
    	}
    	return instructions;
    }
    
    /**
     * Returned either the immediate value of the operand as a Byte or the Byte referenced
     * by the address value of the operand.
     * 
     * @param operand The operand in string form.
     * @param offset The offset (if it exists) in string form. ( "x" or "y" )
     * @return Byte referenced or immediate value in Byte form.
     */
    public static Byte getReferenced( String operand, String offset )
    {
    	if ( operand == null )
    	{
    		return null; // No operand
    	}

    	// Determine whether this value is immediate or not
    	if ( operand.charAt( 0 ) == '#' )
    	{
    		// This is an immediate value. There will be no offset.
   			return new Byte( operand.substring( 1, operand.length() ) );
    	}
    	else
    	{
    		// Find out if this is going to be a dereferenced value
    		if ( operand.charAt( 0 ) == '(' )
    		{
    			// Indirect addresses must have an offset
    			if ( offset == null )
    			{
    				// Syntax error
    				return null;
    			}
    			
    			// We're going to dereference something, let's figure out what
    			if ( operand.charAt( operand.length() - 1 ) == ')' )
    			{
    				// Only dereferencing the operand - This means is has to be a Byte
    				operand = operand.substring( 1, operand.length() - 1 );
    				// Get the referenced Word in memory
    				Word byteAddr = Memory.getWord( new Byte( operand ) );
    				// Add the offset (must be y, so we'll assume)
    				int finalAddr = byteAddr.getVal() + Processor.Y.getVal();
    				// Return the referenced Byte
    				return Memory.getByte( new Word( finalAddr ) );
    			}
    			else if ( offset.charAt( offset.length() - 1 ) == ')' )
    			{
    				// Dereference the operand + offset - Also has to be a Byte
    				operand = operand.substring( 1, operand.length() );
    				// Add the offset (must be x, so we'll assume)
    				int finalAddr = new Byte( operand ).getVal() + Processor.X.getVal();
    				// Get the referenced Word in memory
    				Word byteAddr = Memory.getWord( new Byte( finalAddr ) );
    				// Return the referenced Byte
    				return Memory.getByte( byteAddr );
    			}
    			else
    			{
    				// Syntax error
    				return null;
    			}
    		}
    		else
    		{
    			// Nothing will be dereferenced
    			if ( offset == null )
    			{
    				// Could be word or byte
    				return Memory.getByte( new Word( operand ) );
    			}
    			
    			if ( offset.charAt( 0 ) == 'x' || offset.charAt( 0 ) == 'X' )
    			{
    				return Memory.getByte( new Word ( new Byte( operand ).getVal() + Processor.X.getVal() ) );
    			}
    			else if ( offset.charAt( 0 ) == 'y' || offset.charAt( 0 ) == 'Y' )
    			{
        			return Memory.getByte( new Word ( new Byte( operand ).getVal() + Processor.Y.getVal() ) );
    			}
    			else
    			{
    				// Syntax error
    				return null;
    			}
    		}
    	}
    }
}
