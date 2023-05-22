package synthesizer.WaveForms;

/**
 * Implements a square waveform.
 */
public class SquareWave extends WaveForm {

    public double sample(double time) {
        final double playTime = time - getStartTime();
        final double frequency = getFrequency();
        final double amplitude = getAmplitude();

        return Math.sin(playTime * frequency * 2 * Math.PI) > 0.5 ? amplitude : -amplitude;
    }
}