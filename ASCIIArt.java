/* 
* Tekijä
* Eetu Rinta-Jaskari (rinta-jaskari.eetu.m@student.uta.fi).
*
* Ohjelma lukee ASCII-taidetta tiedostosta ja tarjoaa toimintoja (mm. mediaanisuodatus) käyttäjälle.
* 
*/
import java.util.*; import java.io.*;
public class ASCIIArt {

    //Määritetään luokkavakiot merkeille.
    public static final char CBLACK = '#', CGREY1 = '@', CGREY2 = '&', CGREY3 = '$',
    CGREY4 = '%', CGREY5 = 'x', CGREY6 = '*', CGREY7 = 'o', CGREY8 = '|', CGREY9 = '!',
    CGREY10 = ';', CGREY11 = ':', CGREY12 = '\'', CGREY13 = ',', CGREY14 = '.', CWHITE = ' ';

    public static void main(String[] args) {
        //Luodaan vakiot komennoille ja teksteille
        final String KUVA = "printa", LUVUT = "printi", TIEDOT = "info", 
        SUODATA = "filter", RESET = "reset", LOPETA = "quit",
        GUIDELINE = "printa/printi/info/filter [n]/reset/quit?", HEIPPA = "Bye, see you soon.";

        //Tervehdys
        System.out.println("-------------------");
        System.out.println("| A S C I I A r t |");
        System.out.println("-------------------");

        //Tarkistetaan että tiedostonimi parametri on annettu käynnistettäessä ja sijoitetaan muuttujaan.
        String tiedostoNimi = "";
        if(args.length != 0) {
            tiedostoNimi = args[0];
        }

        //Luetaan tiedosto ja muunnetaan merkit luvuiksi.
        String merkit[][] = readFile(tiedostoNimi);
        int luvut[][] = convertArrayToInt(merkit);

        if(luvut != null) {
            //Laitetaan ohjelma silmukkaan, jota suoritetaan kunnes ei haluta jatkaa.
            boolean runProgram = true;
            while(runProgram) {
                //Otetaan käyttäjän syöte
                System.out.println(GUIDELINE);
                String syote = In.readString();

                //Valitaan toiminto käyttäjän syötteestä.
                if(syote.equals(KUVA)) { //printa
                    printArrayString(luvut);
                } else if(syote.equals(LUVUT)) { //printi
                    printArrayInt(luvut);
                } else if(syote.equals(TIEDOT)) { //info
                    printInfo(luvut);
                } else if(syote.startsWith(SUODATA)) { //filter x
                    //Katkaistaan numero lopusta, jos on
                    String filter[] = syote.split(" ");

                    //Jos on numero
                    if(filter.length == 2) {
                        //Otetaan numero
                        int filterSize = Integer.parseInt(filter[1]);
                        //Varmistetaan, että luku on yli 3, pariton eikä suurempaa kun kuva
                        if(filterSize % 2 != 0 && filterSize >= 3
                        && filterSize < luvut.length && filterSize < luvut[0].length) {
                            //Kutsutaan operaatio
                            filterASCII(filterSize, luvut);
                        }
                    } else { //Jos ei numeroa tai virheellinen luku, suoritetaan oletuksena luvulla 3.
                        //Kutsutaan operaatio
                        filterASCII(3, luvut);
                    }
                } else if(syote.equals(RESET)) {
                    //Luetaan tiedosto uudestaan.
                    merkit = readFile(tiedostoNimi);
                    //Muunnetaan merkit luvuiksi
                    luvut = convertArrayToInt(merkit);
                } else if(syote.equals(LOPETA)) {
                    //Sammutetaan ohjelma
                    runProgram = false;
                }
            }
        }
        //Hyvästelyt.
        System.out.println(HEIPPA);
    }

    public static void copyArray(int kohde[][], int lahde[][]) {
        //Operaatio palauttaa kopioi taulukon toiseen. Parametrinä kohde- ja lähdetaulukot.
        for(int x = 0; x < kohde.length; x++) {
            for(int y = 0; y < kohde[x].length; y++) {
                kohde[x][y] = lahde[x][y];
            }
        }
    }

    public static char getCharacter(int indeksi) {
        //Operaatio saa parametreinä indeksiluvun ja palauttaa sitä vastaavan merkin.
        //Jos indeksinumero on virheellinen palautetaan MAX_VALUE.

        //Haetaan luokkavakiot taulukkoon
        char[] table = { CBLACK, CGREY1, CGREY2, CGREY3, CGREY4, CGREY5, CGREY6,
        CGREY7, CGREY8, CGREY9, CGREY10, CGREY11, CGREY12, CGREY13, CGREY14, CWHITE };
        if(indeksi >= 0 && indeksi < table.length) {
            return table[indeksi];
        } else {
            return Character.MAX_VALUE;
        }
    }

    public static void filterASCII(int size, int luvut[][]) {
        //Luodaan aputaulukko ja kopioidaan siihen arvot
        int taulukko[][] = new int[luvut.length][luvut[0].length];
        copyArray(taulukko, luvut);

        //Filteröinnin aloituskohta / määrä mitä ei filteröidä reunoilta
        int aloitus = (int)((double)size / 2 - 0.5);

        //Filteröinti, luodaan suodinikkunalle taulu, johon kaapataan arvot
        int suodinIkkuna[] = new int[size * size];
        for(int x = aloitus; x < taulukko.length - aloitus; x++) {
            for(int y = aloitus; y < taulukko[x].length - aloitus; y++) {

                //Kerätään numerot suodinikkunaan
                int laskuri = 0;
                for(int a = 0; a < size; a++) {
                    for(int b = 0; b < size; b++) {
                        suodinIkkuna[laskuri] = luvut[(x - aloitus) + a][(y - aloitus) + b];
                        laskuri++;
                    }
                }
                //Numerot kerätty, lajitellaan pienimmästä suurimpaan.
                for(int i = 0; i < suodinIkkuna.length; i++) {
                    //Etsitään pienin luku.
                    int pienin = findSmallest(suodinIkkuna, i);

                    //Vaihdetaan taulukon arvot keskenään.
                    swapValues(i, pienin, suodinIkkuna);
                }

                //Haetaan mediaani
                int mediaani = suodinIkkuna[(int)(suodinIkkuna.length / 2)];

                //Sijoitetaan mediaani aputaulukkoon
                taulukko[x][y] = mediaani;
            }
        }

        //Suodatus suoritettu, siirretään aputaulukosta arvot oikeaan taulukkoon
        copyArray(luvut, taulukko);
    }
    public static int findSmallest(int taulu[], int i) {
        //Operaatio etsii pienimmän arvon taulukosta.
        //Parametrina taulukko, sekä indeksi jottei aloiteta alusta.
        //Palautuksena lyhimmän luvun indeksi.

        //Selataan taulukko
        int smallest = 0, indeksi = 0;
        boolean first = true; //Ensimmäistä kierrosta varten
        while(i < taulu.length) {
            if(taulu[i] < smallest || first) {
                smallest = taulu[i];
                indeksi = i;
                first = false;
            }
            i++;
        }
        //Palautetaan pienimmän luvun indeksi.
        return indeksi;
    }

    public static void swapValues(int eka, int toka, int suodinIkkuna[]) {
        //Operaatio vaihtaa kahden taulukon arvojen paikkaa.
        //Parametrinä annetaan taulukon kaksi indeksiä, joiden arvot vaihdetaan
        //Sekä itse taulukko.
        if(eka >= 0 && eka < suodinIkkuna.length
        && toka >= 0 && toka < suodinIkkuna.length) {
            //Luodaan apumuuttuja vaihtoa varten, joka varastoi ensimmäisen indeksin arvon.
            int apu = suodinIkkuna[eka];

            //Vaihdos
            suodinIkkuna[eka] = suodinIkkuna[toka];
            suodinIkkuna[toka] = apu;
        }
    }

    public static void printInfo(int luvut[][]) {
        //Operaatio laskee ja tulostaa tietoa (koko, merkkien määrä) ASCII-kuviosta
        //Parametreinä lukumuotoinen taulukko kuviosta.

        //Kuvan koko
        int length = luvut.length;
        int width = luvut[0].length;
        System.out.println(length + " x " + width);

        //Lasketaan merkkien määrä
        for (int i = 0; getCharacter(i) != Character.MAX_VALUE; i++) {
            //Luodaan laskuri
            int maara = 0;

            //Tarkistetaan taulu
            for(int x = 0; x < luvut.length; x++) {
                for(int y = 0; y < luvut[x].length; y++) {
                    //Onko kuvan kyseisen kohdan luku haluttu merkki
                    if(getCharacter(luvut[x][y]) == getCharacter(i)) {
                        maara++;
                    }
                }
            }

            //Tulostetaan määrä
            System.out.println(getCharacter(i) + " " + maara);
        }
    }
    public static int[][] convertArrayToInt(String merkit[][]) {
        //Operaatio muuttaa tiedostosta saadut ASCII-merkit lukumuotoon.
        //Parametrinä merkkimuotoinen taulukko ja palautuksena lukumuotoinen.
        if (merkit != null) {
            //Luodaan lukutaulukko ja merkkitaulukko vertailua varten
            int luvut[][] = new int[merkit.length][merkit[0].length];

            //Muunnosoperaatio silmukoissa
            for(int x = 0; x < luvut.length; x++) {
                for(int y = 0; y < luvut[x].length; y++) {
                    //Muunnetaan merkkitaulukon arvo char-muotoon
                    char merkki = merkit[x][y].charAt(0);

                    //Merkin muunto luvuksi
                    int luku = 0;
                    for(int i = 0; getCharacter(i) != Character.MAX_VALUE; i++) {
                        if(merkki == getCharacter(i)) {
                            luku = i;
                        }
                    }

                    //Sijoitetaan luku
                    luvut[x][y] = luku;
                }
            }
            //Palautetaan valmis taulu
            return luvut;
        }
        else {
            return null;
        }
    }

    public static void printArrayString(int arr[][]) {
        //Operaatio tulostaa taulukon merkkimuodossa. Parametrinä taulukko.
        String teksti = "";
        for(int rivi = 0; rivi < arr.length; rivi++) {
            for(int sarake = 0; sarake < arr[rivi].length; sarake++) {
                teksti += getCharacter(arr[rivi][sarake]);
            }
            teksti += "\n";
        }
        //Tulostetaan lopullinen tulos
        System.out.print(teksti);
    }

    public static void printArrayInt(int arr[][]) {
        //Operaatio tulostaa taulukon numeroina. Parametrinä taulukko.
        String teksti = "";
        for(int rivi = 0; rivi < arr.length; rivi++) {
            for(int sarake = 0; sarake < arr[rivi].length; sarake++) {
                //Jos yksinumeroinen luku
                if(arr[rivi][sarake] < 10 && arr[rivi][sarake] >= 0) {
                    teksti += " ";
                }

                //Sijoitetaan arvo tekstijonoon
                teksti += arr[rivi][sarake];

                //Jos ei viimeinen arvo
                if(sarake != arr[rivi].length - 1) {
                    teksti += " ";
                }
            }
            teksti += "\n"; //Joka rivin jälkeen rivinvaihto
        }
        System.out.print(teksti);
    }

    public static String[][] readFile(String tiedosto) {
        //Operaatio lukee ohjelmalle parametrinä annetun tiedoston,
        //siirtää sen kaksiulotteiseen taulukkoon ja palauttaa sen.
        //Jos luku epäonnistuu, palautetaan null.
        try {
            //Luodaan puskuroitu lukija
            FileInputStream virta = new FileInputStream(tiedosto);
            InputStreamReader lukija = new InputStreamReader(virta);
            BufferedReader puskuri = new BufferedReader(lukija);

            //Lasketaan tiedoston rivien määrä ja leveys
            int laskuri = 0, pituus = 0;
            while(puskuri.ready()) {
                String rivi = puskuri.readLine();
                if(rivi.length() > pituus || pituus == 0) {
                    pituus = rivi.length();
                }

                laskuri++;
            }
            
            //Määrä saatu, nollataan puskuri, lukija ja lukuvirta.
            puskuri.close();
            virta = new FileInputStream(tiedosto);
            lukija = new InputStreamReader(virta);
            puskuri = new BufferedReader(lukija);

            //Alustetaan taulukkomuuttuja. Nollataan laskuri.
            String sisalto[][] = new String[laskuri][pituus];
            laskuri = 0;
            //Luetaan tiedosto uudestaan, tällä kertaa tallennetaan sisältö.
            while(puskuri.ready()) {
                String rivi = puskuri.readLine();
                sisalto[laskuri] = rivi.split("");
                laskuri++;
            }
            //Sisältö saatu, suljetaan puskuri
            puskuri.close();

            return sisalto;
        }
        catch (FileNotFoundException e) {
            System.out.println("Invalid command-line argument!");
            return null; //Jos epäonnistui
        }
        catch (Exception e) {
            System.out.println("Invalid command-line argument!");
            return null; //Jos epäonnistui
        }
    }
}