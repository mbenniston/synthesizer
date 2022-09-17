package synthesizer.WaveForms;

public class TriangleWave extends WaveForm {

    public double sample(double time) {
        final double playTime = time - getStartTime();
        final double frequency = getFrequency();
        final double amplitude = getAmplitude();

        return Math.asin(Math.sin(playTime * frequency * 2 * Math.PI)) * 2 / Math.PI * amplitude;
    }
}