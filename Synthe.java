import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Synthe {
	static int tempo;
	static boolean harm = false;
	static boolean guitar = false;
	public static boolean guitare;

	public Synthe() {
	}

	public static void main(String[] args) {
		if(args.length < 2) {
			System.out.println("Usage : java Synthe <fichier> <tempo> <harm/guitar>");
			System.exit(-1);
		}

		tempo = Integer.parseInt(args[1]);
		if(args.length == 3) {
			if(args[2].equals("harm")) {
				harm = true;
			} else if(args[2].equals("guitar")) {
				guitar = true;
			}
		}

		try {
			BufferedReader ex = new BufferedReader(new FileReader(args[0]));

			String ligne;
			while((ligne = ex.readLine()) != null) {
				playLigne(ligne, harm, guitar);
			}

			ex.close();
		} catch (IOException var3) {
			var3.printStackTrace();
		}

		System.exit(0);
	}

	public static void playLigne(String ligne, boolean harm, boolean guitar) {
		guitare = guitar;
		int k = 0;
		int virgule = 0;
		int nbNote = 0;

		String[] section;
		for(section = new String[]{"", "", "", "", "", "", ""}; k < ligne.length(); ++k) {
			if(ligne.charAt(k) != 44) {
				switch(virgule) {
					case 0:
						section[0] = section[0] + ligne.charAt(k);
						break;
					case 1:
						section[1] = section[1] + ligne.charAt(k);
						break;
					case 2:
						section[2] = section[2] + ligne.charAt(k);
						break;
					case 3:
						section[3] = section[3] + ligne.charAt(k);
						break;
					case 4:
						section[4] = section[4] + ligne.charAt(k);
						break;
					case 5:
						section[5] = section[5] + ligne.charAt(k);
						break;
					case 6:
						section[6] = section[6] + ligne.charAt(k);
						break;
					default:
						System.out.println("Error de section");
				}
			} else {
				++virgule;
			}
		}

		for(int i = 0; i < section.length; ++i) {
			if(!section[i].equals("noire") && !section[i].equals("blanche") && !section[i].equals("croche") && !section[i].equals("ronde") && !section[i].equals("double-croche")) {
				++nbNote;
			} else {
				i = section.length;
			}
		}

		String portee = section[nbNote];
		double amplitude = Double.parseDouble(section[nbNote + 1]);
		Note note1 = Note.sToNote(section[0], amplitude, Note.faceToDuration(portee, tempo), harm);
		Accord chord = new Accord(note1);
		Note note4;
		if(nbNote >= 2) {
			note4 = Note.sToNote(section[1], amplitude, Note.faceToDuration(portee, tempo), harm);
			chord.addNote(note4);
		}

		if(nbNote >= 3) {
			note4 = Note.sToNote(section[2], amplitude, Note.faceToDuration(portee, tempo), harm);
			chord.addNote(note4);
		}

		if(nbNote >= 4) {
			note4 = Note.sToNote(section[3], amplitude, Note.faceToDuration(portee, tempo), harm);
			chord.addNote(note4);
		}

		chord.play();
	}
}