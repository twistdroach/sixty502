/**
 * Program.java
 * Handles all file reading, writing, and accessing
 *
 * @author Christopher Erickson
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class Program {
    /* Class Variables */
    Vector<String> theProgram;  // Stores the program.
    Vector<Integer> instTable;  // Keeps track of where instructions are located in theProgram.

    /**
     * Creates an instance of Program.
     * Reads in fileName as a vector and indexes instructions.
     * 
     * @param fileName Location of the file to be read.
     */
    public Program( String fileName ) {
        // Read the file in as a vector
        theProgram = asmFileToVector( fileName );
        // Parse the instructions and index them
        instTable = parseInstructions();
        System.out.println( "Number of instructions: " + instTable.size() );
    }

    /**
     * Creates a vector from a 6502 asm file.
     * Based on code from:
     *   http://www.java2s.com/Code/Java/File-Input-Output/Filereadandwrite.htm
     * 
     * @param asmFile The full location of the input file.
     * @return Vector containing all the lines of the file.
     */
    public Vector<String> asmFileToVector( String asmFile ) {
        // Okay, maybe there's room for just a little Spaceballs humor :)
        Vector<String> victor = new Vector<String>();
        String curLine;
        try {
            File asm = new File( asmFile );
            BufferedReader buffReader = new BufferedReader(
                                            new InputStreamReader(
                                                new FileInputStream( asm )));

            while ( ( curLine = buffReader.readLine() ) != null ) {
                victor.addElement( curLine );
            }

            buffReader.close();
        }
        catch (FileNotFoundException ex) {
            System.out.println( "File not found!" );
        }
        catch (IOException ex) {
            System.out.println( "I/O Error!" );
        }
        // Return the vector
        return victor;
    }

    /**
     * Creates a vector from a 6502 asm string.
     * 
     * @param asmString The newline-delimited string containing asm.
     * @return Vector containing all the lines of the file.
     */
    public Vector<String> asmToVector( String asm ) {
        Vector<String> victor = new Vector<String>();
        String[] asmString = asm.split( "\\n" );
        for ( int i = 0; i < asmString.length; i++ ) {
            victor.addElement( asmString[i] );
        }
        return victor;
    }

    /**
     * Creates an ArrayList indicative of the location of instructions in theProgram.
     * 
     * @return ArrayList containing the indicies of instructions.
     */
    public Vector<Integer> parseInstructions() {
        Vector<Integer> retArray = new Vector<Integer>();
        for ( int i = 0; i < theProgram.size(); i++ ) {
            if ( Parser.isInstruction( theProgram.get( i ) ) )
                retArray.add( i );
        }
        return retArray;
    }

    /**
     * Returns the program as a newline-delimited string.
     * 
     * @return theProgram as a String.
     */
@Override
    public String toString() {
        String retString = "";
        for ( int i = 0; i < theProgram.size(); i++ ) {
            retString += theProgram.get( i ) + "\n";
        }
        return retString;
    }
}
