type Point3D is record
    var x : real;
    var y : real;
    var z : real;
end;

type Cube is record
    var vertices : array [8] Point3D;
end;

var cube : Cube;

routine translateCube(cube : Cube, dx : real, dy : real, dz : real) is
    for i in 1..8 loop
        cube.vertices[i].x := cube.vertices[i].x + dx;
        cube.vertices[i].y := cube.vertices[i].y + dy;
        cube.vertices[i].z := cube.vertices[i].z + dz;
    end;
end;

cube.vertices[1].x := 0.0; cube.vertices[1].y := 0.0; cube.vertices[1].z := 0.0;
cube.vertices[2].x := 1.0; cube.vertices[2].y := 0.0; cube.vertices[2].z := 0.0;
translateCube(cube, 1.0, 1.0, 1.0);
