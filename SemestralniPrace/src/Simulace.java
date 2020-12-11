import java.util.Arrays;
import java.util.List;

/**
 * 
 */

/**
 * Trida zajistujici simulaci predikce rozvozu zbozi z D do S
 * @author jandrlik
 * @author kmotycko
 */
public class Simulace {
	/** Instance tridy zajistujici zapis do souboru - prehled tovaren a jejich rozvoz */
	public PrintTo outputTovarny;
	/** Instance tridy zajistujici zapis do souboru  - kazdodenni prehled skladovych zasob */
	public PrintTo outputSklady;
	/** Instance tridy zajistujici zapis do souboru - dovoz z Ciny */
	public PrintTo vystupCina;
	/** Nazev vystupniho souboru - prehled tovaren a rozvozu */
	public static final String NAZEV_PREHLEDU_TOV = "vystup-soubory/prehledTovaren.txt";
	/** Nazev vystupniho souboru - kazdodenni prehled skladovych zasob supermarketu */
	public static final String NAZEV_PREHLEDU_SKLADU = "vystup-soubory/prehledSkladu.txt";
	/** Nazve vystupniho souboru - dovoz z Ciny */
	public static final String NAZEV_VYSTUPU_CINA = "vystup-soubory/vystup-cina.txt";
	
	/** Seznam tovaren */
	public List<Tovarna> tovarny;
	/** Seznam supermarketu */
	public List<Supermarket> supermarkety;
	/** Hodnota poctu tovaren */
	public int pocetD = 0;
	/** Hodnota poctu supermarketu */
	public int pocetS = 0;
	/** Hodnota poctu druhu zbozi */
	public int pocetZ = 0;
	/** Hodnota poctu Dnu */
	public int pocetT = 0;
	/** Matice cen prevozu (mezi D a S) */
	public int[][] cenyPrevozu;
	
	/** Promenna vyjadrujici zda jiz byla v prubehu cyklu uspokojona poptavka supermarketu */
	public boolean uspokojenaPopt = false;
	/** Hodnota celkove ceny prepravy za dane obdobi */
	public int celkovaCena = 0;
	
	//pomocne hodnoty pro kontrolu: celkova poptavka po vsech kusech za cele obdobi = celkem odeslano z tovaren + celkem vzato ze skladu + celkem z Ciny
	/** Pomocna hodnota celkoveho poctu odeslanych ks z tovaren do supermarketu  */
	public int celkemOdeslano = 0;
	/** Pomocna hodnota celk poctu ks vzatych ze skladu supermarketu */
	public int celkemZeSkladu = 0;
	/** Pomocna hodnota celk poctu ks nutnych objednat z Ciny */
	public int celkemZCiny = 0;
	
	/**	Pole retezcu pro vypsani prehledu tovaren */
	public StringBuilder[] prehledTovaren;
	/**	Pole retezcu pro vypsani prehledu skladu supermarketu */
	public StringBuilder[] prehledSkladuSup;
	/** Retezec pro vypsani dovozu z ciny */
	public StringBuilder dovozZciny;
	
	public StringBuilder[] simulace;
	
	/**
	 * Konstruktor vytvori instanci tridy Simulace
	 * @param d Seznam tovaren
	 * @param s Seznam supermarketu
	 * @param c Matice cen prevozu
	 * @param pocetD Hodnota poctu tovaren
	 * @param pocetS Hodnota poctu supermarketu
	 * @param pocetZ Hodnota poctu druhu zbozi
	 * @param pocetT Hodnota poctu dnu
	 */
	public Simulace(List<Tovarna> d, List<Supermarket> s, int[][] c, int pocetD, int pocetS, int pocetZ, int pocetT) {
		this.tovarny = d;
		this.supermarkety = s;
		this.cenyPrevozu = c;
		this.pocetD = pocetD;
		this.pocetS = pocetS;
		this.pocetZ = pocetZ;
		this.pocetT = pocetT;
		outputTovarny = new PrintTo(NAZEV_PREHLEDU_TOV);
		outputSklady = new PrintTo(NAZEV_PREHLEDU_SKLADU);
		vystupCina = new PrintTo(NAZEV_VYSTUPU_CINA);
		prehledTovaren = new StringBuilder[pocetD];
		for (int i = 0; i < prehledTovaren.length; i++) {
		  prehledTovaren[i] = new StringBuilder();
		}
		prehledSkladuSup = new StringBuilder[pocetS];
		for (int i = 0; i < prehledSkladuSup.length; i++) {
			prehledSkladuSup[i] = new StringBuilder();
			prehledSkladuSup[i].append("Poc. zasoby - " + s.get(i).skladToString() + "\n");
		}
		dovozZciny = new StringBuilder();
		simulace = new StringBuilder[pocetT];
		for (int i = 0; i < simulace.length; i++) {
			simulace[i] = new StringBuilder();
		}
	}
	
	/**
	 *  Metoda spousti simulaci
	 * @return celkova cena spocitana behem simulace
	 */
	public int startSimulation() {
		BST bstSup; //strom pro urceni nejvyssich poptavek
		BST bstCenyDS; //strom pro urceni tovaren ze kterych lze dovezt nejlevneji
		int pocetSveStromu = 0; //pocet supermarketu ve stromu
		//stanoveniCelkPopt();
		
		for (int t = 0; t < pocetT; t++) { //cyklus pres vsechny dny
			//System.out.println("T" + (t+1));
			simulace[t].append("T" + (t+1) + "\n");
			if (t >= 1) {
				prepocteniProdukceDalsiDen(tovarny, t); // metoda prepocitani produkce ve vsech tovarnach pro druhy den a vys (pripocitani nevyuzitych ks z predchoziho dne)
			}
	
			for (int z = 0; z < pocetZ; z++) { // cyklus prochazeni druhu zbozi
				//System.out.println("Pro Z" + (z + 1));
				simulace[t].append("Pro Z" + (z + 1) + "\n");
				bstSup = vytvoreniStromuPoptavek(t, z);
				pocetSveStromu = bstSup.getCounterNodes();
				for (int s = 0; s < pocetSveStromu; s++) {		//pro kazdy supermarket potrebuji zjistit poradi tovaren a uspokojit poptavku po zbozi v dany den
					int supermarketSnejPoptID = bstSup.getMaxID();
					uspokojenaPopt = false;	
					
					bstCenyDS = vytvoreniStromuCenDS(supermarketSnejPoptID, t, z);
					hlavniMetodaACyklusUspokojovani(bstCenyDS, supermarketSnejPoptID, t, z);			
					bstSup.removeMax(); //odstraneni supermarketu s nejvyssi poptavkou po jejim uspokojeni
				}
				//System.out.println();
				simulace[t].append("\n");
				bstSup.clearBST();
			}
			zapisDoPrehleduSkladu(t+1); //na konci kazdeho dne zapiseme aktualni stav skladu
			//System.out.println("------------------------");	
			simulace[t].append("------------------------\n");
			System.out.print(simulace[t].toString()); //vypsani prubehu simulace pro den (ze StringBuilderu)
		}
		System.out.println("\nCelkova cena prepravy za cele obdobi = " + celkovaCena);
		vytvoreniPrehleduTovaren();
		vytvoreniPrehleduSkladu();
		vytvoreniVystupuSimulace();
		vytvoreniPrehleduDovozuZciny();
		return celkovaCena;
	}
	
	/**
	 * Hlavni metoda (cyklus) uspokojovani poptavek (uspokojovani pri nedostupnosti tovaren, uspokojovani z tovaren ci skladu - dle ceny cesty, uspokojovani z Ciny)
	 * @param bstCenyDS Predany vytvoreny strom cen cest mezi D a supermarketem s nejvetsi popt
	 * @param supermarketSnejPoptID ID supermarketu s nejvetsi poptavkou
	 * @param den Aktualni den
	 * @param druhZbozi Aktualni druh zbozi
	 */
	public void hlavniMetodaACyklusUspokojovani(BST bstCenyDS, int supermarketSnejPoptID, int den, int druhZbozi) {
		int tovarnaSnejlevCestouID = -1; //vychozi hodnota -1 reprezentuje nedostupnost tovarny
		if ((bstCenyDS.root == null) && (supermarkety.get(supermarketSnejPoptID-1).getSkladoveZasoby(druhZbozi) > 0)) { //pokud nejsou dostupne tovarny pro vytvoreni stromu a supermarket ma na sklade => uspokojeni popt ze skladu
			uspokojeniPoptPriNedostupnostiTovaren(supermarketSnejPoptID, den, druhZbozi);
		} else { //jinak klasicky vybirame nejlevnejsi tovarny ze stromu
			double prumernaCenaDS = prumernaCenaDS(supermarketSnejPoptID); //prumerna cena mezi vybranym supermarketem a vsemi dostupnymi tovarnami
			while (bstCenyDS.root != null) { //prochazi postupne tovarny s nejlevnejsi cestou dokud je nejaka ve stromu	
				tovarnaSnejlevCestouID = bstCenyDS.getMinID();
				//volani metody pro uspokojovani poptavky (parametry - zjisteny S s nejvyssi poptavkou, zjisteny D s nejlevnejsi cestou, cena)
				uspokojeniPoptavky(supermarkety.get(supermarketSnejPoptID-1), tovarny.get(tovarnaSnejlevCestouID-1), den, druhZbozi, cenyPrevozu[tovarnaSnejlevCestouID-1][supermarketSnejPoptID-1], prumernaCenaDS);
				bstCenyDS.removeMin(); //odstraneni tovarny s nejnizsi cestou, opakovani cyklus pro najiti dalsi nejlevnejsi tovarny ze ktere lze dovezt a douspokojt popt
				
				if (Boolean.compare(uspokojenaPopt, true) == 0) {
					bstCenyDS.clearBST(); //pri predcasnem ukonceni cyklu je nutne vycistit strom, aby se pro dalsi supermarket vkladaly tovarny do prazdneho stromu 
					break;
				}
			}

		}
		if (Boolean.compare(uspokojenaPopt, false) == 0) {
			uspokojenizCiny(tovarnaSnejlevCestouID, supermarketSnejPoptID, den, druhZbozi);	//pokud po vypotrebovani dostupnych tovaren stale neni uspokojena popt, nutne objednat z Ciny
			uspokojenaPopt = true;
		}
	}
	
	/**
	 * Metoda vytvori strom poptavek supermarketu
	 * @param den Aktualni den
	 * @param druhZbozi Aktualni druh zbozi
	 * @return Vytvoreny strom poptavek
	 */
	private BST vytvoreniStromuPoptavek(int den, int druhZbozi) {
		BST sup = new BST();
		for (int s = 0; s < supermarkety.size(); s++) { // sestaveni stromu pro urceni nejvyssi poptavky
			int popt = supermarkety.get(s).getPoptavka(den, druhZbozi);
			if (popt > 0) {
				sup.add(popt, supermarkety.get(s).getID()); //do stromu vlozim jen nezaporne poptavky, pokud by S tedy chtel 0ks, simulace s nim vubec nebude pocitat	
			}
		}
		return sup;
	}
	
	/**
	 * Metoda vytvori strom cen cest mezi D a supermarketem s nejvetsi poptavkou
	 * @param supermarketSnejPoptID Aktualni supermarket s nejvetsi poptavkou 
	 * @param den Aktualni den
	 * @param druhZbozi Aktualni druh zbozi
	 * @return Vytvoreny strom cen cest
	 */
	private BST vytvoreniStromuCenDS(int supermarketSnejPoptID, int den, int druhZbozi) {
		BST cenyDS = new BST();
		for (int d = 0; d < pocetD; d++) { // sestaveni stromu pro urceni tovarny s nejlevnejsi cenou
			int cena = cenyPrevozu[d][supermarketSnejPoptID - 1];
			int prod = tovarny.get(d).getProdukce(den, druhZbozi);
			if ((cena > 0) && (prod > 0)) { //do stromu se neprida tovarna s nulovou produkci, nebo ke ktere nevede cesta
				cenyDS.add(cena, d + 1); // klic je cena Prevozu z i-te tovarny do nejvyssiho supermarketu, ID je (d+1)-ta tovarna
			}
		}
		return cenyDS;
	}
	
	/** Metoda zapise do souboru prehled tovaren a rozvozu */
	public void vytvoreniPrehleduTovaren() {
		for (int t = 0; t < prehledTovaren.length; t++) {
			outputTovarny.zapisDoSouboru("Tovarna " + (t+1) + "\n");
			outputTovarny.zapisDoSouboru(prehledTovaren[t].toString());
			outputTovarny.zapisDoSouboru("\nVyprodukovano zbytecne: " + Arrays.toString(tovarny.get(t).getZbytekProdukcePoslDen()) + "\n---\n");
		}
	}
	
	/**
	 * Metoda prida do prehledu stav zasob na skladech na konci kazdeho dne
	 * @param den Den
	 */
	public void zapisDoPrehleduSkladu(int den) {
		for (int s = 0; s < pocetS; s++) {
			prehledSkladuSup[s].append("Na konci " + den + ".dne -" + supermarkety.get(s).skladToString() + "\n");
		}
	}
	
	/** Metoda zapise do souboru kazdodenni rozpis skladovych zasob */
	public void vytvoreniPrehleduSkladu() {
		for (int s = 0; s < prehledSkladuSup.length; s++) {
			outputSklady.zapisDoSouboru("Sklad S" + (s+1) + "\n");
			outputSklady.zapisDoSouboru(prehledSkladuSup[s].toString() + "\n");
		}
	}

	/**	Metoda vypise informace o souhrnnem dovozu zbozi */
	public void vytvoreniVystupuSimulace() {
		System.out.println("\nCelkem ze skladu: " + celkemZeSkladu + "ks");
		System.out.println("Celkem z Ciny: " + celkemZCiny);
		System.out.println("Celkem odeslano z tovaren: " + celkemOdeslano + "ks");
		
	}
	
	/**	Metoda zapise do souboru informace o dovozu z Ciny */
	public void vytvoreniPrehleduDovozuZciny() {
		if (dovozZciny.length() == 0) {
			vystupCina.zapisDoSouboru("Za cele obdobi nebude potreba objednavat zbozi z Ciny.");
		}
		else {
			vystupCina.zapisDoSouboru("V prubehu obdobi bude nutne objednavat zbozi z Ciny: \n");
			vystupCina.zapisDoSouboru(dovozZciny.toString());
		}
	}
	
	/**
	 * Metoda zajistuje uspokojeni poptavky supermarketu, pokud nejsou dostupne tovarny ze kterych lze dovazet a pokud ma supermarket zasoby na sklade
	 * @param supermarketSnejPoptID Aktualni supermarket
	 * @param den Aktualni den
	 * @param druhZbozi Aktualni druh zbozi
	 */
	public void uspokojeniPoptPriNedostupnostiTovaren(int supermarketSnejPoptID, int den, int druhZbozi) {
		zeSkladu(supermarkety.get(supermarketSnejPoptID-1), den, druhZbozi);
	}
	
	/**
	 * Metoda uspokojovani poptavek
	 * @param supermarket Supermarket, jehoz poptavka se uspokojuje 
	 * @param tovarna Tovarna z niz se dovazi zbozi
	 * @param den Den, ve ktery uspokojujem poptavku
	 * @param druhZbozi Druh zbozi
	 * @param cenaDS Cena cesty mezi aktualni tovarnou a supermarketem
	 * @param prumernaCena Prumerna cena cest mezi aktulanim supermarketem a vsemi dostupnymi tovarnami (pro porovnani, zda je aktualni cena cesty draha / levna)
	 */
	public void uspokojeniPoptavky(Supermarket supermarket, Tovarna tovarna, int den, int druhZbozi, int cenaDS, double prumernaCena) {
		int potrebujeKoupitMesic = supermarkety.get(supermarket.getID()-1).getPotrebujeKoupitZaObdobi(druhZbozi); //kolik potrebuje koupit supermarket druhu Z za cele obdobi (celkova poptavka - to co ma na zacatku na sklade)
		if (potrebujeKoupitMesic > 0) {
			if (supermarket.getSkladoveZasoby(druhZbozi) > 0) {
				if (cenaDS < prumernaCena) {
					zTovarny(supermarket, tovarna, den, druhZbozi, cenaDS); //z tovarny (snizit potrebujeKoupitMesic)
				} else { //cenaDS >= prumernaCena
					zeSkladu(supermarket, den, druhZbozi);
					//if (uspokojenaPopt == false) { 
					if (Boolean.compare(uspokojenaPopt, false) == 0) {//pokud po uspokojeni ze skladu stale neni popt S uspokojena, je nutne i za drahou cenu dovezt z tovarny
						zTovarny(supermarket, tovarna, den, druhZbozi, cenaDS);
					}
				}
			} 
			else { // supermarket.sklad[druhZbozi] <= 0
				zTovarny(supermarket, tovarna, den, druhZbozi, cenaDS); //z tovarny (snizit potrebujeKoupitMesic)
			}
		} else { //potrebujeKoupitMesic <= 0
			zeSkladu(supermarket, den, druhZbozi); 	//ze skladu
		}
	}
	
	/**
	 * Metoda uspokojeni poptavky supermarketu z tovarny - urci kolik ks se bude prevazet
	 * @param supermarket Aktualni supermarket, jehoz poptavku uspokojujeme
	 * @param tovarna Aktualni tovarna, ze ktere dovazime
	 * @param den Aktualni den
	 * @param druhZbozi Aktualni druh zbozi ktery prevazime
	 * @param cenaDS Cena cesty mezi D a S
	 */
	private void zTovarny(Supermarket supermarket, Tovarna tovarna, int den, int druhZbozi, int cenaDS) {
		//nutno osetrit situaci kdy ackoliv je cesta levna a tovarna ma dostatecnou produkci, tak supermarket za obdobi potrebuje koupit mene nez je popt. 
		//	(nesmi vse koupit a na sklade zustat vyrobky; musi dobrat to co ma na sklade a koupit jen to co mu zbyva koupit)
		if (supermarket.getPotrebujeKoupitZaObdobi(druhZbozi) < supermarket.getPoptavka(den, druhZbozi)) {
			zTovarnyCast(supermarket, tovarna, den, druhZbozi, cenaDS);
			return;
		}
		int pocet = tovarna.getProdukce(den, druhZbozi) - supermarket.getPoptavka(den, druhZbozi);
		if (pocet >= 0) { //staci produkce tovarny k uspokojeni poptavky
			int odeslanozDdoS = supermarket.getPoptavka(den, druhZbozi);
			uspokojovanizTovarny(supermarket, tovarna, den, druhZbozi, cenaDS, odeslanozDdoS);
			uspokojenaPopt = true;
		} else if (pocet < 0) { // v tovarne nezbylo, poptavka neuspokojena (nutno brat z dalsi tovarny)
			int odeslanozDdoS = tovarna.getProdukce(den, druhZbozi);
			uspokojovanizTovarny(supermarket, tovarna, den, druhZbozi, cenaDS, odeslanozDdoS);
			uspokojenaPopt = false;
		}
	}
	
	/**
	 * Metoda uspokojeni poptavky z casti, kdy supermarket potrebuje koupit za zbytek obdobi mene nez je poptavka pro dany den (zbytek poptavky vezme ze skladu) 
	 * @param supermarket Aktualni supermarket, jehoz poptavku uspokojujeme
	 * @param tovarna  Aktualni tovarna, ze ktere dovazime
	 * @param den Aktualni den
	 * @param druhZbozi Aktualni druh zbozi ktery prevazime 
	 * @param cenaDS Cena cesty mezi D a S
	 */
	private void zTovarnyCast(Supermarket supermarket, Tovarna tovarna, int den, int druhZbozi, int cenaDS) {
		int odeslanozDdoS; 
		if (supermarket.getPotrebujeKoupitZaObdobi(druhZbozi) < tovarna.getProdukce(den, druhZbozi)) {
			odeslanozDdoS = supermarket.getPotrebujeKoupitZaObdobi(druhZbozi);
		}
		else { 
			odeslanozDdoS = tovarna.getProdukce(den, druhZbozi);
		}
		uspokojovanizTovarny(supermarket, tovarna, den, druhZbozi, cenaDS, odeslanozDdoS);
		uspokojenaPopt = false; 
	}
	
	/**
	 * Metoda zajistuje potrebne zmeny atributu a vypis prevozu
	 * @param supermarket Aktualni supermarket
	 * @param tovarna Aktualni tovarna
	 * @param den Aktualni den
	 * @param druhZbozi Aktualni druh zbozi
	 * @param cenaDS Cena cesty mezi D a S
	 * @param odeslanozDdoS Pocet ks zbozi odeslanych mezi D a S
	 */
	private void uspokojovanizTovarny(Supermarket supermarket, Tovarna tovarna, int den, int druhZbozi, int cenaDS, int odeslanozDdoS) {
		celkemOdeslano += odeslanozDdoS;
		int cenaDilci = cenaDS * odeslanozDdoS;
		celkovaCena += cenaDilci;
		tovarna.setSnizeniProdukce(den, druhZbozi, odeslanozDdoS);
		supermarket.setSnizeniPoptavky(den, druhZbozi, odeslanozDdoS);
		//supermarkety.get(supermarket.getID()-1).potrebujeKoupitMesic[druhZbozi] -= odeslanozDdoS;
		supermarkety.get(supermarket.getID()-1).setSnizeniPotrebujeKoupitZaObdobi(druhZbozi, odeslanozDdoS);
		
		//System.out.println("D" + tovarna.getID() + " (nakl.auto) => " + "S" + supermarket.getID() + ": " + odeslanozDdoS + "ks, cena=" + cenaDilci);
		simulace[den].append("D" + tovarna.getID() + " (nakl.auto) => " + "S" + supermarket.getID() + ": " + odeslanozDdoS + "ks, cena=" + cenaDilci + "\n");
		prehledTovaren[tovarna.getID()-1].append((den+1) + ". den - D" + tovarna.getID() + " => " + "S" + supermarket.getID() + ": " + odeslanozDdoS + "ks" + " (Z" + (druhZbozi+1) + "), cena=" + cenaDilci + "\n");
	}
	
	/**
	 * Metoda uspokojeni poptavky spotrebou ks ze skladu supermarketu
	 * @param supermarket Aktualni supermarket
	 * @param den Aktualni den
	 * @param druhZbozi Aktualni druh zbozi 
	 */
	private void zeSkladu(Supermarket supermarket, int den, int druhZbozi) {
		int pocet = supermarket.getSkladoveZasoby(druhZbozi) - supermarket.getPoptavka(den, druhZbozi);
		if (pocet >= 0) { //staci zasoby na sklade k uspokojeni poptavky
			int pouzitoZeSkladu = supermarket.getPoptavka(den, druhZbozi);
			uspokojovanizeSkladu(supermarket, den, druhZbozi, pouzitoZeSkladu);
			uspokojenaPopt = true;
		} else if (pocet < 0) {
			int pouzitoZeSkladu = supermarket.getSkladoveZasoby(druhZbozi);
			uspokojovanizeSkladu(supermarket, den, druhZbozi, pouzitoZeSkladu);
			uspokojenaPopt = false;
		}
	}
	
	/**
	 * Metoda zajistuje potrebne zmeny atributu a vypis spotreby zbozi skladu
	 * @param supermarket Aktualni supermarket
	 * @param den Aktualni den
	 * @param druhZbozi Aktualni druh zbozi
	 * @param pouzitoZeSkladu Pocet ks vzanych ze skladu supermarketu k uspokojeni popt
	 */
	private void uspokojovanizeSkladu(Supermarket supermarket, int den, int druhZbozi, int pouzitoZeSkladu) {
		celkemZeSkladu += pouzitoZeSkladu;
		supermarket.setSnizeniSkladZasob(druhZbozi, pouzitoZeSkladu);
		supermarket.setSnizeniPoptavky(den, druhZbozi, pouzitoZeSkladu); //snizeni poptavky o pocet ks pouzitych ze skladu
		//System.out.println("sklad => " + "S" + supermarket.getID() + ": " + pouzitoZeSkladu + "ks");
		simulace[den].append("sklad => " + "S" + supermarket.getID() + ": " + pouzitoZeSkladu + "ks\n");
	}
	
	/**
	 * Metoda zajistujici upozorneni o nemoznosti uzasobit supermarket a nutnosti objednani zbozi z Ciny 
	 * @param tovarnaID ID aktualni tovarny
	 * @param supermarketID ID aktualniho supermarketu
	 * @param den Aktualni den
	 * @param druhZbozi Aktualni druh zbozi
	 */
	private void uspokojenizCiny(int tovarnaID, int supermarketID, int den, int druhZbozi) {
		celkemZCiny += supermarkety.get(supermarketID-1).getPoptavka(den, druhZbozi);
		if (tovarnaID == (-1)) { //pokud neexistuje tovarna ze ktere by se mohlo dovazet (strom prazdny), take nutne obednat z Ciny, ale neni znamo ve ktere tov vznikl problem
			//System.out.println("T" + (den+1) + ": Nedostupne tovarny - neni mozne uzasobit S" + supermarketID +  ". Nutne objednat " + supermarkety.get(supermarketID-1).getPoptavka(den, druhZbozi) + "ks z Ciny.");
			simulace[den].append("T" + (den+1) + ": Nedostupne tovarny - neni mozne uzasobit S" + supermarketID +  ". Nutne objednat " + supermarkety.get(supermarketID-1).getPoptavka(den, druhZbozi) + "ks z Ciny.\n");
		}
		else {
			//System.out.println("T" + (den+1) + ": D" + tovarnaID + " nemuze uzasobit S" + supermarketID +  ". Nutne objednat " + supermarkety.get(supermarketID-1).getPoptavka(den, druhZbozi) + "ks z Ciny.");
			simulace[den].append("T" + (den+1) + ": D" + tovarnaID + " nemuze uzasobit S" + supermarketID +  ". Nutne objednat " + supermarkety.get(supermarketID-1).getPoptavka(den, druhZbozi) + "ks z Ciny.\n");
			dovozZciny.append("T" + (den+1) + ": D" + tovarnaID + " nemuze uzasobit S" + supermarketID +  ". Nutne objednat " + supermarkety.get(supermarketID-1).getPoptavka(den, druhZbozi) + "ks z Ciny.\n");
		}
	}
	
	/**
	 * Metoda vypocte prumernou cenu prevozu mezi vybranym supermarketem a vsemi tovarnami
	 * @param s ID aktualniho supermarketu
	 * @return prumerna cena prevozu 
	 */
	private double prumernaCenaDS(int s) {
		double sum = 0;
		for (int d = 0; d < cenyPrevozu.length; d++) {
			sum += cenyPrevozu[d][s-1];
		}
		return sum/cenyPrevozu.length;
	}
	
	/**
	 * Metoda zajistuje pripocteni zbylych vyrobku z predchoziho dne k aktualni produkci (umoznuje v simualci uvazovat odesilani i ks zbozi ktere zbyly v tovarnach z predchozich dnu) 
	 * @param tovarnyPrepocteniProd Seznam tovaren
	 * @param den Aktualni den
	 */
	private void prepocteniProdukceDalsiDen(List<Tovarna> tovarnyPrepocteniProd, int den) {
		for (int i = 0; i < tovarnyPrepocteniProd.size(); i++) { //k produkcim kazdeho druhu zbozi v dany den pricist to co zbylo z minuleho dne
			for (int j = 0; j < pocetZ; j++) {
				tovarnyPrepocteniProd.get(i).setZvyseniProdukce(den, j, tovarnyPrepocteniProd.get(i).getProdukce(den-1, j));
			}
		}
	}
	

	/**
	 * Pomocna metoda stanoveni celkove poptavky pro cele obdobi (pro kontrolu s pocty odeslanych vyrobku)
	 * @return hodnota celkove poptavky
	 */
	/*
	private void stanoveniCelkPopt() {
		int celkPopt = 0; 
		for (int i = 0; i < pocetS; i++) {
			Supermarket s = supermarkety.get(i);
			for (int j = 0; j < pocetT; j++) {
				for (int k = 0; k < pocetZ; k++) {
					celkPopt += s.getPoptavka(j, k);					
				}
			}
		}
		System.out.println("celkova popt: " + celkPopt);
	}
	*/
	
}
