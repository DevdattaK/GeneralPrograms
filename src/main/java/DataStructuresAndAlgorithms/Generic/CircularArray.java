package DataStructuresAndAlgorithms.Generic;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CircularArray<T> implements Iterable<T>{
    private int headPosition;
    //private final int capacity;
    private T[] array;

    public CircularArray(/*int capacity, */
                         Supplier<T[]> arraySupplier) {
        //this.capacity = capacity;
        array = arraySupplier.get();
    }

    public int getHeadPosition() {
        return headPosition;
    }



    private void setHeadPosition(int headPosition) throws IllegalArgumentException{
        if (headPosition < array.length) {
            this.headPosition = headPosition;
        }else {
            throw new IllegalArgumentException("HeadPosition value passed : " + headPosition
                            + " is out of the bounds for array.");
        }
    }

    public void rotateRightByPositions(int numberOfPositions){
        int newHeaderPosition = Math.abs(this.headPosition - numberOfPositions) % array.length;
        this.setHeadPosition(newHeaderPosition);
    }

    public void displayArray() {
        int tempHeadPosition = headPosition;

        for(int i = 0; i < array.length-1; i++){
            System.out.print(array[tempHeadPosition < array.length ? tempHeadPosition : (tempHeadPosition%array.length)] + "->");
            tempHeadPosition++;
        }
        System.out.println(array[tempHeadPosition < array.length ? tempHeadPosition : (tempHeadPosition%array.length)]);

        System.out.println("\n");
    }

    public int getUpdatedHeadPosition(int numberOfPositions){
        int adjustedPosition = numberOfPositions + array.length;
        if(numberOfPositions < 0){
            numberOfPositions = adjustedPosition < 0 ? (-1 * adjustedPosition) : adjustedPosition;
        }
        return (headPosition + numberOfPositions) % array.length;
    }

    public void rotateLeftByPositions(int numberOfPositions) {
        int newHeaderPosition = (this.headPosition + numberOfPositions) % array.length;
        this.setHeadPosition(newHeaderPosition);
    }

    public int getArrayLength(){
        return array.length;
    }

    //-ve is right rotation.
    public void universalRotation(int numberOfPositions){
        this.setHeadPosition(this.getUpdatedHeadPosition(numberOfPositions));
    }

    public T get(int position){
        return array[position];
    }

    @Override
    public Iterator<T> iterator() {
        System.out.println("Returning list iterator..");
        // return Arrays.stream(array).collect(Collectors.toList()).listIterator();
        //OR .. custom implem
        return new CircularArrayIterator<T>(this);
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        System.out.println("Haven't provided implementation");
    }

    @Override
    public Spliterator<T> spliterator() {
        System.out.println("Haven't provided implementation");
        return null;
    }
}
