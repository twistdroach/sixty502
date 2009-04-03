import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser.java
 * Static class that handles all regex and parsing.
 *
 * @author Christopher Erickson
 */
public class Parser {

	/**
	 * Returns true if line is an instruction. False otherwise.
	 * 
	 * @param line The line to be parsed.
	 * @return True if line contains an instruction.
	 */
	public static boolean isInstruction( String line ) {
		// Setup instruction regex               Instruction  Operand  Comment
		// This is not perfect and needs to accomodate symbols
		Pattern instruction = Pattern.compile( "^\\s*\\w{3}\\s+\\w*\\s*(;.*)?$" );
		Matcher regex = instruction.matcher( line );
		if ( regex.matches() )
			return true;
		return false;
	}
}
