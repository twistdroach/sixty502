/**
 * Parser.java
 * Static class that handles all regex and parsing.
 *
 * @author Christopher Erickson
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    /**
     * Returns true if line is an instruction. False otherwise.
     * 
     * @param line The line to be parsed.
     * @return True if line contains an instruction.
     */
    public static boolean isInstruction( String line ) {
        // This is not perfect and needs to accomodate symbols        
        // Setup instruction regex                Label       Inst          Operand         Comment
        Pattern instruction = Pattern.compile( "^(\\w+:)?\\s*(\\w{3})(\\s+[\\w#<>%\\$]+)?\\s*(;.*)?$" );
        Matcher regex = instruction.matcher( line );
        if ( regex.find() ) {
            // For testing, print the instruction
            System.out.println( regex.group( 2 ) );
            return true;
        }
        return false;
    }
}
