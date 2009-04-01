/**
 * Processor.java
 * Emulates the NES' main processor
 *
 * @author Christopher Erickson
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
    
    /* OPCODE IMPLEMENTATION */
    // Your end :) //
    
    // Add with carry - Opcode
    private void ADC( Byte dest, Byte src1, Byte src2 ) {
        // Obviously needs to be updated with carry, but
        // this is just an example.
        dest.setVal( src1.getVal() + src2.getVal() );
    }
}
