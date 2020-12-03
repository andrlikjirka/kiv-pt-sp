import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * 
 */

/**
 * Hlavni trida programu obsahujici spousteci kod programu
 * @author jandrlik
 * @author kmotycko
 */
public class Main {
	/** Nazev vstupniho souboru */
	public static String NAZEV_VSTUPNIHO_SOUBORU;
	/** Nazev vystupniho souboru - vystup simulace (doba, celk. cena) */
	public static final String NAZEV_VYSTUPU_SIMULACE = "vystup-soubory/vystup-simulace.txt";
	public static final String NAZEV_VYSTUPU_GENERATORU = "vygenerovana-data.txt";
	/** Pocet voleb v menu */
	public final static int POCET_VOLEB_MENU = 3;
	
	/** Pocet tovaren */
	public static int pocetD;
	/** Pocet supermarketu */
	public static int pocetS;
	/** Pocet druhu zbozi */
	public static int pocetZ;
	/** Pocet dnu */
	public static int pocetT;
	/** Matice cen prevozu */
	public static int[][] cenyPrevozu;
	/** Matice pocatecnich zasob */
	public static int[][] pocZasoby;
	/** Matice produkce tovaren */
	public static int[][] produkceD;
	/** Matice poptavek supermarketu */
	public static int[][] poptavkyS;
	
	/** Seznam tovaren */
	public static List<Tovarna> tovarny;
	/** Seznam supermarketu */
	public static List<Supermarket> supermarkety;
	
	/** Scanner pro uzivatelsky vstup */ 
	public static Scanner user;
	/**	Seznam nactenych dat (vyfiltrovanych) */
	public static LinkedList<String> nactenaData;
	/**	Promenna oznacuje stav uspesneho nacteni dat (vstupniho souboru), pokud je true, lze prejit na cteni */
	public static boolean uspesneNacteniDat;
	
	/**
	 * Hlavni metoda (vstupni bod programu)
	 * @param args parametry prikazove radky (nevyuzite)
	 */
	public static void main(String[] args) {
		System.out.println("LUBOSUV LOGISTICKY SYSTEM\n=========================");
		out: while (true) {
			int volba = startVolba();
			switch (volba)
			{
				case 1: //[1] - Generovani vstupnich datasetu
					generator();
					break;
				case 2: //[2] - Spusteni zakladni simulace
					simulace();
					break;
					
				case 3: //[3] - Exit
					System.out.println("_________________________\nProgram ukoncen.");
					break out;
				default:
					System.exit(0);
			}
		}
	}
	
	/**	Metoda nacte od uzivatele potrebne hodnoty a spusti generovani vstupnich datasetu */
	public static void generator() {
		System.out.print("Pocet tovaren: ");
		int d = user.nextInt();
		System.out.print("Pocet supermarketu: ");
		int s = user.nextInt();
		System.out.print("Pocet druhu zbozi: ");
		int z = user.nextInt();
		System.out.print("Pocet dnu: ");
		int t = user.nextInt();
		Generator g = new Generator(d, s, z, t);
		g.generovani(NAZEV_VYSTUPU_GENERATORU);
		System.out.println("Vygenerovana data se nachazi v souboru: " + NAZEV_VYSTUPU_GENERATORU);
		System.out.println("=========================");
	}
	
	/**
	 * Metoda nacte a inicializuje potrebna data a zavola simulaci
	 */
	public static void simulace() {
		uspesneNacteniDat = false;
		while (Boolean.compare(uspesneNacteniDat, false) == 0) {//dokud se nepodari nacist platny soubor, opakuje se cyklus nacitani
			NAZEV_VSTUPNIHO_SOUBORU = zadaniVstupu();
			if (NAZEV_VSTUPNIHO_SOUBORU.equals("")) { //pokud uzivatel zada prazdny nazev, opakuje se dotaz
				continue;
			}
			ReadFrom vstup = new ReadFrom(NAZEV_VSTUPNIHO_SOUBORU);
			nacteni(vstup); //metoda nacte data ze souboru, inicializuje potrebne parametry (D,S,Z,T) a matice cenyPrevozu, pocZasoby, produkceD, poptavkyS 
		}
		System.out.println("Spusteni simulace:");
		long start = System.currentTimeMillis();
		Simulace s = new Simulace(tovarny, supermarkety, cenyPrevozu, pocetD, pocetS, pocetZ, pocetT);
		int celkovaCena = s.startSimulation();
		long konec = System.currentTimeMillis();
		
		PrintTo vystupSimulace = new PrintTo(NAZEV_VYSTUPU_SIMULACE);
		vystupSimulace.zapisDoSouboru("Celkova cena prepravy za cele obdobi = " + celkovaCena);	
		vystupSimulace.zapisDoSouboru("\n\nCas simulace: " + (konec-start) + "ms"); 
		System.out.println("=========================");
	}
	
	/**
	 * Metoda nacte od uzivatele nazev vstupniho souboru
	 * @return nazev vstup souboru
	 */
	private static String zadaniVstupu() {
		System.out.print("Zadejte nazev vstupniho souboru: ");
		String zadanyVstup = user.nextLine();
		return zadanyVstup;
	}
	
	/**
	 * Metoda vola potrebne metody pro nacteni a inicializaci dat
	 * @param soubor Soubor ze ktereho probiha cteni dat
	 */
	public static void nacteni(ReadFrom soubor) {
		//seznam stringu obsahuje vycistena data ze souboru
		nactenaData = soubor.nactiData(); //pokud metoda nactiData vrati null (nepodarilo se nacist ze souboru kvuli IOException), nutno pozadovat znovu zadani platneho nazvu souboru
		if (nactenaData == null) {
			uspesneNacteniDat = false;
			return;
		}
		uspesneNacteniDat = true;
		radkaInicializace(); 	//1. radku vyberu a odeberu (vytvorim z ni pole pro inicialiazci hodnot)		
		rozdeleniDatDoSouhrnMatic();
		inicializaceTovarenSupermarketu(); //rozdeleni do matic poptavek a produkci
	}
	
	/**
	 * Metoda vybere z nactenych dat inicializacni radku s potrebnymi hodnotami (pocetD, pocetS, pocetZ, pocetT)
	 */
	public static void radkaInicializace() {
		String[] radkaInicializace = nactenaData.getFirst().split(" ");
		nactenaData.removeFirst();
		
		int[] inicializace = new int[radkaInicializace.length];
		for (int i = 0; i < radkaInicializace.length; i++){
			inicializace[i] = Integer.parseInt(radkaInicializace[i]); //na danou pozici do pole inicializacnich hodnot vlozim preparsovanou String hodnotu z nacteni
		}
		pocetD = inicializace[0];
		pocetS = inicializace[1];
		pocetZ = inicializace[2];
		pocetT = inicializace[3];
	}
	
	/**
	 * Metoda rozdeli nactena data do souhrnnych matic
	 */
	public static void rozdeleniDatDoSouhrnMatic() {
		try {
			//vytvoreni matic pro data (ceny prevozu, pocatecni zasoby S, produkce D, poptavka S), rozmery dle vstupniho souboru
			cenyPrevozu = new int[pocetD][pocetS];
			pocZasoby = new int[pocetZ][pocetS];
			produkceD = new int[pocetZ*pocetT][pocetD];
			poptavkyS = new int[pocetZ*pocetT][pocetS];
			dataDoSouhrnMatic();
			if (nactenaData.size() != 0) {
				throw new Exception();
			}
		} catch (Exception format) {
			System.err.println("Vstupni data neodpovidaji pozadovanemu formatu.");
			System.out.println("Program ukoncen. Zkontrolujte vstupni soubor. ");
			System.exit(1);
		}
	}
	
	/**	Metoda obsahuje potrebne prikazy pro rozdeleni dat do souhrnnych matic */
	private static void dataDoSouhrnMatic() {
		for (int d = 0; d < pocetD; d ++) { //inicializace pole cen prevozu
			String[] line = nactenaData.getFirst().split(" "); //vzdy prvni radku rozdelim do pole Stringu - kazda hodnota je na svem miste v poli
			nactenaData.removeFirst();
			for (int s = 0; s < line.length; s++) {
				cenyPrevozu[d][s] = Integer.parseInt(line[s]); //preparsovane hodnoty vlozim do vysledneho intoveho pole cenProvozu
 			}
		}
		for (int z = 0; z < pocetZ; z ++) { //inicializace pole pocatecnich zasob S
			String[] line = nactenaData.getFirst().split(" ");
			nactenaData.removeFirst();
			for (int s = 0; s < line.length; s++) {
				pocZasoby[z][s] = Integer.parseInt(line[s]);
 			}
		}
		for (int i = 0; i < (pocetZ*pocetT); i++) { //inicializace pole produkce tovaren
			String[] line = nactenaData.getFirst().split(" ");
			nactenaData.removeFirst();
			for (int j = 0; j < line.length; j++) {
				produkceD[i][j] = Integer.parseInt(line[j]);
			}
		}
		for (int i = 0; i < (pocetZ*pocetT); i++) { //inicializace pole poptavek zakazniku S
			String[] line = nactenaData.getFirst().split(" ");
			nactenaData.removeFirst();
			for (int j = 0; j < line.length; j++) {
				poptavkyS[i][j] = Integer.parseInt(line[j]);
			}
		}
	}
	
	/**	Metoda vola inicializace Tovaren a Supermarketu */
	public static void inicializaceTovarenSupermarketu() {
		inicializaceTovaren();
		inicializaceSupermarketu();
	}
	
	/**
	 * Metoda rozdeli souhrnnou matici produkci tovaren, vytvori instance Tovaren s prislusnymi atributy a vlozi je do seznamu tovaren
	 */
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
	
	/**
	 * Metoda rozdeli souhrnnou matici poptavek a poc sklad. zasob supermarketu, vytvori instance Supermarket s prislusnymi atributy a vlozi je do seznamu S 
	 */
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
	public static StringBuilder menu() {
		StringBuilder menu = new StringBuilder();
		menu.append("Menu\n");
		menu.append("[1] Generovani vstupnich dat\n");
		menu.append("[2] Spustit simulaci\n");
		menu.append("[3] EXIT\n"); 
		menu.append("-------------------------\n");
		menu.append("Volba: ");
		return menu;
	}
	
	/**
	 * Uzivatelske zadavani volby menu (osetreni proti jinym formatum nez int)
	 * @return zadane cislo uzivatelem
	 */
	public static int userMenuQuery() {
		user = new Scanner(System.in);
		int input;
		try{
		    input = Integer.parseInt(user.nextLine());
		} catch(NumberFormatException ex){ 
		    System.err.print("Zadejte cislo. ");
		    input = -1;
		}
		return input;
	}
	
	/**
	 * Metoda zajistuje spusteni menu a osetreni vstupu volby
	 * @return platna volba zadana uzivatelem
	 */
	public static int startVolba() {
		StringBuilder menu = menu();
		System.out.print(menu);
		int volba = userMenuQuery();
		while ((volba <= 0) || (volba > POCET_VOLEB_MENU)) {
			System.out.print("Zadejte dostupnou volbu: ");
			volba = userMenuQuery();
		}
		System.out.println();
		return volba;
	}
}
