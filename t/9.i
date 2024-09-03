type Account is record
    var accountNumber : integer;
    var balance : real;
end;

var acc : Account;
acc.accountNumber := 123456;
acc.balance := 1000.0;

routine deposit(acc : Account, amount : real) is
    acc.balance := acc.balance + amount;
end;

routine withdraw(acc : Account, amount : real) is
    if acc.balance >= amount then
        acc.balance := acc.balance - amount;
    else
        var penalty : real is 50.0;
        acc.balance := acc.balance - penalty;
    end;
end;

deposit(acc, 500.0);
withdraw(acc, 2000.0);
