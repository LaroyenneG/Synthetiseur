public class Accord {

    private Note[] notes = new Note[4];
    private double[] signal;

    public Accord(Note note) {
        notes[0] = new Note(note);
        notes[1] = null;
        notes[2] = null;
        notes[3] = null;
        signal = note.signal.clone();
    }

    public void addNote(Note not) {

        int z;
        for (z = 0; notes[z] != null && z < 5; ++z) ;

        if(z != 0) {
            this.notes[z] = not;
        }

        for(int i = 0; i < this.signal.length; ++i) {
            this.signal[i] = (this.signal[i] * (double)z + not.signal[i]) / (double)(z + 1);
        }

    }

    public void play() {
        for (double s : signal) {
            StdAudio.play(s);
        }
    }
}
