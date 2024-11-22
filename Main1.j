.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 10
.limit locals 10
.class public TreeNode
.super java/lang/Object

.field public value I
.field public left LTreeNode;
.field public right LTreeNode;
.method public <init>()V
aload_0
invokenonvirtual java/lang/Object/<init>()V
return
.end method

.class public FreeNode
.super java/lang/Object

.field public free LTreeNode;
.method public <init>()V
aload_0
invokenonvirtual java/lang/Object/<init>()V
return
.end method

.method public static insert(LTreeNode;I)LTreeNode;
.limit stack 10
.limit locals 12
aload 0
dcmpg
ifeq L1
iload 1
goto L0
L1:
L0:
return
.end method

return
.end method
