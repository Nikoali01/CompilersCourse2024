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
ifgt L3
ldc "Fever"
astore 4
goto L2
L3:
ldc "Normal"
astore 5
L2:
return
.end method
