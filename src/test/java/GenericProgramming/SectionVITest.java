package GenericProgramming;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class SectionVITest {
    private SectionVI obj;

    @BeforeEach
    void setUp() {
        obj = new SectionVI();
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
}
