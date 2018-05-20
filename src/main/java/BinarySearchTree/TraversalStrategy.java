package BinarySearchTree;

public interface TraversalStrategy<T extends Comparable> {
    void display(Node<T> root);
    default void displayTree(Node<T> root){
        this.display(root);
    }
}
