package BinarySearchTree;

public class PreOrderTraversal<T extends Comparable> implements TraversalStrategy<T> {


    @Override
    public void display(Node<T> root) {
        if(root != null) {
            System.out.print(root.toString() + ", ");
            this.display(root.getLeft());
            this.display(root.getRight());
        }
    }
}
