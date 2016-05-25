import java.util.*;

public class Note {
final static double[] fondFreq = {32.70, 65.41, 130.81, 261.63, 523.25, 1046.50, 2093.00, 4186.01};
final static String[] tons = {"do", "re", "mi", "fa", "sol", "la", "si"};
final static int[] haut = {0, 2, 4, 5, 7, 9, 11};
public String toneBase ;
public char alter ;
public int octave ;
public double freq ;
public double duree;
public double amp;
public static boolean harmo;
double[] signal;
 
/*
La methode prend en parametre le ton l'alter, et l'octave de la note afin 
de returner la frequence correspondante de la note.
*/
private static double freqTone(String toneBase, char alter, int octave){
    // Compteur de boucle
    int i;
    
    // n correspond au demi ton de la note
    int n=0; // Initialisation de n
    
    // Boucle qui parcour le tableau "ton" 
    for (i=0;i<tons.length;i++) {
        /* 
        * On regarde dans quel enplacement du tableau "ton" la valeur et identique
        * à "toneBase". Dans ce cas le demis ton correspond à la valeur du tableau "haut" 
        * dans l'emplacement trouvé. 
        */ 
        if ((toneBase.equals(tons[i])) && (alter=='#')) { n=haut[i]+1;} // Si la note posséde un # alors le demis ton est une valeur plus haut 
    
        if ((toneBase.equals(tons[i])) && (alter=='b')) { n=haut[i]-1;  }// Si la note posséde un b alors le demis ton est une valeur plus basse 
    
        if ((toneBase.equals(tons[i]))&& !(alter=='b') && !(alter=='#')) { n=haut[i]; } // Si il n'y a pas de # et pas de b alors pas de modification du demi ton 
    }


    // Dans le cas d'un do b on transforme le demis ton en si
    if(n==-1){ n=11; }
    
    // Dans le cas d'un si # on transforme le demi tons en do
    if(n==12){ n=0; }
    
    // Calcul de la frequence de la note : frequence = frequence fondamental * 2^(n/12)  
    return (fondFreq[octave]*(Math.pow(2.0,n/12.0)));
}

 /*
 * Le constructeur permettant de déclarer/allouer une note par
 * Note note = new Note(ton, alter, octave, duree, amplitude);
 */
public Note(String tB, char alt, int oct, double dur, double amp){ 
    duree = dur ;
    alter = alt ;
    toneBase = tB ;
    octave = oct ;
    freq = freqTone(toneBase, alter, octave) ;
 
    // définition de **signal**
     
    // Compteur de boucle
    int i;
     
    double echantillon = 44100.0; // Correspond a l'echantillonage
     
    double harmonique1; // Correspond au signal de la note jouer
    double harmonique2; // Correspond au signal du premier harmonique
    double harmonique3; // Correspond au signal du deuxieme harmonique
    double harmonique4; // Correspond au signal du troisieme harmonique
     
    double sommeAmp; // Correspond à la somme de toute les amplitudes pour calculer le signal avec harmonique
     
    int nbsignal=((int)Math.round(echantillon*duree)) + 1; // Correspond au nombre de signaux qui seront placé dans le tableau "signal"
     
    signal=new double[nbsignal]; // Creation du nouveau tableu "signal"
     
    // Signal sans harmonique
    if((!harmo) && (!Synthe.guitare)){
         
        for (i = 0; i <= echantillon*duree; i++) {
            // Le signal est calculé par la formule : S(t)= amplitude x sin(2pi x freq * t / echantillonage)  
            signal[i]=(amp*Math.sin(2*Math.PI * freq * i / echantillon));    
        }
    }
     
     
    // Signal avec harmoniques (a/4, f*0.5) (a/4, f*2) (a/8, f*3) 
    if(harmo){
        for (i = 0; i < nbsignal; i++) {          
            // Calcul du signal de la note
            harmonique1=(amp*Math.sin(2*Math.PI * freq * i / echantillon));
            // Calcul du signal de ses harmoniques
            harmonique2=((amp/4)*Math.sin(2*Math.PI * freq*0.5 * i / echantillon));
            harmonique3=((amp/4)*Math.sin(2*Math.PI * freq*2 * i / echantillon));
            harmonique4=((amp/8)*Math.sin(2*Math.PI * freq*3 * i / echantillon));
           
            sommeAmp=(amp+amp/4+amp/4+amp/8);
           
            /*
            Pour calculer le signal avec harmonique on fait la somme
            de toute les harmoniques divise par la somme des amplitudes à l'instant t.
            */
            signal[i]=((harmonique1+harmonique2+harmonique3+harmonique4)/sommeAmp);
        } 
    }
     
    /*
    Pour tenter de reproduire le son de la guitar on doit ce procurer ses harmoniques.
    Ils sont: (a/2.1, f*2); (a/4.2, f*3); (a/4.3, f*4).
    Mais les harmoniques ne suffisent pas pour reproduire un son similaire.
    En effet le son est produit par la vibration de la corde et ce qui en fait un oscillateur mecanique.
    L'oscillateur mecanique est soumit aux frottements, c'est donc un oscillateur mecanique semi periodique.
    Comme pour tout oscillateur semi periodique, la frequence reste identique au cour du temps, mais son
    amplitude diminue. On sait donc que pour reproduire le son de la corde, l'amplitude du signal doit 
    etre amortit au cour du temps.  
    */
    if(Synthe.guitare){
        double accordeur=0.5; // Permet de modifier l'amortissement de l'amplitude du signal au cour du temps
        double divGuitar=1.0; // Initialisation de l'amortissement de l'amplitude
        for (i = 0; i <= echantillon*duree; i++) {
            // Calcul du signal de la note
            harmonique1=(amp*Math.sin(2*Math.PI * freq * i / echantillon));
            // Calcul du signal de ses harmoniques
            harmonique2=((amp/2.1)*Math.sin(2*Math.PI * freq*2 * i / echantillon));
            harmonique3=((amp/4.2)*Math.sin(2*Math.PI * freq*3 * i / echantillon));
            harmonique4=((amp/4.3)*Math.sin(2*Math.PI * freq*4 * i / echantillon));
            sommeAmp=(amp+amp/2.1+amp/4.2+amp/4.3);
            // Calcul du nouveau signal
            signal[i]=((harmonique1+harmonique2+harmonique3+harmonique4)/sommeAmp); 
            signal[i]=signal[i]/divGuitar; // Amortissement du signal à l'intant t
            divGuitar=i/(echantillon*duree*accordeur); // Augmentation de l'amortissement au cour du temps
        }
    }         
}

/*
La methode play permet de jouer le signal de la class Accord.
En envoyant une par une les valeurs du tableau signal vers la class StdAudio 
*/
public void play(){
    //compteur de boucle
    int i;
    
    //boucle qui parcoure le signal
    for(i=0;i<signal.length;i++)
    StdAudio.play(signal[i]); 
}

/*
La methode sToNote a pour but de retourner un note à partir de la tonalite, 
de l'amplitude, de la duree passé en parametre. Le fait que l'on gere les harmoniques 
ou non et aussi un paramètre.
*/
public static Note sToNote(String tonalite, double amplitude, double duree, boolean harmon){
    
    harmo=harmon; // Pour que "harmon" soit accessible dans toute la class
   
    // Intialisation des variables
    String toneBase=""; // Variable qui stock le ton de base 
    int octave=0; // Variable qui stock l'octave
    char alter='x'; // Variable qui stock l'alter
    
    // Compteur de bouble
    int i;
    
    /*
    Pour séparer les diffamantes informations: "tonalite", "alter" et "octave", 
    on parcoure caractère par caractère la chaine (de gauche à droite).
    */
    for(i=0;i<tonalite.length();i++){
        // Si les caractères sont une lettre differente de b et de # alors ils sont stocke dans toneBase.
        if((tonalite.charAt(i)!='0')&&(tonalite.charAt(i)!='1')&&(tonalite.charAt(i)!='2')
            &&(tonalite.charAt(i)!='3')&&(tonalite.charAt(i)!='4')&&(tonalite.charAt(i)!='5')
            &&(tonalite.charAt(i)!='6')&&(tonalite.charAt(i)!='7')&&(i<=3)&&(tonalite.charAt(i)!='#')
            &&(tonalite.charAt(i)!='b')){
            
            toneBase= toneBase + tonalite.charAt(i);         
        }
        // Si le caractère est b ou # alors il est stocke dans alter.
        if((tonalite.charAt(i)=='#')||(tonalite.charAt(i)=='b')){
            alter=(char)tonalite.charAt(i);  
        }
        
        // Si le caractère est un nombre entre 0 et 7 alors il est stoke dans octave.
         if((tonalite.charAt(i)=='0')||(tonalite.charAt(i)=='1')||(tonalite.charAt(i)=='2')
            ||(tonalite.charAt(i)=='3')||(tonalite.charAt(i)=='4')||(tonalite.charAt(i)=='5')
            ||(tonalite.charAt(i)=='6')||(tonalite.charAt(i)=='7')){
            
            octave= Character.getNumericValue(tonalite.charAt(i));
        }
    }
    // Une fois que tous les éléments sont récupéré la note est cree puis returner.  
    return new Note(toneBase, alter, octave,duree,amplitude);
    
}
/*
La methode faceToDuration permet de determiner la duree d'une note en fonction de son type
et du tempo passé en parametre. La duree est calculé avec le nombre de noire et le tempo.
*/
public static double faceToDuration(String figure, int tempo){
    // variables
    double duree;
    double nbnoire=1.0; // Initialisation du nombre de noire 
    
    /*
    Chaque figure equivaut à un nombre de noire.
    La duree de la note est calculé par :
    duree = nbnoire/(tempo/60) car il y a 60 secondes dans une minute.  
    */
    if(figure.equals("double-croche")){ nbnoire = 0.25; }
    if(figure.equals("noire")){ nbnoire = 1.0; }
    if(figure.equals("croche")){ nbnoire = 0.5; }
    if(figure.equals("blanche")){ nbnoire = 0.5; }
    if(figure.equals("ronde")){ nbnoire = 0.25; }
   
    duree=nbnoire/(tempo/60.0);
    
    return duree;
}

// Pour faire une copy de la note passée en parametre
public Note(Note oldNote){
       duree = oldNote.duree ;
       alter = oldNote.alter ;
       toneBase = oldNote.toneBase ;
       octave = oldNote.octave ;
       amp = oldNote.amp ;
       freq = oldNote.freq;
       signal = oldNote.signal.clone() ;
}


public static void main(String[] args){
    int i, oct ;
    if (args.length < 1) oct = 3 ; else oct = Integer.parseInt(args[0]) ;
    for (i = 0; i< 7; i++){
        Note not = new Note(tons[i], ' ', oct, 1.0, 1.0);
        System.out.print(not.toneBase + ", octave " + not.octave + "  f0 =" + fondFreq[not.octave] + "Hz, F =");
        System.out.format("%.2f Hz%n",not.freq);
        not.play();
    }
}
}