routine isPrime(n : integer) : boolean is
    if n <= 1 then
        return false;
    end;
    for i in 2..n-1 loop
        if n % i = 0 then
            return false;
        end;
    end;
    return true;
end;

var primes : array [10] integer;
var count : integer is 0;
var num : integer is 2;

while count < 10 loop
    if isPrime(num) then
        primes[count + 1] := num;
        count := count + 1;
    end;
    num := num + 1;
end;
