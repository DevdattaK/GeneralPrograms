package GenericProgramming;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SectionVITest {
    private SectionVI obj;
    private HeapSort<Integer> heapSort;

    @BeforeEach
    void setUp() {
        obj = new SectionVI();
        heapSort = new HeapSort<>();
    }

    @Test
    void arrayCopyRangeTest() {
        int arr[] = {4, 5, 6, 7, 1, 2, 3};
        int arr2[] = Arrays.copyOfRange(arr, 3, 5);

        Arrays.stream(arr2).forEach(i -> System.out.println(" " + i));
    }

    @Test
    void minimumNumberTest() {
        //int arr[] = {4, 5, 6, 7, 8, 1, 2, 3};
        //int arr[] = {0, 1};
        int arr[] = {0};

        int min = obj.getMinimumFromSortedAndRotatedArray(arr);

        System.out.println("Minimum : " + min);
    }

    @Test
    void ransomeNoteTest() {
        String note = "this is a ransome note . you should take this note seriously . ransome amount is $5. thank you!";

        boolean isGoodMagazine = obj.isThisMagazineGoodForRansomeNote("C:\\Users\\Kanawade_D\\IdeaProjects\\practicePrograms\\" +
                "src\\main\\java\\GenericProgramming\\MagazineFile.txt", note);

        System.out.println("Is this magazine usable for ransome note : " + isGoodMagazine);
    }

    @Test
    void substringTest() {
        String testStr = "abc";
        System.out.println(testStr.substring(3, 3).length());
    }

    @Test
    void permutationGenerationTest() {
        String seed = "abcdef";

        obj.printPermutation(seed);
    }

    @Test
    void combinationTest() {
        obj.printCombinations("abc");
    }

    @Test
    void LZWCompression() {
        String str = "aaabbccccdd";         //output : a3b2c4d2
        System.out.println(obj.compressStringUsingLZWApproach(str));
    }

    @Test
    void IfGivenNumber_ThenReturnNumberOfPositionsToStoreIt() {
        int num = 123;

        assertEquals(3, obj.getNumberOfSpacesRequiredToStore(num));
        num = 1;
        assertEquals(1, obj.getNumberOfSpacesRequiredToStore(num));
        num = 0;
        assertEquals(0, obj.getNumberOfSpacesRequiredToStore(num));
    }

    @Test
    void getNumberAsArrTest() {
        char[] arr = obj.getNumberAsArray(234);
        System.out.println(arr);
    }

    @Test
    void getTreeHeight() {
        Integer[] arr = {1, 2, 3, 4, 5};

        assertEquals(heapSort.getHeight(arr), 2);
    }

    @Test
    void offsetPrintingTest() {
        int offset = 4;
        //heapSort.printOffsetSpacesWithMarkers(offset);
    }

    @Test
    void CollectorsJoiningTest() {
        String str = IntStream.rangeClosed(1, 3).mapToObj(i -> ""+i).collect(Collectors.joining("."));
        System.out.println(str);
    }

    @Test
    void printHeapTest() {
        Integer[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};

        heapSort.displayHeap(arr);
    }

    @Test
    void buildHeapTest() {
        Integer[] arr = {4, 1, 3, 2, 16, 9, 10, 14, 8, 7};
        Comparator<Integer> regular = Comparator.comparing(Integer::intValue);

        heapSort.heapSort(arr, regular);
        System.out.println("Max HeapSort output : \n");
        Arrays.stream(arr).forEach(i -> System.out.print(" " + i));


        heapSort.heapSort(arr, regular.reversed());
        System.out.println("Min Heapsort output : \n");
        Arrays.stream(arr).forEach(i -> System.out.print(" " + i));
    }

    @Test
    void checkArraySize() {
        int[] arr = new int[10];
        double val = (44 + 45) / 2.0;
        boolean b = false;
        boolean[] booleans = {true, false};


    }

    @Test
    void IfGivenEverIncreaingArray_PrintMedianContinuously() {
        int[] generatedNums = null;
        try {
            generatedNums = obj.printMedianOfEverIncreasinArray();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("===========Tradition result : ===============" );
        traditionalMedianTest(generatedNums);
    }

    @Test
    void traditionalMedianTest(int[] arr) {
       // int[] arr = {59, 58, 57, 54, 45, 20, 37, 21, 23, 34, 59, 62, 73, 68, 62, 88, 83, 85, 74, 74};
        double median = -1.0;
        arr = Arrays.stream(arr).sorted().toArray();

        if(arr.length > 1) {
            if (arr.length % 2 == 0) {
                median = (arr[arr.length / 2 - 1] + arr[arr.length / 2]) / 2.0;
            } else {
                median = arr[arr.length / 2 + 1];
            }
        }

        System.out.println("Median : " + median);
    }



}
