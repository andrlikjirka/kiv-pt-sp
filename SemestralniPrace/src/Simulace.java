import java.util.ArrayList;
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
	/** Instance tridy zajistujici zapis do souboru - informace k vystupu simulace */
	public PrintTo vystupSimulace;
	/** Instance tridy zajistujici zapis do souboru - dovoz z Ciny */
	public PrintTo vystupCina;
	
	/** Seznam tovaren */
	public List<Tovarna> tovarny;
	
	/** Seznam supermarketu */
	public ArrayList<Supermarket> supermarkety;
	/** Hodnota poctu tovaren */
	public int pocetD;
	/** Hodnota poctu supermarketu */
	public int pocetS;
	/** Hodnota poctu druhu zbozi */
	public int pocetZ;
	/** Hodnota poctu Dnu */
	public int pocetT;
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
	
	public StringBuilder[] prehledTovaren;	
	public StringBuilder[] prehledSkladuSup;
	public StringBuilder dovozZciny;
	
	/**
	 * Konstruktor vytvori instanci tridy Simulace
	 * @param outputTovarny Soubor do ktereho se zapise prehled tovaren
	 * @param d Seznam tovaren
	 * @param s Seznam supermarketu
	 * @param c Matice cen prevozu
	 * @param pocetD Hodnota poctu tovaren
	 * @param pocetS Hodnota poctu supermarketu
	 * @param pocetZ Hodnota poctu druhu zbozi
	 * @param pocetT Hodnota poctu dnu
	 */
	public Simulace(PrintTo outputTovarny, PrintTo outputSklady, PrintTo vystupSim, PrintTo vystupCina, ArrayList<Tovarna> d, ArrayList<Supermarket> s, int[][] c, int pocetD, int pocetS, int pocetZ, int pocetT) {
		this.tovarny = d;
		this.supermarkety = s;
		this.cenyPrevozu = c;
		this.pocetD = pocetD;
		this.pocetS = pocetS;
		this.pocetZ = pocetZ;
		this.pocetT = pocetT;
		this.outputTovarny = outputTovarny;
		this.outputSklady = outputSklady;
		this.vystupSimulace = vystupSim;
		this.vystupCina = vystupCina;
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
	}
	
	/**
	 * Metoda spousti simulaci
	 */
	public void startSimulation() {
		BST BSTsup = new BST(); //strom pro urceni nejvyssich poptavek
		BST BSTcenyD = new BST(); //strom pro urceni tovaren ze kterych lze dovezt nejlevneji
		int pocetSveStromu = 0; //pocet supermarketu ve stromu
		stanoveniCelkPopt();
		
		//hlavni cyklus simulace
		for (int t = 0; t < pocetT; t++) { //cyklus pres vsechny dny
			System.out.println("T" + (t+1));
			// metoda prepocitani produkce ve vsech tovarnach pro druhy den a vys (pripocitani nevyuzitych ks z predchoziho dne)
			if (t >= 1) {
				prepocteniProdukceDalsiDen(tovarny, t);
			}
			
			for (int z = 0; z < pocetZ; z++) { // cyklus prochazeni druhu zbozi
				System.out.println("Pro Z" + (z + 1));
			
				for (int s = 0; s < supermarkety.size(); s++) { // sestaveni stromu pro urceni nejvyssi poptavky
					int popt = supermarkety.get(s).poptavka[t][z];
					if (popt > 0)
						BSTsup.add(popt, supermarkety.get(s).getID()); //do stromu vlozim jen nezaporne poptavky, pokud by S tedy chtel 0ks, simulace s nim vubec nebude pocitat	
				}
				pocetSveStromu = BSTsup.getCounterNodes();

				for (int s = 0; s < pocetSveStromu; s++) {		//pro kazdy supermarket potrebuji zjistit poradi tovaren a uspokojit poptavku po zbozi v dany den
					uspokojenaPopt = false;			
					int supermarketSnejPoptID = BSTsup.getMaxID();
					
					for (int d = 0; d < pocetD; d++) { // sestaveni stromu pro urceni tovarny s nejlevnejsi cenou
						int cena = cenyPrevozu[d][supermarketSnejPoptID - 1];
						int prod = tovarny.get(d).produkce[t][z];
						if ((cena > 0) && (prod > 0)) { //do stromu se neprida tovarna s nulovou produkci, nebo ke ktere nevede cesta
							BSTcenyD.add(cena, d + 1); // klic je cena Prevozu z i-te tovarny do nejvyssiho supermarketu, ID je (d+1)-ta tovarna
						}
					}
					
					if ((BSTcenyD.root == null) && (supermarkety.get(supermarketSnejPoptID-1).sklad[z] > 0)) { //pokud nejsou dostupne tovarny pro vytvoreni stromu a supermarket ma na sklade => uspokojeni popt ze skladu
						zeSkladu(supermarkety.get(supermarketSnejPoptID-1), t, z);
						if (supermarkety.get(supermarketSnejPoptID-1).poptavka[t][z] == 0) {
							uspokojenaPopt = true;
						} 
						if (uspokojenaPopt == false)
							uspokojenizCiny(-1, supermarketSnejPoptID, t, z);	//pokud po odeslani ks ze skladu stale neni popt uspokojena, nutno objednat z Ciny
						continue;
					}
					
					double prumernaCenaDS = prumernaCenaDS(supermarketSnejPoptID); //prumerna cena mezi vybranym supermarketem a vsemi dostupnymi tovarnami
					int tovarnaSnejlevCestouID = 0;
					while (BSTcenyD.root != null) { //prochazi postupne tovarny s nejlevnejsi cestou dokud je nejaka ve stromu	
						tovarnaSnejlevCestouID = BSTcenyD.getMinID();
						//volani metody pro uspokojovani poptavky (parametry - zjisteny S s nejvyssi poptavkou, zjisteny D s nejlevnejsi cestou, cena)
						uspokojeniPoptavky(supermarkety.get(supermarketSnejPoptID-1), tovarny.get(tovarnaSnejlevCestouID-1), t, z, cenyPrevozu[tovarnaSnejlevCestouID-1][supermarketSnejPoptID-1], prumernaCenaDS);
								
						BSTcenyD.removeMin(); //odstraneni tovarny s nejnizsi cestou, opakovani cyklus pro najiti dalsi nejlevnejsi tovarny ze ktere lze dovezt a douspokojt popt
						
						if (uspokojenaPopt == true) {
							BSTcenyD.clearBST(); //pri predcasnem ukonceni cyklu je nutne vycistit strom, aby se pro dalsi supermarket vkladaly tovarny do prazdneho stromu 
							break;
						}
					}

					if (uspokojenaPopt == false) {
						uspokojenizCiny(tovarnaSnejlevCestouID, supermarketSnejPoptID, t, z);	//pokud po vypotrebovani dostupnych tovaren stale neni uspokojena popt, nutne objednat z Ciny
					}				
					BSTsup.removeMax(); //odstraneni supermarketu s nejvyssi poptavkou po jejim uspokojeni
				}
				System.out.println();
				BSTsup.clearBST();
			}
			zapisDoPrehleduSkladu(t+1); //na konci kazdeho dne zapiseme aktualni stav skladu
			
			System.out.println("------------------------");	
		}
		System.out.println("\nCelkova cena prepravy za cele obdobi = " + celkovaCena);
		vytvoreniPrehleduTovaren();
		vytvoreniPrehleduSkladu();
		vytvoreniVystupuSimulace();
		vytvoreniPrehleduDovozuZciny();
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

	/**	Metoda zapise do souboru informace k vystupu simulace */
	public void vytvoreniVystupuSimulace() {
		vystupSimulace.zapisDoSouboru("\nCelkem ze skladu: " + celkemZeSkladu + "ks");
		vystupSimulace.zapisDoSouboru("\nCelkem z Ciny: " + celkemZCiny);
		vystupSimulace.zapisDoSouboru("\nCelkem odeslano z tovaren: " + celkemOdeslano + "ks");
		vystupSimulace.zapisDoSouboru("\n\nCelkova cena prepravy za cele obdobi = " + celkovaCena);
	}
	
	/**	Metoda zapise do souboru informace o dovozu z Ciny */
	public void vytvoreniPrehleduDovozuZciny() {
		if (dovozZciny.length() == 0)
			vystupCina.zapisDoSouboru("Za cele obdobi nebude potreba objednavat zbozi z Ciny.");
		else {
			vystupCina.zapisDoSouboru("V prubehu obdobi bude nutne objednavat zbozi z Ciny: \n");
			vystupCina.zapisDoSouboru(dovozZciny.toString());
		}
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
		int potrebujeKoupitMesic = supermarkety.get(supermarket.getID()-1).potrebujeKoupitMesic[druhZbozi]; //kolik potrebuje koupit supermarket druhu Z za cele obdobi (celkova poptavka - to co ma na zacatku na sklade)
		if (potrebujeKoupitMesic > 0) {
			if (supermarket.sklad[druhZbozi] > 0) {
				if (cenaDS < prumernaCena) {
					zTovarny(supermarket, tovarna, den, druhZbozi, cenaDS); //z tovarny (snizit potrebujeKoupitMesic)
				} else { //cenaDS >= prumernaCena
					zeSkladu(supermarket, den, druhZbozi);
					if (uspokojenaPopt == false) { //pokud po uspokojeni ze skladu stale neni popt S uspokojena, je nutne i za drahou cenu dovezt z tovarny
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
		if (supermarket.potrebujeKoupitMesic[druhZbozi] < supermarket.poptavka[den][druhZbozi]) {
			zTovarnyCast(supermarket, tovarna, den, druhZbozi, cenaDS);
			return;
		}
		int pocet = tovarna.produkce[den][druhZbozi] - supermarket.poptavka[den][druhZbozi];
		if (pocet >= 0) { //staci produkce tovarny k uspokojeni poptavky
			int odeslanozDdoS = supermarket.poptavka[den][druhZbozi];
			uspokojovanizTovarny(supermarket, tovarna, den, druhZbozi, cenaDS, odeslanozDdoS);
			uspokojenaPopt = true;
		} else if (pocet < 0) { // v tovarne nezbylo, poptavka neuspokojena (nutno brat z dalsi tovarny)
			int odeslanozDdoS = tovarna.produkce[den][druhZbozi];
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
		if (supermarket.potrebujeKoupitMesic[druhZbozi] < tovarna.produkce[den][druhZbozi])
			odeslanozDdoS = supermarket.potrebujeKoupitMesic[druhZbozi];
		else 
			odeslanozDdoS = tovarna.produkce[den][druhZbozi];
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
		tovarna.produkce[den][druhZbozi] -= odeslanozDdoS;
		supermarket.poptavka[den][druhZbozi] -= odeslanozDdoS;
		supermarkety.get(supermarket.getID()-1).potrebujeKoupitMesic[druhZbozi] -= odeslanozDdoS;
		
		System.out.println("D" + tovarna.getID() + " (nakl.auto) => " + "S" + supermarket.getID() + ": " + odeslanozDdoS + "ks, cena=" + cenaDilci);
		prehledTovaren[tovarna.getID()-1].append((den+1) + ". den - D" + tovarna.getID() + " => " + "S" + supermarket.getID() + ": " + odeslanozDdoS + "ks" + " (Z" + (druhZbozi+1) + "), cena=" + cenaDilci + "\n");
	}
	
	/**
	 * Metoda uspokojeni poptavky spotrebou ks ze skladu supermarketu
	 * @param supermarket Aktualni supermarket
	 * @param den Aktualni den
	 * @param druhZbozi Aktualni druh zbozi 
	 */
	private void zeSkladu(Supermarket supermarket, int den, int druhZbozi) {
		int pocet = supermarket.sklad[druhZbozi] - supermarket.poptavka[den][druhZbozi];
		if (pocet >= 0) { //staci zasoby na sklade k uspokojeni poptavky
			int pouzitoZeSkladu = supermarket.poptavka[den][druhZbozi];
			uspokojovanizeSkladu(supermarket, den, druhZbozi, pouzitoZeSkladu);
			uspokojenaPopt = true;
		} else if (pocet < 0) {
			int pouzitoZeSkladu = supermarket.sklad[druhZbozi];
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
		supermarket.sklad[druhZbozi] -= pouzitoZeSkladu;
		supermarket.poptavka[den][druhZbozi] -= pouzitoZeSkladu;
		System.out.println("sklad => " + "S" + supermarket.getID() + ": " + pouzitoZeSkladu + "ks");
	}
	
	/**
	 * Metoda zajistujici upozorneni o nemoznosti uzasobit supermarket a nutnosti objednani zbozi z Ciny 
	 * @param tovarnaID ID aktualni tovarny
	 * @param supermarketID ID aktualniho supermarketu
	 * @param den Aktualni den
	 * @param druhZbozi Aktualni druh zbozi
	 */
	public void uspokojenizCiny(int tovarnaID, int supermarketID, int den, int druhZbozi) {
		celkemZCiny += supermarkety.get(supermarketID-1).poptavka[den][druhZbozi];
		if (tovarnaID == (-1)) //pokud neexistuje tovarna ze ktere by se mohlo dovazet (strom prazdny), take nutne obednat z Ciny, ale neni znamo ve ktere tov vznikl problem
			System.out.println("T" + (den+1) + ": Neni mozne uzasobit S" + supermarketID +  ". Nutne objednat " + supermarkety.get(supermarketID-1).poptavka[den][druhZbozi] + "ks z Ciny.");
		else {
			System.out.println("T" + (den+1) + ": D" + tovarnaID + " nemuze uzasobit S" + supermarketID +  ". Nutne objednat " + supermarkety.get(supermarketID-1).poptavka[den][druhZbozi] + "ks z Ciny.");
			dovozZciny.append("T" + (den+1) + ": D" + tovarnaID + " nemuze uzasobit S" + supermarketID +  ". Nutne objednat " + supermarkety.get(supermarketID-1).poptavka[den][druhZbozi] + "ks z Ciny.\n");
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
	 * Pomocna metoda stanoveni celkove poptavky pro cele obdobi (pro kontrolu s pocty odeslanych vyrobku)
	 * @return hodnota celkove poptavky
	 */
	public void stanoveniCelkPopt() {
		int celkPopt = 0; 
		for (int i = 0; i < pocetS; i++) {
			Supermarket s = supermarkety.get(i);
			for (int j = 0; j < pocetT; j++) {
				for (int k = 0; k < pocetZ; k++) {
					celkPopt += s.poptavka[j][k];					
				}
			}
		}
		vystupSimulace.zapisDoSouboru("celkova popt: " + celkPopt);
	}
	
	/**
	 * Metoda zajistuje pripocteni zbylych vyrobku z predchoziho dne k aktualni produkci (umoznuje v simualci uvazovat odesilani i ks zbozi ktere zbyly v tovarnach z predchozich dnu) 
	 * @param tovarny2 Seznam tovaren
	 * @param den Aktualni den
	 */
	public void prepocteniProdukceDalsiDen(List<Tovarna> tovarny2, int den) {
		for (int i = 0; i < tovarny2.size(); i++) { //k produkcim kazdeho druhu zbozi v dany den pricist to co zbylo z minuleho dne
			for (int j = 0; j < pocetZ; j++) {
				tovarny2.get(i).produkce[den][j] += tovarny2.get(i).produkce[den-1][j];
			}
		}
	}
	
}
