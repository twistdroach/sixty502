/**
 * Word.java
 * Defines the Word datatype.
 *
 * @author Christopher Erickson
 */

public class Word {
    // The two bytes that this Word is composed of
    private Byte low = new Byte();
    private Byte high = new Byte();
    
    /**
     * Creates a default Word ( value = 0 )
     */
    public Word() {
        setVal( 0 );
    }
    
    /**
     * Creates a Word initialized with a starting value.
     * 
     * @param startingVal The initial value as an integer.
     */
    public Word( int startingVal ) {
        setVal( startingVal );
    }

    /**
     * Creates a Word initialized with a starting value.
     * 
     * @param startingVal The initial value as a String.
     */
    public Word( String startingVal ) {
        setVal( startingVal );
    }

    /**
     * Returns the low Byte of the Word
     * 
     * @return The low Byte.
     */
    public Byte getLowByte() {
        return low;
    }
    
    /**
     * Returns the high Byte of the Word
     * 
     * @return The high Byte.
     */
    public Byte getHighByte() {
        return high;
    }
    
    /**
     * Sets the value stored in the Word based on the string passed to it.
     * If string starts with a '%', it gets converted from binary.
     * If string starts with a '$', it gets converted from hex.
     * Otherwise, the string gets parsed and stored in value.
     * 
     * @param newVal New value to store in the Word
     */
    public void setVal( String newVal ) {
        int value = 0;

        // If string begins with a %, it is binary
        if ( newVal.charAt(0) == '%' ) {
            // Hack off the '%'
            newVal = newVal.substring( 1, newVal.length() );
            // Parse the binary
            value = Integer.parseInt( newVal, 2 );
        } else if ( newVal.charAt(0) == '$' ) {
            // Hack off the '$'
            newVal = newVal.substring( 1, newVal.length() );
            // Parse the hex
            value = Integer.parseInt( newVal, 16 );
        } else {
            // Parse the decimal
            value = Integer.parseInt( newVal );
        }
        
        setVal( value );
    }

    /**
     * Sets the value in the Word to the decimal value newVal.
     * 
     * @param newVal New value to store in the Word.
     */
    public void setVal( int newVal ) {
        
        newVal &= 0xffff; // Word can represent up to $ffff
        
        // Store the high byte (Shift right 8 times)
        high.setVal( newVal >> 8 );
        // Store the low byte (AND with $00ff)
        low.setVal( newVal & 255 );
    }
    
    /**
     * Returns the value stored in the Word as a decimal.
     * 
     * @return An integer representing the value stored.
     */
    public int getVal() {
        // Shift the high byte to the left 8 times, then or it with the low
        return ( ( high.getVal() << 8 ) | low.getVal() );
    }
    
    /**
     * Returns the value stored in the Word as a binary string.
     * 
     * @return A string of binary, preceeded by a '%'
     */
    public String getValBin() {
        // Grab the binary version of the low byte
        String lowByte = low.getValBin();
        // Hack off the '%' and affix it to the high byte
        return ( high.getValBin() + lowByte.substring( 1, lowByte.length() ) );
    }
    
    /**
     * Returns the value stored in the Word as a hexadecimal string.
     * 
     * @return A string of hexadecimal, preceeded by a '$'
     */
    public String getValHex() {
        // Grab the hex version of the low byte
        String lowByte = low.getValHex();
        // Hack off the '$' and affix it to the high byte
        return ( high.getValHex() + lowByte.substring( 1, lowByte.length() ));
    }
}
