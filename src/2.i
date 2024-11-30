var counter: real is 0.9;
while counter < 5.0 loop
  counter := counter + 1.0;
end;

var temperature: real is 37.1;
var status: string;
while temperature < 37.4 loop
  status := "Fever";
  temperature := temperature + 0.1;
  print(temperature);
end;
print(temperature)