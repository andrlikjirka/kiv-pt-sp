import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * 
 */

/**
 * Trida reprezentujici praci se souborem
 * Trida obsahuje metody pro cteni ze souboru a zpais do souboru
 * @author jandrlik
 */
public class File {
	/**	Atribut nazvu souboru */
	private String nazevSouboru;

	/**
	 * Konstruktor 
	 * @param nazevSouboru nazev
	 */
	public File(String nazevSouboru) {
		this.nazevSouboru = nazevSouboru;
	}
	
	/**
	 * Metoda nacitajici a filtrujici vstupni data ze souboru
	 * @return Seznam obsahujici potrebne radky (bez prazdnych a komentaru)
	 */
	public LinkedList<String> nactiData() {
		LinkedList<String> data = new LinkedList<String>(); 
		try (Scanner sc = new Scanner(Paths.get(nazevSouboru))){
			while(sc.hasNextLine()) {
				String line = sc.nextLine(); //nacteni radky
				if (line.equals("")) { //ignorovani prazdne radky
					continue;
				}
				if (line.charAt(0)=='#') { //ignorovani radky zacinajici krizkem
					continue;
				}
				data.add(line);	//do seznamu Stringu pridam nactenou radku
			}
		} catch (IOException e) {
			System.err.println("Reading from file failed.");
		}
		return data;
	}
	
}
