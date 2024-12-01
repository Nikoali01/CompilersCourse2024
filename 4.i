var temperature: real is 36.5;
if temperature > 37.0 then
  var status: string is "Fever";
  print(status);
else
  var status: string is "Normal";
  print(status);
end;
