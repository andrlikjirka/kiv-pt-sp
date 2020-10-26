import java.util.ArrayList;

/**
 * 
 */

/**
 * @author Jirka Andrlík
 *
 */
public class Simulace {
	public ArrayList<Tovarna> tovarny;
	public ArrayList<Supermarket> supermarkety;
	public int pocetD;
	public int pocetS;
	public int pocetZ;
	public int pocetT;
	public int[][] cenyPrevozu;
	
	public boolean uspokojenaPopt = false;
	public int celkovaCena = 0; 

	public Simulace(ArrayList<Tovarna> d, ArrayList<Supermarket> s, int[][] c, int pocetD, int pocetS, int pocetZ, int pocetT) {
		this.tovarny = d;
		this.supermarkety = s;
		this.cenyPrevozu = c;
		this.pocetD = pocetD;
		this.pocetS = pocetS;
		this.pocetZ = pocetZ;
		this.pocetT = pocetT;
	}

	public void startSimulation(int pocetDnu, int pocetZbozi) {
		BST BSTsup; //strom pro urceni nejvyssich poptavek
		BST BSTcenyD; //strom pro urceni tovaren ze kterych lze dovezt nejlevneji
		
		int pocetSveStromu = 0;
		
		System.out.println("T1");
		for (int z = 0; z < pocetZbozi; z++) { // prochazeni druhu zbozi
			System.out.println("Pro Z" + (z + 1));
			BSTsup = new BST(); // zjistovani poradi poptavek
			BSTcenyD = new BST(); // zjistovani cen mezi D a S
			for (int s = 0; s < supermarkety.size(); s++) { // sestaveni stromu pro urceni nejvyssi poptavky
				int popt = supermarkety.get(s).poptavka[0][z];
				if (popt > 0)
					BSTsup.add(popt, supermarkety.get(s).getID()); //do stromu vlozim jen nezaporne poptavky, pokud by S tedy chtel 0ks, simulace s nim vubec nebude pocitat
				pocetSveStromu = BSTsup.getCounterNodes();
			}
					
			for (int s = 0; s < pocetSveStromu; s++) {		//pro kazdy supermarket potrebuji zjistit poradi tovaren a uspokojit poptavku po zbozi v dany den
				uspokojenaPopt = false;			
				for (int d = 0; d < cenyPrevozu.length; d++) { // sestaveni stromu pro urceni tovarny s nejlevnejsi cenou
					int cena = cenyPrevozu[d][BSTsup.getMaxID() - 1];
					if ((cena > 0) && (tovarny.get(d).produkce[0][z] > 0)) //do stromu se neprida tovarna s nulovou produkci, nebo ke ktere nevede cesta
						BSTcenyD.add(cena, d + 1); // klic je cena Prevozu z i-te tovarny do nejvyssiho supermarketu, ID je (d+1)-ta tovarna
				}
				int supermarketSnejPopt = BSTsup.getMaxID();
				//int nejvysiPopt = BSTsup.getMaxKey();
				//System.out.print("S" + supermarketSnejPopt + "(" + nejvysiPopt + ")" + ": ceny cest z tovaren od nejlevnejsi: ");
										
				double prumernaCenaDS = prumernaCenaDS(supermarketSnejPopt);
				
				while (BSTcenyD.root != null) { //vracet tovarnu s nejlevnejsi cestou dokud je nejaka ve stromu	
					int tovarnaSnejlevCestou = BSTcenyD.getMinID();
					//System.out.print("D" + tovarnaSnejlevCestou + " "); // tovarna s nejlevnejsi cestou
					
					//volani metody pro uspokojovani poptavky (parametry - zjisteny S s nejvyssi poptavkou, zjisteny D s nejlevnejsi cestou, cena)
					uspokojeniPoptavky(supermarkety.get(supermarketSnejPopt-1), tovarny.get(tovarnaSnejlevCestou-1), 0, z, cenyPrevozu[tovarnaSnejlevCestou-1][supermarketSnejPopt-1], prumernaCenaDS);
							
					BSTcenyD.removeMin(); //odstraneni tovarny s nejnizsi cestou, opakovani cyklus pro najiti dalsi nejlevnejsi tovarny ze ktere lze dovezt a douspokojt popt
					
					if (uspokojenaPopt == true) {
						break;
					}
				}
				//System.out.println();
				if (uspokojenaPopt == false) {
					System.out.println("Neni mozne uzasobit S" + supermarketSnejPopt +  ". Nutne objednat " + supermarkety.get(supermarketSnejPopt-1).poptavka[0][z] + "ks z Ciny.");
				}
				
				BSTsup.removeMax(); //odstraneni supermarketu s nejvyssi poptavkou po jejim uspokojeni
			}
			System.out.println();
		}
		System.out.println("\nCelkova cena prepravy za cele obdobi = " + celkovaCena);	
	}
		
	public void uspokojeniPoptavky(Supermarket supermarket, Tovarna tovarna, int den, int druhZbozi, int cenaDS, double prumernaCena) {
		int potrebujeKoupitMesic = supermarkety.get(supermarket.getID()-1).potrebujeKoupitMesic[druhZbozi]; //kolik potrebuje koupit supermarket druhu Z za cele obdobi (celkova poptavka - to co ma na zacatku na sklade)
		//System.out.println(potrebujeKoupitMesic);
		if (potrebujeKoupitMesic > 0) {
			if (supermarket.sklad[druhZbozi] > 0) {
				if (cenaDS < prumernaCena) {
					//z tovarny (snizit potrebujeKoupitMesic)
					zTovarny(supermarket, tovarna, den, druhZbozi, cenaDS, potrebujeKoupitMesic, prumernaCena);
				} else { //cenaDS >= prumernaCena
					zeSkladu(supermarket, tovarna, den, druhZbozi, cenaDS, potrebujeKoupitMesic, prumernaCena);
					if (uspokojenaPopt == false) { //pokud po uspokojeni ze skladu stale neni popt S uspokojena, je nutne i za drahou cenu dovezt z tovarny
						zTovarny(supermarket, tovarna, den, druhZbozi, cenaDS, potrebujeKoupitMesic, prumernaCena);
					}
				}
			} 
			else { // supermarket.sklad[druhZbozi] <= 0
				//z tovarny (snizit potrebujeKoupitMesic)
				zTovarny(supermarket, tovarna, den, druhZbozi, cenaDS, potrebujeKoupitMesic, prumernaCena);
			}
		} else { //potrebujeKoupitMesic <= 0
			//ze skladu
			zeSkladu(supermarket, tovarna, den, druhZbozi, cenaDS, potrebujeKoupitMesic, prumernaCena);
	
		}
	}
	
	private void zTovarny(Supermarket supermarket, Tovarna tovarna, int den, int druhZbozi, int cenaDS, int potrebujKoupitMesic, double prumernaCena) {
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
		int cenaDilci = cenaDS * odeslanozDdoS;
		celkovaCena += cenaDilci;
		tovarna.produkce[den][druhZbozi] -= odeslanozDdoS;
		supermarket.poptavka[den][druhZbozi] -= odeslanozDdoS;
		supermarkety.get(supermarket.getID()-1).potrebujeKoupitMesic[druhZbozi] -= odeslanozDdoS;
		System.out.println("D" + tovarna.getID() + "=>" + "S" + supermarket.getID() + ": " + odeslanozDdoS + "ks, cena=" + cenaDilci);
	}
	
	private void zeSkladu(Supermarket supermarket, Tovarna tovarna, int den, int druhZbozi, int cenaDS, int potrebujKoupitMesic, double prumernaCena) {
		int pocet = supermarket.sklad[druhZbozi] - supermarket.poptavka[den][druhZbozi];
		if (pocet >= 0) { //staci zasoby na sklade k uspokojeni poptavky
			int pouzitoZeSkladu = supermarket.poptavka[den][druhZbozi];
			uspokojovanizeSkladu(supermarket, tovarna, den, druhZbozi, cenaDS, pouzitoZeSkladu);
			uspokojenaPopt = true;
		} else if (pocet < 0) {
			int pouzitoZeSkladu = supermarket.sklad[druhZbozi];
			uspokojovanizeSkladu(supermarket, tovarna, den, druhZbozi, cenaDS, pouzitoZeSkladu);
			uspokojenaPopt = false;
		}
	}
	
	private void uspokojovanizeSkladu(Supermarket supermarket, Tovarna tovarna, int den, int druhZbozi, int cenaDS, int pouzitoZeSkladu) {
		supermarket.sklad[druhZbozi] -= pouzitoZeSkladu;
		supermarket.poptavka[den][druhZbozi] -= pouzitoZeSkladu;
		System.out.println("sklad=>" + "S" + supermarket.getID() + ": " + pouzitoZeSkladu + "ks");
	}
	
	private double prumernaCenaDS(int s) {
		double sum = 0;
		for (int d = 0; d < cenyPrevozu.length; d++) {
			sum += cenyPrevozu[d][s-1];
		}
		return sum/cenyPrevozu.length;
	}
	
}
