package synthesizer;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import synthesizer.Config.VoiceReader;
import synthesizer.Playback.AudioPlayer;
import synthesizer.WaveForms.ModulatedWave;
import synthesizer.WaveForms.SineWave;
import synthesizer.WaveForms.Voice;

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

    @Test
    void voiceReaderTest() throws IOException {
        var out = VoiceReader.load(ToneTests.class.getClassLoader().getResourceAsStream("voices.json"));

        Voice v = out.get("myVoice2");
        v.start(0);

        AudioPlayer.play(new AudioPlayer.SampleProvider() {
            @Override
            public double nextSample(double time, long sample) {
                return v.sample(time);
            }
        });
    }
}
