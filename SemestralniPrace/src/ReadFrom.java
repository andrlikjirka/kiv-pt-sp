import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * 
 */

/**
 * Trida zajistujici cteni ze souboru
 * @author Jirka Andrl�k
 */
public class ReadFrom {
	/** Scanner pro cteni ze vstupniho souboru */
	private Scanner vstup;
	
	/**
	 * Konstruktor vytvori novou instanci tridy nacitajici ze souboru 
	 * @param nazevSouboru nazev souboru, ze ktereho bude probihat cteni
	 */
	public ReadFrom(String nazevSouboru) {
		try {
			vstup = new Scanner(Paths.get(nazevSouboru));
		} catch (IOException e) {
			System.err.println("Nelze cist ze souboru: " + nazevSouboru + ". Zkontrolujte nazev souboru.");
			System.out.println("Program ukoncen.");
			System.exit(1);
			
		} catch (NoSuchElementException e) {
			System.err.println("Soubor: " + nazevSouboru + " je prazdny.");
			System.out.println("Program ukoncen.");
			System.exit(1);
			
		} catch (Exception e){
			System.err.println("Doslo k nezname chybe pri cteni souboru: " + nazevSouboru);
			System.out.println("Program ukoncen.");
			System.exit(1);
		}
	}
	
	/**
	 * Metoda zajistujici cteni radek, ignoruje prazdne radky a radky zacinajici #
	 * @return LinkedList<String> spojovy seznam nactenych (vyfiltrovanych radek)
	 */
	public LinkedList<String> nactiData() {
		LinkedList<String> data = new LinkedList<String>(); 
	
		while(vstup.hasNextLine()) {
			String line = vstup.nextLine(); //nacteni radky
			if (line.equals("")) { //ignorovani prazdne radky
				continue;
			}
			if (line.charAt(0)=='#') { //ignorovani radky zacinajici krizkem
				continue;
			}
			data.add(line);	//do seznamu Stringu pridam nactenou radku
		}
		vstup.close();
		return data;
	} 
}
