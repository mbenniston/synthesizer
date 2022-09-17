package synthesizer.Playback;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat.Encoding;

public class AudioPlayer {
    private static final int SAMPLE_RATE = 44100;
    private static final double TIME_PER_SAMPLE = 1.0 / SAMPLE_RATE;
    private static final int SAMPLE_BITS = Short.BYTES * 8;

    public static abstract class AudioProvider {
        public abstract double nextFrame(double time, long sample);

        public boolean isPlaying() {
            return true;
        }
    }

    public static void play(AudioProvider audioProvider) throws AudioPlayerException {
        AudioFormat format = new AudioFormat(
                Encoding.PCM_SIGNED, SAMPLE_RATE, SAMPLE_BITS,
                1, Short.BYTES, SAMPLE_RATE, true);

        SourceDataLine line = acquireLine(format);
        try {
            line.open();
        } catch (LineUnavailableException e) {
            throw new AudioPlayerException();
        }
        line.start();

        long sampleCount = 0;

        while (audioProvider.isPlaying()) {
            int bytesNeeded = line.available();
            int numSamples = bytesNeeded / Short.BYTES;

            if (numSamples > 0) {
                ByteBuffer buffer = ByteBuffer.allocate(numSamples * Short.BYTES);

                for (int i = 0; i < numSamples; i++) {
                    double sample = audioProvider.nextFrame(
                            sampleCount * TIME_PER_SAMPLE,
                            sampleCount);

                    buffer.putShort((short) (sample *
                            Short.MAX_VALUE));

                    sampleCount++;
                }

                byte[] bytes = buffer.array();
                line.write(bytes, 0, bytes.length);
            }
        }

        line.drain();
        line.stop();
    }

    private static SourceDataLine acquireLine(AudioFormat format) {
        DataLine.Info info = new DataLine.Info(
                SourceDataLine.class,
                format);

        if (!AudioSystem.isLineSupported(info)) {
            throw new AudioPlayerException();
        }

        try {
            return (SourceDataLine) AudioSystem.getLine(info);
        } catch (LineUnavailableException e) {
            throw new AudioPlayerException();
        }
    }

    public static class AudioPlayerException extends RuntimeException {

    }
}
