/**
 * 
 */

/**
 * Trida reprezentujici binarni vyhledavaci strom
 * @author jandrlik
 * @author kmotycko
 */
public class BST {
	/**	Koren stromu */
	public Node root;
	
	/** Pocitadlo vrcholu */
	private int counterNodes = 0; 

	/**
	 * Getr vrati pocet uzlu stromu
	 * @return
	 */
	public int getCounterNodes() {
		return counterNodes;
	}
	
	/**
	 * Metoda prida do BST uzel s danym klicem a ID
	 * @param key klic
	 * @param ID id (identifikacni cislo supermarketu nebo tovarny)
	 */
	public void add(int key, int ID) {
		counterNodes++;
		if (root == null) {
			root = new Node(key, ID); 
		}
		else {
			addUnder(root, key, ID);
		}
	}

	/**
	 * Metoda vlozi pod uzel n novy uzel s klicem key a ID
	 * @param n vrchol, pod ktery se vlozi novy vrchol
	 * @param key klic
	 * @param ID id (identifikacni cislo supermarketu nebo tovarny)
	 */
	private void addUnder(Node n, int key, int ID) {
		if (key < n.key) {
			// uzel patri doleva, je tam misto?
			if (n.left == null) {
				n.left = new Node(key, ID);
			}
			else {
				addUnder(n.left, key, ID); // debug: novy uzel se vlozi pod n.left
			}
		} else {
			// uzel patri doprava, je tam misto?
			if (n.right == null) {
				n.right = new Node(key, ID);
			}
			else {
				addUnder(n.right, key, ID); // debug: novy uzel se vlozi pod n.right
			}
		}
	}

	/**	Metoda odstrani min uzel */
	public void removeMin() {
		counterNodes--;
		Node n = root; // n = odebirany vrchol
		Node ancestor = null; // predek odebiraneho uzlu
		
		while (n.left != null) {
			ancestor = n;
			n = n.left;
		}
		
		// na konci smycky n ukazuje na vrchol ktery chceme odebrat
		this.remove(n, ancestor);
	}
	
	/**	Metoda odstrani max uzel */
	public void removeMax() {
		counterNodes--;
		Node n = root; // n = odebirany vrchol
		Node ancestor = null; // predek odebiraneho uzlu
		
		while (n.right != null) {
			ancestor = n;
			n = n.right;
		} 
		// na konci smycky n ukazuje na vrchol ktery chceme odebrat
		this.remove(n, ancestor);
	}
	
	/**
	 * Metoda zajistujici odstraneni prvku (spolecna pro removeMax i removeMin)
	 * @param n Odstranovany vrchol
	 * @param ancestor Predek
	 */
	private void remove(Node n, Node ancestor) {
		if ((n.left == null) || (n.right == null)) {
			Node replacement = n.left;
			if (n.right != null) {
				replacement = n.right;
			}
			if (ancestor == null) {
				root = replacement;
			}
			else if (ancestor.left == n) {
				ancestor.left = replacement;
			}
			else {
				ancestor.right = replacement;
			}
		} else {
			Node leftMax = n.left;
			Node leftMaxAncestor = n;
			while (leftMax.right != null) {
				leftMaxAncestor = leftMax;
				leftMax = leftMax.right;
			}
			n.key = leftMax.key;
			n.ID = leftMax.ID;
			if (leftMax != n.left) {
				leftMaxAncestor.right = leftMax.left;
			}
			else {
				n.left = leftMax.left;
			}
		}
	}
	
	/** Metoda vypise serazene vsechny vrcholy stromu  */
	public void printSorted() {
		printSorted(root);
	}
	
	/**
	 * Rekurzivni metoda vypisu od vrcholu n 
	 * @param n vrchol od ktereho je spusteny vypis
	 */
	private void printSorted(Node n) {
		if (n != null) {
			printSorted(n.right);
			System.out.print(n.key + "(" + n.ID + ") ");
			printSorted(n.left);
		}
	}
	
	/**
	 * Metoda najde max uzel ve stromu
	 * @param n uzel ze ktereho zaciname hledat max uzel
	 * @return max uzel stromu
	 */
	private Node getMaxNode(Node n) {
		if (n.right != null) {
			return getMaxNode(n.right);
		}
		else {
			return n;
		}
	}
	
	/**
	 * Metoda vrati ID max uzlu stromu
	 * @return ID max uzlu
	 */
	public int getMaxID() {
		return getMaxNode(root).ID;
	}
	
	/**
	 * Metoda vrati klic max uzlu stromu 
	 * @return klic max uzlu
	 */
	public int getMaxKey() {
		return getMaxNode(root).key;
	}
	
	/**
	 * Metoda najde min uzel ve stromu
	 * @param n uzel ze kterho zaciname hledat min uzel
	 * @return min uzel stromu
	 */
	private Node getMinNode(Node n) {
		if (n.left != null) {
			return getMinNode(n.left);
		} 
		else {
			return n;
		}
	}
	
	/**
	 * Metoda vrati ID min uzlu stromu
	 * @return ID min uzlu 
	 */
	public int getMinID() {
		return getMinNode(root).ID;
	}
	
	/**
	 * Metoda vrati klic min uzlu stromu
	 * @return klic min uzlu
	 */
	public int getMinKey() {
		return getMinNode(root).key;
	}
	
	/** Metoda vycisti strom (odstrani vsechny uzly) */
	public void clearBST() {
		while (root != null) {
			removeMax();
		}
	}
	
}
