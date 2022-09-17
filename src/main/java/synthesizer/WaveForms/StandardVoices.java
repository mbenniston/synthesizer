package synthesizer.WaveForms;

public class StandardVoices {

    public static Voice createDefaultVoice() {
        WaveForm w = new TriangleWave();
        Envelope envelope = new Envelope();
        w.setAmplitude(1 / 12.0);
        // envelope.releaseTime = 0.7;

        return new Voice(w, envelope);
    }

    public static Voice createDefaultVoice2() {
        WaveForm w = new SquareWave();
        Envelope envelope = new Envelope();
        w.setAmplitude(1 / 12.0);

        WaveForm modulatingWave = new NoiseWave();
        modulatingWave.setAmplitude(0.005);
        modulatingWave.setFrequency(5.0);

        return new Voice(new ModulatedWave(w, modulatingWave), envelope);
    }

    public static Voice createDefaultVoice3() {
        WaveForm w = new SquareWave();
        Envelope envelope = new Envelope();
        w.setAmplitude(1 / 12.0);

        WaveForm modulatingWave = new NoiseWave();
        modulatingWave.setAmplitude(0.005);
        modulatingWave.setFrequency(5.0);

        return new Voice(new ModulatedWave(w, modulatingWave), envelope);
    }

}
