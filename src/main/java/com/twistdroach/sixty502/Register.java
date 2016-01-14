package com.twistdroach.sixty502;

/**
 * Register.java
 * Handles a virtual register
 *
 * @author Christopher Erickson
 */

public class Register extends Byte
{
    /**
     * Creates a default Register ( value = 0 )
     */
    public Register()
    {
        super();
    }
    
    /**
     * Creates a Register initialized with a starting value.
     * 
     * @param startingVal The initial value.
     */
    public Register( String startingVal )
    {
        super( startingVal );
    }
    
    // Set all bits to zero unless the given flags are set
    public void clear( boolean flag )
    {
        setVal( 0 );
        if (flag)
            setBit( 4, true );
    }

}
