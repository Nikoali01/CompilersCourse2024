.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 10
.limit locals 10
ldc2_w 36.5
dstore_0
dload_0
ldc2_w 37.0
icmpgt LABEL_TRUE
ifgt LABEL_TRUE
LABEL_TRUE: 
ifeq L3
ldc ""Fever""
astore_4
goto L2
L3:
ldc ""Normal""
astore_5
L2:
return
.end method
