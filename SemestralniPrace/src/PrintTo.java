import java.io.IOException;
import java.io.PrintStream;

/**
 * 
 */

/**
 * Trida zajistujici zapis dat do souboru
 * @author jandrlik, kmotycko 
 */
public class PrintTo {
	/**	PrintStream pro zapis vystupu*/
	private PrintStream vystup;
	
	/**
	 * Konstrukor vytvori novou instanci tridy zapisujici do souboru
	 * @param nazevSouboru nazev souboru, do ktereho bude probihat zapis dat
	 */
	public PrintTo(String nazevSouboru) {
		try {
			vystup = new PrintStream(nazevSouboru);
		} catch (IOException e) {
			System.err.println("Nelze zapsat data do souboru: " + nazevSouboru);
		} catch (Exception e) {
			System.err.println("Doslo k nezname chybe pri zapisu do souboru: " + nazevSouboru);
		}
	}
	
	/**
	 * Metoda zajistujici zapis retezce do souboru
	 * @param line String retezec predany k zapsani do souboru
	 */
	public void zapisDoSouboru(String line) {
		vystup.print(line);
	}
}
