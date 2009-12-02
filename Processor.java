import java.util.ArrayList;

/**
 * Processor.java
 * Emulates the NES' main processor
 *
 * TODO: Correct addition & subtraction when in decimal mode.
 * 
 * @author Christopher Erickson and Christopher Pable
 */

public class Processor
{
    /* Status bit positions */
    public static final int P_N = 7;
    public static final int P_V = 6;
    public static final int P_1 = 5;
    public static final int P_B = 4;
    public static final int P_D = 3;
    public static final int P_I = 2;
    public static final int P_Z = 1;
    public static final int P_C = 0;

    /* Registers */   		//                   7 6 5 4 3 2 1 0
    public static int P;    // Status register - N|V|1|B|D|I|Z|C
    public static int A;    // Accumulator
    public static int X;    // Index register
    public static int Y;    // Index register
    public static int SP;   // Stack Pointer
    public static int PC;	// Program counter
    
    /**
     * Creates an instance of a processor.
     */
    public Processor()
    {
        // Initialize the registers
        P = 1 << 5; // Set 1 to 1
        A = 0x00;
        X = 0x00;
        Y = 0x00;
        SP = Memory.stackStart;
        PC = 0x0000;
    }

    /**
     * Creates a processor and starts using it to run a program.
     * 
     * @param args Command line arguments
     */
    public static void main( String[] args )
    {      
        // Run a program
        if ( args.length == 1 )
        {
            // Initialize the processor
            Processor NES = new Processor();
            NES.start( args[0] );
        }
        else
        {
        	System.out.println( "You must specify a program." );
        	System.out.println( "    i.e. java -jar sixty502 file.asm" );
        }
    }

    /**
     * Index the program and start the processor.
     */
    private void start( String filename )
    {
    	// Print some information
    	System.out.println( "-------------------------------------------------" );
    	System.out.println( " 6502 Emulator by Chris Pable and Chris Erickson" );
    	System.out.println( "-------------------------------------------------" );

    	System.out.println( "Initial state" );
    	printAllRegisters();
    	
    	// Parse the input file
    	ArrayList<Integer> program = new ArrayList<Integer>();
    	Parser asmParser = new AssemblyParser();
    	asmParser.parseFile( program, filename );

    	// Load the program into memory
    	for ( int i = 0; i < program.size(); ++i )
    	{
    		Memory.address[Memory.programStart + i] = program.get( i );
    	}
    	
    	// Run the program
    	int inst, addr, byte1, hibyte, result, pcmem;
    	while ( PC  < program.size() )
    	{
    		// Temporarily change PC to map to correct spot in memory
    		PC += Memory.programStart;
    		
    		inst = Memory.address[PC++];
    		switch( inst )
    		{
    			// ADC
    			case 0x69: // Immediate
    				byte1 = Memory.address[PC++];
    				result = byte1 + A;
    				if ( getBit( P, P_C ) )
    					++result;
    				checkNegative( result );
    				checkOverflow( result );
    				checkZero( result );
    				A = result & 0xFF;
    				System.out.println( "ADC $" + Integer.toHexString( byte1 ) );
    				break;
    			case 0x65: // Zero Page
    				addr = Memory.address[PC++];
    				byte1 = Memory.address[addr & 0xFF];
    				result = byte1 + A;
    				if ( getBit( P, P_C ) )
    					++result;
    				checkNegative( result );
    				checkOverflow( result );
    				checkZero( result );
    				A = result & 0xFF;
    				System.out.println( "ADC $" + Integer.toHexString( byte1 ) );
    				break;
    			case 0x75: // Zero Page, X
    				addr = Memory.address[PC++];
    				byte1 = Memory.address[(addr + X) & 0xFF];
    				result = byte1 + A;
    				if ( getBit( P, P_C ) )
    					++result;
    				checkNegative( result );
    				checkOverflow( result );
    				checkZero( result );
    				A = result & 0xFF;
    				System.out.println( "ADC $" + Integer.toHexString( byte1 ) );
    				break;
    			case 0x6D: // Absolute
    				addr = Memory.address[PC++];
    				addr = ( Memory.address[PC++] << 8 ) | addr;
    				byte1 = Memory.address[addr];
    				result = byte1 + A;
    				if ( getBit( P, P_C ) )
    					++result;
    				checkNegative( result );
    				checkOverflow( result );
    				checkZero( result );
    				A = result & 0xFF;
    				System.out.println( "ADC $" + Integer.toHexString( byte1 ) );
    				break;
    			case 0x7D: // Absolute, X
    				addr = Memory.address[PC++];
    				addr = ( Memory.address[PC++] << 8 ) | addr;
    				byte1 = Memory.address[addr + X];
    				result = byte1 + A;
    				if ( getBit( P, P_C ) )
    					++result;
    				checkNegative( result );
    				checkOverflow( result );
    				checkZero( result );
    				A = result & 0xFF;
    				System.out.println( "ADC $" + Integer.toHexString( byte1 ) );
    				break;
    			case 0x79: // Absolute, Y
    				addr = Memory.address[PC++];
    				addr = ( Memory.address[PC++] << 8 ) | addr;
    				byte1 = Memory.address[addr + Y];
    				result = byte1 + A;
    				if ( getBit( P, P_C ) )
    					++result;
    				checkNegative( result );
    				checkOverflow( result );
    				checkZero( result );
    				A = result & 0xFF;
    				System.out.println( "ADC $" + Integer.toHexString( byte1 ) );
    				break;
    			case 0x61: // Indirect, X
    				addr = Memory.address[PC++];
    				addr = Memory.address[(addr + X) & 0xFF];
    				byte1 = Memory.address[addr];
    				result = byte1 + A;
    				if ( getBit( P, P_C ) )
    					++result;
    				checkNegative( result );
    				checkOverflow( result );
    				checkZero( result );
    				A = result & 0xFF;
    				System.out.println( "ADC $" + Integer.toHexString( byte1 ) );
    				break;
    			case 0x71: // Indirect, Y
    				addr = Memory.address[PC++];
    				addr = Memory.address[addr & 0xFF];
    				byte1 = Memory.address[(addr + Y) & 0xFF];
    				result = byte1 + A;
    				if ( getBit( P, P_C ) )
    					++result;
    				checkNegative( result );
    				checkOverflow( result );
    				checkZero( result );
    				A = result & 0xFF;
    				System.out.println( "ADC $" + Integer.toHexString( byte1 ) );
    				break;
    			// AND
    			case 0x29: // Immediate
    				byte1 = Memory.address[PC++];
    				result = A & byte1;
    				checkNegative( result );
    				checkZero( result );
    				A = result & 0xFF;
    				System.out.println( "AND $" + Integer.toHexString( byte1 ) );
    				break;
    			case 0x25: // Zero Page
    				addr = Memory.address[PC++];
    				byte1 = Memory.address[addr];
    				result = A & byte1;
    				checkNegative( result );
    				checkZero( result );
    				A = result & 0xFF;
    				System.out.println( "AND $" + "$" + Integer.toHexString( byte1 ) );
    				break;
    			case 0x35: // Zero Page, X
    				addr = Memory.address[PC++];
    				byte1 = Memory.address[addr + X];
    				result = A & byte1;
    				checkNegative( result );
    				checkZero( result );
    				A = result & 0xFF;
    				System.out.println( "AND $" + "$" + Integer.toHexString( byte1 ) );
    				break;
    			case 0x2D: // Absolute
    				addr = Memory.address[PC++];
    				addr = ( Memory.address[PC++] << 8 ) | addr;
    				byte1 = Memory.address[addr];
    				result = A & byte1;
    				checkNegative( result );
    				checkZero( result );
    				A = result & 0xFF;
    				System.out.println( "AND $" + "$" + Integer.toHexString( byte1 ) );
    				break;
    			case 0x3D: // Absolute, X
    				addr = Memory.address[PC++];
    				addr = ( Memory.address[PC++] << 8 ) | addr;
    				byte1 = Memory.address[addr + X];
    				result = A & byte1;
    				checkNegative( result );
    				checkZero( result );
    				A = result & 0xFF;
    				System.out.println( "AND $" + "$" + Integer.toHexString( byte1 ) );
    				break;
    			case 0x39: // Absolute, Y
    				addr = Memory.address[PC++];
    				addr = ( Memory.address[PC++] << 8 ) | addr;
    				byte1 = Memory.address[addr + Y];
    				result = A & byte1;
    				checkNegative( result );
    				checkZero( result );
    				A = result & 0xFF;
    				System.out.println( "AND $" + Integer.toHexString( byte1 ) );
    				break;
    			case 0x21: // Indirect, X
    				addr = Memory.address[PC++];
    				addr = Memory.address[(addr + X) & 0xFF];
    				byte1 = Memory.address[addr];
    				result = A & byte1;
    				checkNegative( result );
    				checkZero( result );
    				A = result & 0xFF;
    				System.out.println( "AND $" + Integer.toHexString( byte1 ) );
    				break;
    			case 0x31: // Indirect, Y
    				addr = Memory.address[PC++];
    				addr = Memory.address[addr & 0xFF];
    				byte1 = Memory.address[(addr + Y) & 0xFF];
    				result = A & byte1;
    				checkNegative( result );
    				checkZero( result );
    				A = result & 0xFF;
    				System.out.println( "AND $" + Integer.toHexString( byte1 ) );
    				break;
    			// ASL
    			case 0x0A: // Accumulator
    				byte1 = A;
					if ( getBit( byte1, 7 ) )
						P = setBit( P, P_C, true );
					result = byte1 << 1;
					checkNegative( result );
					checkZero( result );
					A = result & 0xFF;
					System.out.println( "ASL A" );
					break;
				case 0x06: // Zero Page
					addr = Memory.address[PC++];
    				byte1 = Memory.address[addr];
					if ( getBit( byte1, 7 ) )
						P = setBit( P, P_C, true );
					result = byte1 << 1;
					checkNegative( result );
					checkZero( result );
					A = result & 0xFF;
    				System.out.println( "ASL $" + Integer.toHexString( byte1 ) );
    				break;
				case 0x16: // Zero Page, X
					addr = Memory.address[PC++];
    				byte1 = Memory.address[addr + X];
					if ( getBit( byte1, 7 ) )
						P = setBit( P, P_C, true );
					result = byte1 << 1;
					checkNegative( result );
					checkZero( result );
					A = result & 0xFF;
    				System.out.println( "ASL $" + Integer.toHexString( byte1 ) );
    				break;
				case 0x0E: // Absolute
					addr = Memory.address[PC++];
    				addr = ( Memory.address[PC++] << 8 ) | addr;
    				byte1 = Memory.address[addr];
					if ( getBit( byte1, 7 ) )
						P = setBit( P, P_C, true );
					result = byte1 << 1;
					checkNegative( result );
					checkZero( result );
					A = result & 0xFF;
    				System.out.println( "ASL $" + Integer.toHexString( byte1 ) );
    				break;
				case 0x1E: // Absolute, X
					addr = Memory.address[PC++];
    				addr = ( Memory.address[PC++] << 8 ) | addr;
    				byte1 = Memory.address[addr + X];
					if ( getBit( byte1, 7 ) )
						P = setBit( P, P_C, true );
					result = byte1 << 1;
					checkNegative( result );
					checkZero( result );
					A = result & 0xFF;
    				System.out.println( "ASL $" + Integer.toHexString( byte1 ) );
    				break;
    			// BCC
				case 0x90: // Relative
					byte1 = Memory.address[PC++];
					System.out.println( Integer.toHexString( byte1 ) );
					// Make negative if it is
					if ( getBit( byte1, P_N ) )
						byte1 |= 0xFFFFFF00;
					System.out.println( Integer.toHexString( byte1 ) );
					if ( getBit( P, P_C ) )
						PC += byte1;
					System.out.println( "BCC $" + PC );
					break;
    			// PLA
				case 0x68: // Implied
					byte1 = Memory.address[SP];
					SP--;
					A = byte1 & 0xFF;
					System.out.println( "PLA A" );
					break;
    			// PHA
				case 0x48: // Implied
					SP++;
					Memory.address[SP] = A;
					System.out.println( "PHA A" );
					break;
    			// PHP
				case 0x08: // Implied
					SP++;
					Memory.address[SP] = P;
					System.out.println( "PHP P" );
					break;
    			// PLP
				case 0x28: // Implied
					P = Memory.address[SP]
					SP--;
					System.out.println( "PLP P" );
					break;


    			default:
    				System.out.println( "(ERROR) Unsupported opcode: " + inst );
    				//System.exit( 0 );
    				break;
    		}
    		// Change it back
    		PC -= Memory.programStart;
    		printAllRegisters();
    	}
    }
    
    /**
     * Branch on Carry Clear
     */
    private void BCC( Byte src1 )
    {
    	if ( P.getBit( P_C ) == false )
    	{
    		// Jump
    		PC.setVal( src1.getVal() - 1 );
    	}
    }

    /**
     * Test Bits
     * 
     * Used Flags:
     *   N - Bit 7 of src1.
     *   V - Bit 6 of src1.
     *   Z - 1 if result of src1 AND accumulator is zero.
     */
    private void BIT( Byte src1 )
    {
    	P.setBit( P_N, src1.getBit( P_N ) );
    	P.setBit( P_V, src1.getBit( P_V ) );
    	
    	if ( ( src1.getVal() & A.getVal() ) == 0 )
    		P.setBit( P_Z, true );
    }
    
    /**
     * Branch on Carry Set
     */
    private void BCS( Byte src1 )
    {
    	if ( P.getBit( P_C ) == true )
    	{
    		// Jump
    		PC.setVal( src1.getVal() - 1 );
    	}
    }
    
    /**
     * Branch on Equal
     */
    private void BEQ( Byte src1 )
    {
    	if ( P.getBit( P_Z ) == true )
    	{
    		// Jump
    		PC.setVal( src1.getVal() - 1 );
    	}
    }
    
    // BIT to come
    
    /**
     * Branch on Minus (Less Than)
     */
    private void BMI( Byte src1 )
    {
    	if ( P.getBit( P_N ) == true )
    	{
    		// Jump
    		PC.setVal( src1.getVal() - 1 );
    	}
    }
    
    /**
     * Branch on Not Equal
     */
    private void BNE( Byte src1 )
    {
    	if ( P.getBit( P_Z ) == false )
    	{
    		// Jump
    		PC.setVal( src1.getVal() - 1 );
    	}
    }
    
    /**
     * Branch on Plus (Greater Than)
     */
    private void BPL( Byte src1 )
    {
    	if ( P.getBit( P_Z ) == false && P.getBit( P_C ) == true )
    	{
    		// Jump
    		PC.setVal( src1.getVal() - 1 );
    	}
    }
    
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
     * Branch on Overflow Clear
     */
    private void BVC( Byte src1 )
    {
    	if ( P.getBit( P_V ) == false )
    	{
    		// Jump
    		PC.setVal( src1.getVal() - 1 );
    	}
    }
    
    /**
     * Branch on Overflow Set
     */
    private void BVS( Byte src1 )
    {
    	if ( P.getBit( P_V ) == true )
    	{
    		// Jump
    		PC.setVal( src1.getVal() - 1 );
    	}
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
        if ( A.getVal() < src1.getVal() )
        {
            P.setBit( P_N, true );
            P.setBit( P_Z, false );
            P.setBit( P_C, false );
        }
        else if ( A.getVal() > src1.getVal() )
        {
            P.setBit( P_N, false );
            P.setBit( P_Z, false );
            P.setBit( P_C, true );
        }
        else
        {
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
        if ( X.getVal() < src1.getVal() )
        {
            P.setBit( P_N, true );
            P.setBit( P_Z, false );
            P.setBit( P_C, false );
        }
        else if ( X.getVal() > src1.getVal() )
        {
            P.setBit( P_N, false );
            P.setBit( P_Z, false );
            P.setBit( P_C, true );
        }
        else
        {
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
        if ( Y.getVal() < src1.getVal() )
        {
            P.setBit( P_N, true );
            P.setBit( P_Z, false );
            P.setBit( P_C, false );
        }
        else if ( Y.getVal() > src1.getVal() )
        {
            P.setBit( P_N, false );
            P.setBit( P_Z, false );
            P.setBit( P_C, true );
        }
        else
        {
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
        for ( int i = 0; i < 8; ++i )
        {
            if ( A.getBit(i) != src1.getBit(i) )
            {
                result.setBit( i, true );
            }
            else
            {
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

    /**
     * Jump
     * Jumps to the instruction at the given address.
     */
    private void JMP( Byte src1 )
    {
    	// Set the PC to the next instruction to be executed - 1
    	PC.setVal( src1.getVal() - 1 );
    }

    /**
     * Jump to Subroutine
     * Pushes the PC+1 onto the stack and jumps.
     */
    private void JSR( Byte src1 )
    {
        // Increment the stack pointer
        SP.setVal( SP.getVal() + 1 );
        
        // Determine the address in memory
        Word addr = new Word( stackOffset.getVal() + SP.getVal() );
        
        // Store the PC
        theMemory.getByte( addr ).setVal( PC.getVal() + 1 );
        
        // Jump to src1
        PC.setVal( src1.getVal() - 1 );
    }
    
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
        for ( int i = 0; i < 7; ++i )
        {
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
        for ( int i = 0; i < 8; ++i )
        {
            if ( A.getBit(i) || src1.getBit(i) )
            {
                result.setBit( i, true );
            } 
            else
            {
                result.setBit( i, false );
            }
        }
        
        // Set result and flags
        Byte flags = A.setVal( result.getVal() );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }

    /**
     * Push Accumulator
     * Pushes the accumulator onto the stack.
     */
    private void PHA()
    {
        // Increment the stack pointer
        SP.setVal( SP.getVal() + 1 );
        
        // Determine the address in memory
        Word addr = new Word( stackOffset.getVal() + SP.getVal() );
        
        // Store the accumulator
        theMemory.getByte( addr ).setVal( A.getVal() );
    }
    
    /**
     * Pull Accumulator
     * Pulls the accumulator from the stack.
     */
    private void PLA()
    {
        // Determine the address in memory
        Word addr = new Word( stackOffset.getVal() + SP.getVal() );
        
        // Decrement the stack pointer
        SP.setVal( SP.getVal() - 1 );
        
        // Store the byte into the accumulator
        A.setVal( theMemory.getByte( addr ).getVal() );
    }
    
    /**
     * Push Processor Status
     * Pushes the status register onto the stack.
     */
    private void PHP()
    {
        // Increment the stack pointer
        SP.setVal( SP.getVal() + 1 );
        
        // Determine the address in memory
        Word addr = new Word( stackOffset.getVal() + SP.getVal() );
        
        // Store the status register
        theMemory.getByte( addr ).setVal( P.getVal() );
    }
    
    /**
     * Pull Processor Status
     * Pulls the the status register from the stack.
     */
    private void PLP()
    {
        // Determine the address in memory
        Word addr = new Word( stackOffset.getVal() + SP.getVal() );
        
        // Decrement the stack pointer
        SP.setVal( SP.getVal() - 1 );
        
        // Store the byte into the status register
        P.setVal( theMemory.getByte( addr ).getVal() );
    }
    
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
        for ( int i = 7; i > 0; --i )
        {
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
        for ( int i = 0; i < 7; ++i )
        {
            result.setBit( i, src1.getBit(i+1) );
        }

        // Set result and flags
        Byte flags = A.setVal( result.getVal() );
        P.setBit( P_N, flags.getBit( P_N ) );
        P.setBit( P_Z, flags.getBit( P_Z ) );
    }
    
    /**
     * Return from Interrupt
     * Sets the PC and Status register based on the stack.
     * 
     * Used Flags:
     *   All flags
     */
    private void RTI()
    {
        // Determine the stack address in memory
        Word addr = new Word( stackOffset.getVal() + SP.getVal() );
        
        // Pull the status register
        P.setVal( theMemory.getByte( addr ).getVal() );
        addr.setVal( addr.getVal() - 1 ); // Traverse stack
        
        // Pull the lower byte of the PC
        Byte lower = theMemory.getByte( addr );
        addr.setVal( addr.getVal() - 1 ); // Traverse stack
        
        // Pull the higer byte of the PC
        Byte upper = theMemory.getByte( addr );
        
        // Set the PC
        PC.setVal( (upper.getVal() << 4) + lower.getVal() );
        
        // Decrement stack pointer
        SP.setVal( SP.getVal() - 3 );
    }
    
    /**
     * Return from Subroutine
     * Sets the PC based on the stack.
     */
    private void RTS()
    {
        // Determine the stack address in memory
        Word addr = new Word( stackOffset.getVal() + SP.getVal() );
        
        // Pull the lower byte of the PC
        Byte lower = theMemory.getByte( addr );
        addr.setVal( addr.getVal() - 1 ); // Traverse stack
        
        // Pull the higher byte of the PC
        Byte upper = theMemory.getByte( addr );
        
        // Set the PC
        PC.setVal( ( upper.getVal() << 4 ) + lower.getVal() );
        PC.setVal( PC.getVal() + 1 ); // Address + 1
        
        // Decrement stack pointer
        SP.setVal( SP.getVal() - 2 );
    }
    
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
        
        // Add the carry if present
        if ( P.getBit(7) ) result++;
        
        // Clear the carry flag
        P.setBit( P_C, false );
        
        // Set result and flags
        Byte flags = A.setVal( result );
        checkOverflow( result );
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
    
    /**
     * Transfer Stack pointer to X
     * Copies the value in the stack pointer and stores it in register X.
     */
    private void TSX()
    {
        // Store SP in X, no flags changed
        X.setVal( SP.getVal() );
    }
    
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
    
    /**
     * Transfer X to Stack pointer
     * Copies the value in register X and stores it in the stack pointer.
     */
    private void TXS()
    {
        // Store X in SP, no flags changed
        SP.setVal( X.getVal() );
    }
    
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
    
    private void checkOverflow( int binary )
    {
    	if ( binary > 127 || binary < -128 )
    		P = setBit( P, P_V, true );
    	else
    		P = setBit( P, P_V, false );
    }
    
    private void checkNegative( int binary )
    {
    	if ( ( binary & ( 1 << P_N ) ) != 0 )
    		P = setBit( P, P_N, true );
    	else
    		P = setBit( P, P_N, false );
    }
    
    private void checkZero( int binary )
    {
    	if ( binary == 0 )
    		P = setBit( P, P_Z, true );
    	else
    		P = setBit( P, P_Z, false );
    }
    
    private boolean getBit( int binary, int position )
    {
    	if ( ( binary & ( 1 << position ) ) != 0 )
    		return true;
    	else
    		return false;
    }
    
    private int setBit( int binary, int position, boolean setIt )
    {
    	if ( setIt )
    		binary |= ( 1 << position );
    	else
    		binary &= ~( 1 << position );
    	
    	return binary;
    }
    
    /**
     * Print the value of all registers, used for debugging.
     */
    private void printAllRegisters()
    {
    	System.out.println( "-------------------------------------------------" );
        System.out.println( "Status: $" + Integer.toHexString( P ) );
        System.out.println( "A:      $" + Integer.toHexString( A ) );
        System.out.println( "X:      $" + Integer.toHexString( X ) );
        System.out.println( "Y:      $" + Integer.toHexString( Y ) );
        System.out.println( "PC:     $" + Integer.toHexString( PC ) );
        System.out.println( "-------------------------------------------------" );
    }
}
