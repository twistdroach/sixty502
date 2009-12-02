import java.util.ArrayList;
import java.util.HashMap;

/**
 * Parser.java
 * Interface for designing parser plug-ins
 *
 * @author Christopher Erickson
 */

interface Parser
{	
	public void parseFile( ArrayList<Integer> binary, String filename );
	public void parseString( ArrayList<Integer> binary, String input );
}