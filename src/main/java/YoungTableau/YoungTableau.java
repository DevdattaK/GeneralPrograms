package YoungTableau;
/*
Credit : Coreman (Acad problem)
YoungTableau is 'rowLen x colLen' matrix with rows sorted from left to right and columns from top to bottom. Some cells may be empty with infinity value.
This is a special/custom case of applied min-heap
 */

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class YoungTableau {
    private final int rowLen;        //row dimention
    private final int colLen;        //column dimention
    private int[][] table;    //table

    public YoungTableau(Supplier<Integer[][]> tableProvider) {
        this.buildTable(tableProvider);
        this.rowLen = table.length;
        this.colLen = table[0].length;
    }

    public YoungTableau(int[][] table) {
        rowLen = table[0].length;
        colLen = table.length;
        this.table = table;
    }

    private int getElementLength(int element) {
        int length = 0;

        while (element > 0) {
            element = element / 10;
            length++;
        }
        return length;
    }

    private int getMaxSpaceBetweenElements(int[][] arr) {
        int curMax = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                curMax = curMax > this.getElementLength(arr[i][j]) ? curMax : this.getElementLength(arr[i][j]);
            }
        }

        return curMax;
    }

    public void displayTable() {
        int maxSpaceBetweenElements = this.getMaxSpaceBetweenElements(table);

        Function<Integer, String> spacePrinter = (numSpaces) -> IntStream.range(0, numSpaces)
                .mapToObj(i -> " ")
                .collect(Collectors.joining());

        for (int row = 0; row < rowLen; row++) {
            for (int column = 0; column < colLen; column++) {
                System.out.print(table[row][column] + spacePrinter.apply(maxSpaceBetweenElements - this.getElementLength(table[row][column]) + 2));
            }
            System.out.println();
        }
    }

    public void buildTable(Supplier<Integer[][]> tableGenerator) {
        Integer[][] arr = tableGenerator.get();
        int row = arr.length, column = arr[0].length;

        this.table = new int[row][column];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                if (arr[i].length > 0) {
                    table[i][j] = arr[i][j].intValue();
                } else {
                    for (int colFil = 0; colFil < column; colFil++) {
                        table[i][colFil] = Integer.MAX_VALUE;
                    }
                }
            }
        }
    }

    public int getLeftOfSelf(int i, int j) {
        return j - 1 >= 0 ? table[i][j - 1] : -1;
    }

    public int getRightOfSelf(int i, int j) {
        return j + 1 < colLen ? table[i][j + 1] : -1;
    }

    public int getTopOfSelf(int i, int j) {
        return i - 1 >= 0 ? table[i - 1][j] : -1;
    }

    public int getBottomOfSelf(int i, int j) {
        return i + 1 < rowLen ? table[i + 1][j] : -1;
    }

    //imposes the YoungTableau constraint
    public void yongifyTable(/*int key, */int rowStart, int columnStart) {
        int left = -1, top = -1;
        int value = table[rowStart][columnStart];
        int largeRowIndex, largeColumnIndex;

        if (this.isWithinLimit(rowStart, columnStart, rowLen - 1, colLen - 1)) {
            left = this.getLeftOfSelf(rowStart, columnStart);
            top = this.getTopOfSelf(rowStart, columnStart);

            if (value < left) {
                this.swap(rowStart, columnStart - 1, rowStart, columnStart);
                this.yongifyTable(rowStart, columnStart - 1);
            }

            value = table[rowStart][columnStart];
            if (value < top) {
                this.swap(rowStart - 1, columnStart, rowStart, columnStart);
                this.yongifyTable(rowStart - 1, columnStart);
            }
        }

        // System.out.println("After yongify is done, the YT would look like this : ");
        // this.displayTable();
    }

    //rowStart1/columnStart1 are indexes of left or top element.
    private void swap(int rowStart1, int columnStart1, int rowStart, int columnStart) {
        int temp = table[rowStart][columnStart];
        table[rowStart][columnStart] = table[rowStart1][columnStart1];
        table[rowStart1][columnStart1] = temp;
    }

    private boolean isWithinLimit(int rowStart, int columnStart, int rowEnd, int colEnd) {
        return rowStart>=0 && rowStart <= rowEnd && columnStart  >= 0 && columnStart <= colEnd;
    }

    public void buildYoungifiedTable() {
        for (int i = 0; i < rowLen; i++) {
            for (int j = 0; j < colLen; j++) {
                this.yongifyTable(i, j);
            }
        }
    }

    public int[] searchElement(int key) {
        int diagRow = 0, diagCol = 0;
        int[] elementLocation = null;

        //traverse till key is not smaller than the diag element. X/Y represents axis i.e. x is column and y is row
        while (this.isWithinLimit(diagRow, diagCol, rowLen-1, colLen-1)) {
            if(table[diagRow][diagCol] == key)
                return new int[]{diagRow, diagCol};
            else{
                if(key < table[diagRow][diagCol])
                    break;

                diagRow++; diagCol++;
            }
        }

        if (!this.isWithinLimit(diagRow, diagCol, rowLen-1, colLen-1))
            return null;

        //now, the key we want to search is either in lower left section, or upper right section of the table,
        // w.r.t. table[diagY][diagX].
        int lowerStartRow = diagRow, lowerStartCol = 0, lowerEndRow = rowLen - 1, lowerEndCol = diagCol - 1;
        int upperStartRow = 0, upperStartCol = diagCol, upperEndRow = diagRow - 1, upperEndCol = colLen - 1;

        elementLocation = this.searchElementWithinDimensions(lowerStartRow, lowerStartCol, lowerEndRow, lowerEndCol, key);
        if (elementLocation == null){
            elementLocation = this.searchElementWithinDimensions(upperStartRow, upperStartCol, upperEndRow, upperEndCol, key);
        }

        /*ExecutorService service = Executors.newFixedThreadPool(2);
        Future<int[]> lowerLeft = service.submit(() -> this.searchElementWithinDimensions(lowerStartRow, lowerStartCol, lowerEndRow, lowerEndCol, key));
        Future<int[]> topRight = service.submit(() -> this.searchElementWithinDimensions(upperStartRow, upperStartCol, upperEndRow, upperEndCol, key));


        try {
            service.shutdown();

            if(lowerLeft.get() != null){
                elementLocation = lowerLeft.get();
            }else{
                elementLocation = topRight.get();
            }

            service.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
*/


        return elementLocation;
    }

    public int[] searchElementWithinDimensions(int startRowIndex, int startColIndex, int endRowIndex, int endColIndex, int key) {
        int i = startRowIndex, j = startColIndex;
        int curElement;

        if (this.isWithinLimit(i, j, endRowIndex, endColIndex)) {

            curElement = table[i][j];

            if (curElement == key) {
                return new int[]{i, j};
            } else {
                if (curElement < key) {
                    //go right, if possible, otherwise go down
                    if (j < endColIndex)
                        return this.searchElementWithinDimensions(i, j + 1, endRowIndex, endColIndex, key);
                    else
                        return this.searchElementWithinDimensions(i + 1, j, endRowIndex, endColIndex, key);
                } else {
                    //go down and then left.
                    return this.searchElementWithinDimensions(i + 1, j - 1, endRowIndex, endColIndex, key);
                }
            }
        }
        return null;
    }


    public void insertKeyToYoungTable(int key) {
        int[] availableSpace;
        if((availableSpace = this.isThereAvailableSpaceForInsertingNewElement()) != null){
            table[availableSpace[0]][availableSpace[1]] = key;
            this.buildYoungifiedTable();
        }else {
            System.out.println("No space available for inserting new key");
        }
    }

    public int[] isThereAvailableSpaceForInsertingNewElement() {
        for (int i = 0; i < rowLen; i++){
            for(int j = 0; j < colLen; j++){
                if (table[i][j] == Integer.MAX_VALUE){
                    return new int[]{i, j};
                }
            }
        }

        return null;
    }
}

