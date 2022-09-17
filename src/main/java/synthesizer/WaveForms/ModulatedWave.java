package synthesizer.WaveForms;

public class ModulatedWave extends WaveForm {
    private WaveForm messageWave;
    private WaveForm modulatingWave;

    public ModulatedWave(WaveForm messageWav, WaveForm modulatingWave) {
        this.messageWave = messageWav;
        this.modulatingWave = modulatingWave;
    }

    @Override
    public double sample(double time) {
        final double playTime = time - getStartTime();

        messageWave.setFrequency(getFrequency() + modulatingWave.sample(playTime));

        return messageWave.sample(playTime) * getAmplitude();
    }

    public WaveForm clone() throws CloneNotSupportedException {
        ModulatedWave wave = new ModulatedWave(messageWave.clone(), modulatingWave.clone());
        wave.setFrequency(getFrequency());
        wave.setAmplitude(getAmplitude());
        wave.setStartTime(getStartTime());
        return wave;
    }
}
