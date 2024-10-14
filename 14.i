var marks: array[5] integer;
marks[1] := 80;
marks[2] := 95;
marks[3] := 70;
marks[4] := 85;
marks[5] := 90;

var total: integer is 0;
for i in 1..5 loop
  total := total + marks[i];
end;


var maxMark: integer is marks[1];
for i in 2..5 loop
  if marks[i] > maxMark then
    maxMark := marks[i];
  end;
end;

print(maxMark);

var minMark: integer is marks[1];
for i in 2..5 loop
  if marks[i] < minMark then
    minMark := marks[i];
  end;
end;
print(minMark);


for i in 1..5 loop
  marks[i] := 0;
end;

//error
var invalidAccess: integer is marks[6];
