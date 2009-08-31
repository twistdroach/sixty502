/**
 * Memory.java
 * Emulates word-addressable NES memory.
 *
 * @author Christopher Erickson
 */

public class Memory
{
    /* Class variables */
    private static Byte[] theMemory = new Byte[65536];   // 64 KB of mem
    
    /**
     * Retrieves a Byte from memory.
     * 
     * @param addr Word containing the address of the Byte to be retrieved.
     * @return The Byte referenced by addr.
     */
    public static Byte getByte( Word addr )
    {
        if ( addr.getVal() > 65535 ||  addr.getVal() < 0 )
        {
            // Syntax error
            return null;
        }
        else
        {
            return theMemory[ addr.getVal() ];
        }
    }
    
    /**
     * Retrieves a Byte from memory.
     * 
     * @param addr Byte containing the address of the Word to be retrieved (in Zero Page).
     * @return The Byte referenced by addr.
     */
    public static Byte getByte( Byte addr )
    {
        if ( addr.getVal() > 255 ||  addr.getVal() < 0 )
        {
            // Syntax error
            return null;
        }
        else
        {
            return theMemory[ addr.getVal() ];
        }
    }
    
    /**
     * Retrieves a Word from memory.
     * 
     * @param addr Word containing the address of the Word to be retrieved.
     * @return The Word referenced by addr.
     */
    public static Word getWord( Word addr )
    {
        if ( addr.getVal() > 65534 ||  addr.getVal() < 0 )
        {
            // Syntax error
            return null;
        }
        else
        {
        	// Get the word at that address
            return new Word( theMemory[ addr.getVal() ], theMemory[ addr.getVal() + 1 ] );
        }
    }
    
    /**
     * Retrieves a Word from memory.
     * 
     * @param addr Byte containing the address of the Word to be retrieved (in Zero Page).
     * @return The Word referenced by addr.
     */
    public static Word getWord( Byte addr )
    {
        if ( addr.getVal() > 254 ||  addr.getVal() < 0 )
        {
            // Syntax error
            return null;
        }
        else
        {
        	// Get the word at that address
            return new Word( theMemory[ addr.getVal() ], theMemory[ addr.getVal() + 1 ] );
        }
    }
}
