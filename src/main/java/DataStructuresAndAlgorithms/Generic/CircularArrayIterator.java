package DataStructuresAndAlgorithms.Generic;

import java.util.Iterator;


public class CircularArrayIterator<T> implements Iterator<T> {
    private CircularArray<T> arrayType;
    private int currentHeadPosition;

    public CircularArrayIterator(CircularArray<T> arrayType) {
        this.arrayType = arrayType;
        currentHeadPosition = arrayType.getHeadPosition();
    }

    @Override
    public boolean hasNext() {
        return currentHeadPosition < arrayType.getArrayLength();
    }

    @Override
    public T next() {
        T result = arrayType.get(currentHeadPosition++);
        return result;
    }
}
