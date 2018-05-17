package GenericProgramming;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HeapSort<T> {

    //1. MaxHeapify
    public void heapify(T[] arr, Comparator<T> comparator, int index, int heapSize) {
        int l = ((index + 1) * 2) - 1;
        int r = (index + 1) * 2;
        int largestIndex;

        if (l < heapSize && comparator.compare(arr[l], arr[index]) > 0) {
            largestIndex = l;
        } else {
            largestIndex = index;
        }

        if (r < heapSize && comparator.compare(arr[r], arr[largestIndex]) > 0) {
            largestIndex = r;
        }


        if (largestIndex != index) {
            this.swap(arr, largestIndex, index);
            heapify(arr, comparator, largestIndex, heapSize);
        }
    }

    public void swap(T[] arr, int i, int j) {
        T temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    //2. BuildMaxHeap. arrLength is actual number of positions filled in the array.
    public void buildHeap(T[] arr, int arrLength, Comparator<T> comparator, int heapSize) {
        for (int i = arrLength / 2 - 1; i >= 0; i--) {
            heapify(arr, comparator, i, heapSize);
        }
    }

    //3. HeapSort
    public void heapSort(T[] arr, Comparator<T> comparator){
        buildHeap(arr, arr.length, comparator, arr.length - 1);

        for(int end = arr.length - 1; end >= 0; end--){
            this.swap(arr, end, 0);
            //System.out.print(" - " + arr[end]);
            heapify(arr, comparator, 0, end);
        }
    }

    //4. display heap.
    /*

     */
    public void displayHeap(T[] arr) {
        int maxHeight = (this.getHeight(arr) * 2) + 1;
        int maxWidth = (arr.length * 2) - 1;
        int height = 0, index = 0;
        int numbersToBePrintedAtThisLevel = 0;
        int markerOffset = arr.length / 2 - 1;
        int spaceOffset = arr.length - 1;


        while (index < arr.length && height < maxHeight) {
            if (height % 2 == 0) {
                numbersToBePrintedAtThisLevel = numbersToBePrintedAtThisLevel * 2 < 1 ? 1 : numbersToBePrintedAtThisLevel * 2;
                numbersToBePrintedAtThisLevel = numbersToBePrintedAtThisLevel > (arr.length - index) ? (arr.length - index) : numbersToBePrintedAtThisLevel;

                this.printNumbers(arr, spaceOffset, index, numbersToBePrintedAtThisLevel);

                spaceOffset = spaceOffset/2;
                index += numbersToBePrintedAtThisLevel;
            } else {

                this.printMarkers(spaceOffset, markerOffset, ((maxHeight - height) * 2 + 1), numbersToBePrintedAtThisLevel);

                markerOffset = markerOffset/2 - 1;
                spaceOffset--;
            }

            height++;
        }

    }


    private void printMarkers(int spaceOffset, int markerOffset, int blankOffsetBetweenTwoMarkers, int numbersPrintedInPrevRow) {
        this.printOffsetSpaces(spaceOffset);

        for(int markerCount = numbersPrintedInPrevRow; markerCount > 0; markerCount--){
            printChildConnectors(markerOffset);
            printOffsetSpaces(blankOffsetBetweenTwoMarkers);
        }
        System.out.println();
    }


    private void printNumbers(T[] arr, int spaceOffset, int index, int numbersToBePrintedAtThisLevel) {
        //print number and space combo
        int curNum = 0;

        Function<Integer, Integer> spaceComputer = (num) ->  num < 10 ? 3 : 2;


        while (curNum < numbersToBePrintedAtThisLevel) {
            this.printOffsetSpaces(spaceOffset);

            System.out.print(arr[index]);

            this.printOffsetSpaces(spaceOffset + spaceComputer.apply((Integer) arr[index]));

            curNum++;
            index++;
        }
        System.out.println();
    }


    private void printOffsetSpaces(int offset) {
        String spaces = IntStream.rangeClosed(0, offset)
                //.mapToObj(o -> o%2==0?"\\":"/")
                .mapToObj(o -> "")
                .collect(Collectors.joining(" "));// + "\n";
        System.out.print(spaces);
    }


    public void printChildConnectors(int offset) {
        String connectors = IntStream.rangeClosed(1, offset)
                .mapToObj(o -> "_")
                .collect(Collectors.joining(""));
        System.out.print(connectors + "/ \\" + connectors);
    }

    //5. get tree height
    public int getHeight(T[] arr) {
        int index = 0, curHeight = 0;

        while (((index * 2) + 1) < (arr.length - 1)) {
            curHeight++;
            index = (index * 2) + 1;
        }

        return curHeight;
    }
}
