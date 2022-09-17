package synthesizer.WaveForms;

public class Envelope implements Cloneable {
    public double attackTime = 0.01;
    public double decayTime = 0.01;
    public double releaseTime = 0.02;

    public double sustainAmplitude = 0.8;
    public double peekAmplitude = 1.0;

    private double triggerOnTime = 0.0;
    private double triggerOffTime = 0.0;
    private boolean triggeredOn = false;
    private boolean triggeredOff = false;

    public double getAmplitude(double time) {

        if (triggeredOff) {
            return getTriggeredOffAmplitude(time - triggerOffTime);
        }
        if (triggeredOn) {
            return getTriggeredOnAmplitude(time - triggerOnTime);
        }

        return 0.0;
    }

    private double getTriggeredOnAmplitude(double duration) {
        if (duration < attackTime) {
            double attackProgress = duration / attackTime;

            return getAttackAmplitude(attackProgress);

        } else if (duration < attackTime + decayTime) {
            double decayProgress = (duration - attackTime) / decayTime;

            return getDecayAmplitude(decayProgress);
        }

        return sustainAmplitude;
    }

    private double getAttackAmplitude(double progress) {
        return peekAmplitude * progress;
    }

    private double getDecayAmplitude(double progress) {
        return peekAmplitude - (peekAmplitude - sustainAmplitude) * progress;
    }

    private double getTriggeredOffAmplitude(double duration) {

        if (duration < releaseTime) {
            double releaseProgress = duration / releaseTime;

            return getReleaseAmplitude(releaseProgress);
        }

        return 0.0;
    }

    private double getReleaseAmplitude(double progress) {
        return sustainAmplitude * (1.0 - progress);
    }

    public void triggerOn(double time) {
        if (!triggeredOn) {
            triggerOnTime = time;
            triggeredOn = true;
        }
    }

    public void triggerOff(double time) {
        if (!triggeredOff) {
            triggerOffTime = time;
            triggeredOff = true;
        }
    }

    public void reset() {
        triggeredOff = false;
        triggeredOn = false;
    }

    public boolean isPlaying(double currentTime) {
        return triggeredOn && !hasFinished(currentTime);
    }

    public boolean hasFinished(double currentTime) {
        if (triggeredOff) {
            return currentTime - triggerOffTime > releaseTime;
        }

        return !triggeredOn;
    }

    public Envelope clone() throws CloneNotSupportedException {
        return (Envelope) super.clone();
    }
}
