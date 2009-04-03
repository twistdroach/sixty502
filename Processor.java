/**
 * Processor.java
 * Emulates the NES' main processor
 *
 * @author Christopher Erickson and Christopher Pable
 */
import java.util.regex.*;

public class Processor {
    /* Registers */         //                   0 1 2 3 4 5 6 7
    private Register P;     // Status register - N|V|1|B|D|I|Z|C
    private Register A;     // Accumulator
    private Register X;     // Index register
    private Register Y;     // Index register
    private Register SP;    // Stack pointer ( Usage of page $01 implied )
    private PC PC;          // Program counter

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
        
        // Parse & execute!
        // Not yet implemented
        executeInstructionAt( PC );
        
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
    
    /*
     * Here's the important stuff.
     * 
     * Of course the stuff below (the parsing) is merely an example of what
     * I'll be doing. You'll handle things like the ADC method. Notice that
     * you will ALWAYS receive three bytes. If I parse a memory address, I will
     * use that Word to get the byte in memory it refers to, and then send you
     * that, so you will never receive a Word. Only Bytes.
     *
     */
    
    // THE FOLLOWING IS MERELY AN EXAMPLE
    
    // Parsing stuff that I would do
    private String inst = "   adc #$45 ";    
    public void executeInstructionAt( Word prgAddr ) {
        // Setup instruction regex                 Instruction         Immediate           Comment
        Pattern instruction = Pattern.compile( "^\\s*(\\w{3})\\s+#(\\$[0-9abcdef]{1,2})\\s*(;\\w*)?.*$" );
        // Grabbed matched items
        Matcher matched = instruction.matcher( inst );
        // Call the opcode!
        ADC( A, A, new Byte( matched.group(2) ) );
    }
    
    // OPCODE HELPERS
    
     // determine if we overflow, 255 in positive, 255 in negative (we use a flag, not twos compliment? WIll need to look into this, maybe byte should store -/+ metadata?)
    private int checkOverflow(int temp)
    {
      if (temp < 255 || temp > 255)
      {
        temp%=255; 
        P.setBit(1,true);//set V
      }
      return temp;
    }
    
    // determine if negative, if so, set negative flag, make val positive
    private int checkNegative(int temp)
    {
    if (temp < 0)
        {
          temp = -temp;
          P.setBit(0, true);
        }
        return temp;
     }
     private void checkZero(int temp)
     {
        if (temp == 0) P.setBit(0, true);
     }
    
    /* OPCODE IMPLEMENTATION */
    
    /**
     * Add with Carry
     * Adds two bytes together.
     *
     * Used Flags:
     *   V - Set if result is outside the range of a signed byte.
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *   C - If set, adds one to the resulting sum.
     *
     * Implied Parameters:
     *   In order to function just as the original opcode,
     *   both src1 and dest will be fixed as the accumulator.
     *   This is done in the parser class to allow for generalized
     *   opcode methods.
     *
     * @param src1 Byte to be added.
     * @param src2 Byte to be added.
     * @param dest Byte to store sum.
     */
    private void ADC( Byte dest, Byte src1, Byte src2 ) 
    {
        int temp = src1.getVal() + src2.getVal();
        if (P.getBit(7)) temp++; // add the carry if present
        P.setBit(7,false); // remove carry if present
        temp = checkOverflow(temp);
        temp = checkNegative(temp);
        checkZero(temp);
        dest.setVal(temp);
    }
    
    /**
     * Bitwise AND
     * ANDs two bytes together, bitwise.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * Implied Parameters:
     *   In order to function just as the original opcode,
     *   both src1 and dest will be fixed as the accumulator.
     *
     * @param src1 Byte to be ANDed.
     * @param src2 Byte to be ANDed.
     * @param dest Byte to store result.
     */
    private void AND( Byte dest, Byte src1, Byte src2 ) 
    {
      Byte temp;
      String t1, t2, t3;
      t1= src1.getValBin();//get strings from the vals
      t2= src1.getValBin();
      t3 = "%";
      for (int i=1; i<t1.length(); i++) //perform a string comp AND
      {
        if (t1.charAt(i) == t2.charAt(i)) t3 += t2.charAt(i); // 1 or 0
        else t3 +="0";// if they dont match, throw out a zero
      }
      temp = new Byte(t3); // save ANDed val
      checkZero(temp.getVal());//set Z flag
      temp.setVal(checkNegative(temp.getVal())); //set N flag
      dest.setVal(temp.getVal()); // return result
    }
    
    /**
     * Arithmetic Shift Left
     * Shifts all bits left one position. 0 is shifted in.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *   C - Set if bit shifted off the left end is set. (bit 7)
     *
     * Implied Parameters:
     *   In order to function just as the original opcode,
     *   dest will be fixed as the accumulator.
     *
     * @param src1 Byte to be shifted left.
     * @param dest Byte to store result.
     */
    private void ASL( Byte dest, Byte src1 ) 
    {
      //is src2 the number of times to shift? - No. Shifts exactly once.
      String temp = src1.getValBin().substring(1,src1.getValBin().length());
      //for (int i =0; i < src2.getVal(); i++)
      //{
        if (temp.charAt(0) == '1') P.setBit(7,true);//shift into carry
        else P.setBit(7,false);
        temp = temp.substring(1,temp.length()) + "0";//shift into string
      //}
      temp = "%" + temp; // replace % for bin string
      dest.setVal(temp);
      checkZero(dest.getVal());
      checkNegative(dest.getVal());
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
      //All parameter data is useless, so we ignore, just setting up the interrupt.
      P.setBit(3,true);
      P.setBit(5,true);
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
      //All parameter data is useless, so we ignore, just clearing C
      P.setBit(7,false);
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
      //All parameter data is useless, so we ignore, just clearing D
      P.setBit(4,false);
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
      //All parameter data is useless, so we ignore, just clearing I
      P.setBit(5,false);
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
      //All parameter data is useless, so we ignore, just clearing V
      P.setBit(1,false);
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
     * Implied Parameters:
     *   In order to function just as the original opcode,
     *   both src1 and dest will be fixed as the accumulator.
     *
     * @param src1 First comparison byte.
     * @param src2 Second comparison byte.
     * @param dest Byte to store result.
     */
    private void CMP(Byte dest, Byte src1, Byte src2)
    {
    // Dest is useless for us, we dont need to set anything there, all we do is set flags
      if (src1.getVal() < src2.getVal())
      {
        P.setBit(0,true);
        P.setBit(6,false);
        P.setBit(7,false);
        return;
      }
      if (src1.getVal() > src2.getVal())
      {
        P.setBit(0,false);
        P.setBit(6,false);
        P.setBit(7,true);
        return;
      }
      P.setBit(0,false);
      P.setBit(6,true);
      P.setBit(7,true);
    
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
     * Implied Parameters:
     *   In order to function just as the original opcode,
     *   src1 will be fixed as register X and dest will be
     *   fixed as the accumulator.
     *
     * @param src1 First comparison byte.
     * @param src2 Second comparison byte.
     * @param dest Byte to store result.
     */
    private void CPX(Byte dest, Byte src1, Byte src2)
    {
    // Dest is useless for us, we dont need to set anything there, all we do is set flags
      if (src1.getVal() < src2.getVal())
      {
        P.setBit(0,true);
        P.setBit(6,false);
        P.setBit(7,false);
        return;
      }
      if (src1.getVal() > src2.getVal())
      {
        P.setBit(0,false);
        P.setBit(6,false);
        P.setBit(7,true);
        return;
      }
      P.setBit(0,false);
      P.setBit(6,true);
      P.setBit(7,true);
    
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
     * Implied Parameters:
     *   In order to function just as the original opcode,
     *   src1 will be fixed as register Y and dest will be
     *   fixed as the accumulator.
     *
     * @param src1 First comparison byte.
     * @param src2 Second comparison byte.
     * @param dest Byte to store result.
     */
    private void CPY(Byte dest, Byte src1, Byte src2)
    {
    // Dest is useless for us, we dont need to set anything there, all we do is set flags
      if (src1.getVal() < src2.getVal())
      {
        P.setBit(0,true);
        P.setBit(6,false);
        P.setBit(7,false);
        return;
      }
      if (src1.getVal() > src2.getVal())
      {
        P.setBit(0,false);
        P.setBit(6,false);
        P.setBit(7,true);
        return;
      }
      P.setBit(0,false);
      P.setBit(6,true);
      P.setBit(7,true);
      }
      
    /**
     * Decrement X
     * Decreases the value stored in register X by one.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * @param dest Byte to decrement.
     */
    private void DEX(Byte dest)
    {
      //src1, src2, and dest should all be the same here
      dest.setVal((dest.getVal()-1)); // do the decrement
      checkNegative(dest.getVal()); //set flags
      checkZero(dest.getVal());
    }
    
    /**
     * Decrement Memory
     * Decreases the value stored in memory by one.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * @param dest Byte to decrement.
     */
    private void DEC(Byte dest, Byte src1, Byte src2)
    {
      //src1, src2, and dest should all be the same here
      dest.setVal((dest.getVal()-1)); // do the decrement
      checkNegative(dest.getVal()); //set flags
      checkZero(dest.getVal());
    }
    
    /**
     * Decrement Y
     * Decreases the value stored in register Y by one.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * @param dest Byte to decrement.
     */
    private void DEY(Byte dest, Byte src1, Byte src2)
    {
      //src1, src2, and dest should all be the same here
      dest.setVal((dest.getVal()-1)); // do the decrement
      checkNegative(dest.getVal()); //set flags
      checkZero(dest.getVal());
    }

    /**
     * Bitwise Exclusive OR
     * XORs two bytes together, bitwise.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * Implied Parameters:
     *   In order to function just as the original opcode,
     *   both src1 and dest will be fixed as the accumulator.
     *
     * @param src1 First byte to be XORd.
     * @param src2 Second byte to be XORd.
     * @param dest Byte to store result.
     */
    private void EOR(Byte dest, Byte src1, Byte src2)
    {
      String t1, t2, t3;
      t1= (src1.getValBin()).substring(1,src1.getValBin().length());
      t2= (src2.getValBin()).substring(1,src2.getValBin().length());
      t3="%";
      for (int i=0; i< t1.length(); i++) //string based XOR
      {
        if (t1.charAt(i) != t2.charAt(i)) // one is a 1, the other is a zero
          t3+="1";
        else t3 += "0";
      }
      dest.setVal(t3);
      checkNegative(dest.getVal());
      checkZero(dest.getVal());
    }

    /**
     * Increment Memory
     * Increases the value stored in the memory by one.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * @param dest Byte to increment.
     */
    private void INC(Byte dest)
    {
      //src1, src2, and dest should all be the same here
      dest.setVal((dest.getVal()+1)); // do the decrement
      checkNegative(dest.getVal()); //set flags
      checkZero(dest.getVal());
    }
    
    /**
     * Increment X
     * Increases the value stored in register X by one.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * @param dest Byte to decrement.
     */
    private void INX(Byte dest, Byte src1, Byte src2)
    {
      //src1, src2, and dest should all be the same here
      dest.setVal((dest.getVal()+1)); // do the decrement
      checkNegative(dest.getVal()); //set flags
      checkZero(dest.getVal());
    }
    
    /**
     * Increment Y
     * Increases the value stored in register Y by one.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * @param dest Byte to decrement.
     */
    private void INY(Byte dest, Byte src1, Byte src2)
    {
      //src1, src2, and dest should all be the same here
      dest.setVal((dest.getVal()+1)); // do the decrement
      checkNegative(dest.getVal()); //set flags
      checkZero(dest.getVal());
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
     * Implied Parameters:
     *   In order to function just as the original opcode,
     *   dest will be fixed as the accumulator.
     *
     * @param src1 Byte to be loaded from.
     * @param dest Byte to be stored into.
     */
    private void LDA(Byte dest, Byte src1)
    {
      //Assuming memory byte is src1, dest and src2 can be A
      dest.setVal(src1.getVal());
      checkNegative(dest.getVal()); //set flags
      checkZero(dest.getVal());
    }

    /**
     * Load Register X
     * Loads a value from memory into register X.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * Implied Parameters:
     *   In order to function just as the original opcode,
     *   dest will be fixed as register X.
     *
     * @param src1 Byte to be loaded from.
     * @param dest Byte to be stored into.
     */
    private void LDX(Byte dest, Byte src1, Byte src2)
    {
      //Assuming memory byte is src1, dest and src2 can be X
      dest.setVal(src1.getVal());
      checkNegative(dest.getVal()); //set flags
      checkZero(dest.getVal());
    }

    /**
     * Load Register Y
     * Loads a value from memory into register Y.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * Implied Parameters:
     *   In order to function just as the original opcode,
     *   dest will be fixed as register Y.
     *
     * @param src1 Byte to be loaded from.
     * @param dest Byte to be stored into.
     */
    private void LDY(Byte dest, Byte src1, Byte src2)
    {
      //Assuming memory byte is src1, dest and src2 can be Y
      dest.setVal(src1.getVal());
      checkNegative(dest.getVal()); //set flags
      checkZero(dest.getVal());
    }

    /**
     * Logical Shift Right
     * Shifts all bits right one position. 0 is shifted in.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *   C - Set if bit shifted off the right end is set. (bit 0)
     *
     * Implied Parameters:
     *   In order to function just as the original opcode,
     *   dest will be fixed as the accumulator.
     *
     * @param src1 Byte to be shifted right.
     * @param dest Byte to store result.
     */
    private void LSR( Byte dest, Byte src1 ) 
    {
      //is src2 the number of times to shift? - Again, no. One shift.
      String temp = src1.getValBin().substring(1,src1.getValBin().length());
      //for (int i =0; i < src2.getVal(); i++)
      //{
        if (temp.charAt(temp.length()-1) == '1') P.setBit(7,true);//shift into carry
        else P.setBit(7,false);
        temp = "0" + temp.substring(1,temp.length());//shift into string
      //}
      temp = "%" + temp; // replace % for bin string
      dest.setVal(temp);
      checkZero(dest.getVal());
      checkNegative(dest.getVal());
    }

    /**
     * NOP
     * Does nothing.
     */
    private void NOP() 
    {
      //No opcode! Do nothing :)
    }
    
    /**
     * Bitwise OR
     * ORs two bytes together, bitwise.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * Implied Parameters:
     *   In order to function just as the original opcode,
     *   both src1 and dest will be fixed as the accumulator.
     *
     * @param src1 First byte to be ORd.
     * @param src2 Second byte to be ORd.
     * @param dest Byte to store result.
     */
    private void ORA( Byte dest, Byte src1, Byte src2 ) 
    {
      Byte temp;
      String t1, t2, t3;
      t1= src1.getValBin();//get strings from the vals
      t2= src1.getValBin();
      t3 = "%";
      for (int i=1; i<t1.length(); i++) //perform a string comp OR
      {
        if (t1.charAt(i) != t2.charAt(i)) t3 += "1"; // One has to be 1, the other has to be zero
        else if (t1.charAt(i) == '0' &&  t2.charAt(i) == '0')
          t3 +="0";// if they have zeros, throw out a zero
        else
          t3 +="1";// if neither of the above is true, they are both 1s
      }
      temp = new Byte(t3); // save ORed val
      checkZero(temp.getVal());//set Z flag
      temp.setVal(checkNegative(temp.getVal())); //set N flag
      dest.setVal(temp.getVal()); // return result
    }
    
    
    //TODO: Implement Stack to use stack operations
    
    
    
    /**
     * Rotate Left
     * Shifts all bits left one position. Carry is shifted in.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *   C - Set if bit shifted off the left end is set. (bit 7)
     *       If set prior, bit 0 takes its value.
     *
     * Implied Parameters:
     *   In order to function just as the original opcode,
     *   dest will be fixed as the accumulator.
     *
     * @param src1 Byte to be rotated left.
     * @param dest Byte to store result.
     */
    private void ROL( Byte dest, Byte src1  ) 
    {
      //is src2 the number of times to shift? - NOOOO
      String temp = src1.getValBin().substring(1,src1.getValBin().length());
      //for (int i =0; i < src2.getVal(); i++)
      //{
        if (temp.charAt(0) == '1') P.setBit(7,true);//shift into carry
        else P.setBit(7,false);
        temp = temp.substring(1,temp.length()) + temp.charAt(0);//shift into string
      //}
      temp = "%" + temp; // replace % for bin string
      dest.setVal(temp);
      checkZero(dest.getVal());
      checkNegative(dest.getVal());
    }
    
    /**
     * Rotate Right
     * Shifts all bits right one position. Carry is shifted in.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *   C - Set if bit shifted off the right end is set. (bit 0)
     *       If set prior, bit 7 takes its value.
     *
     * Implied Parameters:
     *   In order to function just as the original opcode,
     *   dest will be fixed as the accumulator.
     *
     * @param src1 Byte to be rotated right.
     * @param dest Byte to store result.
     */
    private void ROR( Byte dest, Byte src1 ) 
    {
      //is src2 the number of times to shift? - Ugh...
      String temp = src1.getValBin().substring(1,src1.getValBin().length());
      //for (int i =0; i < src2.getVal(); i++)
      //{
        if (temp.charAt(temp.length()-1) == '1') P.setBit(7,true);//shift into carry
        else P.setBit(7,false);
        temp = P.getValBin().charAt(8) + temp.substring(1,temp.length());//shift into string
      //}
      temp = "%" + temp; // replace % for bin string
      dest.setVal(temp);
      checkZero(dest.getVal());
      checkNegative(dest.getVal());
    }
    
    //RTI and RTS Require the Stack
    
    /**
     * Subtract with Carry
     * Subtracts two bytes. (Arithmetically if Carry is set).
     *
     * Used Flags:
     *   V - Set if result is outside the range of a signed byte.
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *   C - If set, adds one to the resulting difference.
     *       (Needed for mathematical correctness)
     *
     * Implied Parameters:
     *   In order to function just as the original opcode,
     *   both src1 and dest will be fixed as the accumulator.
     *
     * @param src1 Byte to be subtracted from.
     * @param src2 Byte to be subtracted.
     * @param dest Byte to store difference.
     */
    private void SBC(Byte dest, Byte src1, Byte src2)
    {
      //Assume src1 is A, src2 is B, dest is A
      int temp;
      //A - M - NOT(C)
      temp = src1.getVal();
      temp -= src2.getVal();
      if (!P.getBit(7)) temp--;
      temp = checkOverflow(temp);
      temp = checkNegative(temp);
      checkZero(temp);
      dest.setVal(temp);
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
      //All parameter data is useless, so we ignore, just setting C
      P.setBit(7,true);
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
      //All parameter data is useless, so we ignore, just setting D
      P.setBit(4,true);
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
      //All parameter data is useless, so we ignore, just setting I
      P.setBit(5,true);
    }
    
    /**
     * Store Accumulator
     * Stores the value of the accumulator into memory.
     *
     * Implied Parameters:
     *   In order to function just as the original opcode,
     *   src1 will be fixed as the accumulator.
     *
     * @param src1 Byte to be stored.
     * @param dest Byte to be stored into.
     */
    private void STA(Byte dest, Byte src1)
    {
      //Assume A is src1 and src2, memory byte is in dest
      dest.setVal(src1.getVal());
    }    
    
    /**
     * Store Register X
     * Stores the value of register X into memory.
     *
     * Implied Parameters:
     *   In order to function just as the original opcode,
     *   src1 will be fixed as register X.
     *
     * @param src1 Byte to be stored.
     * @param dest Byte to be stored into.
     */
    private void STX(Byte dest, Byte src1)
    {
      //Assume X is src1 and src2, memory byte is in dest
      dest.setVal(src1.getVal());
    }    
    
    /**
     * Store Register Y
     * Stores the value of register Y into memory.
     *
     * Implied Parameters:
     *   In order to function just as the original opcode,
     *   src1 will be fixed as register Y.
     *
     * @param src1 Byte to be stored.
     * @param dest Byte to be stored into.
     */
    private void STY(Byte dest, Byte src1)
    {
      //Assume Y is src1 and src2, memory byte is in dest
      dest.setVal(src1.getVal());
    }    
    
    /**
     * Transfer A to X
     * Copies the value in the accumulator and stores it in register X.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * Implied Parameters:
     *   In order to function just as the original opcode,
     *   src1 will be fixed as the accumulator and dest will
     *   be fixed as register X.
     *
     * @param src1 Byte to be transferred.
     * @param dest Byte to accept transfer.
     */
    private void TAX(Byte dest, Byte src1)
    {
      //Assume A is src1 and src2, X byte is in dest
      dest.setVal(checkNegative(src1.getVal()));
      checkZero(dest.getVal());
    }
    
    /**
     * Transfer A to Y
     * Copies the value in the accumulator and stores it in register Y.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * Implied Parameters:
     *   In order to function just as the original opcode,
     *   src1 will be fixed as the accumulator and dest will
     *   be fixed as register Y.
     *
     * @param src1 Byte to be transferred.
     * @param dest Byte to accept transfer.
     */
    private void TAY(Byte dest, Byte src1)
    {
      //Assume A is src1 and src2, Y byte is in dest
      dest.setVal(checkNegative(src1.getVal()));
      checkZero(dest.getVal());
    }
    
    //TSX Requires Stack
    
    /**
     * Transfer X to A
     * Copies the value in register X and stores it in the accumulator.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * Implied Parameters:
     *   In order to function just as the original opcode,
     *   src1 will be fixed as register X and dest will
     *   be fixed as the accumulator.
     *
     * @param src1 Byte to be transferred.
     * @param dest Byte to accept transfer.
     */
    private void TXA(Byte dest, Byte src1, Byte Src2)
    {
      //Assume X is src1 and src2, A byte is in dest
      dest.setVal(checkNegative(src1.getVal()));
      checkZero(dest.getVal());
    }
    
    //TXS requires stack
    
    /**
     * Transfer Y to A
     * Copies the value in register Y and stores it in the accumulator.
     *
     * Used Flags:
     *   N - Set if result is negative.
     *   Z - Set if result is zero.
     *
     * Implied Parameters:
     *   In order to function just as the original opcode,
     *   src1 will be fixed as register Y and dest will
     *   be fixed as the accumulator.
     *
     * @param src1 Byte to be transferred.
     * @param dest Byte to accept transfer.
     */
    private void TYA(Byte dest, Byte src1, Byte Src2)
    {
      //Assume X is src1 and src2, A byte is in dest
      dest.setVal(checkNegative(src1.getVal()));
      checkZero(dest.getVal());
    }
    
}
