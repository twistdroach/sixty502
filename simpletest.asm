; a = 2 + 2 (4)
lda #2
adc #2
; transfer a -> x
tax
; a = 0x22 + 0x10 + 1 (0x33)
lda #$22
sec ; plus one
adc #$10