var n : real is 5.0;
var factorial : real is 1.0;
while n > 0.0 loop
    factorial := factorial * n;
    n := n - 1.0;
    print(n);
end;
