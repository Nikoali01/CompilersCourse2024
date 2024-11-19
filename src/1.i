var temperature: real is 36.5;
if temperature > 37.0 then
  var status: string is "Fever";
else
  var status: string is "Normal";
  print(status);
end;
print(temperature);