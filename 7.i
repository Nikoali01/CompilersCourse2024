type Complex is record
    var real : real;
    var imag : real;
end;

routine addComplex(c1 : Complex, c2 : Complex) : Complex is
    var result : Complex;
    result.real := c1.real + c2.real;
    result.imag := c1.imag + c2.imag;
    return result;
end;

var c1 : Complex;
var c2 : Complex;
var sum : Complex;

c1.real := 3.0;
c1.imag := 2.0;
c2.real := 1.0;
c2.imag := 4.0;

sum := addComplex(c1, c2);
