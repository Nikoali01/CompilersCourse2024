type TreeNode is record
    var value : integer;
    var s : string;
    var left : TreeNode;
    var right : TreeNode;
end;

type FreeNode is record
    var free : TreeNode;
end;

routine insert(node : TreeNode, val : integer) : FreeNode is
    node.value := val;
    var freeTree : FreeNode;
    freeTree.free := node;
    print(node.value);
    return freeTree;
end;

var a : TreeNode;
var a2 : TreeNode;
var b : FreeNode;
b := insert(a, 5);
a2 := b.free;
a2.value := 9;
print(a2.value);
print(a.value);