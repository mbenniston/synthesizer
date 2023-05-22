package synthesizer.Playback;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat.Encoding;

import com.google.common.base.Stopwatch;

/**
 * Allows samples to be played to an audio device.
 * This class blocks while playing these samples and so sample collection is
 * driven by a SampleProvider interface the user must implement.
 */
public class AudioPlayer {
    public static final int SAMPLE_RATE = 44100;
    public static final double TIME_PER_SAMPLE = 1.0 / SAMPLE_RATE;
    public static final int SAMPLE_BITS = Short.BYTES * 8;
    public static final int NUM_BUFFERED_SAMPLES = 512;
    public static final double BLOCK_TIME = NUM_BUFFERED_SAMPLES * TIME_PER_SAMPLE;

    public static abstract class SampleProvider {
        public abstract double nextSample(double time, long sample);

        public void onBlockStart(double startTime, long startSample) {
        }

        public boolean isPlaying() {
            return true;
        }
    }

    public static void play(SampleProvider audioProvider) throws AudioPlayerException {
        AudioFormat format = new AudioFormat(
                Encoding.PCM_SIGNED, SAMPLE_RATE, SAMPLE_BITS,
                1, Short.BYTES, SAMPLE_RATE, true);

        SourceDataLine line = acquireLine(format);
        try {
            line.open(format, NUM_BUFFERED_SAMPLES * Short.BYTES);
        } catch (LineUnavailableException e) {
            throw new AudioPlayerException();
        }
        line.start();

        final int bufferSize = 2 * line.getBufferSize() / 2;
        final int bufferFrameCount = bufferSize / 2;

        byte[] buffer = new byte[bufferSize];
        ByteBuffer bufferWriter = ByteBuffer.wrap(buffer);

        long sampleCount = 0;

        while (audioProvider.isPlaying()) {
            final Stopwatch stopwatch = Stopwatch.createStarted();
            audioProvider.onBlockStart(sampleCount * TIME_PER_SAMPLE, sampleCount);

            for (int i = 0; i < bufferFrameCount; i++) {
                double sample = audioProvider.nextSample(
                        sampleCount * TIME_PER_SAMPLE,
                        sampleCount);

                bufferWriter.putShort((short) (sample * Short.MAX_VALUE));

                sampleCount++;
            }

            stopwatch.stop();

            final long elapsed = stopwatch.elapsed(TimeUnit.NANOSECONDS);
            if (elapsed > secondsToNanoSeconds(BLOCK_TIME)) {
                System.out.println("warning audio player is lagging");
            }

            line.write(buffer, 0, buffer.length);
            bufferWriter.clear();
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

    public static double nanoSecondsToSeconds(long nanoSeconds) {
        return nanoSeconds / 1000000000.0;
    }

    public static double secondsToNanoSeconds(double seconds) {
        return seconds * 1000000000.0;
    }

    public static class AudioPlayerException extends RuntimeException {
    }
}
