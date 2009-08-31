/**
 * PC.java
 * Virtual NES PC register
 *
 * @author Christopher Erickson
 */

public class PC extends Word
{
    
    /**
     * Creates a default PC  ( value = 0 )
     */
    public PC()
    {
        super();
    }

    /**
     * Creates a PC initialized with a starting value.
     * 
     * @param startingVal The initial value.
     */
    public PC( String startingVal )
    {
        super( startingVal );
    }
    
    /**
     * Adds an offset to the PC.
     * 
     * @param offset The offset added to the PC.
     */
    public void setToOffset(int offset)
    {
        // Add the offset to the current value and store it
        setVal( getVal() + offset );
    }
    
}
