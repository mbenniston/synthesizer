package synthesizer;

import org.junit.jupiter.api.Test;

import synthesizer.Playback.AudioPlayer;
import synthesizer.WaveForms.ModulatedWave;
import synthesizer.WaveForms.SineWave;

public class ToneTests {

    @Test
    void toneTest() {
        SineWave wave = new SineWave();

        SineWave w3 = new SineWave();
        w3.setAmplitude(0.05);
        w3.setFrequency(10);

        ModulatedWave w = new ModulatedWave(wave, w3);
        w.setFrequency(440);

        AudioPlayer.play(new AudioPlayer.SampleProvider() {
            @Override
            public double nextSample(double time, long sample) {
                return w.sample(time);
            }
        });
    }
}
