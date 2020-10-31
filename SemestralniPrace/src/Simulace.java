import java.util.ArrayList;

/**
 * 
 */

/**
 * @author Jirka Andrlík
 *
 */
public class Simulace {
	public PrintTo output;
	
	public ArrayList<Tovarna> tovarny;
	public ArrayList<Supermarket> supermarkety;
	public int pocetD;
	public int pocetS;
	public int pocetZ;
	public int pocetT;
	public int[][] cenyPrevozu;
	
	public boolean uspokojenaPopt = false;
	public int celkovaCena = 0; 
	public int celkemOdeslano = 0;
	public int celkemZeSkladu = 0;
	public int celkemZCiny = 0;
		
	public Simulace(PrintTo vystup, ArrayList<Tovarna> d, ArrayList<Supermarket> s, int[][] c, int pocetD, int pocetS, int pocetZ, int pocetT) {
		this.tovarny = d;
		this.supermarkety = s;
		this.cenyPrevozu = c;
		this.pocetD = pocetD;
		this.pocetS = pocetS;
		this.pocetZ = pocetZ;
		this.pocetT = pocetT;
		this.output = vystup;
	}

	public void startSimulation() {
		BST BSTsup; //strom pro urceni nejvyssich poptavek
		BST BSTcenyD; //strom pro urceni tovaren ze kterych lze dovezt nejlevneji
		
		int pocetSveStromu = 0; //pocet supermarketu ve stromu

		int celkPopt = 0; 
		for (int i = 0; i < pocetS; i++) {
			Supermarket s = supermarkety.get(i);
			for (int j = 0; j < pocetT; j++) {
				for (int k = 0; k < pocetZ; k++) {
					celkPopt += s.poptavka[j][k];					
				}
			}
		}
		System.out.println("celkova popt: " + celkPopt);
		output.zapisDoSouboru("celkova popt: " + celkPopt);
		
		for (int i = 0; i < pocetS; i++) {
			System.out.println(supermarkety.get(i).toString()); //vypis supermerketu pro kontrolu (pred a po simulaci)
		}
		
		//hlavni cyklus simulace
		for (int t = 0; t < pocetT; t++) {
			System.out.println("T" + (t+1));
			output.zapisDoSouboru("T" + (t+1));
			
			// metoda prepocitani produkce ve vsech tovarnach pro druhy den a vys (pripocitani nevyuzitych ks z predchoziho dne)
			if (t >= 1) {
				//prepocteniProdukceDalsiDen(tovarny, t);
			}
			
			for (int z = 0; z < pocetZ; z++) { // prochazeni druhu zbozi
				System.out.println("Pro Z" + (z + 1));
				output.zapisDoSouboru("Pro Z" + (z + 1));
				
				BSTsup = new BST(); // zjistovani poradi poptavek
				BSTcenyD = new BST(); // zjistovani cen mezi D a S
				
				for (int s = 0; s < supermarkety.size(); s++) { // sestaveni stromu pro urceni nejvyssi poptavky
					int popt = supermarkety.get(s).poptavka[t][z];
					if (popt > 0)
						BSTsup.add(popt, supermarkety.get(s).getID()); //do stromu vlozim jen nezaporne poptavky, pokud by S tedy chtel 0ks, simulace s nim vubec nebude pocitat
					pocetSveStromu = BSTsup.getCounterNodes();
				}
						
				for (int s = 0; s < pocetSveStromu; s++) {		//pro kazdy supermarket potrebuji zjistit poradi tovaren a uspokojit poptavku po zbozi v dany den
					uspokojenaPopt = false;			
					for (int d = 0; d < pocetD; d++) { // sestaveni stromu pro urceni tovarny s nejlevnejsi cenou
						int cena = cenyPrevozu[d][BSTsup.getMaxID() - 1];
						int prod = tovarny.get(d).produkce[t][z];
						if ((cena > 0) && (prod > 0)) { //do stromu se neprida tovarna s nulovou produkci, nebo ke ktere nevede cesta
							BSTcenyD.add(cena, d + 1); // klic je cena Prevozu z i-te tovarny do nejvyssiho supermarketu, ID je (d+1)-ta tovarna
						}
					}
					int supermarketSnejPopt = BSTsup.getMaxID();
					//int nejvysiPopt = BSTsup.getMaxKey();
					//System.out.print("S" + supermarketSnejPopt + "(" + nejvysiPopt + ")" + ": ceny cest z tovaren od nejlevnejsi: ");
											
					double prumernaCenaDS = prumernaCenaDS(supermarketSnejPopt);
					
					if ((BSTcenyD.root == null) && (supermarkety.get(supermarketSnejPopt-1).sklad[z] > 0)) { //pokud nejsou dostupne tovarny pro vytvoreni stromu a supermarket ma na sklade => uspokojeni popt ze skladu
						zeSkladu(supermarkety.get(supermarketSnejPopt-1), t, z);
						if (supermarkety.get(supermarketSnejPopt-1).poptavka[t][z] == 0) {
							uspokojenaPopt = true;
						} 
						if (uspokojenaPopt == false)
							uspokojenizCiny(supermarketSnejPopt, t, z);	
						continue;
					}
					
					while (BSTcenyD.root != null) { //vracet tovarnu s nejlevnejsi cestou dokud je nejaka ve stromu	
						int tovarnaSnejlevCestou = BSTcenyD.getMinID();
						//System.out.print("D" + tovarnaSnejlevCestou + " "); // tovarna s nejlevnejsi cestou
						
						//volani metody pro uspokojovani poptavky (parametry - zjisteny S s nejvyssi poptavkou, zjisteny D s nejlevnejsi cestou, cena)
						uspokojeniPoptavky(supermarkety.get(supermarketSnejPopt-1), tovarny.get(tovarnaSnejlevCestou-1), t, z, cenyPrevozu[tovarnaSnejlevCestou-1][supermarketSnejPopt-1], prumernaCenaDS);
								
						BSTcenyD.removeMin(); //odstraneni tovarny s nejnizsi cestou, opakovani cyklus pro najiti dalsi nejlevnejsi tovarny ze ktere lze dovezt a douspokojt popt
						
						if (uspokojenaPopt == true) {
							BSTcenyD.clearBST(); //pri pradcasnem ukonceni cyklu je nutne vycistit strom, aby se pro dalsi supermarket vkladaly tovarny do prazdneho stromu 
							break;
						}
					}
					//System.out.println();
					if (uspokojenaPopt == false) {
						uspokojenizCiny(supermarketSnejPopt, t, z);	
					}
									
					BSTsup.removeMax(); //odstraneni supermarketu s nejvyssi poptavkou po jejim uspokojeni
				}
				System.out.println();
			}
		System.out.println("------------------------");	
		output.zapisDoSouboru("------------------------");
		}
		
		for (int i = 0; i < pocetS; i++) {
			System.out.println(supermarkety.get(i).toString());
		}
		
		System.out.print("\nCelkem ze skladu: " + celkemZeSkladu + "ks");
		System.out.print("\nCelkem z Ciny: " + celkemZCiny);
		System.out.print("\nCelkem odeslano z tovaren: " + celkemOdeslano + "ks");
		System.out.println();
		System.out.println("\nCelkova cena prepravy za cele obdobi = " + celkovaCena);
		output.zapisDoSouboru("\nCelkova cena prepravy za cele obdobi = " + celkovaCena);
	}
		
	public void uspokojeniPoptavky(Supermarket supermarket, Tovarna tovarna, int den, int druhZbozi, int cenaDS, double prumernaCena) {
		int potrebujeKoupitMesic = supermarkety.get(supermarket.getID()-1).potrebujeKoupitMesic[druhZbozi]; //kolik potrebuje koupit supermarket druhu Z za cele obdobi (celkova poptavka - to co ma na zacatku na sklade)
		//System.out.println(potrebujeKoupitMesic);
		if (potrebujeKoupitMesic > 0) {
			if (supermarket.sklad[druhZbozi] > 0) {
				if (cenaDS < prumernaCena) {
					//z tovarny (snizit potrebujeKoupitMesic)
					zTovarny(supermarket, tovarna, den, druhZbozi, cenaDS);
				} else { //cenaDS >= prumernaCena
					zeSkladu(supermarket, den, druhZbozi);
					if (uspokojenaPopt == false) { //pokud po uspokojeni ze skladu stale neni popt S uspokojena, je nutne i za drahou cenu dovezt z tovarny
						zTovarny(supermarket, tovarna, den, druhZbozi, cenaDS);
					}
				}
			} 
			else { // supermarket.sklad[druhZbozi] <= 0
				//z tovarny (snizit potrebujeKoupitMesic)
				zTovarny(supermarket, tovarna, den, druhZbozi, cenaDS);
			}
		} else { //potrebujeKoupitMesic <= 0
			//ze skladu
			zeSkladu(supermarket, den, druhZbozi);
		}
	}
	
	private void zTovarny(Supermarket supermarket, Tovarna tovarna, int den, int druhZbozi, int cenaDS) {
		int pocet = tovarna.produkce[den][druhZbozi] - supermarket.poptavka[den][druhZbozi];
		if (pocet >= 0) { //staci produkce tovarny k uspokojeni poptavky
			int odeslanozDdoS = supermarket.poptavka[den][druhZbozi];
			uspokojovanizTovarny(supermarket, tovarna, den, druhZbozi, cenaDS, odeslanozDdoS);
			uspokojenaPopt = true;
			
		} else if (pocet < 0) {
			// v tovarne nezbylo, poptavka neuspokojena (nutno brat z dalsi tovarny)
			int odeslanozDdoS = tovarna.produkce[den][druhZbozi];
			uspokojovanizTovarny(supermarket, tovarna, den, druhZbozi, cenaDS, odeslanozDdoS);
			uspokojenaPopt = false;
		}
	}
	
	private void uspokojovanizTovarny(Supermarket supermarket, Tovarna tovarna, int den, int druhZbozi, int cenaDS, int odeslanozDdoS) {
		celkemOdeslano += odeslanozDdoS;
		int cenaDilci = cenaDS * odeslanozDdoS;
		celkovaCena += cenaDilci;
		tovarna.produkce[den][druhZbozi] -= odeslanozDdoS;
		supermarket.poptavka[den][druhZbozi] -= odeslanozDdoS;
		supermarkety.get(supermarket.getID()-1).potrebujeKoupitMesic[druhZbozi] -= odeslanozDdoS;
		
		System.out.println("D" + tovarna.getID() + "=>" + "S" + supermarket.getID() + ": " + odeslanozDdoS + "ks, cena=" + cenaDilci);
		output.zapisDoSouboru("D" + tovarna.getID() + "=>" + "S" + supermarket.getID() + ": " + odeslanozDdoS + "ks, cena=" + cenaDilci);
	}
	
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
	
	private void uspokojovanizeSkladu(Supermarket supermarket, int den, int druhZbozi, int pouzitoZeSkladu) {
		celkemZeSkladu += pouzitoZeSkladu;
		supermarket.sklad[druhZbozi] -= pouzitoZeSkladu;
		supermarket.poptavka[den][druhZbozi] -= pouzitoZeSkladu;
		
		System.out.println("sklad=>" + "S" + supermarket.getID() + ": " + pouzitoZeSkladu + "ks");
		output.zapisDoSouboru("sklad=>" + "S" + supermarket.getID() + ": " + pouzitoZeSkladu + "ks");
	}
	
	private double prumernaCenaDS(int s) {
		double sum = 0;
		for (int d = 0; d < cenyPrevozu.length; d++) {
			sum += cenyPrevozu[d][s-1];
		}
		return sum/cenyPrevozu.length;
	}
	
	public void prepocteniProdukceDalsiDen(ArrayList<Tovarna> tovarny, int den) {
		for (int i = 0; i < tovarny.size(); i++) {
			//k produkcim kazdeho druhu zbozi v dany den pricist to co zbylo z minuleho dne
			//Tovarna t = tovarny.get(i);
			//for (int j = 0; j < pocetZ; j++) {
				//t.produkce[den][j] += t.produkce[den-1][j];
			//}
			for (int j = 0; j < pocetZ; j++) {
				tovarny.get(i).produkce[den][j] += tovarny.get(i).produkce[den-1][j];
			}
		}
	}
	
	public void uspokojenizCiny(int supermarketID, int den, int druhZbozi) {
		celkemZCiny += supermarkety.get(supermarketID-1).poptavka[den][druhZbozi];
		
		System.out.println("T" + (den+1) + ": Neni mozne uzasobit S" + supermarketID +  ". Nutne objednat " + supermarkety.get(supermarketID-1).poptavka[den][druhZbozi] + "ks z Ciny.");
		output.zapisDoSouboru("T" + (den+1) + ": Neni mozne uzasobit S" + supermarketID +  ". Nutne objednat " + supermarkety.get(supermarketID-1).poptavka[den][druhZbozi] + "ks z Ciny.");
	}
	
}
