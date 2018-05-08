package DataStructureAndAlgorithmTest.Other;

import DataStructuresAndAlgorithms.Other.MissingNumberFromArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.stream.IntStream;

public class MissingNumberFromArrayTest {
    private MissingNumberFromArray obj;

    @BeforeEach
    void setUp() {
        obj = MissingNumberFromArray.getInstance();
    }

    @Test
    void findMissingNumberTest() {
        int[] arr = IntStream.rangeClosed(10, 20).filter(i -> i != 13).toArray();

        System.out.println(arr.length);
        Arrays.stream(arr).forEach(i -> System.out.print(" " + i));

        int missingNum = obj.fetchMissingNumber(arr, arr[0], arr[9]);
        assertEquals(13, missingNum);
    }

    @Test
    void findMissingNumberThroughReflectionTest() {

    }

    @Test
    void tryCatchFinallyTest(){
        try{
            System.out.println("Executing try...last statement is System.exit()");
            System.exit(0);
            throw new Exception("exception to be caught, if reached");
        }catch (Exception e){
            System.out.println("Caught Exception.");
            e.printStackTrace();
        }finally {
            System.out.println("Finally...");
        }
    }
}
