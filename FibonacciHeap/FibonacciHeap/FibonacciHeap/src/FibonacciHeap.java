/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap {
    public HeapNode minNode;
    public static int totalLinks = 0;
    public static int totalCuts = 0; //includes cascading cuts
    public int size;
    public int trees;
    public int markedNodes;
    public HeapNode leftNode;
    public static final int minValue = (int) Double.NEGATIVE_INFINITY;

    public FibonacciHeap() {
        this.minNode = null;
        this.size = 0;
        this.markedNodes = 0;
    }

   /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    *   
    */
    public boolean isEmpty() { return minNode == null; }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.  
    * 
    * Returns the newly created node.
    */
    public HeapNode insert(int key) {
        HeapNode newNode = new HeapNode(key);
        size++;
        trees++;
        if (isEmpty()) {
            leftNode = newNode;
            minNode = newNode;
            return newNode;
        }
        if (minNode.getKey() > key) {
            minNode = newNode;
        }
        HeapNode tempLeftPrev = leftNode.prev;
        leftNode.prev = newNode;
        newNode.next = leftNode;
        newNode.prev = tempLeftPrev;
        tempLeftPrev.next = newNode;
        leftNode = newNode;
        return newNode;
    }

   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    *
    */
    public void deleteMin() {
        HeapNode tempChildPrev = minNode.child.prev;
        minNode.child.prev = minNode.prev;
        minNode.child.prev.next = minNode.next;
        tempChildPrev.next = minNode.next;
        minNode.next.prev = tempChildPrev;

        HeapNode x = minNode.child;

        do {
          x.mark = false;
          x.parent = null;
          x = x.next;
        } while (x.getKey() != minNode.child.getKey());

         minNode.child = null;
         consolidate();
    }

    private void consolidate() {
        int ranks = (int) (Math.log(size) / Math.log(2));
        HeapNode[] treeArray = new HeapNode[ranks];

        HeapNode x = leftNode;
        do {
            if (treeArray[x.rank] == null) {
                treeArray[x.rank] = x;
            } else {
                HeapNode y = treeArray[x.rank];
                if (x.getKey() < y.getKey()) {
                /// continue here
                }
            }

        } while (x.getKey() != leftNode.getKey());

    }
    private void Link(HeapNode smaller, HeapNode larger) {
        larger.next.prev = larger.prev;
        larger.prev.next = larger.next;

        HeapNode tempChild = smaller.child;
        smaller.child = larger;
        larger.parent = smaller;
        larger.next = tempChild;
        HeapNode tempChildPrev = tempChild.prev;
        tempChild.prev = larger;
        tempChildPrev.next = larger;
        larger.prev = tempChildPrev;
        smaller.rank++;
    }

   /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    *
    */
    public HeapNode findMin() { return minNode; } //assuming that the heap maintains the heap law
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    *
    */
    public void meld (FibonacciHeap heap2) {
        if (isEmpty()) {
            this.leftNode = heap2.leftNode;
            this.minNode = heap2.minNode;
            this.size = heap2.size;
            this.trees = heap2.trees;
            return;
        }
        if (heap2.isEmpty()) {
            return;
        }
        if (minNode.getKey()>heap2.minNode.getKey()) {
            minNode = heap2.minNode;
        }
        size += heap2.size;
        trees += heap2.trees;
        markedNodes += heap2.markedNodes;

        HeapNode right = leftNode.prev;
        right.next = heap2.leftNode;
        HeapNode tempLeftPrev = heap2.leftNode.prev;
        heap2.leftNode.prev = right;
        leftNode.prev = tempLeftPrev;
        tempLeftPrev.next = leftNode;
    }

   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
    *   
    */
    public int size() { return this.size; }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
    * 
    */
    public int[] countersRep() {
        int counterSize = (int) (Math.log(size) / Math.log(2));
    	int[] counter = new int[counterSize];
        counter[leftNode.rank]++;
        HeapNode x = leftNode.next;
        while (x.getKey() != leftNode.getKey()) {
            counter[x.rank]++;
            x = x.next;
        }
        return counter;
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    *
    */
    public void delete(HeapNode x)
    {
    	decreaseKey(x,minValue);
    	deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta) {
        x.key -= delta;
        if (x.getKey() < minNode.getKey()){
           minNode = x;
        }
        heapifyUp(x);
    }

    private void heapifyUp(HeapNode node) {
        HeapNode currParent = node.parent;
        while (currParent != null && currParent.getKey() > node.getKey()) {
            HeapNode tempParent = currParent.parent;
            HeapNode tempChild = node.child;
            HeapNode tempNext = currParent.next;
            HeapNode tempPrev = currParent.prev;

            currParent.parent = node;
            currParent.child = tempChild;
            tempChild.parent = currParent;
            currParent.next = node.next;
            currParent.prev = node.prev;

            node.parent = tempParent;
            node.child = currParent;
            node.next = tempNext;
            node.prev = tempPrev;

            currParent = node.parent;
        }
    }

//    private void heapifyDown(heapNode node) { //not fully implemented
//        heapNode currChild = node.child;
//        while (currChild == null || currChild.getKey() > currChild.getKey()) {
//            heapNode tempNodeParent = node.parent;
//            heapNode tempChild = currChild.child;
//            heapNode tempNodeNext = node.next;
//            heapNode tempNodePrev = node.prev;
//
//            node.parent = currChild;
//            node.child = tempChild;
//            node.next = currChild.next;
//            node.prev = currChild.prev;
//
//            currChild.parent = tempNodeParent;
//            currChild.child = node;
//            currChild.next = tempNodeNext;
//            currChild.prev = tempNodePrev;
//        }
//    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap. 
    */
    public int potential() { return trees + (2*markedNodes); }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    */
    public static int totalLinks() { return totalLinks;}

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts() { return totalCuts; }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *  
    * ###CRITICAL### : you are NOT allowed to change H. 
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {    
        int[] arr = new int[100];
        return arr; // should be replaced by student code
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in another file. 
    *  
    */
    public static class HeapNode{
    	public int key;
        //public String info;
        public int rank; //numner of children
        public boolean mark;
        public HeapNode child;
        public HeapNode next;
        public HeapNode prev;
        public HeapNode parent;

    	public HeapNode(int key) {
    	    this.key = key;
    	    this.rank = 0;
    	    this.mark = false;
    	    this.next = this;
    	    this.prev = this;
    	    this.parent = null;
    	    this.child = null;
    	}

    	public int getKey() {
    		return this.key;
    	}
    }
}
