package BinarySearchTree;

import java.util.Comparator;

public class Node<T extends Comparable> implements Comparable<Node<T>> {
    private T data;
    private Node<T> left;
    private Node<T> right;

    public Node(T data) {
        this.data = data;
        this.left = this.right = null;
    }

    public Node(T data, Node<T> left, Node<T> right) {
        this.data = data;
        this.left = left;
        this.right = right;
    }

    public T getData() {
        return data;
    }


    public Node<T> getLeft() {
        return left;
    }

    public Node<T> getRight() {
        return right;
    }

    public void setLeft(Node<T> left) {
        if (this != left)
            this.left = left;
        else
            this.left = null;
    }

    public void setRight(Node<T> right) {
        if (this != right)
            this.right = right;
        else
            this.right = null;
    }

    @Override
    public String toString() {
        return data == null ? " " : data.toString();
    }


    public int getLenthForPrint() {
        return this.toString()
                .length();
    }

    @Override
    public boolean equals(Object obj) {
        return this.data.equals(((Node<T>) obj).data);
    }


    @Override
    public int compareTo(Node<T> o) {
        return this.getData()
                .compareTo(o.data);
    }
}
