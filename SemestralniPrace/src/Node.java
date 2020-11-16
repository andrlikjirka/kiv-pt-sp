/**
 * 
 */

/**
 * Trida reprezentujici uzel ve stromu
 * @author jandrlik
 * @author kmotycko
 */
public class Node {
	/** Klic uzlu */
	public int key; 
	/** ID uzlu */
	public int ID;
	/** Potomci uzlu (levy a pravy) */
	public Node left, right; 
	
	/**
	 * Konstruktor vytvori novy uzel
	 * @param key klic uzlu
	 * @param ID id uzlu
	 */
	public Node (int key, int ID) {
		this.key = key;
		this.ID = ID;
	}	
}
