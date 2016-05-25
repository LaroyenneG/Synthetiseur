public class Accord {
Note[] notes ;
double duree ;
double[] signal ;

/*
La methode addNote a pour fonction de modifier le signal en fonction de la note passée 
en paramétre afin de realiser un accord avec le sinal initial existant.
La methode tiens compte de la valeur des amplitudes du signal avant modification,
afin que l'amplitude du signal reste inférieur ou egaul à 1.

Pour cela la methode parcoure le tableau "notes" pour evaluer le nombre de note déjà
ajouté.
Et le nouveau signal correspond à la moyenne du signal initial et du signal de la note passée
en paramétre.  
*/	   
public void addNote(Note not){
    // Compteur de boucle
    int i;
    
    // Compteur du nombre de note accordée
    int z=0;
    // Evalutation du nombre de note grâce aux valeurs dans le tableau "notes" 
    while((notes[z]!=null)&&(z<5)){ z=z+1; }    //la condition z<5 permet de verifier qu'il n'y a pas plus de 4 notes
    
    // Ajout de la note passée en paramétre dans le tableau "notes", si le tableau "notes" a au moin une note
    if(z!=0){ 
        notes[z]=not;   // ajout de la note dans le tableau "notes"
    }
    
    // Modification du signal pour faire l'accord avec la nouvelle note (calcul de la moyenne des signaux)        
    for(i=0;i<signal.length;i++){
        signal[i]=(signal[i]*z+not.signal[i])/(z+1);
        // signal[i]*z pour retablir la valeur initial de l'amplitude, et z+1 pour le nombre total de notes
    }    
}


/*
La methode play permet de jouer le signal de la class Accord.
En envoyant une par une les valeurs du tableau signal vers la class StdAudio 
*/      
public void play(){
    // Compteur de boucle
    int i;
    
    // Boucle qui parcoure le signal
    for(i=0;i<signal.length;i++){    
        StdAudio.play(signal[i]);        
    }
}


public Accord(Note note1) {
    notes = new Note[4] ;
    notes[0] = new Note(note1) ;
    notes[1] = null ;
    notes[2] = null ;
    notes[3] = null ;
    duree = note1.duree;
    signal = note1.signal.clone() ;
}



/*
Le programme du main permet de tester la class Accord.
Si la class fonctionne le prgramme doit pouvoir jouer une note composé d'un :
- do6# croche d'amplitude 1 
- re4# croche d'amplitude 0.5 
- do4 croche d'amplitude 0.8
- sol4b noire d'amplitude 0.4 
Sur un tempo de 60 avec harmoniques
*/
public static void main(String[] args){
    // Creation de la premiere note obligatoire          
    Note note1 = Note.sToNote("do6#", 1, Note.faceToDuration("croche",60), true);
    // Creation de l'accord appelé "chord" avec la note 1
    Accord chord = new Accord(note1) ;  
    
    // Ajout des 3 autre notes 
    Note note2 = Note.sToNote("re4#", 0.5, Note.faceToDuration("croche",60), true);    //creation de la note
    chord.addNote(note2);   //envoi de la note creer vers addNote pour modifier le signal de chord
    Note note3 = Note.sToNote("do2", 0.8, Note.faceToDuration("croche",60), true);
    chord.addNote(note3);
    Note note4 = Note.sToNote("sol4b", 0.4, Note.faceToDuration("noire",60), true);
    chord.addNote(note4);
    
    // Envoi du signal de l'accord vers la fontion play pour faire la lecture du signal
    chord.play();
}

}
