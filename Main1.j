.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 10
.limit locals 10
ldc2_w 36.5
dstore 0
dload 0
ldc2_w 37.0
dcmpg
iflt L3
ldc "Fever"
astore 4
goto L2
L3:
ldc "Normal"
astore 5
getstatic java/lang/System/out Ljava/io/PrintStream;
aload 5
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
L2:
getstatic java/lang/System/out Ljava/io/PrintStream;
dload 0
invokevirtual java/io/PrintStream/println(D)V
return
.end method
