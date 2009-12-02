; Sprite.asm
; A sprite example with drawing routine.
;
; This could be applied to any sprite with
; a little modification.
;
; Keys: WASD - Move mario is that direction
;          Q - Quit
;
; by NeoScriptor

start:
  ; Let's draw Mario, and even move him!

  ; sprite = mario
  lda #<mario
  sta sprlow
  lda #>mario
  sta sprhigh

  ; Setup coordinates
  lda #8
  sta xpos
  lda #8
  sta ypos

  ; Width & height of sprite
  lda #16  ; width
  sta width
  lda #16  ; height
  sta height

mainloop:
  ; Draw the sprite!
  jsr drawSprite

  ; Get the pressed key
  jsr getKey
  lda key
  cmp #0
  beq mainloop
  cmp #119
  beq wkey
  cmp #97
  beq akey
  cmp #115
  beq skey
  cmp #100
  beq dkey
  cmp #113
  beq qkey
  jmp mainloop
wkey:
  ; The w key was pressed
  ; Move the sprite up
  lda ypos
  cmp #1
  bmi mainloop
  ; Clear the bottom of the sprite
  clc
  adc height
  tay
  dey
  sty clsrow
  jsr clearLine
  dec ypos
  jmp mainloop
akey:
  ; The a key was pressed
  ; Move the sprite left
  lda xpos
  cmp #0
  bmi mainloop
  dec xpos
  jmp mainloop
skey:
  ; The s key was pressed
  ; Move the sprite down
  lda ypos
  cmp #16
  bpl mainloop
  ; Clear the top of the sprite
  tay
  sty clsrow
  jsr clearLine
  inc ypos
  jmp mainloop
dkey:
  ; The d key was pressed
  ; Move the sprite right
  lda xpos
  cmp #18
  bpl mainloop
  inc xpos
  jmp mainloop
qkey:
  ; The q key was pressed
  ; End
  rts

; drawSprite - Draws a sprite
; Note: Can only handle widths that are
; multiples of two. i.e. 2, 4, 8, 16, etc
drawSprite:
  ; Initialize row & col
  lda #0
  sta row
  lda #0
  sta col

  ; Load the sprite
  lda sprlow
  sta $40
  lda sprhigh
  sta $41

drawloop:
  ; Calculate where to grab data from using
  ; row & col (width*row)+col
  ldy row
  lda #1
  ; Do as many left shifts as it takes to mult
  ; by the sprite width
rowshift:
  tax
  tya
  asl A
  tay
  txa
  asl A
  cmp width
  bne rowshift
  ; Done multiplying, result in y
  ; Add col
  clc
  tya
  adc col
  tay

  lda ($40), y ; Grab this pixel from sprite
  sta pixel

  ; Now we have to calculate where to put it!
  ; ylookup(row + ypos) + (col + xpos)
  ; Add row & ypos
  lda row
  clc
  adc ypos
  ; Get appropriate value from table
  asl A
  tax
  lda ylookup, x
  sta $50
  inx
  lda ylookup, x
  sta $51
  ; Add col & xpos
  lda $50
  clc
  adc col
  adc xpos
  sta $50
  lda pixel
  ldy #0
  sta ($50), y ; Put it on the screen

  ; Increase the col
  inc col
  ; If col != width, continue looping
  lda col
  cmp width
  bne drawloop
  ; Otherwise, col = 0, row++
  lda #0
  sta col
  lda row
  clc
  adc #1
  sta row
  ; If row == height, return
  lda row
  cmp height
  bne drawloop
  rts ; return

; drawSprite variables
sprlow: dcb 0
sprhigh: dcb 0
width: dcb 0
height: dcb 0
row: dcb 0
col: dcb 0
pixel: dcb 0
xpos: dcb 0
ypos: dcb 0

; clearLine - Clears the line clsrow
clearLine:
  lda clsrow
  ; Get appropriate value from table
  asl A
  tax
  lda ylookup, x
  sta $c0
  inx
  lda ylookup, x
  sta $c1
  lda #0
  ldy #0
  sta ($c0), y
  ldy #31
clsLoop:
  sta ($c0), y
  dey
  bne clsLoop
  rts

; clearLine variables
clsrow: dcb 0

; getKey - Stores the value of the pressed key
getKey:
  lda $ff
  sta key
  lda #0
  sta $ff
  rts

; getKey variables
key: dcb 0

; Table of mem corresponding to each row
ylookup:
 dcb $00,$02,$20,$02,$40,$02,$60,$02
 dcb $80,$02,$a0,$02,$c0,$02,$e0,$02
 dcb $00,$03,$20,$03,$40,$03,$60,$03
 dcb $80,$03,$a0,$03,$c0,$03,$e0,$03
 dcb $00,$04,$20,$04,$40,$04,$60,$04
 dcb $80,$04,$a0,$04,$c0,$04,$e0,$04
 dcb $00,$05,$20,$05,$40,$05,$60,$05
 dcb $80,$05,$a0,$05,$c0,$05,$e0,$05

; Sprites
mario: ; 16 x 16
  dcb 0,0,0,0,2,2,2,2,2,2,0,0,0,0,0,0
  dcb 0,0,0,2,2,2,2,2,2,2,2,2,2,0,0,0
  dcb 0,0,0,9,9,9,9,8,8,9,8,0,0,0,0,0
  dcb 0,0,9,9,8,9,8,8,8,9,8,8,8,0,0,0
  dcb 0,0,9,9,8,9,9,8,8,8,9,8,8,8,0,0
  dcb 0,0,9,9,9,8,8,8,8,9,9,9,9,0,0,0
  dcb 0,0,0,0,8,8,8,8,8,8,8,8,0,0,0,0
  dcb 0,0,0,9,9,9,2,9,9,9,0,0,0,0,0,0
  dcb 0,0,9,9,9,9,2,9,9,2,9,9,9,0,0,0
  dcb 0,9,9,9,9,9,2,2,2,2,9,9,9,9,0,0
  dcb 0,8,8,8,9,2,8,2,2,8,2,9,8,8,0,0
  dcb 0,8,8,8,8,2,2,2,2,2,2,8,8,8,0,0
  dcb 0,8,8,8,2,2,2,2,2,2,2,2,8,8,0,0
  dcb 0,0,0,2,2,2,2,0,2,2,2,2,0,0,0,0
  dcb 0,0,9,9,9,9,0,0,0,9,9,9,9,0,0,0
  dcb 0,9,9,9,9,9,0,0,0,9,9,9,9,9,0,0
