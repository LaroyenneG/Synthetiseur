import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

/******************************************************************************
 *  Compilation:  javac Synthe.java
 *  Execution:    java Synthe input.txt tempo
 *  Dependencies: StdAudio.java Accord.java Note.java
 *
 *
 *
 ******************************************************************************/

public class Synthe {

    static int tempo ;
    static boolean harm = false ;
    static boolean guitar = false ;
	public static boolean guitare;
    public static void main(String[] args) {
        //input.useLocale(Locale.ENGLISH);
		
        if (args.length < 2) {
            System.out.println("Usage : java Synthe <fichier> <tempo> <harm/guitar>");
            System.exit(-1);
        }
        tempo = Integer.parseInt(args[1]);
        if (args.length == 3){
            if (args[2].equals("harm")) harm = true ;
            else if (args[2].equals("guitar")) guitar = true ;
        }

        try {
            BufferedReader fichier = new BufferedReader(new FileReader(args[0]));
            String ligne;
            while ((ligne = fichier.readLine()) != null){
                playLigne(ligne, harm, guitar);
            }
            fichier.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }

public static void playLigne(String ligne, boolean harm, boolean guitar){

	guitare=guitar;  // Permet que guitar soit accessible à toute les class
	
	// Compteur de boucle
	int i;
	int k=0; // Compteur de caractère dans la chaine ligne
	
	// Variables
	int virgule=0; // Compte le nombre de virgule sur la ligne
	int nbNote=0; // Compte le nombre de note à accorder sur la ligne
	String[] section= {"","","","","","",""}; // Tableau qui contient les differents parametre entre les virgules de la ligne.
	String portee; // Correspond au type de note ( blanche, noire, croche )
	double amplitude; // Correspond à l'amplitude de la note
	
		
	/*
	La boucle parcoure caractère par caractère la chaine ligne (de gauche à droite)
	et place les valeurs entre les virgules de la ligne dans le tableau section.
	
	Les informations de la ligne sont stocke de cette façon:
		note,...,note,type de note,aplitude
	*/
	while(k<ligne.length()){
		
		if(ligne.charAt(k)!=','){
			
			switch (virgule){
				
            	case 0:  section[0]=section[0]+ligne.charAt(k);
                     break;
            	case 1:  section[1]=section[1]+ligne.charAt(k);
                     break;
            	case 2:  section[2]=section[2]+ligne.charAt(k);
                     break;
            	case 3:  section[3]=section[3]+ligne.charAt(k);
                     break;
            	case 4:  section[4]=section[4]+ligne.charAt(k);
                     break;
            	case 5:  section[5]=section[5]+ligne.charAt(k);
                     break;
            	case 6:  section[6]=section[6]+ligne.charAt(k);
                     break;
					 
            default: System.out.println("Error de section");
                     break;
			}
					 
		}else{
			virgule++;
		}
		k++;
	}
	
	

	/*
	La boucle parcoure les valeurs du tableau section à la recherche d'un valeur correspondante
	avec un type de note ( ex : croche) de gauche à droite. Tant que le type de note n'est pas
	trouvé, alors c'est que la valeur est un ton de note (ex : do3#).
	*/
	for(i=0;i<section.length;i++){
		
		if((section[i].equals("noire"))||(section[i].equals("blanche"))||(section[i].equals("croche"))
			||(section[i].equals("ronde"))||(section[i].equals("double-croche"))){
		
				i=section.length; // Pour sortir de la boucle for 
		
			}else{
				nbNote++;
			}
	}

	portee=section[nbNote]; // portee prend le type de note
	
	amplitude = Double.parseDouble(section[nbNote+1]); // On stock l'amplitude, on transforme le type string en double.
	
	// Creation d'un accord nomé "chord" avec une note.
	Note note1 = Note.sToNote(section[0], amplitude, Note.faceToDuration(portee,tempo), harm);
         Accord chord = new Accord(note1) ;
		 
	// Si il y a au moin 2 notes, on cree la deuxieme note, on ajoute la deuxieme note dans l'accord "chord".
	if(nbNote>=2){
		Note note2 = Note.sToNote(section[1], amplitude, Note.faceToDuration(portee,tempo), harm);
        chord.addNote(note2);
	}
	// Si il y a au moin 3 notes, on cree la troisieme note, on ajoute la troisieme note dans l'accord "chord".
	if(nbNote>=3){
		Note note3 = Note.sToNote(section[2], amplitude, Note.faceToDuration(portee,tempo), harm);
        chord.addNote(note3);
	}
	// Si il y a au moin 4 notes, on cree la quatrième note, on ajoute la quatrième note dans l'accord "chord".
	if(nbNote>=4){
		Note note4 = Note.sToNote(section[3], amplitude, Note.faceToDuration(portee,tempo), harm);
        chord.addNote(note4);
	}
	


	// Comme le signal de accord est cree on le joue
	chord.play();
		
		
}
}