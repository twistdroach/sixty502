import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * AssemblyParser.java
 * Assembly file parser plug-in
 * 
 * Acts as a triple-bypass assembler.
 * Labels are CASE SENSITIVE and cannot be named A, X, Y, or nothing
 *
 * @author Christopher Erickson
 */

public class AssemblyParser implements Parser
{
	/**
	 * Parses a file containing assembly instructions
	 */
	@Override
	public void parseFile( ArrayList<Integer> binary, String filename )
	{
		Scanner fileIn = null;
		
    	// Set up an input stream on the file
        try
        {
        	fileIn = new Scanner( new FileInputStream( filename ) );
        }
        catch ( FileNotFoundException e )
        {
            System.out.println( "File not found!" );
            System.exit( 0 );
        }

        // Read the file
        String instructions = "";
        for ( int lineNum = 0; fileIn.hasNextLine(); ++lineNum )
        {
        	instructions = instructions + "\n" + fileIn.nextLine();
        }
        fileIn.close();        
        
        // Parse the assembly
        parseString( binary, instructions );
	}

	/**
	 * Parses a newline-delimited string of assembly instructions
	 */
	@Override
	public void parseString( ArrayList<Integer> binary, String input )
	{
		String[] lines = input.split( "\\n" );
		ArrayList<String> asm = new ArrayList<String>();		

		// Remove comments, whitespace, and blank lines to prepare for assemblage
		String prevLabel = "";
		boolean lastLineWasLabel = false;
		for ( int i = 0; i < lines.length; ++i )
		{
			lines[i] = lines[i].trim();
			
			// Skip blank lines and lines of purely comments
			if ( lines[i].equals( "" ) || lines[i].matches( ";.*" ) )
				continue;
			
			// Remove comments and append
			if ( lines[i].indexOf( ';' ) != -1 )
			{
				lines[i] = lines[i].substring( 0, lines[i].indexOf( ';' ) ).trim();
			}
			
			// Error if null label
			if ( lines[i].matches( ":.*" ) )
			{
				System.out.println( "(Error) Null label: " + lines[i] );
				System.exit( 0 );
			}

			// Merge labels on their own line with the instruction on the next
			// TODO: Make this work with redundant labels
			// TODO: Allow labels anywhere in the file (after instructions as well)
			//	i.e. LABEL1: LABEL2:
			if ( lines[i].matches( "\\w+:" ) )
			{
				prevLabel = lines[i].substring( 0, lines[i].indexOf( ':' ) );
				
				// Error if label is A, X, or Y
				if ( prevLabel.matches( "[AXYaxy]" ) )
				{
					System.out.println( "(Error) Illegal label name: " + prevLabel );
					System.exit( 0 );
				}
				
				lastLineWasLabel = true;
				continue;
			}
			else if ( lastLineWasLabel == true )
			{
				lines[i] = prevLabel + ": " + lines[i];
				lastLineWasLabel = false;
			}
			
			asm.add( lines[i] );
		}
		
		// First pass: Map defined labels to their line number
		HashMap<String,Integer> labels = new HashMap<String,Integer>();
		String line = null;
		String label = null;
		for ( int i = 0; i < asm.size(); ++i )
		{
			line = asm.get( i );
			
			// Look for labels
			if ( line.indexOf( ':' ) != -1 )
			{
				// Remove the label
				label = line.substring( 0, line.indexOf( ':' ) );
				asm.set( i, line.substring( line.indexOf( ':' ) + 1 ).trim() );
				
				// See if this label was already declared
				if ( labels.containsKey( label ) )
				{
					System.out.println( "(ERROR) Label declared twice: " + label );
					System.exit( 0 );
				}

				// label -> instruction number
				labels.put( label, i );
			}
		}
		
		// Second pass: Replace each instruction with its corresponding byte code, determine the value of labels
		//  Note: Zero Page - Label is unnecessary, see Absolute - Label
		ArrayList<Integer> opcodes = new ArrayList<Integer>();
		ArrayList<String> operands = new ArrayList<String>();
		ArrayList<Integer> addressmodes = new ArrayList<Integer>();
		HashMap<String,Integer> indexedLabels = new HashMap<String,Integer>();

		// Stores the starting memory address of each instruction
		int[] memoryAddress = new int[asm.size()];
		memoryAddress[0] = Memory.programStart;
		
		String instruction = null;
		String addrMode = null;
		String hashKey = null;
		for ( int i = 0; i < asm.size(); ++i )
		{
			String operand;
			line = asm.get( i );
			if ( line.length() == 3 )
			{
				instruction = line.substring( 0 );
				operand = null;
			}
			else
			{
				instruction = line.substring( 0, 3 );
				operand = line.substring( 3 ).trim();
			}
			
			// Determine the addressing mode
			if ( line.matches( "[A-Za-z]{3}" ) )
			{
				addrMode = "IMP";
				addressmodes.add( 0 );
			}
			else if ( line.matches( "[A-Za-z]{3}\\s+A" ) )
			{
				addrMode = "ACC";
				addressmodes.add( 1 );
			}
			else if ( line.matches( "[A-Za-z]{3}\\s+#\\d{1,3}" ) )
			{
				addrMode = "IMM";
				addressmodes.add( 2 );
			}
			else if ( line.matches( "[A-Za-z]{3}\\s+#\\$[0-9A-Fa-f]{1,2}" ) )
			{
				addrMode = "IMM";
				addressmodes.add( 3 );
			}
			else if ( line.matches( "[A-Za-z]{3}\\s+#(LO|<)\\s*[A-Za-z_]\\w*" ) )
			{
				addrMode = "IMM";
				addressmodes.add( 4 );
			}
			else if ( line.matches( "[A-Za-z]{3}\\s+#(HI|>)\\s*[A-Za-z_]\\w*" ) )
			{
				addrMode = "IMM";
				addressmodes.add( 5 );
			}
			else if ( line.matches( "[A-Za-z]{3}\\s+\\$[0-9A-Fa-f]{1,2}" ) )
			{
				addrMode = "ZPG";
				addressmodes.add( 6 );
			}
			else if ( line.matches( "[A-Za-z]{3}\\s+\\$[0-9A-Fa-f]{1,2}\\s*,\\s*[Xx]" ) )
			{
				addrMode = "ZPX";
				addressmodes.add( 7 );
			}
			else if ( line.matches( "[A-Za-z]{3}\\s+\\$[0-9A-Fa-f]{1,2}\\s*,\\s*[Yy]" ) )
			{
				addrMode = "ZPY";
				addressmodes.add( 8 );
			}
			else if ( line.matches( "[A-Za-z]{3}\\s+\\*[+-]\\d{1,3}" ) )
			{
				addrMode = "REL";
				addressmodes.add( 9 );
			}
			else if ( line.matches( "[Bb][^IiRr][A-Za-z]\\s+[A-Za-z_]\\w*" ) )
			{
				addrMode = "REL";
				addressmodes.add( 10 );
			}
			else if ( line.matches( "[A-Za-z]{3}\\s+\\$[0-9A-Fa-f]{1,4}" ) )
			{
				addrMode = "ABS";
				addressmodes.add( 11 );
			}
			else if ( line.matches( "[A-Za-z]{3}\\s+\\$[0-9A-Fa-f]{1,4}\\s*,\\s*[Xx]" ) )
			{
				addrMode = "ABX";
				addressmodes.add( 12 );
			}
			else if ( line.matches( "[A-Za-z]{3}\\s+\\$[0-9A-Fa-f]{1,4}\\s*,\\s*[Yy]" ) )
			{
				addrMode = "ABY";
				addressmodes.add( 13 );
			}
			else if ( line.matches( "[A-Za-z]{3}\\s+\\(\\s*\\$[0-9A-Fa-f]{1,4}\\s*\\)" ) )
			{
				addrMode = "IND";
				addressmodes.add( 14 );
			}
			else if ( line.matches( "[A-Za-z]{3}\\s+\\(\\s*[A-Za-z_]\\w*\\s*\\)" ) )
			{
				addrMode = "IND";
				addressmodes.add( 15 );
			}
			else if ( line.matches( "[A-Za-z]{3}\\s+\\(\\s*\\$[0-9A-Fa-f]{1,2}\\s*,\\s*[Xx]\\s*\\)" ) )
			{
				addrMode = "INX";
				addressmodes.add( 16 );
			}
			else if ( line.matches( "[A-Za-z]{3}\\s+\\(\\s*[A-Za-z_]\\w*\\s*,\\s*[Xx]\\s*\\)" ) )
			{
				addrMode = "INX";
				addressmodes.add( 17 );
			}
			else if ( line.matches( "[A-Za-z]{3}\\s+\\(\\s*\\$[0-9A-Fa-f]{1,2}\\s*\\)\\s*,\\s*[Yy]" ) )
			{
				addrMode = "INY";
				addressmodes.add( 18 );
			}
			else if ( line.matches( "[A-Za-z]{3}\\s+\\(\\s*[A-Za-z_]\\w*\\s*\\)\\s*,\\s*[Yy]" ) )
			{
				addrMode = "INY";
				addressmodes.add( 19 );
			}
			else if ( line.matches( "[A-Za-z]{3}\\s+[A-Za-z_]\\w*" ) )
			{
				addrMode = "ABS"; // Can use ABS for all cases of ZPG
				addressmodes.add( 20 );
			}
			else if ( line.matches( "[SsTtYy]\\s+[A-Za-z_]\\w*\\s*,\\s*[Xx]" ) )
			{
				addrMode = "ZPX"; // For STY - ABS, X does not exist
				addressmodes.add( 21 );
			}
			else if ( line.matches( "[SsTtXx]\\s+[A-Za-z_]\\w*\\s*,\\s*[Yy]" ) )
			{
				addrMode = "ZPY"; // For STX - ABS, Y does not exist
				addressmodes.add( 22 );
			}
			else if ( line.matches( "[A-Za-z]{3}\\s+[A-Za-z_]\\w*\\s*,\\s*[Xx]" ) )
			{
				addrMode = "ABX";
				addressmodes.add( 23 );
			}
			else if ( line.matches( "[A-Za-z]{3}\\s+[A-Za-z_]\\w*\\s*,\\s*[Yy]" ) )
			{
				addrMode = "ABY";
				addressmodes.add( 24 );
			}
			else
				addrMode = "FAIL";
			
			// If this instruction & addressing mode exist, get its byte code and length
			hashKey = instruction.toUpperCase() + " " + addrMode;
			
			if ( InstructionProperties.BYTECODE.containsKey( hashKey ) )
			{
				int opcode = InstructionProperties.BYTECODE.get( hashKey );

				opcodes.add( opcode );
				operands.add( operand );
				
				// Note what value labels will take on if prepended to this instruction
				if ( i < asm.size() - 1 )
					memoryAddress[i+1] = memoryAddress[i] + InstructionProperties.LENGTH.get( opcode );
				
				// Set label to memory address corresponding to this line
				if ( labels.containsValue( i ) )
				{
					String key = null;
					Map.Entry<String,Integer> pair = null;
					Iterator<Map.Entry<String,Integer>> it = labels.entrySet().iterator();
					while ( it.hasNext() )
					{
						pair = it.next();
						if ( pair.getValue() == i )
						{
							key = pair.getKey();
							break;
						}
					}
				
					if ( key != null )
					{
						// Set this label to its new, real value
						int val = memoryAddress[i];
						indexedLabels.put( key, val );
						labels.remove( key );
					}
				}
			}
			else
			{
				System.out.println( "(ERROR) Syntax error: " + line );
				System.exit( 0 );
			}			
		}
		
		// Check for left over labels - This signifies bad label placement
		if ( !labels.isEmpty() )
		{
			System.out.println( "(ERROR) Badly placed labels: " + labels.keySet() );
			System.exit( 0 );
		}
		
		System.out.println( "Indexed labels:" );
		System.out.println( indexedLabels );
		
		// Third pass: Replace labels and operands with their respective value in the form of bytes
		for ( int i = 0; i < opcodes.size(); ++i )
		{
			int opcode = opcodes.get( i );
			String operand = operands.get( i );
			int val = 0;
			
			binary.add( opcode );
			
			// Handle operands based on addressing mode
			switch ( addressmodes.get( i ) )
			{
				// Implicit modes
				case 0:	// Implicit
				case 1: // Accumulator
					break;
				case 2: // Immediate - Decimal
					operand = operand.substring( operand.indexOf( '#' ) + 1  );
					val = Integer.parseInt( operand );
					binary.add( val );
					break;
				case 3: // Immediate - Hex
				case 6: // Zero Page - Address
				case 11: // Absolute - Address
					operand = operand.substring( operand.indexOf( '$' ) + 1  );
					val = Integer.parseInt( operand, 16 );
					binary.add( val );
					break;
				case 4: // Immediate - Label - Low byte
					// Get rid of #LO or #<
					operand = operand.substring( operand.indexOf( '#' ) + 1  );
					if ( operand.charAt( 0 ) == 'L' )
						operand = operand.substring( 2 ).trim();
					else
						operand = operand.substring( 1 ).trim();
					
					// We're left with the label, get its low byte
					if ( indexedLabels.containsKey( operand ) )
					{
						val = indexedLabels.get( operand );
						val = val & 0xFF; // Mask off the low byte
						binary.add( val );
					}
					else
					{
						System.out.println( "(ERROR) Use of uninitialized label: " + operand );
						System.exit( 0 );
					}
					break;
				case 5: // Immediate - Label - High byte
					// Get rid of #HI or #>
					operand = operand.substring( operand.indexOf( '#' ) + 1  );
					if ( operand.charAt( 0 ) == 'H' )
						operand = operand.substring( 2 ).trim();
					else
						operand = operand.substring( 1 ).trim();
					
					// We're left with the label, get its high byte
					if ( indexedLabels.containsKey( operand ) )
					{
						val = indexedLabels.get( operand );
						val = val >> 8;   // Shift off the low byte
						val = val & 0xFF; // Mask
						binary.add( val );
					}
					else
					{
						System.out.println( "(ERROR) Use of uninitialized label: " + operand );
						System.exit( 0 );
					}
					break;
				case 7: // Zero Page, X - Address
				case 8: // Zero Page, Y - Address
				case 16: // Indirect, X - Address
					operand = operand.substring( operand.indexOf( '$' ) + 1, operand.indexOf( ',' ) ).trim();
					val = Integer.parseInt( operand, 16 );
					binary.add( val );
					break;
				case 9: // Relative - Address
					char sign = operand.charAt( operand.indexOf( '*' ) + 1 );
					operand = operand.substring( operand.indexOf( '*' ) + 2 );
					val = Integer.parseInt( operand );
					if ( sign == '-' )
						val = val ^ 0x80; // Mark the negative bit
					binary.add( val );
					break;
				case 10: // Relative - Label
					if ( indexedLabels.containsKey( operand ) )
					{
						// Make the absolute label relative
						val = indexedLabels.get( operand );
						if ( val > memoryAddress[i] )
						{
							val = val - memoryAddress[i];
						}
						else
						{
							val = memoryAddress[i] - val;
							val = val ^ 0x80; // Mark the negative bit
						}
						val = val & 0xFF;
						binary.add( val );
					}
					else
					{
						System.out.println( "(ERROR) Use of uninitialized label: " + operand );
						System.exit( 0 );
					}
					break;
				case 12: // Absolute, X - Address
				case 13: // Absolute, Y - Address
					operand = operand.substring( operand.indexOf( '$' ) + 1, operand.indexOf( ',' ) ).trim();
					// Absolute, pass low byte then high
					val = Integer.parseInt( operand, 16 );
					binary.add( val & 0xFF );
					val = val >> 8;
					binary.add( val & 0xFF );
					break;
				case 14: // Indirect - Address
					operand = operand.substring( operand.indexOf( '$' ) + 1, operand.indexOf( ')' ) ).trim();
					// Absolute, pass low byte then high
					val = Integer.parseInt( operand, 16 );
					binary.add( val & 0xFF );
					val = val >> 8;
					binary.add( val & 0xFF );
					break;
				case 15: // Indirect - Label
					operand = operand.substring( operand.indexOf( '(' ) + 1, operand.indexOf( ')' ) ).trim();
					
					if ( indexedLabels.containsKey( operand ) )
					{
						// This is absolute, pass low byte then high
						val = indexedLabels.get( operand );
						binary.add( val & 0xFF ); // Pass low byte
						val = val >> 8;
						binary.add( val & 0xFF ); // Pass high byte
					}
					else
					{
						System.out.println( "(ERROR) Use of uninitialized label: " + operand );
						System.exit( 0 );
					}
					break;
				case 17: // Indirect, X - Label
					operand = operand.substring( operand.indexOf( '(' ) + 1, operand.indexOf( ',' ) ).trim();
					
					if ( indexedLabels.containsKey( operand ) )
					{
						// Label is assumed zero page
						val = indexedLabels.get( operand );
						binary.add( val & 0xFF );
					}
					else
					{
						System.out.println( "(ERROR) Use of uninitialized label: " + operand );
						System.exit( 0 );
					}
					break;
				case 18: // Indirect, Y - Address
					operand = operand.substring( operand.indexOf( '$' ) + 1, operand.indexOf( ')' ) ).trim();
					val = Integer.parseInt( operand, 16 );
					binary.add( val );
					break;
				case 19: // Indirect, Y - Label
					operand = operand.substring( operand.indexOf( '(' ) + 1, operand.indexOf( ')' ) ).trim();
					
					if ( indexedLabels.containsKey( operand ) )
					{
						val = indexedLabels.get( operand );
						binary.add( val & 0xFF );
					}
					else
					{
						System.out.println( "(ERROR) Use of uninitialized label: " + operand );
						System.exit( 0 );
					}
					break;
				case 20: // Absolute - Label
					if ( indexedLabels.containsKey( operand ) )
					{
						// This is absolute, pass low byte then high
						val = indexedLabels.get( operand );
						binary.add( val & 0xFF ); // Pass low byte
						val = val >> 8;
						binary.add( val & 0xFF ); // Pass high byte
					}
					else
					{
						System.out.println( "(ERROR) Use of uninitialized label: " + operand );
						System.exit( 0 );
					}
					break;
				case 21: // Zero Page, X - Label
				case 22: // Zero Page, Y - Label
					operand = operand.substring( 0, operand.indexOf( ',' ) ).trim();
					
					if ( indexedLabels.containsKey( operand ) )
					{
						val = indexedLabels.get( operand );
						binary.add( val & 0xFF );
					}
					else
					{
						System.out.println( "(ERROR) Use of uninitialized label: " + operand );
						System.exit( 0 );
					}
					break;
				case 23: // Absolute, X - Label
				case 24: // Absolute, Y - Label
					operand = operand.substring( 0, operand.indexOf( ',' ) ).trim();
					
					if ( indexedLabels.containsKey( operand ) )
					{
						// This is absolute, pass low byte then high
						val = indexedLabels.get( operand );
						binary.add( val & 0xFF ); // Pass low byte
						val = val >> 8;
						binary.add( val & 0xFF ); // Pass high byte
					}
					else
					{
						System.out.println( "(ERROR) Use of uninitialized label: " + operand );
						System.exit( 0 );
					}
					break;
				// Not sure how we'd get here...
				default:
					break;
			}
		}
		
		System.out.println( "Assembled: ");
		for ( int i = 0; i < opcodes.size(); ++i )
		{
			System.out.print( memoryAddress[i] + ":" + addressmodes.get( i ) + ": " );
			for ( int j = 0; j < InstructionProperties.LENGTH.get( opcodes.get( i ) ); ++j )
				System.out.print( Integer.toHexString( binary.get( memoryAddress[i]-memoryAddress[0] + j ) ) + " " );
			System.out.println();
		}
		System.out.println( memoryAddress[1] );
	}
}