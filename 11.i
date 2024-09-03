type TreeNode is record
    var value : integer;
    var left : TreeNode;
    var right : TreeNode;
end;

var root : TreeNode;

routine insert(node : TreeNode, val : integer) is
    if node = null then
        node.value := val;
    elsif val < node.value then
        insert(node.left, val);
    else
        insert(node.right, val);
    end;
end;

routine search(node : TreeNode, val : integer) : boolean is
    if node = null then
        return false;
    elsif node.value = val then
        return true;
    elsif val < node.value then
        return search(node.left, val);
    else
        return search(node.right, val);
    end;
end;

insert(root, 10);
insert(root, 5);
insert(root, 20);
var found : boolean;
found := search(root, 5);
