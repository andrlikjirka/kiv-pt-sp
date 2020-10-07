import java.util.Arrays;
import java.util.LinkedList;


/**
 * 
 */

/**
 * 
 * @author jandrlik
 */
public class Main {
	public static int pocetD;
	public static int pocetS;
	public static int pocetZ;
	public static int pocetT;
	
	public static int[][] cenyPrevozu;
	public static int[][] pocZasoby;
	public static int[][] produkceD;
	public static int[][] poptavkaS;
	
	/**
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		File soubor = new File("test_optim_sink.txt");
		inicializaceDat(soubor); //inicializace dat (vybrani-vycisteni dat ze souboru, rozdeleni do konkretnich matic)
		long konec = System.currentTimeMillis();
		System.out.println("cas nacteni a inicializace dat: " + (konec-start) + "ms\n");
		System.out.println("D " + pocetD);
		System.out.println("S " + pocetS);
		System.out.println("Z " + pocetZ);
		System.out.println("T " + pocetT);
		
		System.out.println("c " + Arrays.deepToString(cenyPrevozu));
		System.out.println("q " + Arrays.deepToString(pocZasoby));
		System.out.println("p " + Arrays.deepToString(produkceD));
		System.out.println("r " + Arrays.deepToString(poptavkaS));
		
	}
	
	/**
	 * Metoda rozdeli seznam nactenych dat do danych matic
	 * @param soubor Soubor ze ktereho se nacitaji data
	 */
	public static void inicializaceDat(File soubor) {
		//pole stringu obsahuje vycistena data ze souboru
		//PROC LINKEDLIST (A NE ARRAYLIST)? LINKEDLIST MA OPERACE GETFIRST A REMOVEFIRST V O(1) 
			// ARRAYLIST MA JEN GET A REMOVE V O(n)
		LinkedList<String> nacteni = soubor.nactiData();
		//1. radku vyberu a odeberu (vytvorim z ni pole pro inicialiazci hodnot)
		String[] radkaInicializace = nacteni.getFirst().split(" ");
		nacteni.removeFirst();
		
		int[] inicializace = new int[radkaInicializace.length];
		for (int i = 0; i < radkaInicializace.length; i++){
			inicializace[i] = Integer.parseInt(radkaInicializace[i]); //na danou pozici do pole inicializacnich hodnot vlozim preparsovanou String hodnotu z nacteni
		}
		pocetD = inicializace[0];
		pocetS = inicializace[1];
		pocetZ = inicializace[2];
		pocetT = inicializace[3];
	
		//vytvoreni matic pro data (ceny prevozu, pocatecni zasoby S, produkce D, poptavka S), rozmery dle vstupniho souboru
		cenyPrevozu = new int[pocetD][pocetS];
		pocZasoby = new int[pocetZ][pocetS];
		produkceD = new int[pocetZ*pocetT][pocetD];
		poptavkaS = new int[pocetZ*pocetT][pocetS];
		for (int d = 0; d < pocetD; d ++) { //inicializace pole cen prevozu
			String[] line = nacteni.getFirst().split(" "); //vzdy prvni radku rozdelim do pole Stringu - kazda hodnota je na svem miste v poli
			nacteni.removeFirst();
			for (int s = 0; s < line.length; s++) {
				cenyPrevozu[d][s] = Integer.parseInt(line[s]); //preparsovane hodnoty vlozim do vysledneho intoveho pole cenProvozu
 			}
		}
		for (int z = 0; z < pocetZ; z ++) { //inicializace pole pocatecnich zasob S
			String[] line = nacteni.getFirst().split(" ");
			nacteni.removeFirst();
			for (int s = 0; s < line.length; s++) {
				pocZasoby[z][s] = Integer.parseInt(line[s]);
 			}
		}
		for (int i = 0; i < (pocetZ*pocetT); i++) { //inicializace pole produkce tovaren
			String[] line = nacteni.getFirst().split(" ");
			nacteni.removeFirst();
			for (int j = 0; j < line.length; j++) {
				produkceD[i][j] = Integer.parseInt(line[j]);
			}
		}
		for (int i = 0; i < (pocetZ*pocetT); i++) { //inicializace pole poptavek zakazniku S
			String[] line = nacteni.getFirst().split(" ");
			nacteni.removeFirst();
			for (int j = 0; j < line.length; j++) {
				poptavkaS[i][j] = Integer.parseInt(line[j]);
			}
		}
	}
		
}
