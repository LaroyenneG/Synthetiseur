public class Accord {
    Note[] notes = new Note[4];
    double duree;
    double[] signal;

    public void addNote(Note not) {
        int z;
        for(z = 0; this.notes[z] != null && z < 5; ++z) {
            ;
        }

        if(z != 0) {
            this.notes[z] = not;
        }

        for(int i = 0; i < this.signal.length; ++i) {
            this.signal[i] = (this.signal[i] * (double)z + not.signal[i]) / (double)(z + 1);
        }

    }

    public void play() {
        for(int i = 0; i < this.signal.length; ++i) {
            StdAudio.play(this.signal[i]);
        }

    }

    public Accord(Note note1) {
        this.notes[0] = new Note(note1);
        this.notes[1] = null;
        this.notes[2] = null;
        this.notes[3] = null;
        this.duree = note1.duree;
        this.signal = (double[])note1.signal.clone();
    }

    public static void main(String[] args) {
        Note note1 = Note.sToNote("do6#", 1.0D, Note.faceToDuration("croche", 60), true);
        Accord chord = new Accord(note1);
        Note note2 = Note.sToNote("re4#", 0.5D, Note.faceToDuration("croche", 60), true);
        chord.addNote(note2);
        Note note3 = Note.sToNote("do2", 0.8D, Note.faceToDuration("croche", 60), true);
        chord.addNote(note3);
        Note note4 = Note.sToNote("sol4b", 0.4D, Note.faceToDuration("noire", 60), true);
        chord.addNote(note4);
        chord.play();
    }
}
