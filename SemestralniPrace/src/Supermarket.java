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
	public int[][] poptavka;
	public int[] sklad;
	public int[] potrebujeKoupitMesic;
	
	public Supermarket(int iD, int[][] poptavka, int[] sklad) {
		ID = iD;
		this.poptavka = poptavka;
		this.sklad = sklad;
		this.potrebujeKoupitMesic = potrebujeKoupitM();
	}
	
	private int[] potrebujeKoupitM() {
		int pocetZ = poptavka[0].length;
		int[] pole = new int[pocetZ];
		
		for (int i = 0; i < pole.length; i++) { //pocetZ - sloupce
			for (int j = 0; j < poptavka.length; j++) { //pocetT - radky
				pole[i] += poptavka[j][i];
			}
			pole[i] -= sklad[i];
		}
		return pole;
	}
	
	public int getID() {
		return ID;
	}
	
	public int getPoptavka(int den, int druhZbozi) {
		return poptavka[den][druhZbozi];
	}
	
	@Override
	public String toString() {
		return "Supermarket [ID=S" + ID + ", poptavka=" + Arrays.deepToString(poptavka) + ", sklad=" + Arrays.toString(sklad) + "]";
	}
	
}
