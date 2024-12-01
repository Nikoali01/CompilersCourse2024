type TreeNode is record
    var value : integer;
    var left : TreeNode;
    var right : TreeNode;
end;

type FreeNode is record
    var free : TreeNode;
end;

routine insert(node : TreeNode, val : integer) is
    node.value := val;
    print(node.value);
end;

var node: TreeNode;
var val: integer;
val := 1;
insert(node, val);