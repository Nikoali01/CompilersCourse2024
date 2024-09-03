type IntArray is array [10] integer;

routine mergeSort(arr : IntArray, start : integer, end : integer) is
    if start >= end then
        return;
    end;
    var mid : integer is (start + end) / 2;
    mergeSort(arr, start, mid);
    mergeSort(arr, mid + 1, end);
    merge(arr, start, mid, end);
end;

routine merge(arr : IntArray, start : integer, mid : integer, end : integer) is
    var left : IntArray;
    var right : IntArray;
    var i : integer is start;
    var j : integer is mid + 1;
    var k : integer is start;

    -- Copy data to left and right arrays
    for l in start..mid loop
        left[l] := arr[l];
    end;
    for r in mid+1..end loop
        right[r] := arr[r];
    end;

    -- Merge the left and right arrays
    while i <= mid and j <= end loop
        if left[i] <= right[j] then
            arr[k] := left[i];
            i := i + 1;
        else
            arr[k] := right[j];
            j := j + 1;
        end;
        k := k + 1;
    end;

    -- Copy remaining elements of left
    while i <= mid loop
        arr[k] := left[i];
        i := i + 1;
        k := k + 1;
    end;

    -- Copy remaining elements of right
    while j <= end loop
        arr[k] := right[j];
        j := j + 1;
        k := k + 1;
    end;
end;

var arr : IntArray;
arr[1] := 38;
arr[2] := 27;
arr[3] := 43;
arr[4] := 3;
arr[5] := 9;
arr[6] := 82;
arr[7] := 10;

mergeSort(arr, 1, 7);
