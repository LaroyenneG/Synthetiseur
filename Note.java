public class Note {
    static final double[] fondFreq = new double[]{32.7D, 65.41D, 130.81D, 261.63D, 523.25D, 1046.5D, 2093.0D, 4186.01D};
    static final String[] tons = new String[]{"do", "re", "mi", "fa", "sol", "la", "si"};
    static final int[] haut = new int[]{0, 2, 4, 5, 7, 9, 11};
    public String toneBase;
    public char alter;
    public int octave;
    public double freq;
    public double duree;
    public double amp;
    public static boolean harmo;
    double[] signal;

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

    public Note(String tB, char alt, int oct, double dur, double amp) {
        this.duree = dur;
        this.alter = alt;
        this.toneBase = tB;
        this.octave = oct;
        this.freq = freqTone(this.toneBase, this.alter, this.octave);
        double echantillon = 44100.0D;
        int nbsignal = (int)Math.round(echantillon * this.duree) + 1;
        this.signal = new double[nbsignal];
        int i;
        if(!harmo && !Synthe.guitare) {
            for(i = 0; (double)i <= echantillon * this.duree; ++i) {
                this.signal[i] = amp * Math.sin(6.283185307179586D * this.freq * (double)i / echantillon);
            }
        }

        double harmonique1;
        double harmonique2;
        double harmonique3;
        double harmonique4;
        double sommeAmp;
        if(harmo) {
            for(i = 0; i < nbsignal; ++i) {
                harmonique1 = amp * Math.sin(6.283185307179586D * this.freq * (double)i / echantillon);
                harmonique2 = amp / 4.0D * Math.sin(6.283185307179586D * this.freq * 0.5D * (double)i / echantillon);
                harmonique3 = amp / 4.0D * Math.sin(6.283185307179586D * this.freq * 2.0D * (double)i / echantillon);
                harmonique4 = amp / 8.0D * Math.sin(6.283185307179586D * this.freq * 3.0D * (double)i / echantillon);
                sommeAmp = amp + amp / 4.0D + amp / 4.0D + amp / 8.0D;
                this.signal[i] = (harmonique1 + harmonique2 + harmonique3 + harmonique4) / sommeAmp;
            }
        }

        if(Synthe.guitare) {
            double accordeur = 0.5D;
            double divGuitar = 1.0D;

            for(i = 0; (double)i <= echantillon * this.duree; ++i) {
                harmonique1 = amp * Math.sin(6.283185307179586D * this.freq * (double)i / echantillon);
                harmonique2 = amp / 2.1D * Math.sin(6.283185307179586D * this.freq * 2.0D * (double)i / echantillon);
                harmonique3 = amp / 4.2D * Math.sin(6.283185307179586D * this.freq * 3.0D * (double)i / echantillon);
                harmonique4 = amp / 4.3D * Math.sin(6.283185307179586D * this.freq * 4.0D * (double)i / echantillon);
                sommeAmp = amp + amp / 2.1D + amp / 4.2D + amp / 4.3D;
                this.signal[i] = (harmonique1 + harmonique2 + harmonique3 + harmonique4) / sommeAmp;
                this.signal[i] /= divGuitar;
                divGuitar = (double)i / (echantillon * this.duree * accordeur);
            }
        }

    }

    public void play() {
        for(int i = 0; i < this.signal.length; ++i) {
            StdAudio.play(this.signal[i]);
        }

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

        if(figure.equals("noire")) {
            nbnoire = 1.0D;
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

        double duree = nbnoire / ((double)tempo / 60.0D);
        return duree;
    }

    public Note(Note oldNote) {
        this.duree = oldNote.duree;
        this.alter = oldNote.alter;
        this.toneBase = oldNote.toneBase;
        this.octave = oldNote.octave;
        this.amp = oldNote.amp;
        this.freq = oldNote.freq;
        this.signal = (double[])oldNote.signal.clone();
    }

    public static void main(String[] args) {
        int oct;
        if(args.length < 1) {
            oct = 3;
        } else {
            oct = Integer.parseInt(args[0]);
        }

        for(int i = 0; i < 7; ++i) {
            Note not = new Note(tons[i], ' ', oct, 1.0D, 1.0D);
            System.out.print(not.toneBase + ", octave " + not.octave + "  f0 =" + fondFreq[not.octave] + "Hz, F =");
            System.out.format("%.2f Hz%n", new Object[]{Double.valueOf(not.freq)});
            not.play();
        }

    }
}