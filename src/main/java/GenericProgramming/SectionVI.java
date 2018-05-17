package GenericProgramming;

import java.io.*;
import java.util.*;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
    CTCI-SectionVi
 */
public class SectionVI {

    //if right is smaller than left, search in right, otherwise search in left subarray
    private int getResetPoint(int[] arr) {
        int start = 0, end = arr.length;
        int mid = (start + end) / 2;
        int result = -1;

        if (arr.length > 2) {
            if (arr[start] > arr[mid]) {
                result = getResetPoint(Arrays.copyOfRange(arr, start, mid + 1));
            }
            if (arr[mid] > arr[end - 1]) {
                result = getResetPoint(Arrays.copyOfRange(arr, mid, end));
            }
        } else {
            if (arr.length == 2) {
                result = arr[0] < arr[1] ? arr[0] : arr[1];
            } else {
                result = arr[0];
            }
        }

        return result;
    }

    public int getMinimumFromSortedAndRotatedArray(int[] arr) {
        return this.getResetPoint(arr);
    }

    private void displayMap(Map<String, Integer> map) {
        for (String key : map.keySet()) {
            System.out.println("Key /Value : " + key + "/" + map.get(key));
        }
    }

    public boolean isThisMagazineGoodForRansomeNote(String magazineFilePath, String ransomeNote) {
        BiConsumer<Map<String, Integer>, String> accumulator = (result, word) -> {
            if (result.get(word) != null) {
                result.put(word, result.get(word) + 1);
            } else {
                result.put(word, 1);
            }
        };
        BiConsumer<Map<String, Integer>, Map<String, Integer>> combiner = (first, second) -> {
            for (String key : first.keySet()
                    ) {
                if (second.containsKey(key)) {
                    first.put(key, first.get(key) + second.get(key));
                } else {
                    first.put(key, 1);
                }
            }
        };
        Map<String, Integer> ransomeNoteStrMap = Arrays.stream(ransomeNote.split(" "))
                .collect(HashMap::new, accumulator, combiner);
        Map<String, Integer> magazineStrMap = null;
        StringBuffer strFuffer = new StringBuffer();
        String str;
        boolean isGoodMagazine = true;

        this.displayMap(ransomeNoteStrMap);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(magazineFilePath)))) {
            while ((str = br.readLine()) != null) {
                strFuffer.append(str);
            }

            magazineStrMap = Arrays.stream(strFuffer.toString()
                    .split(" "))
                    .collect(HashMap::new, accumulator, combiner);
            System.out.println("------------------------------");
            this.displayMap(magazineStrMap);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String key : ransomeNoteStrMap.keySet()
                ) {
            if (!magazineStrMap.containsKey(key)) {
                isGoodMagazine = false;
                System.out.println("Mismatch for ransom key : " + key);
                break;
            } else {
                isGoodMagazine = magazineStrMap.get(key) >= ransomeNoteStrMap.get(key);

                if (!isGoodMagazine) {
                    System.out.println("Mismatch for ransom key count (key : req/act): " + key + " : " + ransomeNoteStrMap.get(key)
                            + "/" + magazineStrMap.get(key));
                    break;
                }
            }
        }

        return isGoodMagazine;
    }

    private void getPartialPermutation(Set<String> partialResult, String newStr) {
        Set<String> tempPermutation = new HashSet<>();

        for (String element : partialResult) {
            for (int i = 0; i <= element.length(); i++) {
                tempPermutation.add(element.substring(0, i) + newStr + element.substring(i, element.length()));
            }
        }
        partialResult.clear();
        partialResult.addAll(tempPermutation);
    }

    public void printPermutation(String seed) {
        if (seed.length() > 1) {
            Set<String> result = new HashSet<>();
            result.add(seed.substring(0, 1));

            for (int start = 1; start < seed.length(); start++) {
                this.getPartialPermutation(result, seed.substring(start, start + 1));
            }

            System.out.println("Total Permutation count : " + result.stream()
                    .count());


            result.stream()
                    .sorted()
                    .forEach(System.out::println);

        } else {
            System.out.println("No permutation possible on one entity");
        }
    }

    private void combine(String instr, StringBuffer outstr, int index) {
        for (int i = index; i < instr.length(); i++) {
            outstr.append(instr.charAt(i));
            System.out.println(outstr);
            combine(instr, outstr, i + 1);
            outstr.deleteCharAt(outstr.length() - 1);
        }
    }


    public void printCombinations(String seed) {
        //build individual lists, store them in map and then merge them all in final step.
        this.combine(seed, new StringBuffer(), 0);
        //System.out.println();
    }

    public int getNumberOfSpacesRequiredToStore(int numberToStore) {
        int count = 0;
        if (numberToStore > 0) {
            while (numberToStore > 0) {
                numberToStore /= 10;
                count++;
            }
            return count;
        } else {
            return 0;
        }
    }

    public int getTargetArraySize(String str) {
        char[] strAsCharArr = str.toCharArray();
        char prevChar = strAsCharArr[0];
        int charCount = 0, totalCharCount = 0;

        for (char ch : strAsCharArr) {
            if (ch == prevChar) {
                charCount++;
            } else {
                totalCharCount += 1 + this.getNumberOfSpacesRequiredToStore(charCount);
                prevChar = ch;
                charCount = 1;
            }
        }
        totalCharCount += 1 + this.getNumberOfSpacesRequiredToStore(charCount);

        return totalCharCount;
    }

    public char[] compressStringUsingLZWApproach(String str) {
        //get string size
        final int targetArraySize = this.getTargetArraySize(str);
        char[] compressedString = new char[targetArraySize];

        //compress string
        char[] source = str.toCharArray();
        int targetIndex = 0, indexForMove, leap = 0;
        char[] sizeAsArr;


        for (int index = 0; index < source.length; ) {
            compressedString[targetIndex++] = source[index];

            indexForMove = index + 1;

            while (indexForMove < source.length && source[index] == source[indexForMove]) {
                leap++;
                indexForMove++;
            }
            sizeAsArr = this.getNumberAsArray(leap + 1);

            for (char c : sizeAsArr) {
                compressedString[targetIndex++] = c;
            }

            leap = 0;
            index = indexForMove;
        }

        return compressedString;
    }

    private void storeInCharArr(char[] arr, AtomicInteger index, int num) {
        if (num > 0) {
            this.storeInCharArr(arr, index, num / 10);
            arr[index.getAndIncrement()] = Character.forDigit(num % 10, 10);//(char)((num % 10) + '0');
        }
    }

    public char[] getNumberAsArray(int number) {
        char[] arr = new char[this.getNumberOfSpacesRequiredToStore(number)];
        AtomicInteger index = new AtomicInteger(0);

        this.storeInCharArr(arr, index, number);

        return arr;
    }

    private void swapAndRemove(Integer[] sourceHeap, Integer[] targetHeap, int sourceHeapSize, int targetHeapSize) {
        Integer itemToBeMoved;

    }

    private double printStatsAndGetMedian(int curCount, Integer[] maxHeap, Integer[] minHeap, int minSize, int maxSize) {
        System.out.println("\n After iteration# : " + curCount);

        System.out.println("\n MaxHeap : ");
        Arrays.stream(maxHeap)
                .forEach(i -> System.out.print(" " + i));

        System.out.println("\n MinHeap : ");
        Arrays.stream(minHeap)
                .forEach(i -> System.out.print(" " + i));

        return (maxSize > minSize) ? maxHeap[0] : (maxSize < minSize) ? minHeap[0] : (minHeap[0] + maxHeap[0]) / 2.0;
    }

    public int[] printMedianOfEverIncreasinArray() throws InterruptedException {
        final int maxCount = 20;
        final int heapMaxSize = maxCount / 2 + 1;
        int curCount = 0, minSize = 0, maxSize = 0;
        Integer[] minHeap = new Integer[heapMaxSize];
        Integer[] maxHeap = new Integer[heapMaxSize];
        AtomicInteger generatedVal = new AtomicInteger();
        Semaphore firstNumberLock = new Semaphore(1);
        Semaphore nextNumberLock = new Semaphore(1);
        HeapSort<Integer> heapSort = new HeapSort<>();
        Integer itemToBeMoved = null;
        Comparator<Integer> naturalOrder = Comparator.comparing(Integer::intValue);

        firstNumberLock.acquire();

        Thread t1 = new Thread(new Runnable() {

            private void updateAtomicInteger() {
                generatedVal.set(ThreadLocalRandom.current()
                        .nextInt(20, 90));
            }

            @Override
            public void run() {
                int count = 0, maxCount = 20;

                while (count < maxCount) {
                    try {
                        nextNumberLock.acquire();
                        this.updateAtomicInteger();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        firstNumberLock.release();
                    }

                    count++;
                }
                System.out.println(Thread.currentThread()
                        .getName() + " stopping random val generation.");
            }
        });


        t1.start();

        //arr[maxHeap | minHeap]
        while (curCount < maxCount) {
            firstNumberLock.acquire();

            System.out.println("new value generated : " + generatedVal.get());

            maxHeap[maxSize++] = generatedVal.get();
            heapSort.buildHeap(maxHeap, maxSize, naturalOrder, maxSize);

            //if heap sizes differ by more than 1, rebalance heaps.
            if (Math.abs(maxSize - minSize) > 1) {
                System.out.println("Popping element...");
                if (maxSize > minSize) {
                    itemToBeMoved = maxHeap[0];
                    minHeap[minSize++] = itemToBeMoved;
                    maxHeap[0] = maxHeap[--maxSize];
                    maxHeap[maxSize] = null;
                } else {
                    itemToBeMoved = minHeap[0];
                    maxHeap[maxSize++] = itemToBeMoved;
                    minHeap[0] = minHeap[--minSize];
                    minHeap[minSize] = null;
                }
                heapSort.buildHeap(maxHeap, maxSize, naturalOrder, maxSize);
                heapSort.buildHeap(minHeap, minSize, naturalOrder.reversed(), minSize);
            }

            curCount++;
            nextNumberLock.release();


            double median = this.printStatsAndGetMedian(curCount, maxHeap, minHeap, minSize, maxSize);

            System.out.println("\n Median is : " + median);
        }

        System.out.println("\n ..................................................");
        this.adjustMisalignedElementsInBothHeaps(maxHeap, maxSize - 1, minHeap, minSize - 1, heapSort, naturalOrder);
        double median = this.printStatsAndGetMedian(curCount, maxHeap, minHeap, minSize, maxSize);
        System.out.println("\n Median After Adjustment : " + median);


        t1.join();

        //return addition of both heaps for traditional mean findings.
        return Arrays.stream((Arrays.stream(maxHeap)
                .filter(i -> i != null)
                .map(i -> i + "")
                .collect(Collectors.joining(",")) + "," +
                Arrays.stream(minHeap)
                        .filter(i -> i != null)
                        .map(i -> i + "")
                        .collect(Collectors.joining(","))).split(","))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    private void adjustMisalignedElementsInBothHeaps(Integer[] maxHeap, int maxSize, Integer[] minHeap, int minSize,
                                                     final HeapSort<Integer> heapSort, Comparator<Integer> naturalOrder) {
        Integer temp;
        //swap heads, rebuild heaps
        if (naturalOrder.compare(minHeap[0], maxHeap[0]) < 0) {
            System.out.println("Doing headswap..");
            temp = maxHeap[0];
            maxHeap[0] = minHeap[0];
            minHeap[0] = temp;

            heapSort.heapify(maxHeap, naturalOrder, 0, maxSize);
            heapSort.heapify(minHeap, naturalOrder.reversed(), 0, minSize);

            this.adjustMisalignedElementsInBothHeaps(maxHeap, maxSize, minHeap, minSize, heapSort, naturalOrder);
        }
    }

}
