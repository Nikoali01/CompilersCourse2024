.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 10
.limit locals 10
ldc 5
newarray int
astore 0
aload 0
ldc 1
ldc 1
iastore
getstatic java/lang/System/out Ljava/io/PrintStream;
aload 0
ldc 1
iaload
invokevirtual java/io/PrintStream/println(I)V
return
.end method
