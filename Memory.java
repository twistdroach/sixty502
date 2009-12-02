/**
 * Memory.java
 * Singleton class that emulates word-addressable NES memory.
 *
 * @author Christopher Erickson
 */

public class Memory
{
	public static final int programStart = 0x0400; // Location in memory where programs are stored
	public static final int stackStart = 0x0100;   // Location in memory where the stack starts
	public static int[] address = new int[65536];  // The memory
}
