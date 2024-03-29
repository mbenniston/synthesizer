package synthesizer.WaveForms;

/**
 * Combines a wave form with an envelope to represent a single playable note.
 */
public class Voice implements Cloneable {
    public final WaveForm waveForm;
    public final Envelope envelope;

    public Voice(WaveForm waveForm, Envelope envelope) {
        this.waveForm = waveForm;
        this.envelope = envelope;
    }

    public void start(double startTime) {
        envelope.reset();
        envelope.triggerOn(startTime);

        waveForm.triggerOn(startTime);
        waveForm.setStartTime(startTime);
    }

    public void stop(double stopTime) {
        envelope.triggerOff(stopTime);
        waveForm.triggerOff(stopTime);
    }

    public boolean isPlaying(double currentTime) {
        return envelope.isPlaying(currentTime);
    }

    public boolean isStopped(double currentTime) {
        return envelope.hasFinished(currentTime);
    }

    public double sample(double time) {
        return waveForm.sample(time) * envelope.getAmplitude(time);
    }

    public Voice clone() throws CloneNotSupportedException {
        return new Voice(waveForm.clone(), envelope.clone());
    }
}
