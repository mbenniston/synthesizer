package synthesizer.WaveForms;

/**
 * Takes two input waves and combines them additively.
 */
public class AdditiveWave extends WaveForm {
    private WaveForm bottomWave;
    private WaveForm topWave;

    private double lastTime = 0;
    private double playTime = 0;

    public AdditiveWave(WaveForm bottomWave, WaveForm topWave) {
        this.bottomWave = bottomWave;
        this.topWave = topWave;
    }

    @Override
    public void setStartTime(double startTime) {
        super.setStartTime(startTime);
        lastTime = 0;
        playTime = 0;
    }

    @Override
    public double sample(double time) {
        final double newPlayTime = (time - getStartTime());

        final double playTimeDelta = newPlayTime - lastTime;
        playTime += playTimeDelta * getFrequency();
        lastTime = newPlayTime;

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
