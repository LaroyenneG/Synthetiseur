import java.applet.Applet;
import java.applet.AudioClip;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.DataLine.Info;

public final class StdAudio {
    public static final int SAMPLE_RATE = 44100;
    private static final int BYTES_PER_SAMPLE = 2;
    private static final int BITS_PER_SAMPLE = 16;
    private static final double MAX_16_BIT = 32767.0D;
    private static final int SAMPLE_BUFFER_SIZE = 4096;
    private static SourceDataLine line;
    private static byte[] buffer;
    private static int bufferSize = 0;

    private StdAudio() {
    }

    private static void init() {
        try {
            AudioFormat e = new AudioFormat(44100.0F, 16, 1, true, false);
            Info info = new Info(SourceDataLine.class, e);
            line = (SourceDataLine)AudioSystem.getLine(info);
            line.open(e, 8192);
            buffer = new byte[2730];
        } catch (LineUnavailableException var2) {
            System.out.println(var2.getMessage());
            System.exit(1);
        }

        line.start();
    }

    public static void close() {
        line.drain();
        line.stop();
    }

    public static void play(double in) {
        if(in < -1.0D) {
            in = -1.0D;
        }

        if(in > 1.0D) {
            in = 1.0D;
        }

        short s = (short)((int)(32767.0D * in));
        buffer[bufferSize++] = (byte)s;
        buffer[bufferSize++] = (byte)(s >> 8);
        if(bufferSize >= buffer.length) {
            line.write(buffer, 0, buffer.length);
            bufferSize = 0;
        }

    }

    public static void play(double[] input) {
        for(int i = 0; i < input.length; ++i) {
            play(input[i]);
        }

    }

    public static double[] read(String filename) {
        byte[] data = readByte(filename);
        int N = data.length;
        double[] d = new double[N / 2];

        for(int i = 0; i < N / 2; ++i) {
            d[i] = (double)((short)(((data[2 * i + 1] & 255) << 8) + (data[2 * i] & 255))) / 32767.0D;
        }

        return d;
    }

    public static void play(String filename) {
        URL url = null;

        try {
            File clip = new File(filename);
            if(clip.canRead()) {
                url = clip.toURI().toURL();
            }
        } catch (MalformedURLException var3) {
            var3.printStackTrace();
        }

        if(url == null) {
            throw new RuntimeException("audio " + filename + " not found");
        } else {
            AudioClip clip1 = Applet.newAudioClip(url);
            clip1.play();
        }
    }

    public static void loop(String filename) {
        URL url = null;

        try {
            File clip = new File(filename);
            if(clip.canRead()) {
                url = clip.toURI().toURL();
            }
        } catch (MalformedURLException var3) {
            var3.printStackTrace();
        }

        if(url == null) {
            throw new RuntimeException("audio " + filename + " not found");
        } else {
            AudioClip clip1 = Applet.newAudioClip(url);
            clip1.loop();
        }
    }

    private static byte[] readByte(String filename) {
        Object data = null;
        AudioInputStream ais = null;

        try {
            File e = new File(filename);
            byte[] data1;
            if(e.exists()) {
                ais = AudioSystem.getAudioInputStream(e);
                data1 = new byte[ais.available()];
                ais.read(data1);
            } else {
                URL url = StdAudio.class.getResource(filename);
                ais = AudioSystem.getAudioInputStream(url);
                data1 = new byte[ais.available()];
                ais.read(data1);
            }

            return data1;
        } catch (IOException var5) {
            System.out.println(var5.getMessage());
            throw new RuntimeException("Could not read " + filename);
        } catch (UnsupportedAudioFileException var6) {
            System.out.println(var6.getMessage());
            throw new RuntimeException(filename + " in unsupported audio format");
        }
    }

    public static void save(String filename, double[] input) {
        AudioFormat format = new AudioFormat(44100.0F, 16, 1, true, false);
        byte[] data = new byte[2 * input.length];

        for(int e = 0; e < input.length; ++e) {
            short ais = (short)((int)(input[e] * 32767.0D));
            data[2 * e + 0] = (byte)ais;
            data[2 * e + 1] = (byte)(ais >> 8);
        }

        try {
            ByteArrayInputStream var7 = new ByteArrayInputStream(data);
            AudioInputStream var8 = new AudioInputStream(var7, format, (long)input.length);
            if(!filename.endsWith(".wav") && !filename.endsWith(".WAV")) {
                if(!filename.endsWith(".au") && !filename.endsWith(".AU")) {
                    throw new RuntimeException("File format not supported: " + filename);
                }

                AudioSystem.write(var8, Type.AU, new File(filename));
            } else {
                AudioSystem.write(var8, Type.WAVE, new File(filename));
            }
        } catch (IOException var6) {
            System.out.println(var6);
            System.exit(1);
        }

    }

    private static double[] note(double hz, double duration, double amplitude) {
        int N = (int)(44100.0D * duration);
        double[] a = new double[N + 1];

        for(int i = 0; i <= N; ++i) {
            a[i] = amplitude * Math.sin(6.283185307179586D * (double)i * hz / 44100.0D);
        }

        return a;
    }

    public static void main(String[] args) {
        System.out.println('걄');
        double freq = 440.0D;

        for(int steps = 0; steps <= '걄'; ++steps) {
            play(0.5D * Math.sin(6.283185307179586D * freq * (double)steps / 44100.0D));
        }

        int[] var7 = new int[]{0, 2, 4, 5, 7, 9, 11, 12};

        for(int i = 0; i < var7.length; ++i) {
            double hz = 440.0D * Math.pow(2.0D, (double)var7[i] / 12.0D);
            play(note(hz, 1.0D, 0.5D));
        }

        close();
        System.exit(0);
    }

    static {
        init();
    }
}