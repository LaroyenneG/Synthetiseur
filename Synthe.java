import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Synthe {

    private static int tempo;

    private static void playLine(String line, boolean harm) {

        int k = 0;
		int virgule = 0;
		int nbNote = 0;

		String[] section;
        for (section = new String[]{"", "", "", "", "", "", ""}; k < line.length(); ++k) {
            if (line.charAt(k) != 44) {
				switch(virgule) {
					case 0:
                        section[0] = section[0] + line.charAt(k);
						break;
					case 1:
                        section[1] = section[1] + line.charAt(k);
						break;
					case 2:
                        section[2] = section[2] + line.charAt(k);
						break;
					case 3:
                        section[3] = section[3] + line.charAt(k);
						break;
					case 4:
                        section[4] = section[4] + line.charAt(k);
						break;
					case 5:
                        section[5] = section[5] + line.charAt(k);
						break;
					case 6:
                        section[6] = section[6] + line.charAt(k);
						break;
					default:
                        System.exit(-6);
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

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Usage : java Synthe <fichier> <tempo> <harm>");
            System.exit(-1);
        }

        tempo = Integer.parseInt(args[1]);

        boolean harm = false;

        if (args.length == 3 && args[2].equals("harm")) {
            harm = true;
        }

        try {
            BufferedReader ex = new BufferedReader(new FileReader(args[0]));

            String line;
            while ((line = ex.readLine()) != null) {
                playLine(line, harm);
            }

            ex.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}
