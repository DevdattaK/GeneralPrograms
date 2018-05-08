package DataStructureAndAlgorithmTest;

import DataStructuresAndAlgorithms.Generic.CircularArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CircularArrayTest {
    private CircularArray<Integer> integerCircularArray;
    private final int SIZE = 10;

    @BeforeEach
    void setUp() {
        Supplier<Integer[]> integerSupplier = () -> {
            return IntStream.rangeClosed(0, 4)
                                        /*.map(i -> ThreadLocalRandom.current().nextInt(10, 100))
                                        .mapToObj(m -> Integer.valueOf(m))
                                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);*/
                                        .mapToObj(i -> ThreadLocalRandom.current().nextInt(10, 100))
                                        //.collect(Collectors.toList());
                                        .toArray(Integer[]::new);

        };
       integerCircularArray = new CircularArray<>(integerSupplier);
    }

    @Test
    void headerPositionTest() {
        int position = integerCircularArray.getHeadPosition();
        assertEquals(0, position);
    }

    @Test
    void moveHeaderToRightTest() {
        integerCircularArray.rotateRightByPositions(3);
        assertEquals(3, integerCircularArray.getHeadPosition());
    }

    @Test
    void displayTest() {
        integerCircularArray.displayArray();
    }

    @Test
    void displayArrayPostRightRotation_Test() {
        integerCircularArray.displayArray();
        System.out.println("Rotating Array Right by 1 positions");
        integerCircularArray.rotateRightByPositions(1);
        System.out.println("Post Rotation state of the circularArray..");
        integerCircularArray.displayArray();
    }

    @Test
    void displayArrayPostLeftRotation_Test() {
        integerCircularArray.displayArray();
        System.out.println("Rotating Array Left by 2 positions");
        integerCircularArray.rotateLeftByPositions(2);
        System.out.println("Post Rotation state of the circularArray..");
        integerCircularArray.displayArray();
    }

    @Test
    void foreachConstructOnCircularArray_Test() {
        System.out.println("Displaying through internal method first");
        integerCircularArray.displayArray();
        System.out.println("Using For-Each construct..");
        for (Integer i :
                integerCircularArray) {
            System.out.print(i + "  ");
        }
    }


    @Test
    void displayArrayNegativeRotation_Test() {
        integerCircularArray.displayArray();
        System.out.println("Rotating Array Right by 1 positions");
        integerCircularArray.universalRotation(-1);
        System.out.println("Post Rotation state of the circularArray..");
        integerCircularArray.displayArray();
    }

    @Test
    void displayArrayPositiveRotation_Test() {
        integerCircularArray.displayArray();
        System.out.println("Rotating Array Left by 2 positions");
        integerCircularArray.universalRotation(2);
        System.out.println("Post Rotation state of the circularArray..");
        integerCircularArray.displayArray();
    }

    @Test
    void displayArrayPositiveRotationIntermediateHead_Test() {
        integerCircularArray.displayArray();
        System.out.println("Rotating Array to Left by 5 positions, after setting head to position 1");
        integerCircularArray.universalRotation(2);
        System.out.println("Head Position : " + integerCircularArray.getHeadPosition());
        integerCircularArray.universalRotation(5);
        System.out.println("Post Rotation state of the circularArray..");
        integerCircularArray.displayArray();
    }

    @Test
    void displayArrayNegativeRotationIntermediateHead_Test() {
        integerCircularArray.displayArray();
        System.out.println("Rotating Array to Right by 5 positions, after setting head to position 1");
        integerCircularArray.universalRotation(1);
        integerCircularArray.displayArray();
        System.out.println("Head Position : " + integerCircularArray.getHeadPosition());
        integerCircularArray.universalRotation(-3);
        System.out.println("Post Rotation state of the circularArray..");
        integerCircularArray.displayArray();
    }
}
