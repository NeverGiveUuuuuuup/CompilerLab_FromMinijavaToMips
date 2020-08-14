         .text
         .globl main
main:
move $fp, $sp
subu $sp, $sp, 324
sw $ra, -4($fp)
li $s0 0
move $v1 $s0
sw $v1, 272($sp)
li $s0 0
move $v1 $s0
sw $v1, 276($sp)
li $s0 0
move $v1 $s0
li $s0 0
move $v1 $s0
li $s0 0
move $v1 $s0
li $s0 0
move $v1 $s0
li $s0 0
move $v1 $s0
sw $v1, 260($sp)
li $s0 0
move $v1 $s0
li $s0 0
move $v1 $s0
li $s0 20
move $s0 $s0
li $s1 4
li $s2 1
add $s2, $s2, $s0
mul $s1, $s1, $s2
move $a0 $s1
jal _halloc
move $s1 $v0
move $s1 $s1
sw $s0, 0($s1)
move $s0 $s1
move $s1 $s0
li $s0 19
move $s0 $s0
Label0: li $s2 0
slt $s2, $s2, $s0
beqz $s2 Label1
li $s2 4
mul $s2, $s2, $s0
add $s2, $s1, $s2
sw $s0, 4($s2)
li $s2 1
sub $s0, $s0, $s2
move $s0 $s0
b Label0
Label1: nop
li $s0 4
move $a0 $s0
jal _halloc
move $s0 $v0
move $s0 $s0
li $s2 4
move $a0 $s2
jal _halloc
move $s2 $v0
move $s3 $s2
sw $s0, 0($s3)
la $s2 E_e
sw $s2, 0($s0)
move $s0 $s3
move $s2 $s0
li $s0 24
move $a0 $s0
jal _halloc
move $s0 $v0
move $s0 $s0
li $s3 16
move $a0 $s3
jal _halloc
move $s3 $v0
move $s3 $s3
sw $s0, 0($s3)
li $s4 0
sw $s4, 4($s3)
li $s4 0
sw $s4, 8($s3)
li $s4 0
sw $s4, 12($s3)
la $s4 B_add
sw $s4, 0($s0)
la $s4 A_loc
sw $s4, 4($s0)
la $s4 A_print
sw $s4, 8($s0)
la $s4 A_set_true
sw $s4, 12($s0)
la $s4 B_set_false
sw $s4, 16($s0)
la $s4 B_show
sw $s4, 20($s0)
move $s0 $s3
lw $v1, 276($sp)
move $v1 $s0
sw $v1, 276($sp)
li $s0 28
move $a0 $s0
jal _halloc
move $s0 $v0
move $s3 $s0
li $s0 16
move $a0 $s0
jal _halloc
move $s0 $v0
move $s0 $s0
sw $s3, 0($s0)
li $s4 0
sw $s4, 4($s0)
li $s4 0
sw $s4, 8($s0)
li $s4 0
sw $s4, 12($s0)
la $s4 B_add
sw $s4, 0($s3)
la $s4 A_loc
sw $s4, 4($s3)
la $s4 A_print
sw $s4, 8($s3)
la $s4 A_set_true
sw $s4, 12($s3)
la $s4 B_set_false
sw $s4, 16($s3)
la $s4 B_show
sw $s4, 20($s3)
la $s4 C_func
sw $s4, 24($s3)
move $s0 $s0
lw $v1, 260($sp)
move $v1 $s0
sw $v1, 260($sp)
move $s0 $s2
lw $s2 0($s0)
lw $s2 0($s2)
move $s2 $s2
sw $s1, 304($sp)
move $a0 $s0
lw $v1, 276($sp)
move $a1 $v1
jalr $s2
lw $s1, 304($sp)
move $s0 $v0
move $s0 $s0
move $s0 $s0
move $v1 $s0
lw $v1, 272($sp)
lw $v0, 276($sp)
move $v1 $v0
sw $v1, 272($sp)
lw $v1, 276($sp)
move $s0 $v1
lw $s2 0($s0)
lw $s2 8($s2)
move $s2 $s2
sw $s1, 308($sp)
move $a0 $s0
jalr $s2
lw $s1, 308($sp)
move $s0 $v0
move $s0 $s0
move $s0 $s0
move $a0 $s0
jal _print
lw $v1, 272($sp)
move $s0 $v1
lw $s2 0($s0)
lw $s2 0($s2)
move $s2 $s2
sw $s1, 308($sp)
move $a0 $s0
jalr $s2
lw $s1, 308($sp)
move $s0 $v0
move $s0 $s0
move $s0 $s0
move $a0 $s0
jal _print
lw $v1, 276($sp)
move $s0 $v1
lw $s2 0($s0)
lw $s2 0($s2)
move $s2 $s2
sw $s1, 308($sp)
move $a0 $s0
jalr $s2
lw $s1, 308($sp)
move $s0 $v0
move $s0 $s0
move $s0 $s0
move $a0 $s0
jal _print
lw $v1, 272($sp)
move $s0 $v1
lw $s2 0($s0)
lw $s2 0($s2)
move $s2 $s2
sw $s1, 304($sp)
move $a0 $s0
jalr $s2
lw $s1, 304($sp)
move $s0 $v0
move $s0 $s0
move $s0 $s0
move $a0 $s0
jal _print
lw $v1, 276($sp)
move $s0 $v1
lw $s2 0($s0)
lw $s2 8($s2)
move $s2 $s2
sw $s1, 308($sp)
move $a0 $s0
jalr $s2
lw $s1, 308($sp)
move $s0 $v0
move $s0 $s0
move $s0 $s0
move $a0 $s0
jal _print
lw $v1, 276($sp)
move $s0 $v1
lw $s2 0($s0)
lw $s2 12($s2)
move $s2 $s2
sw $s1, 308($sp)
move $a0 $s0
jalr $s2
lw $s1, 308($sp)
move $s0 $v0
move $s0 $s0
move $s0 $s0
move $v1 $s0
lw $v1, 272($sp)
move $s0 $v1
lw $s2 0($s0)
lw $s2 16($s2)
move $s2 $s2
sw $s1, 308($sp)
move $a0 $s0
jalr $s2
lw $s1, 308($sp)
move $s0 $v0
move $s0 $s0
move $s0 $s0
move $v1 $s0
li $s3 1
lw $v1, 272($sp)
move $s0 $v1
lw $s2 0($s0)
lw $s2 4($s2)
move $s2 $s2
sw $s3, 300($sp)
sw $s1, 312($sp)
move $a0 $s0
jalr $s2
lw $s3, 300($sp)
lw $s1, 312($sp)
move $s0 $v0
move $s0 $s0
move $s4 $s0
lw $v1, 272($sp)
move $s0 $v1
lw $s2 0($s0)
lw $s2 12($s2)
move $s2 $s2
sw $s3, 300($sp)
sw $s1, 308($sp)
sw $s4, 312($sp)
move $a0 $s0
jalr $s2
lw $s3, 300($sp)
lw $s1, 308($sp)
lw $s4, 312($sp)
move $s0 $v0
move $s0 $s0
move $s0 $s0
add $s0, $s4, $s0
slt $s0, $s3, $s0
beqz $s0 Label2
li $s0 5428543
move $a0 $s0
jal _print
b Label3
Label2: lw $v1, 276($sp)
move $s0 $v1
lw $s2 0($s0)
lw $s2 4($s2)
move $s2 $s2
sw $s1, 308($sp)
move $a0 $s0
jalr $s2
lw $s1, 308($sp)
move $s0 $v0
move $s0 $s0
move $s0 $s0
beqz $s0 Label4
li $s0 1234567
move $a0 $s0
jal _print
b Label5
Label4: li $s0 666
move $a0 $s0
jal _print
Label5: nop
Label3: nop
Label6: lw $v1, 276($sp)
move $s0 $v1
lw $s2 0($s0)
lw $s2 20($s2)
move $s2 $s2
sw $s1, 308($sp)
move $a0 $s0
jalr $s2
lw $s1, 308($sp)
move $s0 $v0
move $s0 $s0
move $s0 $s0
li $s2 50
slt $s0, $s0, $s2
beqz $s0 Label7
lw $v1, 272($sp)
move $s0 $v1
lw $s2 0($s0)
lw $s2 0($s2)
move $s2 $s2
sw $s1, 304($sp)
move $a0 $s0
jalr $s2
lw $s1, 304($sp)
move $s0 $v0
move $s0 $s0
move $s0 $s0
move $v1 $s0
lw $v1, 276($sp)
move $s0 $v1
lw $s2 0($s0)
lw $s2 20($s2)
move $s2 $s2
sw $s1, 308($sp)
move $a0 $s0
jalr $s2
lw $s1, 308($sp)
move $s0 $v0
move $s0 $s0
move $s0 $s0
move $a0 $s0
jal _print
lw $v1, 268($sp)
lw $v0, 260($sp)
move $v1 $v0
sw $v1, 268($sp)
lw $v1, 268($sp)
lw $s0 0($v1)
lw $s0 24($s0)
move $s0 $s0
li $t5 0
li $s5 1
li $s6 2
lw $v1, 288($sp)
li $v1 3
sw $v1, 288($sp)
lw $v1, 284($sp)
li $v1 4
sw $v1, 284($sp)
li $s7 5
lw $v1, 264($sp)
li $v1 6
sw $v1, 264($sp)
li $t4 7
li $s2 8
li $s4 9
li $t1 10
li $t3 11
li $t2 12
li $s3 13
li $t6 14
li $t7 15
li $t0 16
lw $v1, 280($sp)
li $v1 17
sw $v1, 280($sp)
lw $v1, 104($sp)
li $v1 28
sw $v1, 104($sp)
lw $v1, 112($sp)
lw $v0, 104($sp)
move $a0 $v0
jal _halloc
move $v1 $v0
sw $v1, 112($sp)
lw $v1, 112($sp)
move $t8 $v1
lw $v1, 128($sp)
li $v1 18
sw $v1, 128($sp)
lw $v1, 128($sp)
sw $v1, 0($t8)
lw $v1, 248($sp)
li $v1 19
sw $v1, 248($sp)
lw $v1, 248($sp)
sw $v1, 4($t8)
lw $v1, 148($sp)
li $v1 20
sw $v1, 148($sp)
lw $v1, 148($sp)
sw $v1, 8($t8)
li $t9 21
sw $t9, 12($t8)
lw $v1, 256($sp)
li $v1 22
sw $v1, 256($sp)
lw $v1, 256($sp)
sw $v1, 16($t8)
lw $v1, 152($sp)
li $v1 23
sw $v1, 152($sp)
lw $v1, 152($sp)
sw $v1, 20($t8)
lw $v1, 252($sp)
li $v1 24
sw $v1, 252($sp)
lw $v1, 252($sp)
sw $v1, 24($t8)
lw $v1, 124($sp)
move $v1 $t8
sw $v1, 124($sp)
sw $s1, 308($sp)
lw $v1, 268($sp)
move $a0 $v1
move $a1 $t5
move $a2 $s5
move $a3 $s6
lw $v1, 288($sp)
sw $v1, 0($sp)
lw $v1, 284($sp)
sw $v1, 4($sp)
sw $s7, 8($sp)
lw $v1, 264($sp)
sw $v1, 12($sp)
sw $t4, 16($sp)
sw $s2, 20($sp)
sw $s4, 24($sp)
sw $t1, 28($sp)
sw $t3, 32($sp)
sw $t2, 36($sp)
sw $s3, 40($sp)
sw $t6, 44($sp)
sw $t7, 48($sp)
sw $t0, 52($sp)
lw $v1, 280($sp)
sw $v1, 56($sp)
lw $v1, 124($sp)
sw $v1, 60($sp)
jalr $s0
lw $s1, 308($sp)
move $s0 $v0
move $s0 $s0
move $s0 $s0
move $a0 $s0
jal _print
li $s0 28
move $a0 $s0
jal _halloc
move $s0 $v0
move $s2 $s0
li $s0 16
move $a0 $s0
jal _halloc
move $s0 $v0
move $s0 $s0
sw $s2, 0($s0)
li $s3 0
sw $s3, 4($s0)
li $s3 0
sw $s3, 8($s0)
li $s3 0
sw $s3, 12($s0)
la $s3 B_add
sw $s3, 0($s2)
la $s3 A_loc
sw $s3, 4($s2)
la $s3 A_print
sw $s3, 8($s2)
la $s3 A_set_true
sw $s3, 12($s2)
la $s3 B_set_false
sw $s3, 16($s2)
la $s3 B_show
sw $s3, 20($s2)
la $s3 D_func
sw $s3, 24($s2)
move $s0 $s0
move $s0 $s0
lw $v1, 180($sp)
move $v1 $s0
sw $v1, 180($sp)
lw $v1, 180($sp)
lw $s0 0($v1)
lw $s0 24($s0)
lw $v1, 164($sp)
move $v1 $s0
sw $v1, 164($sp)
lw $v1, 192($sp)
li $v1 0
sw $v1, 192($sp)
lw $v1, 228($sp)
li $v1 1
sw $v1, 228($sp)
lw $v1, 220($sp)
li $v1 2
sw $v1, 220($sp)
lw $v1, 216($sp)
li $v1 3
sw $v1, 216($sp)
lw $v1, 224($sp)
li $v1 4
sw $v1, 224($sp)
lw $v1, 200($sp)
li $v1 5
sw $v1, 200($sp)
lw $v1, 232($sp)
li $v1 6
sw $v1, 232($sp)
lw $v1, 196($sp)
li $v1 7
sw $v1, 196($sp)
lw $v1, 212($sp)
li $v1 8
sw $v1, 212($sp)
lw $v1, 172($sp)
li $v1 9
sw $v1, 172($sp)
lw $v1, 240($sp)
li $v1 10
sw $v1, 240($sp)
lw $v1, 188($sp)
li $v1 11
sw $v1, 188($sp)
lw $v1, 184($sp)
li $v1 12
sw $v1, 184($sp)
lw $v1, 88($sp)
li $v1 4
sw $v1, 88($sp)
lw $v1, 84($sp)
li $v1 13
sw $v1, 84($sp)
lw $v1, 72($sp)
lw $v0, 88($sp)
lw $v0, 84($sp)
mul $v1, $v0, $v0
sw $v1, 72($sp)
lw $v1, 64($sp)
lw $v0, 72($sp)
add $v1, $s1, $v0
sw $v1, 64($sp)
lw $v1, 68($sp)
lw $v0, 64($sp)
lw $v1 4($v0)
sw $v1, 68($sp)
lw $v1, 208($sp)
lw $v0, 68($sp)
move $v1 $v0
sw $v1, 208($sp)
lw $v1, 96($sp)
li $v1 4
sw $v1, 96($sp)
lw $v1, 100($sp)
li $v1 14
sw $v1, 100($sp)
lw $v1, 80($sp)
lw $v0, 96($sp)
lw $v0, 100($sp)
mul $v1, $v0, $v0
sw $v1, 80($sp)
lw $v1, 76($sp)
lw $v0, 80($sp)
add $v1, $s1, $v0
sw $v1, 76($sp)
lw $v1, 92($sp)
lw $v0, 76($sp)
lw $v1 4($v0)
sw $v1, 92($sp)
lw $v1, 204($sp)
lw $v0, 92($sp)
move $v1 $v0
sw $v1, 204($sp)
lw $v1, 176($sp)
li $v1 15
sw $v1, 176($sp)
lw $v1, 244($sp)
li $v1 16
sw $v1, 244($sp)
lw $v1, 236($sp)
li $v1 17
sw $v1, 236($sp)
lw $v1, 116($sp)
li $v1 28
sw $v1, 116($sp)
lw $v1, 108($sp)
lw $v0, 116($sp)
move $a0 $v0
jal _halloc
move $v1 $v0
sw $v1, 108($sp)
lw $v1, 168($sp)
lw $v0, 108($sp)
move $v1 $v0
sw $v1, 168($sp)
lw $v1, 144($sp)
li $v1 18
sw $v1, 144($sp)
lw $v1, 168($sp)
lw $v0, 144($sp)
sw $v0, 0($v1)
lw $v1, 140($sp)
li $v1 19
sw $v1, 140($sp)
lw $v1, 168($sp)
lw $v0, 140($sp)
sw $v0, 4($v1)
li $s0 20
lw $v1, 168($sp)
sw $s0, 8($v1)
lw $v1, 160($sp)
li $v1 21
sw $v1, 160($sp)
lw $v1, 168($sp)
lw $v0, 160($sp)
sw $v0, 12($v1)
lw $v1, 132($sp)
li $v1 22
sw $v1, 132($sp)
lw $v1, 168($sp)
lw $v0, 132($sp)
sw $v0, 16($v1)
lw $v1, 136($sp)
li $v1 23
sw $v1, 136($sp)
lw $v1, 168($sp)
lw $v0, 136($sp)
sw $v0, 20($v1)
lw $v1, 156($sp)
li $v1 24
sw $v1, 156($sp)
lw $v1, 168($sp)
lw $v0, 156($sp)
sw $v0, 24($v1)
lw $v1, 120($sp)
lw $v0, 168($sp)
move $v1 $v0
sw $v1, 120($sp)
sw $s1, 308($sp)
lw $v1, 180($sp)
move $a0 $v1
lw $v1, 192($sp)
move $a1 $v1
lw $v1, 228($sp)
move $a2 $v1
lw $v1, 220($sp)
move $a3 $v1
lw $v1, 216($sp)
sw $v1, 0($sp)
lw $v1, 224($sp)
sw $v1, 4($sp)
lw $v1, 200($sp)
sw $v1, 8($sp)
lw $v1, 232($sp)
sw $v1, 12($sp)
lw $v1, 196($sp)
sw $v1, 16($sp)
lw $v1, 212($sp)
sw $v1, 20($sp)
lw $v1, 172($sp)
sw $v1, 24($sp)
lw $v1, 240($sp)
sw $v1, 28($sp)
lw $v1, 188($sp)
sw $v1, 32($sp)
lw $v1, 184($sp)
sw $v1, 36($sp)
lw $v1, 208($sp)
sw $v1, 40($sp)
lw $v1, 204($sp)
sw $v1, 44($sp)
lw $v1, 176($sp)
sw $v1, 48($sp)
lw $v1, 244($sp)
sw $v1, 52($sp)
lw $v1, 236($sp)
sw $v1, 56($sp)
lw $v1, 120($sp)
sw $v1, 60($sp)
lw $v0, 164($sp)
jalr $v0
lw $s1, 308($sp)
move $s0 $v0
move $s0 $s0
move $s0 $s0
move $a0 $s0
jal _print
b Label6
Label7: nop
nop
nop
lw $ra, -4($fp)
addu $sp, $sp, 324
j $ra

         .text
         .globl E_e
E_e:
sw $fp, -8($sp)
move $fp, $sp
subu $sp, $sp, 16
sw $ra, -4($fp)
move $s0 $a1
li $s1 0
move $v1 $s1
move $s1 $s0
lw $s2 0($s1)
lw $s2 0($s2)
move $s2 $s2
sw $s0, 0($sp)
move $a0 $s1
jalr $s2
lw $s0, 0($sp)
move $s1 $v0
move $s1 $s1
move $s1 $s1
move $v1 $s1
move $s0 $s0
nop
move $v0 $s0
lw $ra, -4($fp)
lw $fp, 8($sp)
addu $sp, $sp, 16
j $ra

         .text
         .globl A_print
A_print:
sw $fp, -8($sp)
move $fp, $sp
subu $sp, $sp, 8
sw $ra, -4($fp)
move $s0 $a0
lw $s0 8($s0)
move $s0 $s0
move $s0 $s0
nop
move $v0 $s0
lw $ra, -4($fp)
lw $fp, 0($sp)
addu $sp, $sp, 8
j $ra

         .text
         .globl A_add
A_add:
sw $fp, -8($sp)
move $fp, $sp
subu $sp, $sp, 8
sw $ra, -4($fp)
move $s0 $a0
lw $s1 8($s0)
move $s2 $s1
li $s1 10
add $s1, $s2, $s1
move $s1 $s1
sw $s1, 8($s0)
lw $s1 8($s0)
move $s0 $s1
move $s0 $s0
nop
move $v0 $s0
lw $ra, -4($fp)
lw $fp, 0($sp)
addu $sp, $sp, 8
j $ra

         .text
         .globl A_set_true
A_set_true:
sw $fp, -8($sp)
move $fp, $sp
subu $sp, $sp, 8
sw $ra, -4($fp)
move $s0 $a0
li $s1 1
move $s1 $s1
sw $s1, 4($s0)
lw $s1 4($s0)
move $s0 $s1
move $s0 $s0
nop
move $v0 $s0
lw $ra, -4($fp)
lw $fp, 0($sp)
addu $sp, $sp, 8
j $ra

         .text
         .globl A_set_false
A_set_false:
sw $fp, -8($sp)
move $fp, $sp
subu $sp, $sp, 8
sw $ra, -4($fp)
move $s0 $a0
li $s1 0
move $s1 $s1
sw $s1, 4($s0)
lw $s1 4($s0)
move $s0 $s1
move $s0 $s0
nop
move $v0 $s0
lw $ra, -4($fp)
lw $fp, 0($sp)
addu $sp, $sp, 8
j $ra

         .text
         .globl A_loc
A_loc:
sw $fp, -8($sp)
move $fp, $sp
subu $sp, $sp, 8
sw $ra, -4($fp)
move $s0 $a0
lw $s0 4($s0)
move $s0 $s0
move $s0 $s0
nop
move $v0 $s0
lw $ra, -4($fp)
lw $fp, 0($sp)
addu $sp, $sp, 8
j $ra

         .text
         .globl B_add
B_add:
sw $fp, -8($sp)
move $fp, $sp
subu $sp, $sp, 8
sw $ra, -4($fp)
move $s0 $a0
lw $s1 12($s0)
move $s1 $s1
li $s2 10
add $s1, $s1, $s2
move $s1 $s1
sw $s1, 12($s0)
lw $s1 12($s0)
move $s0 $s1
move $s0 $s0
nop
move $v0 $s0
lw $ra, -4($fp)
lw $fp, 0($sp)
addu $sp, $sp, 8
j $ra

         .text
         .globl B_set_false
B_set_false:
sw $fp, -8($sp)
move $fp, $sp
subu $sp, $sp, 8
sw $ra, -4($fp)
move $s0 $a0
li $s1 0
move $s1 $s1
sw $s1, 4($s0)
lw $s1 4($s0)
move $s0 $s1
move $s0 $s0
nop
move $v0 $s0
lw $ra, -4($fp)
lw $fp, 0($sp)
addu $sp, $sp, 8
j $ra

         .text
         .globl B_show
B_show:
sw $fp, -8($sp)
move $fp, $sp
subu $sp, $sp, 8
sw $ra, -4($fp)
move $s0 $a0
lw $s0 12($s0)
move $s0 $s0
move $s0 $s0
nop
move $v0 $s0
lw $ra, -4($fp)
lw $fp, 0($sp)
addu $sp, $sp, 8
j $ra

         .text
         .globl C_func
C_func:
sw $fp, -8($sp)
move $fp, $sp
subu $sp, $sp, 8
sw $ra, -4($fp)
lw $s0, 40($fp)
lw $s1, 60($fp)
lw $v1 0($s1)
lw $v1 4($s1)
lw $v1 8($s1)
lw $v1 12($s1)
lw $v1 16($s1)
lw $v1 20($s1)
lw $s1 24($s1)
add $s0, $s0, $s1
move $s0 $s0
nop
move $v0 $s0
lw $ra, -4($fp)
lw $fp, 0($sp)
addu $sp, $sp, 8
j $ra

         .text
         .globl D_func
D_func:
sw $fp, -8($sp)
move $fp, $sp
subu $sp, $sp, 8
sw $ra, -4($fp)
move $s2 $a3
lw $s0, 40($fp)
lw $s1, 60($fp)
lw $v1 0($s1)
lw $v1 4($s1)
lw $v1 8($s1)
lw $v1 12($s1)
lw $v1 16($s1)
lw $v1 20($s1)
lw $v1 24($s1)
mul $s0, $s2, $s0
move $s0 $s0
nop
move $v0 $s0
lw $ra, -4($fp)
lw $fp, 0($sp)
addu $sp, $sp, 8
j $ra

         .text
         .globl _halloc
_halloc:
         li $v0, 9
         syscall
         j $ra

         .text
         .globl _print
_print:
         li $v0, 1
         syscall
         la $a0, newl
         li $v0, 4
         syscall
         j $ra

         .data
         .align   0
newl:    .asciiz "\n" 
         .data
         .align   0
str_er:  .asciiz " ERROR: abnormal termination\n" 
         .data
         .align   0
str_kger:  .asciiz " ERROR: syntax error in kanga code\n" 
