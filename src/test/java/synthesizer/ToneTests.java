package synthesizer;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import synthesizer.Config.InstrumentReader;
import synthesizer.Config.InstrumentReader.LoadedInstrument;
import synthesizer.Playback.AudioPlayer;
import synthesizer.WaveForms.ModulatedWave;
import synthesizer.WaveForms.SawWave;
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
            private boolean playing = true;

            @Override
            public double nextSample(double time, long sample) {
                return w.sample(time);
            }

            @Override
            public void onBlockStart(double startTime, long startSample) {
                playing = startTime < 3;
            }

            @Override
            public boolean isPlaying() {
                return playing;
            }
        });
    }

    @Test
    void toneTest2() {
        SawWave wave = new SawWave();

        SineWave w3 = new SineWave();
        w3.setAmplitude(0.05);
        w3.setFrequency(10);

        ModulatedWave w = new ModulatedWave(wave, w3);
        w.setFrequency(440);

        AudioPlayer.play(new AudioPlayer.SampleProvider() {
            private boolean playing = true;

            @Override
            public double nextSample(double time, long sample) {
                return w.sample(time);
            }

            @Override
            public void onBlockStart(double startTime, long startSample) {
                playing = startTime < 3;
            }

            @Override
            public boolean isPlaying() {
                return playing;
            }
        });
    }

    @Test
    void voiceReaderTest() throws IOException, JSONException, UnsupportedAudioFileException {
        // var out =
        // VoiceReader.load(ToneTests.class.getClassLoader().getResourceAsStream("voices.json"));

        // LoadedInstrument v = out.get("myVoice2");
        // v.instrument.start(0);

        // AudioPlayer.play(new AudioPlayer.SampleProvider() {
        // @Override
        // public double nextSample(double time, long sample) {
        // return v.sample(time);
        // }
        // });
    }
}
