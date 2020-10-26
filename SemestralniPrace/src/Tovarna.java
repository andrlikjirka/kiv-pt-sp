import java.util.Arrays;

/**
 * 
 */

/**
 * Trida reprezentujici tovarnu
 * @author jandrlik
 */
public class Tovarna {
	private int ID;
	public int[][] produkce;
	
	public Tovarna(int iD, int[][] produkce) {	
		this.ID = iD;
		this.produkce = produkce;
	}
	
	public int getID() {
		return ID;
	}

	public int[][] getProdukce() {
		return produkce;
	}

	@Override
	public String toString() {
		return "Tovarna [ID=D" + ID + ", produkce=" + Arrays.deepToString(produkce) + "]";
	}
	
	
}
