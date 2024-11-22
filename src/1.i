type TreeNode is record
    var value : integer;
    var left : TreeNode;
    var right : TreeNode;
end;

type FreeNode is record
    var free : TreeNode;
end;

routine insert(node : TreeNode, val : integer) : TreeNode is
    if node = null then
        node.value := val;
    elsif val < node.value then
        insert(node.left, val);
    end;
end;

// рекорды, функция которая принимает рекорд и возвращает другой рекорд
// рекорд а является частью рекорда б функция вернет родительский рекорд