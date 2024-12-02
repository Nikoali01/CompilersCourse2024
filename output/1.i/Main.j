.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 10
.limit locals 10
ldc 2
ldc 4
invokestatic Main/add(II)I
istore 3
getstatic java/lang/System/out Ljava/io/PrintStream;
iload 3
invokevirtual java/io/PrintStream/println(I)V
return
.end method
.method public static add(II)I
.limit stack 10
.limit locals 12
ldc 5
newarray int
astore 2
aload 2
ldc 0
ldc 5
iastore
aload 2
ldc 0
iaload
ireturn
return
.end method

