import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * 
 */

/**
 * 
 * @author jandrlik
 */
public class Main {
	public final static int POCET_VOLEB_MENU = 2;
	
	public static int pocetD;
	public static int pocetS;
	public static int pocetZ;
	public static int pocetT;
	
	public static int[][] cenyPrevozu;
	public static int[][] pocZasoby;
	public static int[][] produkceD;
	public static int[][] poptavkyS;
	
	public static ArrayList<Tovarna> tovarny;
	public static ArrayList<Supermarket> supermarkety;
	
	public static Scanner user = new Scanner(System.in);
	
	/**
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {
		/*
		System.out.println("LUBOSUV LOGISTICKY SYSTEM\n=========================");
		out: while (true) {
			int volba = start();
			switch (volba)
			{
				case 1:
					break;
					
				case 2:
					System.out.println("_________________________\nKonec programu");
					break out;
			}
			System.out.println();	
		}
		*/
		try { 
			File soubor = new File("vstupni-data/real_large.txt");
			inicializace(soubor); //metoda nacte data ze souboru, inicializuje potrebne parametry (D,S,Z,T) a matice cenyPrevozu, pocZasoby, produkceD, poptavkyS 
			
			long start = System.currentTimeMillis();
			Simulace s = new Simulace(tovarny, supermarkety, cenyPrevozu, pocetD, pocetS, pocetZ, pocetT);
			s.startSimulation(pocetT, pocetZ);
			
			long konec = System.currentTimeMillis();
			//System.out.println("cas nacteni a inicializace dat: " + (konec-start) + "ms\n");
			System.out.println("\ncas simulace: " + (konec-start) + "ms\n");
			
			
		} catch (IOException e) {
			System.err.println("Reading from file failed.");
		} 
	}
	
	
	public static void inicializace(File soubor) throws IOException {
		nacteniDat(soubor); //inicializace dat (vybrani-vycisteni dat ze souboru, rozdeleni do konkretnich matic)
		
		System.out.println("D " + pocetD);
		System.out.println("S " + pocetS);
		System.out.println("Z " + pocetZ);
		System.out.println("T " + pocetT);
		
		System.out.println("\nc " + Arrays.deepToString(cenyPrevozu));
		//System.out.println("q " + Arrays.deepToString(pocZasoby));
		//System.out.println("p " + Arrays.deepToString(produkceD));
		//System.out.println("r " + Arrays.deepToString(poptavkyS));
		System.out.println();
		
		inicializaceTovaren();
		/*
		for (int i = 0; i < tovarny.size(); i++) {
			System.out.println(tovarny.get(i).toString());
		}
		System.out.println();
		*/
		inicializaceSupermarketu();
		/*
		for (int i = 0; i < supermarkety.size(); i++) {
			System.out.println(supermarkety.get(i).toString());
		}
		System.out.println();
		*/
	}
	
	/**
	 * Metoda rozdeli seznam nactenych dat do danych matic
	 * @param soubor Soubor ze ktereho se nacitaji data
	 * @throws IOException 
	 */
	public static void nacteniDat(File soubor) throws IOException {
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
		poptavkyS = new int[pocetZ*pocetT][pocetS];
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
				poptavkyS[i][j] = Integer.parseInt(line[j]);
			}
		}
		nacteni = null;
	}
	
	
	public static void inicializaceTovaren(){
		tovarny = new ArrayList<Tovarna>(pocetD);
		int iDen;
		int iZbozi;

		for (int i = 0; i < pocetD; i++) { //pocetD = produkceD[0].length
			iDen = 0;
			iZbozi = 0;
			int[][] produkceTovarny = new int[pocetT][pocetZ];
			for (int j = 0; j < produkceD.length; j++) { //produkceD.length = pocetZ*pocetT
				if (iDen == pocetT) {
					iDen = 0;
					iZbozi++;
				}
				produkceTovarny[iDen][iZbozi] = produkceD[j][i];
				iDen++;
			}
			Tovarna t = new Tovarna(i+1, produkceTovarny);
			tovarny.add(t);
		}
	}
	
	public static void inicializaceSupermarketu() {
		supermarkety = new ArrayList<Supermarket>(pocetS);
		int iDen;
		int iZbozi;
		for (int i = 0; i < pocetS; i++) {
			iDen = 0;
			iZbozi = 0;
			int[][] poptavkaSupermarketu = new int[pocetT][pocetZ];
			for (int j = 0; j < poptavkyS.length; j++) {
				if (iDen == pocetT) {
					iDen = 0;
					iZbozi++;
				}
				poptavkaSupermarketu[iDen][iZbozi] = poptavkyS[j][i];
				iDen++;
			}
			int[] skladSupermarketu = new int[pocetZ];
			iDen = 0;
			iZbozi = 0;
			for (int j = 0; j < pocZasoby.length; j++) {
				skladSupermarketu[iZbozi] = pocZasoby[j][i];
				iZbozi++;
			}
			Supermarket s = new Supermarket(i+1, poptavkaSupermarketu, skladSupermarketu); //+sklady
			supermarkety.add(s);
		}
		
	}
	
	
	/**
	 * Metoda vypisujici menu
	 * @return String retezec voleb menu
	 */
	public static String menu() {
		String menu = "";
		menu += "Menu\n";
		menu += "[1] Spustit zakladni simulaci";
		menu += "[2] EXIT\n";
		menu += "-------------------------\n";
		menu += "Volba: ";
		return menu;
	}
	
	/**
	 * Uzivatelske zadavani volby menu (osetreni proti jinym formatum nez int)
	 * @return zadane cislo uzivatelem
	 */
	public static int userMenuQuery() {
		int input;
		try{
		    input = Integer.parseInt(user.nextLine());
		} catch(NumberFormatException ex){ 
		    System.err.print("Not a number. ");
		    input = -1;
		}
		return input;
	}
	
	/**
	 * Metoda zajistuje spusteni menu a osetreni vstupu volby
	 * @return platna volba zadana uzivatelem
	 */
	public static int start() {
		String menu = menu();
		System.out.print(menu);
		int volba = userMenuQuery();
		while ((volba < 0) || (volba > POCET_VOLEB_MENU)) {
			System.out.print("Zadejte dostupnou volbu: ");
			volba = userMenuQuery();
		}
		System.out.println();
		return volba;
	}
}
