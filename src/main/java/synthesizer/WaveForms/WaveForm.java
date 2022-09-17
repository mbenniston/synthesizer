package synthesizer.WaveForms;

public abstract class WaveForm implements Cloneable {
    private double frequency = 1.0;
    private double amplitude = 1.0;
    private double startTime = 0.0;

    public abstract double sample(double time);

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public WaveForm clone() throws CloneNotSupportedException {
        return (WaveForm) super.clone();
    }
}
