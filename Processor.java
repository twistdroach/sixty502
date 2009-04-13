/**
 * Processor.java
 * Emulates the NES' main processor
 *
 * @author Christopher Erickson and Christopher Pable
 */

public class Processor {
    /* Status bit positions */
    public static final int P_N = 0;
    public static final int P_V = 1;
    public static final int P_1 = 2;
    public static final int P_B = 3;
    public static final int P_D = 4;
    public static final int P_I = 5;
    public static final int P_Z = 6;
    public static final int P_C = 7;
    /* Registers */         //                   0 1 2 3 4 5 6 7
    private Register P;     // Status register - N|V|1|B|D|I|Z|C
    private Register A;     // Accumulator
    private Register X;     // Index register
    private Register Y;     // Index register
    private Register SP;    // Stack pointer ( Usage of page $01 implied )
    private PC PC;          // Program counter
    /* Program */
    private Program theProgram;

    // Default Constructor
    public Processor() {
        // Create registers
        P = new Register();
        A = new Register();
        X = new Register();
        Y = new Register();
        SP = new Register();
        PC = new PC();
    }

    // MAIN
    public static void main(String[] args) {
        // Create a processor!
        Processor NES = new Processor();
        //System.out.println("" + (Integer.toBinaryString(-128)) );
        NES.start();
    }
    
    // Starts the processor
    private void start() {
        // Initialize registers
        // Obviously, most of these values are meaningless
        P.setVal("%00000000");
        A.setVal("%10100111");
        X.setVal("%01100100");
        Y.setVal("%00110101");
        SP.setVal("$ff");
        PC.setVal("%0000000000000000");
        
        // Read in our program
        theProgram = new Program( "test.asm" );

        // Print it out
        System.out.println( theProgram );
    }
    
    // Prints out the value of each register, used for testing
    private void printAllRegisters() {
        System.out.println("Status:\t" + P.getValBin());
        System.out.println("A:\t" + A.getValBin());
        System.out.println("X:\t" + X.getValBin());
        System.out.println("Y:\t" + Y.getValBin());
        System.out.println("S:\t" + SP.getValHex());
        System.out.println("PC:\t" + PC.getValHex());
    }
     
    // OPCODE HELPERS
    
    // Wrote these into the Byte class. Your code for checkNegative made the value positive. Are you sure about that?
    
    /* OPCODE IMPLEMENTATION */
    
    /**
     * Add with Carry
     * Adds the accumulator to an input byte and a carry.
     * Stores result in accumulator.
     *
     * Used Flags:
     *   V - Set if result is outside the range of a signed byte.
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *   C - If set, adds one to the resulting sum.
     *
     * @param src1 Byte to be added to accumulator.
     */
    private void ADC( Byte src1 )
    {
    	int result = src1.getVal() + A.getVal();
        if ( P.getBit(7) ) result++; // add the carry if present
        
        // Does the carry really get cleared?
        P.setBit( 7, false ); // remove carry if present
        
        // Set result and flags
        Byte flags = A.setVal( result );
        P.setBit( P_V, flags.getBit( P_V ) );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }
    
    /**
     * Bitwise AND
     * ANDs the accumulator and an input byte together, bitwise.
     * Stores result in accumulator.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * @param src1 Byte to be ANDed with accumulator.
     */
    private void AND( Byte src1 ) 
    {
        Byte result = new Byte();
        
        // AND bit by bit
        for ( int i = 0; i < 8; i++ ) {
            if ( A.getBit(i) && src1.getBit(i) ) {
                result.setBit( i, true );
            } else {
                result.setBit( i, false );
            }
        }
        
        // Set result and flags
        Byte flags = A.setVal( result.getVal() );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }
    
    /**
     * Arithmetic Shift Left
     * Shifts all bits left one position. 0 is shifted in.
     * Stores result in accumulator.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *   C - Set if bit shifted off the left end is set. (bit 7)
     *
     * @param src1 Byte to be shifted left.
     */
    private void ASL( Byte src1 ) 
    {
        Byte result = new Byte();
        
        // Shift bit 7 into the carry
        P.setBit( P_C , src1.getBit(7) );
        // Shift all bits left
        for ( int i = 7; i > 0; i-- ) {
            result.setBit( i, src1.getBit(i-1) );
        }
        // Shift in zero
        result.setBit( 0, false );

        // Set result and flags
        Byte flags = A.setVal( result.getVal() );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }
    
    //BCC, BCS, BEQ, BIT, BMI, BNE, BPL, BVC, BVS handled by parser
    
    /**
     * Break
     * Causes an interrupt.
     *
     * Used Flags:
     *   B - Set to specify a break.
     *   I - Set to signal an interrupt.
     */
    private void BRK()
    {
        // Sets interrupt flags.
        P.setBit( P_B , true );
        P.setBit( P_I , true );      
    }
    
    /**
     * Clear Flag - Carry
     * Sets the carry flag to zero.
     *
     * Used Flags:
     *   C - Set to zero.
     */
    private void CLC()
    {
        // Clear carry flag
        P.setBit( P_C, false );
    }
    
    /**
     * Clear Flag - Decimal Mode
     * Sets the decimal mode flag to zero.
     *
     * Used Flags:
     *   D - Set to zero.
     */
    private void CLD()
    {
        // Clear decimal flag
        P.setBit( P_D, false );
    }
    
    /**
     * Clear Flag - Interrupt
     * Sets the interrupt flag to zero.
     *
     * Used Flags:
     *   I - Set to zero.
     */
    private void CLI()
    {
        // Clear interrupt flag
        P.setBit( P_I, false );
    }
    
    /**
     * Clear Flag - Overflow
     * Sets the overflow flag to zero.
     *
     * Used Flags:
     *   V - Set to zero.
     */
    private void CLV()
    {
        // Clear overflow flag
        P.setBit( P_V, false );
    }
    
    /**
     * Compare Accumulator
     * Compares the accumulator to a given byte.
     *
     * Used Flags:
     *   N - Set if accumulator is less than the byte.
     *   Z - Set if accumulator and byte are equal.
     *   C - Set if accumulator is greater than or equal to the byte.
     *
     * @param src1 Byte to compare with accumulator.
     */
    private void CMP( Byte src1 )
    {
        // Simply set flags based on A and src1
        if ( A.getVal() < src1.getVal() ) {
            P.setBit( P_N, true );
            P.setBit( P_Z, false );
            P.setBit( P_C, false );
        } else if ( A.getVal() > src1.getVal() ) {
            P.setBit( P_N, false );
            P.setBit( P_Z, false );
            P.setBit( P_C, true );
        } else {
            P.setBit( P_N, false );
            P.setBit( P_Z, true );
            P.setBit( P_C, true );
        }
    }

    /**
     * Compare Index Register X
     * Compares register X to a given byte.
     *
     * Used Flags:
     *   N - Set if register X is less than the byte.
     *   Z - Set if register X and byte are equal.
     *   C - Set if register X is greater than or equal to the byte.
     *
     * @param src1 Byte to compare with register X.
     */
    private void CPX( Byte src1 )
    {
        // Simply set flags based on X and src1
        if ( X.getVal() < src1.getVal() ) {
            P.setBit( P_N, true );
            P.setBit( P_Z, false );
            P.setBit( P_C, false );
        } else if ( X.getVal() > src1.getVal() ) {
            P.setBit( P_N, false );
            P.setBit( P_Z, false );
            P.setBit( P_C, true );
        } else {
            P.setBit( P_N, false );
            P.setBit( P_Z, true );
            P.setBit( P_C, true );
        }
    }

    /**
     * Compare Index Register Y
     * Compares register Y to a given byte.
     *
     * Used Flags:
     *   N - Set if register Y is less than the byte.
     *   Z - Set if register Y and byte are equal.
     *   C - Set if register Y is greater than or equal to the byte.
     *
     * @param src1 Byte to compare with register Y.
     */
    private void CPY( Byte src1 )
    {
        // Simply set flags based on Y and src1
        if ( Y.getVal() < src1.getVal() ) {
            P.setBit( P_N, true );
            P.setBit( P_Z, false );
            P.setBit( P_C, false );
        } else if ( Y.getVal() > src1.getVal() ) {
            P.setBit( P_N, false );
            P.setBit( P_Z, false );
            P.setBit( P_C, true );
        } else {
            P.setBit( P_N, false );
            P.setBit( P_Z, true );
            P.setBit( P_C, true );
        }
    }
      
    /**
     * Decrement X
     * Decreases the value stored in register X by one.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     */
    private void DEX()
    {
        // Decrement and set flags
        Byte flags = X.setVal( X.getVal() - 1 );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }
    
    /**
     * Decrement Memory
     * Decreases the value stored in memory by one.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * @param src1 Byte to decrement.
     */
    private void DEC( Byte src1 )
    {
        // Decrement and set flags
        Byte flags = src1.setVal( src1.getVal() - 1 );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }
    
    /**
     * Decrement Y
     * Decreases the value stored in register Y by one.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     */
    private void DEY()
    {
        // Decrement and set flags
        Byte flags = Y.setVal( Y.getVal() - 1 );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }

    /**
     * Bitwise Exclusive OR
     * XORs two bytes together, bitwise.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * @param src1 Byte to be XORd with accumulator.
     */
    private void EOR( Byte src1 )
    {
        Byte result = new Byte();
        
        // XOR bit by bit
        for ( int i = 0; i < 8; i++ ) {
            if ( A.getBit(i) != src1.getBit(i) ) {
                result.setBit( i, true );
            } else {
                result.setBit( i, false );
            }
        }
        
        // Set result and flags
        Byte flags = A.setVal( result.getVal() );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }

    /**
     * Increment Memory
     * Increases the value stored in the memory by one.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * @param src1 Byte to increment.
     */
    private void INC( Byte src1 )
    {
        // Increment and set flags
        Byte flags = src1.setVal( src1.getVal() + 1 );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }
    
    /**
     * Increment X
     * Increases the value stored in register X by one.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * @param src1 Byte to increment.
     */
    private void INX()
    {
        // Increment and set flags
        Byte flags = X.setVal( X.getVal() + 1 );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }
    
    /**
     * Increment Y
     * Increases the value stored in register Y by one.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * @param dest Byte to increment.
     */
    private void INY()
    {
        // Increment and set flags
        Byte flags = Y.setVal( Y.getVal() + 1 );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }

    //JMP and JSR handled by parser
    
    /**
     * Load Accumulator
     * Loads a value from memory into the accumulator.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * @param src1 Byte to be loaded from.
     */
    private void LDA( Byte src1 )
    {
        // Load A and set flags
        Byte flags = A.setVal( src1.getVal() );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }

    /**
     * Load Register X
     * Loads a value from memory into register X.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * @param src1 Byte to be loaded from.
     */
    private void LDX( Byte src1 )
    {
        // Load X and set flags
        Byte flags = X.setVal( src1.getVal() );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }

    /**
     * Load Register Y
     * Loads a value from memory into register Y.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * @param src1 Byte to be loaded from.
     */
    private void LDY( Byte src1 )
    {
        // Load Y and set flags
        Byte flags = Y.setVal( src1.getVal() );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }

    /**
     * Logical Shift Right
     * Shifts all bits right one position. 0 is shifted in.
     * Stores result in accumulator.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *   C - Set if bit shifted off the right end is set. (bit 0)
     *
     * @param src1 Byte to be shifted right.
     */
    private void LSR( Byte src1 ) 
    {
        Byte result = new Byte();
        
        // Shift bit 0 into the carry
        P.setBit( P_C , src1.getBit(0) );
        // Shift all bits right
        for ( int i = 0; i < 7; i++ ) {
            result.setBit( i, src1.getBit(i+1) );
        }
        // Shift in zero
        result.setBit( 7, false );

        // Set result and flags
        Byte flags = A.setVal( result.getVal() );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }

    /**
     * NOP
     * Does nothing.
     */
    private void NOP() 
    {
        // No opcode! Do nothing :)
    }
    
    /**
     * Bitwise OR
     * ORs a byte with the accumulator, bitwise.
     * Stores result in accumulator.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * @param src1 Byte to be ORd with accumulator.
     */
    private void ORA( Byte src1 ) 
    {
        Byte result = new Byte();
        
        // OR bit by bit
        for ( int i = 0; i < 8; i++ ) {
            if ( A.getBit(i) || src1.getBit(i) ) {
                result.setBit( i, true );
            } else {
                result.setBit( i, false );
            }
        }
        
        // Set result and flags
        Byte flags = A.setVal( result.getVal() );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }
    
    
    //TODO: Implement Stack to use stack operations
    
    
    
    /**
     * Rotate Left
     * Shifts all bits left one position. Carry is shifted in.
     * Stores result in the accumulator.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *   C - Set if bit shifted off the left end is set. (bit 7)
     *       If set prior, bit 0 takes its value.
     *
     * @param src1 Byte to be rotated left.
     */
    private void ROL( Byte src1  ) 
    {
        Byte result = new Byte();
        
        // Shift in the carry
        result.setBit( 0, P.getBit( P_C ) );
        // Shift bit 7 into the carry
        P.setBit( P_C , src1.getBit(7) );
        // Shift all bits left
        for ( int i = 7; i > 0; i-- ) {
            result.setBit( i, src1.getBit(i-1) );
        }

        // Set result and flags
        Byte flags = A.setVal( result.getVal() );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }
    
    /**
     * Rotate Right
     * Shifts all bits right one position. Carry is shifted in.
     * Stores result in the accumulator.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *   C - Set if bit shifted off the right end is set. (bit 0)
     *       If set prior, bit 7 takes its value.
     *
     * @param src1 Byte to be rotated right.
     */
    private void ROR( Byte src1 ) 
    {
        Byte result = new Byte();
        
        // Shift in the carry
        result.setBit( 7, P.getBit( P_C ) );
        // Shift bit 0 into the carry
        P.setBit( P_C , src1.getBit(0) );
        // Shift all bits right
        for ( int i = 0; i < 7; i++ ) {
            result.setBit( i, src1.getBit(i+1) );
        }

        // Set result and flags
        Byte flags = A.setVal( result.getVal() );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }
    
    //RTI and RTS Require the Stack
    
    /**
     * Subtract with Carry
     * Subtracts a byte from the accumulator. (Arithmetically if Carry is set).
     * Stores result in the accumulator.
     *
     * Used Flags:
     *   V - Set if result is outside the range of a signed byte.
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *   C - If set, adds one to the resulting difference.
     *       (Needed for mathematical correctness)
     *
     * @param src1 Byte to be subtracted.
     */
    private void SBC( Byte src1 )
    {
    	int result = A.getVal() - src1.getVal() - 1;
        if ( P.getBit(7) ) result++; // add the carry if present
        
        // Set result and flags
        Byte flags = A.setVal( result );
        P.setBit( P_V, flags.getBit( P_V ) );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }
    
    /**
     * Set Flag - Carry
     * Sets the carry flag to one.
     *
     * Used Flags:
     *   C - Set to one.
     */
    private void SEC()
    {
        // Set carry flag
        P.setBit( P_C, true );
    }
    
    /**
     * Set Flag - Decimal Mode
     * Sets the decimal mode flag to one.
     *
     * Used Flags:
     *   D - Set to one.
     */
    private void SED()
    {
        // Set decimal mode flag
        P.setBit( P_D, true );
    }

    /**
     * Set Flag - Interrupt
     * Sets the interrupt flag to one.
     *
     * Used Flags:
     *   I - Set to one.
     */
    private void SEI()
    {
        // Set interrupt flag
        P.setBit( P_I, true );
    }
    
    /**
     * Store Accumulator
     * Stores the value of the accumulator into memory.
     *
     * @param src1 Byte to store accumulator.
     */
    private void STA( Byte src1 )
    {
        // Store A in src1
        src1.setVal( A.getVal() );
    }
    
    /**
     * Store Register X
     * Stores the value of register X into memory.
     *
     * @param src1 Byte to store register X.
     */
    private void STX( Byte src1 )
    {
        // Store X in src1
        src1.setVal( X.getVal() );
    }
    
    /**
     * Store Register Y
     * Stores the value of register Y into memory.
     *
     * @param src1 Byte to store register Y.
     */
    private void STY( Byte src1 )
    {
        // Store Y in src1
        src1.setVal( Y.getVal() );
    }
    
    /**
     * Transfer A to X
     * Copies the value in the accumulator and stores it in register X.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     */
    private void TAX()
    {
        // Store A in X and set flags
        Byte flags = X.setVal( A.getVal() );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }
    
    /**
     * Transfer A to Y
     * Copies the value in the accumulator and stores it in register Y.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     */
    private void TAY()
    {
        // Store A in Y and set flags
        Byte flags = Y.setVal( A.getVal() );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }
    
    //TSX Requires Stack
    
    /**
     * Transfer X to A
     * Copies the value in register X and stores it in the accumulator.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     */
    private void TXA()
    {
        // Store X in A and set flags
        Byte flags = A.setVal( X.getVal() );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }
    
    //TXS requires stack
    
    /**
     * Transfer Y to A
     * Copies the value in register Y and stores it in the accumulator.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     */
    private void TYA()
    {
        // Store Y in A and set flags
        Byte flags = A.setVal( Y.getVal() );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }
    
}
