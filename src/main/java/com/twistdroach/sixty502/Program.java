package com.twistdroach.sixty502;

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

public class Program
{
    /* Class Variables */
    private Vector<String> data;  // Stores the program text
    private Vector<Instruction> instructions; // Stores the program instructions, parsed

    /**
     * Creates an instance of Program.
     * Reads in fileName as a vector and indexes instructions.
     * 
     * @param fileName Location of the file to be read.
     */
    public Program( String fileName )
    {
        // Read the file in as a vector
        data = readInAsmFile( fileName );
        // Parse the instructions and index them
        instructions = Parser.getInstructions( data );
    }

    /**
     * Returns the instruction at the specified index.
     * 
     * @param instIndex Index of the instruction to return.
     * @return Instruction at instIndex.
     */
    public Instruction getInstruction( int instIndex )
    {
    	if ( instIndex < 0 || instIndex > instructions.size() )
    	{
    		// Out of bounds
    		return null;
    	}
    	return instructions.get( instIndex );
    }
    
    /**
     * Returns the number of instructions parsed out of the assembly.
     * 
     * @return Number of instructions parsed.
     */
    public int numInstructions()
    {
    	return instructions.size();
    }
    
    /**
     * Creates a vector from a 6502 asm file.
     * Based on code from:
     *   http://www.java2s.com/Code/Java/File-Input-Output/Filereadandwrite.htm
     * 
     * @param asmFile The full location of the input file.
     * @return Vector containing all the lines of the file.
     */
    public Vector<String> readInAsmFile( String asmFile )
    {
        // Okay, maybe there's room for just a little Spaceballs humor :)
        Vector<String> victor = new Vector<String>();
        String curLine;
        try
        {
            File asm = new File( asmFile );
            BufferedReader buffReader = new BufferedReader(
                                            new InputStreamReader(
                                                new FileInputStream( asm )));

            while ( ( curLine = buffReader.readLine() ) != null )
            {
                victor.addElement( curLine );
            }

            buffReader.close();
        }
        catch (FileNotFoundException ex)
        {
            System.out.println( "File not found!" );
        }
        catch (IOException ex)
        {
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
    public Vector<String> asmToVector( String asm )
    {
        Vector<String> victor = new Vector<String>();
        String[] asmString = asm.split( "\\n" );
        for ( int i = 0; i < asmString.length; ++i )
        {
            victor.addElement( asmString[i] );
        }
        return victor;
    }

    /**
     * Returns the program as a newline-delimited string.
     * 
     * @return theProgram as a String.
     */
    @Override
    public String toString()
    {
        String retString = "";
        for ( int i = 0; i < data.size(); ++i )
        {
            retString += data.get( i ) + "\n";
        }
        return retString;
    }
}
