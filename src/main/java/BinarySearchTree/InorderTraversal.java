package BinarySearchTree;

public class InorderTraversal<T extends Comparable> implements TraversalStrategy<T> {

    public void display(Node<T> root) {
        if(root != null){
            this.display(root.getLeft());
            System.out.print(root.toString() + ", ");
            this.display(root.getRight());
        }
    }
}
