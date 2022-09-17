package synthesizer.WaveForms;

public class SineWave extends WaveForm {

    public double sample(double time) {
        final double playTime = time - getStartTime();
        final double frequency = getFrequency();
        final double amplitude = getAmplitude();

        return Math.sin(playTime * frequency * 2 * Math.PI) * amplitude;
    }
}