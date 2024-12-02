.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 10
.limit locals 10
new Complex
dup
invokespecial Complex/<init>()V
astore 2
new Complex
dup
invokespecial Complex/<init>()V
astore 2
new Complex
dup
invokespecial Complex/<init>()V
astore 3
new Complex
dup
invokespecial Complex/<init>()V
astore 4
new Complex
dup
invokespecial Complex/<init>()V
astore 5
aload 3
ldc2_w 3.0
putfield Complex/real D
aload 3
ldc2_w 2.0
putfield Complex/imag D
aload 4
ldc2_w 1.0
putfield Complex/real D
aload 4
ldc2_w 4.0
putfield Complex/imag D
aload 3
aload 4
invokestatic Main/addComplex(LComplex;LComplex;)LComplex;
astore 5
return
.end method
.method public static addComplex(LComplex;LComplex;)LComplex;
.limit stack 10
.limit locals 12
new Complex
dup
invokespecial Complex/<init>()V
astore 2
aload 2
aload 0
getfield Complex/real D
aload 1
getfield Complex/real D
dadd
putfield Complex/real D
aload 2
aload 0
getfield Complex/imag D
aload 1
getfield Complex/imag D
dadd
putfield Complex/imag D
aload 2
areturn
return
.end method

