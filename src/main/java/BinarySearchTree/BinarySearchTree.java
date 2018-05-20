package BinarySearchTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import BinarySearchTree.Node;

public class BinarySearchTree<T extends Comparable> {
    private Node<T> root;
    private Comparator<Node<T>> comparator;

    public BinarySearchTree() {
    }

    public BinarySearchTree(Node<T> root, Comparator<Node<T>> comparator) {
        this.root = root;
        this.comparator = comparator;
    }

    public Node<T> getRoot() {
        return root;
    }


    public void displayBinaryTree(Node<T> node) {
        if (node != null) {
            System.out.println("Node : " + node.getData());

            this.displayBinaryTree(node.getLeft());
            this.displayBinaryTree(node.getRight());
        }
    }

    public void insertElement(Node<T> element) {

        if (this.root == null)
            this.root = element;
        else {
            Node<T> curNode = this.root;
            Node<T> parent = null;
            while (curNode != null) {
                //element to insert is smaller than curNode.
                if (comparator.compare(element, curNode) <= 0) {
                    parent = curNode;
                    curNode = curNode.getLeft();
                } else if (comparator.compare(element, curNode) > 0) {
                    //element to insert is larger than curNode
                    parent = curNode;
                    curNode = curNode.getRight();
                }
            }

            if (comparator.compare(element, parent) < 0)
                parent.setLeft(element);
            else
                parent.setRight(element);
        }
    }

    public void buildBinarySearchTree(Node<T>[] elements) {
        Arrays.stream(elements)
                .forEach(e -> insertElement(e));
    }

    public void buildBinarySearchTree(Supplier<Stream<Node<T>>> elementSupplier) {
        elementSupplier.get()
                .forEach(e -> insertElement(e));
    }

    private int getCurNodeMaxHeightFromLeaf(Node<T> node) {
        int curHeight = 0;
        int leftSubtreeHeight, rightSubtreeHeight;

        if (node == null) {
            return -1;
        } else {
            /*if(node.getLeft() == null && node.getRight() == null){
                return 0;
            }else{*/
            leftSubtreeHeight = this.getCurNodeMaxHeightFromLeaf(node.getLeft());
            rightSubtreeHeight = this.getCurNodeMaxHeightFromLeaf(node.getRight());
            curHeight = 1 + (leftSubtreeHeight > rightSubtreeHeight ? leftSubtreeHeight : rightSubtreeHeight);
            //}
        }

        return curHeight;
    }

    public int getHeight() {
        return this.getCurNodeMaxHeightFromLeaf(this.getRoot());
    }

    public int getSpaceRequiredAtBottom() {
        int height = this.getHeight();
        int lengthOfLongestNode = this.getLengthOfLongestNode(this.root);
        int numberOfLeafNodes = (int) Math.pow(2, height);

        return (numberOfLeafNodes * lengthOfLongestNode) * 4 - 3;
    }

    public int getNodeLengthForPrinting(Node<T> node) {
        if (node == null) {
            return 0;
        } else {
            return node.getLenthForPrint();
        }
    }

    public int getLengthOfLongestNode(Node<T> node) {
        int nodeLength = this.getNodeLengthForPrinting(node);

        if (node == null) {
            return nodeLength;
        }

        int leftChildLength = this.getLengthOfLongestNode(node.getLeft());
        int rightChildLenth = this.getLengthOfLongestNode(node.getRight());

        return nodeLength < leftChildLength ? leftChildLength : nodeLength < rightChildLenth ? rightChildLenth : nodeLength;
    }

    public String getSpacesToPrint(int offset) {
        return IntStream.rangeClosed(1, offset)
                .mapToObj(i -> " ")
                .collect(Collectors.joining());
    }

    private String getCharsToPrint(int offset) {
        return IntStream.rangeClosed(1, offset)
                .mapToObj(i -> "_")
                .collect(Collectors.joining()) + "/\\";
    }

    public void printBinarySearchTree() {
        int height = this.getHeight();
        int spaceAtBottom = this.getSpaceRequiredAtBottom();
        int spaceRequiredForLargestNode = this.getLengthOfLongestNode(this.getRoot());
        int spaceOffset = spaceAtBottom / 2;
        ArrayList<Node<T>> curNodes = new ArrayList<Node<T>>();

        if (root != null) {
            //System.out.print(":"+separationOffset+":");
            System.out.print(this.getSpacesToPrint(spaceOffset) + root.toString() + this.getSpacesToPrint(spaceOffset));

            curNodes.add(this.root);
            spaceOffset = spaceOffset % 2 == 0 ? spaceOffset / 2 - 1 : spaceOffset / 2;
            this.printChildNodesOf(curNodes, spaceOffset, spaceRequiredForLargestNode);
        }
    }

    private void printChildNodesOf(ArrayList<Node<T>> curNodes, int offset, int spaceForLongestNode) {

        System.out.println("\n");
        //System.out.print(":"+offset+":");
        for (Node<T> curNode : curNodes) {
            if (curNode != null) {
                if (curNode.getLeft() != null)
                    System.out.print(this.getSpacesToPrint(offset) + curNode.getLeft() + this.getSpacesToPrint(offset));
                else
                    System.out.print(this.getSpacesToPrint(spaceForLongestNode));

                System.out.print(this.getSpacesToPrint(2 + spaceForLongestNode));

                if (curNode.getRight() != null)
                    System.out.print(this.getSpacesToPrint(offset) + curNode.getRight() + this.getSpacesToPrint(offset));
                else
                    System.out.print(this.getSpacesToPrint(spaceForLongestNode));
            }
            System.out.print(this.getSpacesToPrint(3 * spaceForLongestNode));
        }
        this.refreshChildNodeList(curNodes);

        offset = offset % 2 == 0 ? offset / 2 - 1 : offset / 2;

        if (curNodes.size() > 0)
            this.printChildNodesOf(curNodes, offset, spaceForLongestNode);
    }

    private void refreshChildNodeList(ArrayList<Node<T>> curNodes) {
        List<Node<T>> tempList = new ArrayList<Node<T>>();
        int numberOfNodesToRemove = 0;

        for (Node<T> node : curNodes) {
            if (node.getLeft() != null)
                tempList.add(node.getLeft());
            if (node.getRight() != null)
                tempList.add(node.getRight());

            numberOfNodesToRemove++;
        }

        for (int i = 0; i < numberOfNodesToRemove; i++) {
            curNodes.remove(0);
        }
        curNodes.addAll(tempList);
    }


    public void deleteElement(T toDeleteData) {
        //go to the element first
        Node<T> nodeToDelete = this.getNodeWithData(toDeleteData);
        //find smallest node in right subtree
        if (nodeToDelete != null) {
            Node<T> replacementNode = this.getInorderSuccessorOf(nodeToDelete);

            //if a node is to be deleted who doesn't have right subtree
            if (replacementNode == null && nodeToDelete.getLeft() != null)
                replacementNode = nodeToDelete.getLeft();

            Node<T> replacementNodeParent = this.getParentOf(replacementNode);
            Node<T> parentOfToBeDeletedNode = this.getParentOf(nodeToDelete);

            if (this.isLeafNode(nodeToDelete)) {
                if (parentOfToBeDeletedNode.getLeft() == nodeToDelete)
                    parentOfToBeDeletedNode.setLeft(null);
                else
                    parentOfToBeDeletedNode.setRight(null);
            } else if (this.isLeafNode(replacementNode)) {
                replacementNode.setLeft(nodeToDelete.getLeft());
                replacementNode.setRight(nodeToDelete.getRight());

                if (parentOfToBeDeletedNode != null) {
                    if (parentOfToBeDeletedNode.getLeft() == nodeToDelete)
                        parentOfToBeDeletedNode.setLeft(replacementNode);
                    else
                        parentOfToBeDeletedNode.setRight(replacementNode);
                }

                if (replacementNodeParent.getLeft() == replacementNode)
                    replacementNodeParent.setLeft(null);
                else
                    replacementNodeParent.setRight(null);

            } else {
                //2. replacementNode is on right subtree.
                if (replacementNode.getLeft() == null) {
                    parentOfToBeDeletedNode.setRight(replacementNode);
                    replacementNode.setLeft(nodeToDelete.getLeft());

                    //if replacement node is on left of its parent and has its own right subtree
                    if (replacementNodeParent.getLeft() == replacementNode && replacementNode.getRight() != null) {
                        replacementNodeParent.setLeft(replacementNode.getRight());
                    }

                    replacementNode.setRight(nodeToDelete.getRight());
                }
            }

            if(nodeToDelete == this.root)
                this.root = replacementNode;

            nodeToDelete.setLeft(null);
            nodeToDelete.setRight(null);
        }

    }

    public Node<T> getParentOf(Node<T> ofNode) {
        Node<T> parent = null;
        Node<T> curNode = this.root;

        if (ofNode != null) {
            while (curNode != null && curNode.compareTo(ofNode) != 0) {
                if (ofNode.compareTo(curNode) < 0) {
                    parent = curNode;
                    curNode = curNode.getLeft();
                } else {
                    parent = curNode;
                    curNode = curNode.getRight();
                }
            }

            if (curNode.compareTo(ofNode) == 0)
                return parent;

        }
        return null;
    }

    public boolean isLeafNode(Node<T> replacementNode) {
        return replacementNode != null && replacementNode.getLeft() == null && replacementNode.getRight() == null;
    }

    public Node<T> getInorderSuccessorOf(Node<T> nodeToDelete) {
        Node<T> rightChild = nodeToDelete.getRight();
        Node<T> inorderSuccessor = null;

        if (rightChild != null) {
            inorderSuccessor = this.getSmallestChildUnder(rightChild);
        }

        return inorderSuccessor;
    }

    public Node<T> getSmallestChildUnder(Node<T> baseNode) {
        Node<T> curNode = baseNode;

        while (curNode != null && curNode.getLeft() != null)
            curNode = curNode.getLeft();

        return curNode;
    }

    public Node<T> getNodeWithData(T toDeleteData) {
        Node<T> curNode = this.root;

        while (curNode != null && !curNode.getData()
                .equals(toDeleteData)) {
            if (toDeleteData.compareTo(curNode.getData()) < 0) {
                curNode = curNode.getLeft();
            } else {
                curNode = curNode.getRight();
            }
        }

        return curNode;
    }
}
