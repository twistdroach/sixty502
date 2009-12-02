import java.util.HashMap;

/**
 * InstructionProperties.java
 * Keeps track of instruction-specific properties
 * 
 * Map of assembly instruction -> bytecode:
 * 		<INST NAME> <ADDR MODE> -> <0xBYTEVALUE>
 * 		Addressing mode ranges from:
 *	 		IMP - Implicit
 *	 		ACC - Accumulator
 *	 		IMM - Immediate
 *	 		ZPG - Zero Page
 *	 		ZPX - Zero Page, X
 *	 		ZPY - Zero Page, Y
 *	 		REL - Relative
 *	 		ABS - Absolute
 *	 		ABX - Absolute, X
 *	 		ABY - Absolute, Y
 *	 		IND - Indirect
 *	 		INX - Indirect, X
 *	 		INY - Indirect, Y
 *		If a key with a certain addressing mode doesn't exist, it is assumed that
 *		it is not supported by that opcode.
 * 
 * @author Christopher Erickson
 */

public class InstructionProperties {

	public static final HashMap<String,Integer> BYTECODE = new HashMap<String,Integer>();
	public static final HashMap<Integer,Integer> LENGTH = new HashMap<Integer,Integer>();

	static
	{
		// ADC
		BYTECODE.put( "ADC IMM", 0x69 ); // Immediate
		BYTECODE.put( "ADC ZPG", 0x65 ); // Zero Page
		BYTECODE.put( "ADC ZPX", 0x75 ); // Zero Page, X
		BYTECODE.put( "ADC ABS", 0x6D ); // Absolute
		BYTECODE.put( "ADC ABX", 0x7D ); // Absolute, X
		BYTECODE.put( "ADC ABY", 0x79 ); // Absolute, Y
		BYTECODE.put( "ADC INX", 0x61 ); // Indirect, X
		BYTECODE.put( "ADC INY", 0x71 ); // Indirect, Y
		LENGTH.put( 0x69, 2 ); // Immediate
		LENGTH.put( 0x65, 2 ); // Zero Page
		LENGTH.put( 0x75, 2 ); // Zero Page, X
		LENGTH.put( 0x6D, 3 ); // Absolute
		LENGTH.put( 0x7D, 3 ); // Absolute, X
		LENGTH.put( 0x79, 3 ); // Absolute, Y
		LENGTH.put( 0x61, 2 ); // Indirect, X
		LENGTH.put( 0x71, 2 ); // Indirect, Y
		
		// AND
		BYTECODE.put( "AND IMM", 0x29 ); // Immediate
		BYTECODE.put( "AND ZPG", 0x25 ); // Zero Page
		BYTECODE.put( "AND ZPX", 0x35 ); // Zero Page, X
		BYTECODE.put( "AND ABS", 0x2D ); // Absolute
		BYTECODE.put( "AND ABX", 0x3D ); // Absolute, X
		BYTECODE.put( "AND ABY", 0x39 ); // Absolute, Y
		BYTECODE.put( "AND INX", 0x21 ); // Indirect, X
		BYTECODE.put( "AND INY", 0x31 ); // Indirect, Y
		LENGTH.put( 0x29, 2 ); // Immediate
		LENGTH.put( 0x25, 2 ); // Zero Page
		LENGTH.put( 0x35, 2 ); // Zero Page, X
		LENGTH.put( 0x2D, 3 ); // Absolute
		LENGTH.put( 0x3D, 3 ); // Absolute, X
		LENGTH.put( 0x39, 3 ); // Absolute, Y
		LENGTH.put( 0x21, 2 ); // Indirect, X
		LENGTH.put( 0x31, 2 ); // Indirect, Y
		
		// ASL
		BYTECODE.put( "ASL ACC", 0x0A ); // Accumulator
		BYTECODE.put( "ASL ZPG", 0x06 ); // Zero Page
		BYTECODE.put( "ASL ZPX", 0x16 ); // Zero Page, X
		BYTECODE.put( "ASL ABS", 0x0E ); // Absolute
		BYTECODE.put( "ASL ABX", 0x1E ); // Absolute, X
		LENGTH.put( 0x0A, 1 ); // Accumulator
		LENGTH.put( 0x06, 2 ); // Zero Page
		LENGTH.put( 0x16, 2 ); // Zero Page, X
		LENGTH.put( 0x0E, 3 ); // Absolute
		LENGTH.put( 0x1E, 3 ); // Absolute, X
		
		// BCC
		BYTECODE.put( "BCC REL", 0x90 ); // Relative
		LENGTH.put( 0x90, 2 ); // Relative
		
		// BCS
		BYTECODE.put( "BCS REL", 0xB0 ); // Relative
		LENGTH.put( 0xB0, 2 ); // Relative
		
		// BEQ
		BYTECODE.put( "BEQ REL", 0xF0 ); // Relative
		LENGTH.put( 0xF0, 2 ); // Relative
		
		// BIT
		BYTECODE.put( "BIT ZPG", 0x24 ); // Zero Page
		BYTECODE.put( "BIT ABS", 0x2C ); // Absolute
		LENGTH.put( 0x24, 2 ); // Zero Page
		LENGTH.put( 0x2C, 3 ); // Absolute
		
		// BMI
		BYTECODE.put( "BMI REL", 0x30 ); // Relative
		LENGTH.put( 0x30, 2 ); // Relative
		
		// BNE
		BYTECODE.put( "BNE REL", 0xD0 ); // Relative
		LENGTH.put( 0xD0, 2 ); // Relative
		
		// BPL
		BYTECODE.put( "BPL REL", 0x10 ); // Relative
		LENGTH.put( 0x10, 2 ); // Relative
		
		// BRK
		BYTECODE.put( "BRK IMP", 0x00 ); // Implied
		LENGTH.put( 0x00, 1 ); // Implied
		
		// BVC
		BYTECODE.put( "BVC REL", 0x50 ); // Relative
		LENGTH.put( 0x50, 2 ); // Relative
		
		// BVS
		BYTECODE.put( "BVS REL", 0x70 ); // Relative
		LENGTH.put( 0x70, 2 ); // Relative
		
		// CLC
		BYTECODE.put( "CLC IMP", 0x18 ); // Implied
		LENGTH.put( 0x18, 1 ); // Implied
		
		// CLD
		BYTECODE.put( "CLD IMP", 0xD8 ); // Implied
		LENGTH.put( 0xD8, 1 ); // Implied
		
		// CLI
		BYTECODE.put( "CLI IMP", 0x58 ); // Implied
		LENGTH.put( 0x58, 1 ); // Implied
		
		// CLV
		BYTECODE.put( "CLV IMP", 0xB8 ); // Implied
		LENGTH.put( 0xB8, 1 ); // Implied
		
		// CMP
		BYTECODE.put( "CMP IMM", 0xC9 ); // Immediate
		BYTECODE.put( "CMP ZPG", 0xC5 ); // Zero Page
		BYTECODE.put( "CMP ZPX", 0xD5 ); // Zero Page, X
		BYTECODE.put( "CMP ABS", 0xCD ); // Absolute
		BYTECODE.put( "CMP ABX", 0xDD ); // Absolute, X
		BYTECODE.put( "CMP ABY", 0xD9 ); // Absolute, Y
		BYTECODE.put( "CMP INX", 0xC1 ); // Indirect, X
		BYTECODE.put( "CMP INY", 0xD1 ); // Indirect, Y
		LENGTH.put( 0xC9, 2 ); // Immediate
		LENGTH.put( 0xC5, 2 ); // Zero Page
		LENGTH.put( 0xD5, 2 ); // Zero Page, X
		LENGTH.put( 0xCD, 3 ); // Absolute
		LENGTH.put( 0xDD, 3 ); // Absolute, X
		LENGTH.put( 0xD9, 3 ); // Absolute, Y
		LENGTH.put( 0xC1, 2 ); // Indirect, X
		LENGTH.put( 0xD1, 2 ); // Indirect, Y
		
		// CPX
		BYTECODE.put( "CPX IMM", 0xE0 ); // Immediate
		BYTECODE.put( "CPX ZPG", 0xE4 ); // Zero Page
		BYTECODE.put( "CPX ABS", 0xEC ); // Absolute
		LENGTH.put( 0xE0, 2 ); // Immediate
		LENGTH.put( 0xE4, 2 ); // Zero Page
		LENGTH.put( 0xEC, 3 ); // Absolute
		
		// CPY
		BYTECODE.put( "CPY IMM", 0xC0 ); // Immediate
		BYTECODE.put( "CPY ZPG", 0xC4 ); // Zero Page
		BYTECODE.put( "CPY ABS", 0xCC ); // Absolute
		LENGTH.put( 0xC0, 2 ); // Immediate
		LENGTH.put( 0xC4, 2 ); // Zero Page
		LENGTH.put( 0xCC, 3 ); // Absolute
		
		// DEC
		BYTECODE.put( "DEC ZPG", 0xC6 ); // Zero Page
		BYTECODE.put( "DEC ZPX", 0xD6 ); // Zero Page, X
		BYTECODE.put( "DEC ABS", 0xCE ); // Absolute
		BYTECODE.put( "DEC ABX", 0xDE ); // Absolute, X
		LENGTH.put( 0xC6, 2 ); // Zero Page
		LENGTH.put( 0xD6, 2 ); // Zero Page, X
		LENGTH.put( 0xCE, 3 ); // Absolute
		LENGTH.put( 0xDE, 3 ); // Absolute, X
		
		// DEX
		BYTECODE.put( "DEX IMP", 0xCA ); // Implied
		LENGTH.put( 0xCA, 1 ); // Implied
		
		// DEY
		BYTECODE.put( "DEY IMP", 0x88 ); // Implied
		LENGTH.put( 0x88, 1 ); // Implied
		
		// EOR
		BYTECODE.put( "EOR IMM", 0x49 ); // Immediate
		BYTECODE.put( "EOR ZPG", 0x45 ); // Zero Page
		BYTECODE.put( "EOR ZPX", 0x55 ); // Zero Page, X
		BYTECODE.put( "EOR ABS", 0x4D ); // Absolute
		BYTECODE.put( "EOR ABX", 0x5D ); // Absolute, X
		BYTECODE.put( "EOR ABY", 0x59 ); // Absolute, Y
		BYTECODE.put( "EOR INX", 0x41 ); // Indirect, X
		BYTECODE.put( "EOR INY", 0x51 ); // Indirect, Y
		LENGTH.put( 0x49, 2 ); // Immediate
		LENGTH.put( 0x45, 2 ); // Zero Page
		LENGTH.put( 0x55, 2 ); // Zero Page, X
		LENGTH.put( 0x4D, 3 ); // Absolute
		LENGTH.put( 0x5D, 3 ); // Absolute, X
		LENGTH.put( 0x59, 3 ); // Absolute, Y
		LENGTH.put( 0x41, 2 ); // Indirect, X
		LENGTH.put( 0x51, 2 ); // Indirect, Y
		
		// INC
		BYTECODE.put( "INC ZPG", 0xE6 ); // Zero Page
		BYTECODE.put( "INC ZPX", 0xF6 ); // Zero Page, X
		BYTECODE.put( "INC ABS", 0xEE ); // Absolute
		BYTECODE.put( "INC ABX", 0xFE ); // Absolute, X
		LENGTH.put( 0xE6, 2 ); // Zero Page
		LENGTH.put( 0xF6, 2 ); // Zero Page, X
		LENGTH.put( 0xEE, 3 ); // Absolute
		LENGTH.put( 0xFE, 3 ); // Absolute, X
		
		// INX
		BYTECODE.put( "INX IMP", 0xE8 ); // Implied
		LENGTH.put( 0xE8, 1 ); // Implied
		
		// INY
		BYTECODE.put( "INY IMP", 0xC8 ); // Implied
		LENGTH.put( 0xC8, 1 ); // Implied
		
		// JMP
		BYTECODE.put( "JMP ABS", 0x4C ); // Absolute
		BYTECODE.put( "JMP IND", 0x6C ); // Indirect
		LENGTH.put( 0x4C, 3 ); // Absolute
		LENGTH.put( 0x6C, 3 ); // Indirect
		
		// JSR
		BYTECODE.put( "JSR ABS", 0x20 ); // Absolute
		LENGTH.put( 0x20, 3 ); // Absolute
		
		// LDA
		BYTECODE.put( "LDA IMM", 0xA9 ); // Immediate
		BYTECODE.put( "LDA ZPG", 0xA5 ); // Zero Page
		BYTECODE.put( "LDA ZPX", 0xB5 ); // Zero Page, X
		BYTECODE.put( "LDA ABS", 0xAD ); // Absolute
		BYTECODE.put( "LDA ABX", 0xBD ); // Absolute, X
		BYTECODE.put( "LDA ABY", 0xB9 ); // Absolute, Y
		BYTECODE.put( "LDA INX", 0xA1 ); // Indirect, X
		BYTECODE.put( "LDA INY", 0xB1 ); // Indirect, Y
		LENGTH.put( 0xA9, 2 ); // Immediate
		LENGTH.put( 0xA5, 2 ); // Zero Page
		LENGTH.put( 0xB5, 2 ); // Zero Page, X
		LENGTH.put( 0xAD, 3 ); // Absolute
		LENGTH.put( 0xBD, 3 ); // Absolute, X
		LENGTH.put( 0xB9, 3 ); // Absolute, Y
		LENGTH.put( 0xA1, 2 ); // Indirect, X
		LENGTH.put( 0xB1, 2 ); // Indirect, Y
		
		// LDX
		BYTECODE.put( "LDX IMM", 0xA2 ); // Immediate
		BYTECODE.put( "LDX ZPG", 0xA6 ); // Zero Page
		BYTECODE.put( "LDX ZPY", 0xB6 ); // Zero Page, Y
		BYTECODE.put( "LDX ABS", 0xAE ); // Absolute
		BYTECODE.put( "LDX ABY", 0xBE ); // Absolute, Y
		LENGTH.put( 0xA2, 2 ); // Immediate
		LENGTH.put( 0xA6, 2 ); // Zero Page
		LENGTH.put( 0xB6, 2 ); // Zero Page, Y
		LENGTH.put( 0xAE, 3 ); // Absolute
		LENGTH.put( 0xBE, 3 ); // Absolute, Y
		
		// LDY
		BYTECODE.put( "LDY IMM", 0xA0 ); // Immediate
		BYTECODE.put( "LDY ZPG", 0xA4 ); // Zero Page
		BYTECODE.put( "LDY ZPX", 0xB4 ); // Zero Page, X
		BYTECODE.put( "LDY ABS", 0xAC ); // Absolute
		BYTECODE.put( "LDY ABX", 0xBC ); // Absolute, X
		LENGTH.put( 0xA0, 2 ); // Immediate
		LENGTH.put( 0xA4, 2 ); // Zero Page
		LENGTH.put( 0xB4, 2 ); // Zero Page, X
		LENGTH.put( 0xAC, 3 ); // Absolute
		LENGTH.put( 0xBC, 3 ); // Absolute, X
		
		// LSR
		BYTECODE.put( "LSR ACC", 0x4A ); // Accumulator
		BYTECODE.put( "LSR ZPG", 0x46 ); // Zero Page
		BYTECODE.put( "LSR ZPX", 0x56 ); // Zero Page, X
		BYTECODE.put( "LSR ABS", 0x4E ); // Absolute
		BYTECODE.put( "LSR ABX", 0x5E ); // Absolute, X
		LENGTH.put( 0x4A, 1 ); // Accumulator
		LENGTH.put( 0x46, 2 ); // Zero Page
		LENGTH.put( 0x56, 2 ); // Zero Page, X
		LENGTH.put( 0x4E, 3 ); // Absolute
		LENGTH.put( 0x5E, 3 ); // Absolute, X
		
		// NOP
		BYTECODE.put( "NOP IMP", 0xEA ); // Implied
		LENGTH.put( 0xEA, 1 ); // Implied
		
		// ORA
		BYTECODE.put( "ORA IMM", 0x09 ); // Immediate
		BYTECODE.put( "ORA ZPG", 0x05 ); // Zero Page
		BYTECODE.put( "ORA ZPX", 0x15 ); // Zero Page, X
		BYTECODE.put( "ORA ABS", 0x0D ); // Absolute
		BYTECODE.put( "ORA ABX", 0x1D ); // Absolute, X
		BYTECODE.put( "ORA ABY", 0x19 ); // Absolute, Y
		BYTECODE.put( "ORA INX", 0x01 ); // Indirect, X
		BYTECODE.put( "ORA INY", 0x11 ); // Indirect, Y
		LENGTH.put( 0x09, 2 ); // Immediate
		LENGTH.put( 0x05, 2 ); // Zero Page
		LENGTH.put( 0x15, 2 ); // Zero Page, X
		LENGTH.put( 0x0D, 3 ); // Absolute
		LENGTH.put( 0x1D, 3 ); // Absolute, X
		LENGTH.put( 0x19, 3 ); // Absolute, Y
		LENGTH.put( 0x01, 2 ); // Indirect, X
		LENGTH.put( 0x11, 2 ); // Indirect, Y
		
		// PHA
		BYTECODE.put( "PHA IMP", 0x48 ); // Implied
		LENGTH.put( 0x48, 1 ); // Implied
		
		// PHP
		BYTECODE.put( "PHP IMP", 0x08 ); // Implied
		LENGTH.put( 0x08, 1 ); // Implied
		
		// PLA
		BYTECODE.put( "PLA IMP", 0x68 ); // Implied
		LENGTH.put( 0x68, 1 ); // Implied
		
		// PLP
		BYTECODE.put( "PLP IMP", 0x28 ); // Implied
		LENGTH.put( 0x28, 1 ); // Implied
		
		// ROL
		BYTECODE.put( "ROL ACC", 0x2A ); // Accumulator
		BYTECODE.put( "ROL ZPG", 0x26 ); // Zero Page
		BYTECODE.put( "ROL ZPX", 0x36 ); // Zero Page, X
		BYTECODE.put( "ROL ABS", 0x2E ); // Absolute
		BYTECODE.put( "ROL ABX", 0x3E ); // Absolute, X
		LENGTH.put( 0x2A, 1 ); // Accumulator
		LENGTH.put( 0x26, 2 ); // Zero Page
		LENGTH.put( 0x36, 2 ); // Zero Page, X
		LENGTH.put( 0x2E, 3 ); // Absolute
		LENGTH.put( 0x3E, 3 ); // Absolute, X
		
		// ROR
		BYTECODE.put( "ROR ACC", 0x6A ); // Accumulator
		BYTECODE.put( "ROR ZPG", 0x66 ); // Zero Page
		BYTECODE.put( "ROR ZPX", 0x76 ); // Zero Page, X
		BYTECODE.put( "ROR ABS", 0x6E ); // Absolute
		BYTECODE.put( "ROR ABX", 0x7E ); // Absolute, X
		LENGTH.put( 0x6A, 1 ); // Accumulator
		LENGTH.put( 0x66, 2 ); // Zero Page
		LENGTH.put( 0x76, 2 ); // Zero Page, X
		LENGTH.put( 0x6E, 3 ); // Absolute
		LENGTH.put( 0x7E, 3 ); // Absolute, X
		
		// RTI
		BYTECODE.put( "RTI IMP", 0x40 ); // Implied
		LENGTH.put( 0x40, 1 ); // Implied
		
		// RTS
		BYTECODE.put( "RTS IMP", 0x60 ); // Implied
		LENGTH.put( 0x60, 1 ); // Implied
		
		// SBC
		BYTECODE.put( "SBC IMM", 0xE9 ); // Immediate
		BYTECODE.put( "SBC ZPG", 0xE5 ); // Zero Page
		BYTECODE.put( "SBC ZPX", 0xF5 ); // Zero Page, X
		BYTECODE.put( "SBC ABS", 0xED ); // Absolute
		BYTECODE.put( "SBC ABX", 0xFD ); // Absolute, X
		BYTECODE.put( "SBC ABY", 0xF9 ); // Absolute, Y
		BYTECODE.put( "SBC INX", 0xE1 ); // Indirect, X
		BYTECODE.put( "SBC INY", 0xF1 ); // Indirect, Y
		LENGTH.put( 0xE9, 2 ); // Immediate
		LENGTH.put( 0xE5, 2 ); // Zero Page
		LENGTH.put( 0xF5, 2 ); // Zero Page, X
		LENGTH.put( 0xED, 3 ); // Absolute
		LENGTH.put( 0xFD, 3 ); // Absolute, X
		LENGTH.put( 0xF9, 3 ); // Absolute, Y
		LENGTH.put( 0xE1, 2 ); // Indirect, X
		LENGTH.put( 0xF1, 2 ); // Indirect, Y
		
		// SEC
		BYTECODE.put( "SEC IMP", 0x38 ); // Implied
		LENGTH.put( 0x38, 1 ); // Implied
		
		// SED
		BYTECODE.put( "SED IMP", 0xF8 ); // Implied
		LENGTH.put( 0xF8, 1 ); // Implied
		
		// SEI
		BYTECODE.put( "SEI IMP", 0x78 ); // Implied
		LENGTH.put( 0x78, 1 ); // Implied
		
		// STA
		BYTECODE.put( "STA ZPG", 0x85 ); // Zero Page
		BYTECODE.put( "STA ZPX", 0x95 ); // Zero Page, X
		BYTECODE.put( "STA ABS", 0x8D ); // Absolute
		BYTECODE.put( "STA ABX", 0x9D ); // Absolute, X
		BYTECODE.put( "STA ABY", 0x99 ); // Absolute, Y
		BYTECODE.put( "STA INX", 0x81 ); // Indirect, X
		BYTECODE.put( "STA INY", 0x91 ); // Indirect, Y
		LENGTH.put( 0x85, 2 ); // Zero Page
		LENGTH.put( 0x95, 2 ); // Zero Page, X
		LENGTH.put( 0x8D, 3 ); // Absolute
		LENGTH.put( 0x9D, 3 ); // Absolute, X
		LENGTH.put( 0x99, 3 ); // Absolute, Y
		LENGTH.put( 0x81, 2 ); // Indirect, X
		LENGTH.put( 0x91, 2 ); // Indirect, Y
		
		// STX
		BYTECODE.put( "STX ZPG", 0x86 ); // Zero Page
		BYTECODE.put( "STX ZPY", 0x96 ); // Zero Page, Y
		BYTECODE.put( "STX ABS", 0x8E ); // Absolute
		LENGTH.put( 0x86, 2 ); // Zero Page
		LENGTH.put( 0x96, 2 ); // Zero Page, Y
		LENGTH.put( 0x8E, 3 ); // Absolute
		
		// STY
		BYTECODE.put( "STY ZPG", 0x84 ); // Zero Page
		BYTECODE.put( "STY ZPX", 0x94 ); // Zero Page, X
		BYTECODE.put( "STY ABS", 0x8C ); // Absolute
		LENGTH.put( 0x84, 2 ); // Zero Page
		LENGTH.put( 0x94, 2 ); // Zero Page, X
		LENGTH.put( 0x8C, 3 ); // Absolute
		
		// TAX
		BYTECODE.put( "TAX IMP", 0xAA ); // Implied
		LENGTH.put( 0xAA, 1 ); // Implied
		
		// TAY
		BYTECODE.put( "TAY IMP", 0xA8 ); // Implied
		LENGTH.put( 0xA8, 1 ); // Implied
		
		// TSX
		BYTECODE.put( "TSX IMP", 0xBA ); // Implied
		LENGTH.put( 0xBA, 1 ); // Implied
		
		// TXA
		BYTECODE.put( "TXA IMP", 0x8A ); // Implied
		LENGTH.put( 0x8A, 1 ); // Implied
		
		// TXS
		BYTECODE.put( "TXS IMP", 0x9A ); // Implied
		LENGTH.put( 0x9A, 1 ); // Implied
		
		// TYA
		BYTECODE.put( "TYA IMP", 0x98 ); // Implied
		LENGTH.put( 0x98, 1 ); // Implied
	}
}
