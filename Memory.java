/**
 * Memory.java
 * Emulates word-addressable NES memory.
 *
 * @author Christopher Erickson
 */

public class Memory {
    /* Class variables */
    private Byte[] theMemory;   // Stores the bytes.
    
    /**
     * Creates an instance of a 64KB of memory.
     */
    public Memory() {
        theMemory = new Byte[65536];
    }
    
    /**
     * Retrieves a Byte from memory.
     * 
     * @param addr Word containing the address of the Byte to be retrieved.
     */
    public Byte getByte( Word addr ) {
        if ( addr.getVal() > 65535 ||  addr.getVal() < 0 ) {
            // Syntax error
            return null;
        } else {
            return theMemory[ addr.getVal() ];
        }
    }
}
