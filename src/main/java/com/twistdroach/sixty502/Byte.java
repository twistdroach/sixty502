package com.twistdroach.sixty502;

/**
 * Byte.java
 * A class that keeps track of a specific byte of information.
 *
 * @author Christopher Erickson
 */

public class Byte
{
    // This class stores a signed byte.
    private int value;
    
    /**
     * Creates a default Byte ( value = 0 )
     */
    public Byte()
    {
        value = 0;
    }
    
    /**
     * Creates a Byte initialized with a starting value.
     * 
     * @param startingVal The initial value as an integer.
     */
    public Byte( int startingVal )
    {
        setVal( startingVal );
    }

    /**
     * Creates a Byte initialized with a starting value.
     * 
     * @param startingVal The initial value as a string.
     */
    public Byte( String startingVal )
    {    
        setVal( startingVal );
    }
    
    /**
     * Sets the value stored in the Byte based on the string passed to it.
     * If string starts with a '%', it gets converted from binary.
     * If string starts with a '$', it gets converted from hex.
     * Otherwise, the string gets parsed and stored in value.
     * 
     * @param newVal New value to store in the Byte.
     * @return A Byte with bits corresponding to these flags:
     *  0 - Value is negative.
     *  1 - Value caused overflow.
     *  6 - Value is zero.
     */
    public Byte setVal( String newVal )
    {
        int val = 0;
        
        // If string begins with a %, it is binary
        if ( newVal.charAt(0) == '%' )
        {
            // Hack off the '%'
            newVal = newVal.substring( 1, newVal.length() );
            // Parse the binary
            val = Integer.parseInt( newVal, 2 );
        }
        else if ( newVal.charAt(0) == '$' )
        {
            // Hack off the '$'
            newVal = newVal.substring( 1, newVal.length() );
            // Parse the hex
            val = Integer.parseInt( newVal, 16 );
        }
        else
        {
            // Parse the decimal
            val = Integer.parseInt( newVal );
        }
        
        return setVal( val );
    }
    
    /**
     * Sets the value in the Byte to the decimal value newVal.
     * 
     * @param newVal New value to store in the Byte.
     * @return A Byte with bits corresponding to these flags:
     *  7 - Value is negative.
     *  6 - Value caused overflow.
     *  1 - Value is zero.
     */
    public Byte setVal( int newVal )
    {
    	Byte flags = new Byte();
    	
    	// Check for negative
    	if ( newVal < 0 )
    	{
    		// Set the negative flag
    		flags.setBit( Processor.P_N, true );
    	}

    	// Check for overflow
    	if ( newVal > 127 || newVal < -128 )
    	{
    		// Set the overflow flag
    		flags.setBit( Processor.P_V, true );
    	}
    	
    	// Check for zero
    	if ( newVal == 0 )
    	{
    		// Set the zero flag
    		flags.setBit( Processor.P_Z, true );
    	}
    	
    	 // This assumes java handles overflow correctly
    	value = (byte) newVal;
        return flags;
    }
    
    /**
     * Returns the value stored in the Byte as a decimal.
     * 
     * @return An integer representing the value stored.
     */
    public int getVal()
    {
        return value;
    }
    
    /**
     * Returns the value stored in the Byte as a binary string.
     * 
     * @return A string of binary, preceeded by a '%'.
     */
    public String getValBin()
    {
        String binVal = "%";
        // Pad left with 0s
        for ( int i = 128; i > 0; i/=2 )
        {
            if ( value < i )
                binVal += "0";
        }
        // Convert value to binary
        if ( value != 0 )   // Because of how padding works, we need this
            binVal += Integer.toBinaryString( value );
        return binVal;
    }
    
    /**
     * Returns the value stored in the Byte as a hexadecimal string.
     * 
     * @return A string of hexadecimal, preceeded by a '$'.
     */
    public String getValHex()
    {
        String hexVal = "$";
        // Pad left with zero if needed
        if ( value < 16 )
            hexVal += "0";
        // Convert value to hex
        hexVal += Integer.toHexString( value );
        return hexVal;
    }
    
    /**
     * Returns the value of the bit at (position).
     * 
     * @param position Bit position within the Byte ( 0 <= position <= 7 ).
     * @return True if bit is '1', false if bit is '0'.
     */
    public boolean getBit( int position )
    {
        // Disallow non-existent bits
        if ( position < 0 || position > 7 )
            return false;
        // Convert our value to binary
        String binVal = getValBin();
        // Get the bit at 8 - position
        // ( because of '%' and position of the lowest bit )
        char bit = binVal.charAt( 8 - position );
        if ( bit == '1' )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Sets or unsets bit at (position) based on newVal.
     * 
     * @param position Bit position within the Byte ( 0 <= position <= 7 ).
     * @param newVal If true, sets bit. If false, unsets bit.
     */
    public void setBit( int position, boolean newVal )
    {
        // Disallow non-existent bits
        if ( position < 0 || position > 7 )
            return;
        // Get the value of this bit
        int bitVal = (int) Math.pow( (double) 2, (double) position );
        // To set a bit, OR ( 2 ^ position ) with value
        if ( newVal )
        {
            value |= bitVal;
        }
        else
        {
            // To remove a bit, XOR ( 2 ^ position ) with value
        	if ( getBit( position ) == true )
        		value ^= bitVal;
        }
    }
}
