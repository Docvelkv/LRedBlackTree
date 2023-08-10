import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LRedBlackTree<T extends Comparable<T>> {

    Node root;

    public boolean add(T value){
        if (root == null){
            root = new Node(value);
            root.color = Color.black;
            return true;
        }
        return addNode(root, value) != null;
    }

    /**
     * Приватный рекурсивный метод добавления узла,
     * правого - большего или левого - меньшего,
     * к родительскому узлу с балансировкой
     * @param node узел родитель
     * @param value значение нового узла
     * @return Node (новый узел)
     */
    private Node addNode(Node node, T value){
        if(node.value.compareTo(value) == 0) return null;
        if(node.value.compareTo(value) > 0){
            if (node.left == null){
                node.left = new Node(value);
                return node.left;
            }
            Node addedNode = addNode(node.left, value);
            balancingTree(node.left);
            return addedNode;
        }
        if(node.right == null){
            node.right = new Node(value);
            return node.right;
        }
        Node addedNode = addNode(node.right, value);
        node.right = balancingTree(node.right);
        return addedNode;
    }

    /**
     * Приватный метод балансировки, относительно родительского узла
     * в зависимости от цвета потомков
     * @param parent родительский узел
     * @return Node (родитель после балансировки)
     */
    private Node balancingTree(Node parent){
        boolean needForBalancing = true;
        while (needForBalancing){
            needForBalancing = false;
            // Левый поворот, если есть правый потомок красного цвета
            if(parent.right != null && parent.right.color == Color.red &&
                    (parent.left == null || parent.left.color == Color.black)){
                return leftSwap(parent);
            }
            // Правый поворот, если два левых потомка красные (по вертикали)
            if(parent.left != null && parent.left.color == Color.red &&
                    parent.left.left != null && parent.left.left.color == Color.red){
                return rightSwap(parent);
            }
            // Смена цвета, если правый и левый потомки красные
            if(parent.left != null && parent.right != null &&
            parent.left.color == Color.red && parent.right.color == Color.red){
                needForBalancing = true;
                colorSwap(parent);
            }
        }
        return parent;
    }

    /**
     * Приватный метод реализации правого поворота (< >)
     * @param parent родительский узел
     * @return Node (родитель после поворота)
     */
    private Node rightSwap(Node parent){
        Node newParent = parent.left;
        Node between = parent.left.right;
        newParent.right = parent;
        newParent.right.left = between;
        newParent.color = parent.color;
        newParent.right.color = Color.red;
        return newParent;
    }

    /**
     * Приватный метод реализации левого поворота (> <)
     * @param parent родительский узел
     * @return Node (родитель после поворота)
     */
    private Node leftSwap(Node parent){
        Node newParent = parent.right;
        Node between = parent.right.left;
        newParent.left = parent;
        newParent.left.right = between;
        newParent.color = parent.color;
        newParent.left.color = Color.red;
        return newParent;
    }

    /**
     * Приватный метод реализации смены цвета
     * @param parent родительский узел
     */
    private void colorSwap(Node parent){
        parent.left.color = Color.black;
        parent.right.color = Color.black;
        parent.color = Color.red;
    }

    public class Node {
        T value;
        Color color;
        Node left;
        Node right;

        /**
         * Конструктор - создаёт новый узел, красного цвета
         * @param value значение узла
         */
        private Node(T value) {
            this.value = value;
            this.color = Color.red;
        }
    }

    private class PrintNode {
        Node node;
        String str;
        int depth;

        public PrintNode() {
            node = null;
            str = " ";
            depth = 0;
        }

        public PrintNode(Node node) {
            depth = 0;
            this.node = node;
            this.str = node.value.toString();
        }
    }

    private void printLines(List<List<PrintNode>> list, int i, int j, int i2, int j2) {
        // Идём вниз
        if (i2 > i) {
            while (i < i2) {
                i++;
                list.get(i).get(j).str = "|";
            }
            list.get(i).get(j).str = "\\";
            while (j < j2) {
                j++;
                list.get(i).get(j).str = "-";
            }
        }
        else
        {
            while (i > i2) {
                i--;
                list.get(i).get(j).str = "|";
            }
            list.get(i).get(j).str = "/";
            while (j < j2) {
                j++;
                list.get(i).get(j).str = "-";
            }
        }
    }

    public int maxDepth() {
        return maxDepth2(0, root);
    }

    private int maxDepth2(int depth, Node node) {
        depth++;
        int left = depth;
        int right = depth;
        if (node.left != null)
            left = maxDepth2(depth, node.left);
        if (node.right != null)
            right = maxDepth2(depth, node.right);
        return Math.max(left, right);
    }

    private int nodeCount(Node node, int count) {
        if (node != null) {
            count++;
            return count + nodeCount(node.left, 0) + nodeCount(node.right, 0);
        }
        return count;
    }

    public void print() {

        int maxDepth = maxDepth() + 3;
        int nodeCount = nodeCount(root, 0);
        int width = 50;//maxDepth * 4 + 2;
        int height = nodeCount * 5;
        List<List<PrintNode>> list = new ArrayList<>();
        for (int i = 0; i < height; i++) /*Создание ячеек массива*/ {
            ArrayList<PrintNode> row = new ArrayList<>();
            for (int j = 0; j < width; j++) {
                row.add(new PrintNode());
            }
            list.add(row);
        }

        list.get(height / 2).set(0, new PrintNode(root));
        list.get(height / 2).get(0).depth = 0;

        for (int j = 0; j < width; j++)  /*Принцип заполнения*/ {
            for (int i = 0; i < height; i++) {
                PrintNode currentNode = list.get(i).get(j);
                if (currentNode.node != null) {
                    currentNode.str = currentNode.node.value.toString();
                    if (currentNode.node.left != null) {
                        int in = i + (maxDepth / (int) Math.pow(2, currentNode.depth));
                        int jn = j + 3;
                        printLines(list, i, j, in, jn);
                        list.get(in).get(jn).node = currentNode.node.left;
                        list.get(in).get(jn).depth = list.get(i).get(j).depth + 1;

                    }
                    if (currentNode.node.right != null) {
                        int in = i - (maxDepth / (int) Math.pow(2, currentNode.depth));
                        int jn = j + 3;
                        printLines(list, i, j, in, jn);
                        list.get(in).get(jn).node = currentNode.node.right;
                        list.get(in).get(jn).depth = list.get(i).get(j).depth + 1;
                    }

                }
            }
        }
        for (int i = 0; i < height; i++) /*Чистка пустых строк*/ {
            boolean flag = true;
            for (int j = 0; j < width; j++) {
                if (!Objects.equals(list.get(i).get(j).str, " ")) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                list.remove(i);
                i--;
                height--;
            }
        }

        for (var row : list) {
            for (var item : row) {
                if (item.node != null) {
                    if (item.node.color == Color.red)
                        System.out.print("\u001B[31m" + item.str + " " + "\u001B[0m");
                    else
                        System.out.print("\u001B[30m" + item.str + " " + "\u001B[0m");
                } else
                    System.out.print(item.str + " ");
            }
            System.out.println();
        }
    }
}