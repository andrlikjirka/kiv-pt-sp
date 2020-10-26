/**
 * 
 */

/**
 * Trida reprezentujici binarni vyhledavaci strom
 * @author jandrlik
 */
public class BST {
	public Node root; // koren stromu
	private int counterNodes = 0; 

	public int getCounterNodes() {
		return counterNodes;
	}
	
	/**
	 * Metoda prida do BST uzel s danym klicem
	 * @param key klic 
	 */
	public void add(int key, int ID) {
		counterNodes++;
		if (root == null) // debug ==; jedno = je prirazeni, dve == porovnani
			root = new Node(key, ID);
		else
			addUnder(root, key, ID);
	}

	/**
	 * Metoda vlozi pod uzel n novy uzel s klicem key
	 * @param n vrchol, pod ktery se vlozi novy vrchol
	 * @param key klic
	 */
	private void addUnder(Node n, int key, int ID) {
		if (key < n.key) {
			// uzel patri doleva, je tam misto?
			if (n.left == null)
				n.left = new Node(key, ID);
			else
				addUnder(n.left, key, ID); // debug: novy uzel se vlozi pod n.left
		} else {
			// uzel patri doprava, je tam misto?
			if (n.right == null)
				n.right = new Node(key, ID);
			else
				addUnder(n.right, key, ID); // debug: novy uzel se vlozi pod n.right
		}
	}

	
	public void removeMin() {
		remove("min");
	}
	
	public void removeMax() {
		remove("max");
	}
	
	/**
	 * Metoda odebere max vrchol s nejvetsim klicem z BST
	 * @param key klic
	 */
	private void remove(String minMax) {
		counterNodes--;
		Node n = root; // n = odebirany vrchol
		Node ancestor = null; // predek odebiraneho uzlu
		
		if (minMax.equals("max")) {
			while (n.right != null) {
				ancestor = n;
				n = n.right;
			} 
		} else if (minMax.equals("min")) {
			while (n.left != null) {
				ancestor = n;
				n = n.left;
			}
		} else {
			n = null; 
			ancestor = null;
		}
		// na konci smycky n ukazuje na vrchol ktery chceme odebrat
		
		if ((n.left == null) || (n.right == null)) {
			Node replacement = n.left;
			if (n.right != null)
				replacement = n.right;
			if (ancestor == null)
				root = replacement;
			else if (ancestor.left == n)
				ancestor.left = replacement;
			else
				ancestor.right = replacement;
		} else {
			Node leftMax = n.left;
			Node leftMaxAncestor = n;
			while (leftMax.right != null) {
				leftMaxAncestor = leftMax;
				leftMax = leftMax.right;
			}
			n.key = leftMax.key;
			n.ID = leftMax.ID;
			if (leftMax != n.left)
				leftMaxAncestor.right = leftMax.left;
			else
				n.left = leftMax.left;
		}
	}
	
	/**
	 * Metoda vypise vsechny vrcholy stromu (abecedne)
	 */
	public void printSorted() {
		printSorted(root);
	}
	
	private void printSorted(Node n) {
		if (n != null) {
			printSorted(n.right);
			System.out.print(n.key + "(" + n.ID + ") ");
			printSorted(n.left);
		}
	}
	
	public Node getMaxNode(Node n) {
		if (n.right != null) {
			return getMaxNode(n.right);
		}
		else return n;
	}
	
	public int getMaxID() {
		return getMaxNode(root).ID;
	}
	
	public int getMaxKey() {
		return getMaxNode(root).key;
	}
	
	public Node getMinNode(Node n) {
		if (n.left != null) {
			return getMinNode(n.left);
		} 
		else return n;
	}
	
	public int getMinID() {
		return getMinNode(root).ID;
	}
	
	public int getMinKey() {
		return getMinNode(root).key;
	}
}
