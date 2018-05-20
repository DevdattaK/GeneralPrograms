package BinarySearchTree;

public class PostOrderTraversal<T extends Comparable> implements TraversalStrategy<T> {

    @Override
    public void display(Node<T> root) {
        if(root != null){
            this.display(root.getLeft());
            this.display(root.getRight());
            System.out.print(root.toString() + ", ");
        }
    }
}
