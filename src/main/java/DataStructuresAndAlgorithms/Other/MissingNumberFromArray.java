package DataStructuresAndAlgorithms.Other;

import java.util.Arrays;
import java.util.stream.IntStream;

public class MissingNumberFromArray {

    private MissingNumberFromArray(){

    }

    public static MissingNumberFromArray getInstance(){
        return new MissingNumberFromArray();
    }

    public int fetchMissingNumber(int[] arr, int start, int end){
        long arrayTotal   = Arrays.stream(arr).sum();
        long naturalTotal = IntStream.rangeClosed(start, end).sum();
        Long diff = naturalTotal - arrayTotal;
        return diff.intValue();
    }
}
