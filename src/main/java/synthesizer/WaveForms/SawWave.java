package synthesizer.WaveForms;

/**
 * Implements a saw waveform.
 */
public class SawWave extends WaveForm {

    public double sample(double time) {
        final double playTime = time - getStartTime();
        final double frequency = getFrequency();
        final double amplitude = getAmplitude();

        return ((2 / Math.PI) * (frequency * Math.PI * (playTime % (1.0 / frequency)) - (Math.PI / 2))) * amplitude;
    }
}