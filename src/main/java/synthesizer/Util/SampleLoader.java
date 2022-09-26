package synthesizer.Util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFormat.Encoding;

// load sample data from audio file
// sample data is converted to our audio format
// so no runtime conversion is needed
public class SampleLoader {

    public static double[] loadSample(InputStream stream) throws IOException, UnsupportedAudioFileException {

        final AudioInputStream inputStreamAnyFormat = AudioSystem.getAudioInputStream(stream);
        final AudioFormat targetFormat = new AudioFormat(Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, true);
        final AudioInputStream targetStream = AudioSystem.getAudioInputStream(targetFormat, inputStreamAnyFormat);

        byte[] bytes = targetStream.readAllBytes();
        double[] output = new double[bytes.length / 2];
        var buffer = ByteBuffer.wrap(bytes).asShortBuffer();

        for (int i = 0; i < bytes.length / 2; i++) {
            output[i] = buffer.get(i) / (double) Short.MAX_VALUE;
        }

        return output;
    }

}
