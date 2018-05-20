package BinarySearchTree;

public class Traversal<T extends Comparable> {
    private TraversalStrategy strategy;

    public Traversal(TraversalStrategy strategy) {
        this.strategy = strategy;
    }

    public void traverseAndDisplay(Node<T> root){
        this.strategy.displayTree(root);
    }
}
