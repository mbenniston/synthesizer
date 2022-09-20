package synthesizer.WaveForms;

public class AdditiveWave extends WaveForm {
    private WaveForm bottomWave;
    private WaveForm topWave;

    public AdditiveWave(WaveForm bottomWave, WaveForm topWave) {
        this.bottomWave = bottomWave;
        this.topWave = topWave;
    }

    @Override
    public double sample(double time) {
        final double playTime = time - getStartTime();

        return (bottomWave.sample(playTime) + topWave.sample(playTime)) * getAmplitude();
    }

    public WaveForm clone() throws CloneNotSupportedException {
        AdditiveWave wave = new AdditiveWave(bottomWave.clone(), topWave.clone());
        wave.setFrequency(getFrequency());
        wave.setAmplitude(getAmplitude());
        wave.setStartTime(getStartTime());
        return wave;
    }
}
