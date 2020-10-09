import java.util.Arrays;

/**
 * 
 */

/**
 * Trida reprezentujici supermarket
 * @author jandrlik
 */
public class Supermarket {
	private int ID;
	private int[][] poptavka;
	public int[][] sklad;
	
	public Supermarket(int iD, int[][] poptavka, int[][] sklad) {
		ID = iD;
		this.poptavka = poptavka;
		this.sklad = sklad;
	}

	public int getID() {
		return ID;
	}

	public int[][] getPoptavka() {
		return poptavka;
	}
	
	@Override
	public String toString() {
		return "Supermarket [ID=S" + ID + ", poptavka=" + Arrays.deepToString(poptavka) + ", sklad=" + Arrays.deepToString(sklad) + "]";
	}
		
}
