routine main(n: integer) : integer is
    var myVar : integer;
    myVar := 10;
    print(efw);
    return myVar;
end;

routine mainError() : integer is
    main(a);
    return -1;
end;