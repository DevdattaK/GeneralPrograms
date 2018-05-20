package BST;

import BinarySearchTree.Node;
import BinarySearchTree.BinarySearchTree;
import BinarySearchTree.InorderTraversal;
import BinarySearchTree.PreOrderTraversal;
import BinarySearchTree.PostOrderTraversal;
import BinarySearchTree.Traversal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BinsarySearchTreeTest {
    private BinarySearchTree<Integer> bst;
    private Supplier<Stream<Node<Integer>>> elementSupplier = () -> IntStream.rangeClosed(1, 10)
            .mapToObj(i -> new Node<Integer>(ThreadLocalRandom.current()
                    .nextInt(5, 50)));

    @BeforeEach
    void setUp() {
        bst = new BinarySearchTree<Integer>(null, Comparator.comparing(Node::getData));
    }

    @Test
    void builtTreeTest() {
        bst.buildBinarySearchTree(elementSupplier);
        bst.displayBinaryTree(bst.getRoot());
    }

    @Test
    void heightTest() {
        /*int arrLen = (int) elementSupplier.get()
                .count();
        bst.buildBinarySearchTree(elementSupplier.get()
                .toArray(value -> new Node[arrLen]));*/
        Node<Integer>[] arr = new Node[]{new Node(39), new Node(40), new Node(45), new Node(38), new Node(5), new Node(26), new Node(10),
                new Node(24),new Node(28)};
        arr = new Node[]{new Node(28), new Node(24), new Node(38), new Node(10), new Node(45), new Node(26), new Node(5),
                new Node(40),new Node(39)};
        bst.buildBinarySearchTree(arr);
        int height = bst.getHeight();
        System.out.println(height);
    }

    @Test
    void nodeLength() {
        Node<Integer> intNode = new Node(234);
        Node<String> stringNode = new Node("HelloThere");
        Node<String> emptyStr = new Node(null);

        int len1 = bst.getNodeLengthForPrinting(intNode);

        BinarySearchTree<String> strBST = new BinarySearchTree<String>(null, null);

        int len2 = strBST.getNodeLengthForPrinting(stringNode);
        int len3 = strBST.getNodeLengthForPrinting(emptyStr);

        assertEquals(3, len1);
        assertEquals(10, len2);
        assertEquals(0, len3);
    }

    @Test
    void maxNodeLengthTest() {
        Node<String> node1 = new Node("HelloThere");
        Node<String> node2 = new Node("I");
        Node<String> node3 = new Node("Am");
        Node<String> node4 = new Node("ExtremelyHappyToda");
        Node<String>[] nodes = new Node[]{node1, node2, node3, node4};

        BinarySearchTree<String> strBST = new BinarySearchTree<String>(null, Comparator.comparing(Node::getData));
        strBST.buildBinarySearchTree(nodes);

        int maxLen = strBST.getLengthOfLongestNode(strBST.getRoot());
        assertEquals(18, maxLen);
    }

    @Test
    void getSpaceReqAtBottomTest() {
        Node<Integer>[] nodes = new Node[]{new Node(4), new Node(2), new Node(6), new Node(1), new Node(3), new Node(5)};
        bst.buildBinarySearchTree(nodes);
        int spaceAtBottom = bst.getSpaceRequiredAtBottom();

        assertEquals(13, spaceAtBottom);
    }

    @Test
    void printTest() {
        Node<Integer>[] nodes = new Node[]{new Node(4), new Node(2), new Node(6), new Node(1), new Node(3), new Node(5)};
        bst.buildBinarySearchTree(nodes);
        bst.printBinarySearchTree();

        Node<String> node1 = new Node("HelloThere");
        Node<String> node2 = new Node("I");
        Node<String> node3 = new Node("Am");
        Node<String> node4 = new Node("ExtremelyHappyToda");
        Node<String>[] strNodes = new Node[]{node1, node2, node3, node4};

        BinarySearchTree<String> strBST = new BinarySearchTree<String>(null, Comparator.comparing(Node::getData));
        strBST.buildBinarySearchTree(strNodes);
        strBST.printBinarySearchTree();
    }

    @Test
    void getNodeToDelTest() {
        Integer toDel = 3;
        Node<Integer>[] nodes = new Node[]{new Node(4), new Node(2), new Node(6),
                new Node(1), new Node(3), new Node(5), new Node(8), new Node(7), new Node(9)};
        bst.buildBinarySearchTree(nodes);

        Node<Integer> NodeToDel = bst.getNodeWithData(toDel);
        assertEquals(3, NodeToDel.getData().intValue());

        toDel = 31;
        NodeToDel = bst.getNodeWithData(toDel);
        assertNull(NodeToDel);
    }

    @Test
    void smallestChildTest() {
        Node<Integer>[] nodes = new Node[]{new Node(4), new Node(2), new Node(6),
                new Node(1), new Node(3), new Node(5), new Node(8), new Node(7), new Node(9)};
        bst.buildBinarySearchTree(nodes);

        Node<Integer> inorderSuccessorFor = bst.getNodeWithData(new Integer(6));
        Node<Integer> smallest = bst.getSmallestChildUnder(inorderSuccessorFor.getRight());
        assertEquals(7, smallest.getData().intValue());

        inorderSuccessorFor = bst.getNodeWithData(new Integer(9));
        smallest = bst.getSmallestChildUnder(inorderSuccessorFor.getRight());
        assertNull(smallest);

        inorderSuccessorFor = bst.getNodeWithData(new Integer(4));
        smallest = bst.getSmallestChildUnder(inorderSuccessorFor.getRight());
        assertEquals(5, smallest.getData().intValue());
    }

    @Test
    void parentNodeFetchTest() {
        Node<Integer>[] nodes = new Node[]{new Node(4), new Node(2), new Node(6),
                new Node(1), new Node(3), new Node(5), new Node(8), new Node(7), new Node(9)};
        bst.buildBinarySearchTree(nodes);

        Node<Integer> node = new Node(8);
        Node<Integer> nodeParent = bst.getParentOf(node);

        assertEquals(nodeParent.getData().intValue(), 6);
    }

    @Test
    void deleteNodeTest() {
        Node<Integer>[] nodes = new Node[]{new Node(4), new Node(2), new Node(6),
                new Node(1), new Node(3), new Node(5), new Node(8), new Node(7), new Node(9)};
        bst.buildBinarySearchTree(nodes);
        System.out.println("Before deleting");
        bst.printBinarySearchTree();
        //delete element with data=6.
        bst.deleteElement(6);
        System.out.println("\n After deleting 6 \n ");
        bst.printBinarySearchTree();

        bst.deleteElement(9);
        System.out.println("\n After deleting 9 \n ");
        bst.printBinarySearchTree();

        bst.deleteElement(8);
        System.out.println("\n After deleting 9 \n ");
        bst.printBinarySearchTree();

        bst.deleteElement(7);
        System.out.println("\n After deleting 9 \n ");
        bst.printBinarySearchTree();

        bst.deleteElement(5);
        System.out.println("\n After deleting 9 \n ");
        bst.printBinarySearchTree();

        bst.insertElement(new Node(6));
        System.out.println("\n After inserting 6 \n ");
        bst.printBinarySearchTree();

        bst.insertElement(new Node(5));
        System.out.println("\n After inserting 5 \n ");
        bst.printBinarySearchTree();

        bst.insertElement(new Node(9));
        System.out.println("\n After inserting 9 \n ");
        bst.printBinarySearchTree();

        bst.insertElement(new Node(7));
        System.out.println("\n After inserting 7 \n ");
        bst.printBinarySearchTree();

        bst.insertElement(new Node(10));
        System.out.println("\n After inserting 10 \n ");
        bst.printBinarySearchTree();

        bst.insertElement(new Node(8));
        System.out.println("\n After inserting 8 \n ");
        bst.printBinarySearchTree();

        bst.deleteElement(6);
        System.out.println("\n After deleting 6 \n ");
        bst.printBinarySearchTree();

        bst.deleteElement(4);
        System.out.println("\n After deleting Root : 4 \n ");
        bst.printBinarySearchTree();
    }

    @Test
    void deleteSubtreeTest() {
        Node<Integer>[] nodes = new Node[]{new Node(4), new Node(2), new Node(6),
                new Node(1), new Node(3), new Node(5), new Node(8), new Node(7), new Node(9)};
        bst.buildBinarySearchTree(nodes);
        System.out.println("\n Before deleting");
        bst.printBinarySearchTree();

        bst.deleteSubtreeRecursively(8);
        System.out.println("\n Before deleting subtree rooted at 8");
        bst.printBinarySearchTree();

        bst.deleteSubtreeRecursively(4);
        System.out.println("\n After deleting root");
        bst.printBinarySearchTree();
    }

    @Test
    void inOrderTraversalTest() {
        Node<Integer>[] nodes = new Node[]{new Node(4), new Node(2), new Node(6),
                new Node(1), new Node(3), new Node(5), new Node(8), new Node(7), new Node(9)};
        bst.buildBinarySearchTree(nodes);
        System.out.println("Inorder traversal...");

        Traversal<Integer> traversal = new Traversal(new InorderTraversal());
        traversal.traverseAndDisplay(bst.getRoot());
    }

    @Test
    void preOrderTraversalTest() {
        Node<Integer>[] nodes = new Node[]{new Node(4), new Node(2), new Node(6),
                new Node(1), new Node(3), new Node(5), new Node(8), new Node(7), new Node(9)};
        bst.buildBinarySearchTree(nodes);
        System.out.println("Inorder traversal...");

        Traversal<Integer> traversal = new Traversal(new PreOrderTraversal());
        traversal.traverseAndDisplay(bst.getRoot());
    }

    @Test
    void postOrderTraversalTest() {
        Node<Integer>[] nodes = new Node[]{new Node(4), new Node(2), new Node(6),
                new Node(1), new Node(3), new Node(5), new Node(8), new Node(7), new Node(9)};
        bst.buildBinarySearchTree(nodes);
        System.out.println("Inorder traversal...");

        Traversal<Integer> traversal = new Traversal(new PostOrderTraversal());
        traversal.traverseAndDisplay(bst.getRoot());
    }
}
