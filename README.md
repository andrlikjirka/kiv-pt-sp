# SP-PT
Semestrální práce z předmětu KIV-PT

#### Zadání semestrální práce: 
Na základě vstupních dat (produkce továren, plánované odběry supermarketů, ceny cest mezi továrnami a supermarkety) naprogramujte aplikaci zajišťující naplánování tras rozvozu požadovaného množství výrobků na dané období dopředu tak, aby celkové náklady na cesty byly minimální.  

Výstup simulace: *k(s,d,z,t) pro ꓯs Є {1,…, S}, d Є {1,…, D}, z Є {1,…, Z}, t Є {1,…, T}*, tedy počty kusů všech druhů zboží z rozvezených ze všech továren d do všech supermarketů s každý den t. Pokud by se stalo, že továrny nedokáží uzásobit supermarkety, Luboš potřebuje znát, kdy to bude (den t), aby doobjednal zboží z Číny.

## Analýza řešeného problému
Ze zadání vyplývá, že budeme muset vytvořit program, který bude simulovat uspokojování poptávek supermarketů při co nejnižší celkové ceně přepravy. Na první pohled se zdálo, že řešení úlohy bude probíhat pomocí reprezentace grafem a s využitím grafového algoritmu. Při bližší analýze jsme tuto možnost zavrhli a zvolíme níže popsaný alternativní přístup.

**Vstupní data**  
Vstupní data jsou zadaná ve vstupních textových souborech. Data budeme postupně načítat a rozdělovat do potřebných matic týkajících se konkrétních továren a supermarketů. Protože soubory obsahují i slovní komentáře a prázdné řádky, samozřejmostí bude jejich ignorování. 

**Generátor dat**  
Generátor vlastních dat bude využívat normálního (Gaussovo) rozdělení. Data budou zapisována do souborů, kde forma generovaných dat bude odpovídat vzorovým data-setům přiloženým k zadání práce (úvodní informace; počty D, S, Z, T; matice počátečních zásob supermarketů, matice produkcí továren a matice poptávek supermarketů). 

**Simulace**  
Aby byla celková cena přepravy co možná nejnižší, bude třeba začít uspokojovat největší poptávky z nejdostupnějších továren. K výběru maximálních hodnot poptávek supermarketů a minimálních hodnot cen cest budeme využívat prioritní frontu. Simulace tedy bude probíhat od supermarketu s nejvyšší poptávkou až po nejnižší. Ke každému supermarketu vybereme z prioritní fronty nejdostupnější továrnu (s nejlevnější cenou cesty) a převezeme požadovaný počet kusů zboží. Pokud by cena cesty byla příliš drahá, budou supermarkety přednostně uspokojovat svoji poptávku ze svých počátečních zásob na skladě. V případě, že by nebylo možné supermarket uzásobit, vypíše se informace, kdy bude nutné objednat zboží z Číny. 

**Výstupní data**  
Na základě proběhlé simulace pak budeme generovat potřebné statistiky výsledků do výstupních souborů, k čemuž bude sloužit další třída pro tento zápis. Ten pak bude probíhat konkrétně do 4 textových souborů, které se vytvoří v příslušné složce. 

## Návrh programu
Bližší informace k jednotlivým třídám včetně zobrazení vazeb a závislosti jsou k dispozici pomocí UML diagramu [zde](https://github.com/andrlikjirka/SP-PT/blob/main/SemestralniPrace/uml/uml.png).

## Uživatelská dokumentace  
#### Spuštění programu
Ke spuštění programu je potřeba mít ve složce, kde máte uložený jar soubor, složku se vstupními soubory (jedná se o textové soubory se vstupními data-sety) a složku s názvem „vystup-soubory“ připravenou pro soubory k zápisu dat výstupních. Jelikož se nejedná o okenní aplikaci, lze ji spustit pouze v příkazovém řádku (najdete ho zmáčknutím klávesy `Windows` a napsáním příkazu `cmd`), kde následně zadáte cestu k místu, kde je uložený jar soubor a nakonec příkaz: `java -jar nazevsouboru.jar`.  

#### Ovládání programu   
Po spuštění programu se v příkazovém řádku vypíše menu, které vidíte na obrázku. Na výběr jsou zde 3 možnosti. Pro výběr požadavku napište jeho číslo do řádku se slovem Volba.  
![menu](https://raw.githubusercontent.com/andrlikjirka/SP-PT/main/SemestralniPrace/dokumentace/img/menu.png)

* **Generování vstupních dat**  
Při Zvolení možnosti *„1“ – Generování vstupních dat* bude program postupně vyžadovat požadovaný počet továren, počet supermarketů, počet druhů zboží a počet dnů. Po zadání těchto čtyř požadavků se vygenerují nová vstupní data s těmito požadovanými počty a zapíší se do souboru `vygenerovana-data.txt`. Vygenerovaný data-set najdete ve stejné složce, kde se nachází i Vámi spuštěný jar soubor. Tyto data pak můžete použít jako vstupní soubor pro simulaci, pokud nemáte k dispozici vlastní.  
![generator](https://raw.githubusercontent.com/andrlikjirka/SP-PT/main/SemestralniPrace/dokumentace/img/generovani-dat.png)

* **Spuštění simulace**    
Pokud zvolíte možnost *„2“ - Spustit simulaci*, program bude vyžadovat název vstupního souboru, se kterým chcete pracovat. Pokud je tento soubor uložen ve složce, je potřeba zadat i jméno složky. Po jeho potvrzení se spustí simulace a začnou se po jednotlivých dnech a druzích zboží (Z) vypisovat informace, ze které továrny (D) do jakého supermarketu (S) se uskuteční převoz kolika kusů daného druhu zboží včetně ceny za tuto cestu. Také se vypíše celková cena přepravy za celé období, počet kusů vzatých ze skladu, počet kusů objednaných z Číny a počet kusů celkem odeslaných z továren. Zároveň se ve výstupní složce vytvoří soubory s výstupními daty.  
*Vstupní data*  
![vstupni data](https://raw.githubusercontent.com/andrlikjirka/SP-PT/main/SemestralniPrace/dokumentace/img/vstupni-data.png)  
*Spuštění simulace*  
![simulace](https://raw.githubusercontent.com/andrlikjirka/SP-PT/main/SemestralniPrace/dokumentace/img/simulace.png)  

* **Ukonceni programu**  
Pokud zvolíte možnost „3“ – EXIT, program se ukončí a vypíše větu „Program ukončen“.  
![ukonceni](https://raw.githubusercontent.com/andrlikjirka/SP-PT/main/SemestralniPrace/dokumentace/img/ukonceni.png)  

#### Popis výstupních charakteristik  
![vystupni charakteristiky](https://raw.githubusercontent.com/andrlikjirka/SP-PT/main/SemestralniPrace/dokumentace/img/vystup-soubory.png)  
* **Přehled továren**  
V souboru `prehledTovaren.txt` se vygenerují přehledy pro každou továrnu ze vstupního souboru. Tyto informace zahrnují ID továrny (D), číslo dne, ve kterém se převoz uskutečnil, ID supermarketu (S), do kterého jsme zboží vezli, druh převáženého zboží (Z), jeho počet kusů a cenu tohoto převozu. Pro každou továrnu se také vypíše počet zbytečně vyrobených kusů všech druhů zboží. 

* **Přehled skladů**  
Soubor `prehledSkladu.txt` obsahuje ID uspokojovaného supermarketu (S), informaci o počátečních zásobách pro jednotlivé druhy zboží (Z) a průběh jejich úbytků po dnech.

* **Čína**  
Informace o tom, zda bude (nebo nebude) nutné objednat zboží z Číny a případně v jaký den, jaký druh zboží a počet jeho kusů je uveden v souboru `vystup-cina.txt`.

* **Simulace**  
Celkovou cenu převozu za celé období a celkovou dobu běhu simulace pak uvádí soubor `vystup-simulace.txt`.  
