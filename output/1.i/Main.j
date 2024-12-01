.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 10
.limit locals 10
new TreeNode
dup
invokespecial TreeNode/<init>()V
astore 2
new TreeNode
dup
invokespecial TreeNode/<init>()V
astore 3
new TreeNode
dup
invokespecial TreeNode/<init>()V
astore 4
new FreeNode
dup
invokespecial FreeNode/<init>()V
astore 5
aload 3
ldc 5
invokestatic Main/insert(LTreeNode;I)LFreeNode;
astore 5
aload 5
getfield FreeNode/free LTreeNode;
astore 4
aload 4
ldc 9
putfield TreeNode/value I
getstatic java/lang/System/out Ljava/io/PrintStream;
aload 4
getfield TreeNode/value I
invokevirtual java/io/PrintStream/println(I)V
getstatic java/lang/System/out Ljava/io/PrintStream;
aload 3
getfield TreeNode/value I
invokevirtual java/io/PrintStream/println(I)V
return
.end method
.method public static insert(LTreeNode;I)LFreeNode;
.limit stack 10
.limit locals 12
aload 0
iload 1
putfield TreeNode/value I
new FreeNode
dup
invokespecial FreeNode/<init>()V
astore 2
aload 2
aload 0
putfield FreeNode/free LTreeNode;
getstatic java/lang/System/out Ljava/io/PrintStream;
aload 0
getfield TreeNode/value I
invokevirtual java/io/PrintStream/println(I)V
aload 2
areturn
return
.end method

