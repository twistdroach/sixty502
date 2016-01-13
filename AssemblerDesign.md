# Introduction #

This section should detail all 6502-specific items necessary to write an assembler. It should also map out the implementation.

Check out this great resource: [How to program the Apple II](http://www.scribd.com/doc/19068650/HOW-TO-PROGRAM-THE-APPLE-II-USING-6502-ASSEMBLY-LANGUAGE-With-an-Introduction-to-Sweet16)
Much of it is summarized and used here.

## Source Format ##

Every line in a 6502 assembly source file is either blank or adheres to the layout
| LABEL MNEMONIC OPERAND ;COMMENTS |
|:---------------------------------|

One or more spaces/tabs must separate each column. Spaces within labels, mnemonics, and operands are not allowed. If a label is omitted, the space preceding the mnemonic must remain. See below for more details on each piece.

Examples:
```
LABEL adc #1 ; This adds one to the accumulator
      cmp #$4A  ; Compare A to 0x4A
      beq LABEL ; Branch to LABEL if A == 0x4A (74)

LABEL2 ; This is legal
      dex
```

## Label ##

  * Optional.
  * 1 to 8 characters long.
  * Must begin with an uppercase letter, followed by either numbers or uppercase letters.

**Feature:** This assembler will convert all source files to uppercase, therefore making most lowercase code valid. However, this means labels are case insensitive.

This makes something like the following illegal:
```
laBel
LABEL
BNZ LABEL
```

## Mnemonic ##

## Operand ##

**Feature:** Support address expressions. i.e. `*`+4, LABEL-2, $1293+$12, $00FF-1

### Value Representation ###

On the 6502, numbers are represented in binary, hexadecimal, or decimal. These numbers are typically preceded by a character specifying the radix.

These are:
| ! | Decimal |
|:--|:--------|
| % | Binary  |
| $ | Hexadecimal |

If a radix character is omitted, the value is assumed to be decimal.

**Feature:** The assembler will determine if a specified value is outside of the bounds of the intended addressing mode.

**Feature:** The assembler will determine if a specified value uses characters outside its radix. (i.e. %0121 will give an error)

## Comments ##