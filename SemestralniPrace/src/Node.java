/**
 * 
 */

/**
 * @author Jirka Andrlík
 *
 */
public class Node {
	public int key; //jmeno souboru
	public int ID;
	public Node left, right; //potomci
	
	public Node (int key, int ID) {
		this.key = key;
		this.ID = ID;
	}
}
