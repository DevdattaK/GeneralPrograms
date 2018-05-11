package GenericProgramming;

import java.io.*;
import java.util.*;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
    CTCI-SectionVi
 */
public class SectionVI {

    //if right is smaller than left, search in right, otherwise search in left subarray
    private int getResetPoint(int[] arr){
        int start = 0, end = arr.length;
        int mid = (start + end) / 2;
        int result = -1;

        if(arr.length > 2) {
            if (arr[start] > arr[mid]) {
                result = getResetPoint(Arrays.copyOfRange(arr, start, mid + 1));
            }
            if(arr[mid] > arr[end - 1]){
                result = getResetPoint(Arrays.copyOfRange(arr, mid, end));
            }
        }else{
            if(arr.length == 2){
                result = arr[0] < arr[1] ? arr[0] : arr[1];
            }else{
                result = arr[0];
            }
        }

        return result;
    }

    public int getMinimumFromSortedAndRotatedArray(int[] arr){
        return this.getResetPoint(arr);
    }

    private void displayMap(Map<String, Integer> map){
        for(String key : map.keySet()){
            System.out.println("Key /Value : " + key + "/" + map.get(key));
        }
    }

    public boolean isThisMagazineGoodForRansomeNote(String magazineFilePath, String ransomeNote){
        BiConsumer<Map<String, Integer>, String> accumulator = (result, word) -> {
                                                                    if(result.get(word) != null){
                                                                        result.put(word, result.get(word) + 1);
                                                                    }else{
                                                                        result.put(word, 1);
                                                                    }
            };
        BiConsumer<Map<String, Integer>, Map<String, Integer>> combiner = (first, second) -> {
            for (String key: first.keySet()
                 ) {
                if(second.containsKey(key)){
                    first.put(key, first.get(key) + second.get(key));
                }else{
                    first.put(key, 1);
                }
            }
        };
        Map<String, Integer> ransomeNoteStrMap = Arrays.stream(ransomeNote.split(" ")).collect(HashMap::new, accumulator, combiner);
        Map<String, Integer> magazineStrMap = null;
        StringBuffer strFuffer = new StringBuffer();
        String str;
        boolean isGoodMagazine = true;

        this.displayMap(ransomeNoteStrMap);

       try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(magazineFilePath)))){
            while((str = br.readLine()) != null){
                strFuffer.append(str);
            }

            magazineStrMap = Arrays.stream(strFuffer.toString().split(" "))
                                    .collect(HashMap::new, accumulator, combiner);
           System.out.println("------------------------------");
           this.displayMap(magazineStrMap);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String key: ransomeNoteStrMap.keySet()
             ) {
            if(!magazineStrMap.containsKey(key)){
                isGoodMagazine = false;
                System.out.println("Mismatch for ransom key : " + key);
                break;
            }else {
                isGoodMagazine = magazineStrMap.get(key) >= ransomeNoteStrMap.get(key);

                if(!isGoodMagazine){
                    System.out.println("Mismatch for ransom key count (key : req/act): " + key + " : " + ransomeNoteStrMap.get(key)
                            + "/" + magazineStrMap.get(key));
                    break;
                }
            }
        }

        return isGoodMagazine;
    }

    private void getPartialPermutation(Set<String> partialResult, String newStr){
        Set<String> tempPermutation = new HashSet<>();

        for (String element : partialResult){
            for(int i = 0; i <= element.length(); i++){
                tempPermutation.add(element.substring(0, i) + newStr + element.substring(i, element.length()));
            }
        }
        partialResult.clear();
        partialResult.addAll(tempPermutation);
    }

    public void printPermutation(String seed) {
        if(seed.length() > 1) {
            Set<String> result = new HashSet<>();
            result.add(seed.substring(0, 1));

            for(int start = 1; start < seed.length(); start++) {
                this.getPartialPermutation(result, seed.substring(start, start + 1));
            }

            System.out.println("Total Permutation count : " + result.stream().count());



            result.stream().sorted().forEach(System.out::println);

        }else {
            System.out.println("No permutation possible on one entity");
        }
    }

    public void printCombinations(String seed){

    }
}
