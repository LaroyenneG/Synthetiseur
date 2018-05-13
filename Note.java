public class Note {

    private static final double[] fondFreq = new double[]{32.7D, 65.41D, 130.81D, 261.63D, 523.25D, 1046.5D, 2093.0D, 4186.01D};
    private static final String[] tons = new String[]{"do", "re", "mi", "fa", "sol", "la", "si"};
    private static final int[] haut = new int[]{0, 2, 4, 5, 7, 9, 11};
    private static boolean harmo;
    private String toneBase;
    private char alter;
    private int octave;
    private double freq;
    private double duree;
    private double amp;
    double[] signal;

    public Note(Note oldNote) {
        duree = oldNote.duree;
        alter = oldNote.alter;
        toneBase = oldNote.toneBase;
        octave = oldNote.octave;
        amp = oldNote.amp;
        freq = oldNote.freq;
        signal = oldNote.signal.clone();
    }


    public Note(String tB, char alt, int oct, double dur, double amp) {
        duree = dur;
        alter = alt;
        toneBase = tB;
        octave = oct;
        freq = freqTone(toneBase, alter, octave);
        double echantillon = 44100.0D;
        int nbsignal = (int) Math.round(echantillon * duree) + 1;

        signal = new double[nbsignal];

        if (harmo) {

            double harmonique1;
            double harmonique2;
            double harmonique3;
            double harmonique4;
            double sommeAmp;

            for (int i = 0; i < nbsignal; ++i) {
                harmonique1 = amp * Math.sin(Math.PI * 2.0D * freq * (double) i / echantillon);
                harmonique2 = amp / 4.0D * Math.sin(Math.PI * 2.0D * freq * 0.5D * (double) i / echantillon);
                harmonique3 = amp / 4.0D * Math.sin(Math.PI * 2.0D * freq * 2.0D * (double) i / echantillon);
                harmonique4 = amp / 8.0D * Math.sin(Math.PI * 2.0D * freq * 3.0D * (double) i / echantillon);
                sommeAmp = amp + amp / 4.0D + amp / 4.0D + amp / 8.0D;
                signal[i] = (harmonique1 + harmonique2 + harmonique3 + harmonique4) / sommeAmp;
            }
        } else {
            for (int i = 0; i < nbsignal; ++i) {
                signal[i] = Math.sin(Math.PI * 2.0D * freq * (double) i / echantillon);
            }
        }
    }

    private static double freqTone(String toneBase, char alter, int octave) {

        int n = 0;

        for(int i = 0; i < tons.length; ++i) {
            if(toneBase.equals(tons[i]) && alter == 35) {
                n = haut[i] + 1;
            }

            if(toneBase.equals(tons[i]) && alter == 98) {
                n = haut[i] - 1;
            }

            if(toneBase.equals(tons[i]) && alter != 98 && alter != 35) {
                n = haut[i];
            }
        }

        if(n == -1) {
            n = 11;
        }

        if(n == 12) {
            n = 0;
        }

        return fondFreq[octave] * Math.pow(2.0D, (double)n / 12.0D);
    }


    public static Note sToNote(String tonalite, double amplitude, double duree, boolean harmon) {

        harmo = harmon;
        String toneBase = "";
        int octave = 0;
        char alter = 120;

        for(int i = 0; i < tonalite.length(); ++i) {
            if(tonalite.charAt(i) != 48 && tonalite.charAt(i) != 49 && tonalite.charAt(i) != 50 && tonalite.charAt(i) != 51 && tonalite.charAt(i) != 52 && tonalite.charAt(i) != 53 && tonalite.charAt(i) != 54 && tonalite.charAt(i) != 55 && i <= 3 && tonalite.charAt(i) != 35 && tonalite.charAt(i) != 98) {
                toneBase = toneBase + tonalite.charAt(i);
            }

            if(tonalite.charAt(i) == 35 || tonalite.charAt(i) == 98) {
                alter = tonalite.charAt(i);
            }

            if(tonalite.charAt(i) == 48 || tonalite.charAt(i) == 49 || tonalite.charAt(i) == 50 || tonalite.charAt(i) == 51 || tonalite.charAt(i) == 52 || tonalite.charAt(i) == 53 || tonalite.charAt(i) == 54 || tonalite.charAt(i) == 55) {
                octave = Character.getNumericValue(tonalite.charAt(i));
            }
        }

        return new Note(toneBase, alter, octave, duree, amplitude);
    }

    public static double faceToDuration(String figure, int tempo) {
        double nbnoire = 1.0D;

        if(figure.equals("double-croche")) {
            nbnoire = 0.25D;
        }

        if(figure.equals("croche")) {
            nbnoire = 0.5D;
        }

        if(figure.equals("blanche")) {
            nbnoire = 0.5D;
        }

        if(figure.equals("ronde")) {
            nbnoire = 0.25D;
        }

        return nbnoire / ((double) tempo / 60.0D);
    }
}