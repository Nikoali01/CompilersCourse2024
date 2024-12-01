.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 10
.limit locals 10
new TreeNode
dup
invokespecial TreeNode/<init>()V
astore 0
new TreeNode
dup
invokespecial TreeNode/<init>()V
astore 0
ldc 0
istore 1
ldc 1
istore 1
aload 0
iload 1
invokestatic Main/insert(LTreeNode;I)V
return
.end method
.method public static insert(LTreeNode;I)V
.limit stack 10
.limit locals 12
aload 0
iload 1
putfield TreeNode/value I
getstatic java/lang/System/out Ljava/io/PrintStream;
aload 0
getfield TreeNode/value I
invokevirtual java/io/PrintStream/println(I)V
return
.end method

