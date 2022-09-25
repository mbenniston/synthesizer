package synthesizer.WaveForms;

import java.util.Random;

public class NoiseWave extends WaveForm {
    private Random random = new Random();

    public double sample(double time) {
        final double playTime = time - getStartTime();

        random.setSeed((long) (playTime * 123971307 * getFrequency()));
        return random.nextDouble(-1.0, 1.0) * getAmplitude();
    }
}
