import java.util.Arrays;

/**
 * 
 */

/**
 * Trida reprezentujici tovarnu
 * @author jandrlik
 */
public class Tovarna {
	/** Atribut - ID tovarny (identifikator) */
	private int ID;
	/** Atribut - matice produkci (po vsech druzich zbozi ve vsechny dny) */
	public int[][] produkce;
	
	/**
	 *  Konstruktor vytvari instance tridy
	 * @param iD ID tovarny
	 * @param produkce matice produkci 
	 */
	public Tovarna(int iD, int[][] produkce) {	
		this.ID = iD;
		this.produkce = produkce;
	}
	
	/** 
	 * Getr atributu ID instance tovarny
	 * @return ID
	 */
	public int getID() {
		return ID;
	}

	/**
	 *  Getr atributu produkce daneho druhu zbozi v dany den 
	 * @param den Den
	 * @param druhZbozi Druh zbozi
	 * @return hodnota produkce
	 */
	public int getProdukce(int den, int druhZbozi) {
		return produkce[den][druhZbozi];
	}
	
	/** 
	 * Setr snizi hodnotu produkce druhu zbozi v den o urcitou hodnotu (zmenu)
	 * @param den Den
	 * @param druhZbozi Druh zbozi
	 * @param zmena Hodnota o kterou se snizi hodnota produkce
	 */
	public void setSnizeniProdukce(int den, int druhZbozi, int zmena) {
		produkce[den][druhZbozi] -= zmena;
	}
	
	/** Metoda vypise atributy instance tridy Tovarna */
	@Override
	public String toString() {
		return "Tovarna [ID=D" + ID + ", produkce=" + Arrays.deepToString(produkce) + "]";
	}

}
