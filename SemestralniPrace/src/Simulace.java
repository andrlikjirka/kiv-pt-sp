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
	public int[][] cenyPrevozu;

	public Simulace(ArrayList<Tovarna> d, ArrayList<Supermarket> s, int[][] c) {
		this.tovarny = d;
		this.supermarkety = s;
		this.cenyPrevozu = c;
	}

	public void startSimulation(int pocetDnu, int pocetZbozi) {
		BST BSTsup;
		BST BSTceny;
		System.out.println("T1");
		for (int z = 0; z < pocetZbozi; z++) { // prochazeni druhu zbozi
			System.out.println("Pro Z" + (z + 1) + ": (poptavky serazene od nejvyssi)");
			BSTsup = new BST(); // zjistovani poradi poptavek
			BSTceny = new BST(); // zjistovani cen mezi D a S
			for (int s = 0; s < supermarkety.size(); s++) { // sestaveni stromu pro urceni nejvyssi poptavky
				BSTsup.add(supermarkety.get(s).poptavka[0][z], supermarkety.get(s).ID);
			}

			//BSTsup.printSorted();
			//System.out.println();
			/*
			for (int j = 0; j < supermarkety.size(); j++) {
				System.out.print(BSTsup.getMaxKey() + "(" + BSTsup.getMaxID() + ") ");
				
				BSTsup.removeMax();
			}
			System.out.println();
			*/
			
			for (int j = 0; j < supermarkety.size(); j++) {
				System.out.print("S" + BSTsup.getMaxID() + ": ");

				for (int i = 0; i < cenyPrevozu.length; i++) { // sestaveni stromu pro urceni tovarny s nejlevnejsi
																// cenou
					BSTceny.add(cenyPrevozu[i][BSTsup.getMaxID() - 1], i + 1); // klic je cena Prevozu z i-te tovarny do
																				// nejvyssiho supermarketu, ID je
																				// (i+1)-ta tovarna
				}
				System.out.print("Ceny cest do S" + BSTsup.getMaxID() + " z tovaren od nejlevnejsi: ");
				for (int i = 0; i < cenyPrevozu.length; i++) {
					System.out.print("D" + BSTceny.getMinID() + " "); // tovarna s nejlevnejsi cestou
					BSTceny.removeMin();
				}
				System.out.println();

				BSTsup.removeMax();
			}
			
			System.out.println();

		}

	}
}
