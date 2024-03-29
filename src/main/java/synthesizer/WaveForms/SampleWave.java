package synthesizer.WaveForms;

import synthesizer.Util.Tone;

/**
 * Uses sample data to play audio.
 * Has start, loop and end all in audio sample.
 */
public class SampleWave extends WaveForm {
    private final double[] sampleData;
    private final int loopSampleStart;
    private final int loopSampleEnd;
    private boolean triggeredOff = false;

    public SampleWave(double[] sampleData, int loopSampleStart, int loopSampleEnd) {
        this.sampleData = sampleData;
        this.loopSampleStart = loopSampleStart;
        this.loopSampleEnd = loopSampleEnd;
    }

    public SampleWave(double[] sampleData) {
        this.sampleData = sampleData;
        this.loopSampleStart = 0;
        this.loopSampleEnd = sampleData.length - 1;
    }

    @Override
    public void triggerOn(double startTime) {
        triggeredOff = false;
    }

    @Override
    public void triggerOff(double startTime) {
        triggeredOff = true;
    }

    @Override
    public double sample(double time) {
        final double playTime = Math.max(0.0, time - getStartTime());

        int overallSample = (int) (playTime * 44100.0 * getFrequency());

        int sampleIndex = overallSample % sampleData.length;

        if (overallSample > loopSampleStart) {

            if (triggeredOff) {
            } else {
                final int loopDuration = loopSampleEnd - loopSampleStart;
                sampleIndex = loopSampleStart + (overallSample - loopSampleStart) % loopDuration;
            }
        }

        return sampleData[sampleIndex] * getAmplitude();
    }
}
