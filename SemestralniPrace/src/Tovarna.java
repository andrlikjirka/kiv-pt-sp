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

	public int getProdukce(int den, int druhZbozi) {
		return produkce[den][druhZbozi];
	}
	
	public void setProdukce(int den, int druhZbozi, int p) {
		produkce[den][druhZbozi] = p;
	}
	
	@Override
	public String toString() {
		return "Tovarna [ID=D" + ID + ", produkce=" + Arrays.deepToString(produkce) + "]";
	}

}
