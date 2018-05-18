package YoungTableauTest;

import YoungTableau.YoungTableau;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Supplier;
import java.util.stream.IntStream;

public class YoungTableTester {
    private YoungTableau obj;

    @BeforeEach
    void setUp() {
        Supplier<Integer[][]> tableGenerator = () -> {
            Integer[][] intArray = {{5, 13, 19, 14, 21}, {14, 7, 12, 11, 9}, {5, 10, 8, 20, 16}, {}};
            return intArray;
        };

        obj = new YoungTableau(tableGenerator);

        obj.buildTable(tableGenerator);
    }

    @Test
    void displayTable() {
        obj.displayTable();
    }

    @Test
    void leftOfSelfTest() {
        int i = 1, j = 1;
        int leftKey = obj.getLeftOfSelf(i, j);

        assertEquals(leftKey, 14);

        assertEquals(obj.getLeftOfSelf(0, 0), -1);
        assertEquals(obj.getLeftOfSelf(1, 0), -1);
        assertEquals(obj.getLeftOfSelf(3, 4), 2147483647);
    }

    @Test
    void rightOfSelfTest() {
        int i = 1, j = 1;
        int leftKey = obj.getRightOfSelf(i, j);

        assertEquals(leftKey, 12);

        assertEquals(obj.getRightOfSelf(0, 4), -1);
        assertEquals(obj.getRightOfSelf(1, 0), 7);
        assertEquals(obj.getRightOfSelf(3, 4), -1);
        assertEquals(obj.getRightOfSelf(2, 3), 16);
    }

    @Test
    void topOfSelfTest() {
        int i = 1, j = 1;
        int leftKey = obj.getTopOfSelf(i, j);

        assertEquals(leftKey, 13);

        assertEquals(obj.getTopOfSelf(0, 4), -1);
        assertEquals(obj.getTopOfSelf(1, 0), 5);
        assertEquals(obj.getTopOfSelf(3, 4), 16);
        assertEquals(obj.getTopOfSelf(0, 0), -1);
    }

    @Test
    void bottomOfSelfTest() {
        int i = 1, j = 1;
        int leftKey = obj.getBottomOfSelf(i, j);

        assertEquals(leftKey, 10);

        assertEquals(obj.getBottomOfSelf(0, 0), 14);
        assertEquals(obj.getBottomOfSelf(1, 0), 5);
        assertEquals(obj.getBottomOfSelf(3, 4), -1);
        assertEquals(obj.getBottomOfSelf(3, 3), -1);
    }

    @Test
    void yongifyTest() {
        /*obj.yongifyTable(1, 1);
        obj.displayTable();
*/
        obj.yongifyTable(3, 4);
        obj.displayTable();
    }

    @Test
    void buildYoungifiedTableTest() {
        System.out.println("Before youngifying");
        obj.displayTable();
        System.out.println("After yongifying");
        obj.buildYoungifiedTable();
        obj.displayTable();
    }

    @Test
    void searchElementTest() {
        int key = 13;
        int[] location = obj.searchElement(key);
        assertNull(location, "Should be null, as table is not yongified");

        obj.buildYoungifiedTable();
        location = obj.searchElement(key);
        assertEquals(2, location[0]);
        assertEquals(2, location[1]);

        key = 7;
        location = obj.searchElement(key);
        assertEquals(2, location[0]);
        assertEquals(0, location[1]);
    }


    @Test
    void searchElementWithinDimensionTest() {
        int key = 16;
        obj.buildYoungifiedTable();
        int[] location = obj.searchElementWithinDimensions(1, 2, 2, 4, key);

        assertEquals(2, location[0]);
        assertEquals(3, location[1]);

        key = 17;
        location = obj.searchElementWithinDimensions(1, 2, 2, 4, key);
        assertNull(location, "Should have been null as this is non-existing key");

        key = 13;
        location = obj.searchElementWithinDimensions(0, 0, 2, 3, key);
        assertEquals(2, location[0]);
        assertEquals(2, location[1]);
    }


    @Test
    void spaceAvailabilityTest() {
       int[] locationAvailable = obj.isThereAvailableSpaceForInsertingNewElement();
       assertNotNull(locationAvailable);
    }

    @Test
    void insertKeyTest() {
        int key = 4;
        System.out.println("Before Insertion");
        obj.displayTable();


        obj.insertKeyToYoungTable(key);
        System.out.println("After inserting key");
        obj.displayTable();
    }
}
