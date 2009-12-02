; Simple 6502 Emulator test
start:
	; A = 2 + 2 (4)
	lda #HI loop
	lda $1, x
	adc $02
	beq *-4
	
	; A -> X
	tax

	; A = 0x22 + 0x10 + 1 (0x33)
	lda #$22
	sec	; Set the carry
	adc #$10

	; Some memory usage
	sta $02	; A -> MEM[0x02]
	ldx $02	; MEM[0x02] -> X

	; Stack example
	lda #$32; Load A with 0x32
	pha	; Push 0x32 onto the stack
	adc #1	; Add one to A
	pha	; Push 0x33 onto the stack
	pla	; Pull 0x33 from the stack
	pla	; Pull 0x32 from the stack

	; Status stack usage
	php	; Push the status reg. onto the stack
	sec	; Set the carry flag
	sei	; Set the interrupt flag
	plp

	; Loop example
loop:
	lda #3	; Load A with 3
	adc #1	; Add one to A
	cmp #5	; Repeat while A < 5
	bpl loop
