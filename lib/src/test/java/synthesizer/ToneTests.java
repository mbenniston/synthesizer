package synthesizer;

import org.junit.jupiter.api.Test;

import synthesizer.Playback.AudioPlayer;
import synthesizer.Util.Tone;
import synthesizer.WaveForms.SineWave;

public class ToneTests {

    @Test
    void toneTest() {
        SineWave wave = new SineWave();
        wave.setFrequency(Tone.getFrequencyFromTone(40));

        AudioPlayer.play(new AudioPlayer.AudioProvider() {
            @Override
            public double nextFrame(double time, long sample) {
                return wave.sample(time);
            }
        });
    }
}
