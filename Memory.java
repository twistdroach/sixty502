/**
 * Memory.java
 * Emulates word-addressable NES memory.
 *
 * @author Christopher Erickson
 */

public class Memory {
    /* Class variables */
    private Byte[] theMemory;   // Stores the bytes.
    private Word stackOffset;
    
    /**
     * Creates an instance of a 64KB of memory.
     */
    public Memory() {
        theMemory = new Byte[65536];
        stackOffset = new Word( "$0100" );
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

    /**
     * Pushes a Byte onto the stack.
     * 
     * @param SP Byte containing the stack pointer.
     * @param src1 Byte to be pushed.
     */
    public void stackPush( Byte SP, Byte src1 ) {
        // Increment the stack pointer
        SP.setVal( SP.getVal() + 1 );
        
        // Determine the address in memory.
        Word addr = new Word( stackOffset.getVal() + SP.getVal() );
        
        // Store the byte
        getByte( addr ).setVal( src1.getVal() );
    }
    
    /**
     * Pulls a byte from the stack.
     * 
     * @param SP Byte containing the stack pointer.
     */
    public Byte stackPull( Byte SP ) {      
        // Determine the address in memory.
        Word addr = new Word( stackOffset.getVal() + SP.getVal() );
        
        // Decrement the stack pointer
        SP.setVal( SP.getVal() - 1 );
        
        // Return the byte
        return getByte( addr );
    }
}