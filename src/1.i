routine add(a: integer, b: integer) : integer is
  var marks: array[5] integer;
  marks[0] := 5;
  return marks[0];
end;

var result: integer is add(2, 4);
print(result);