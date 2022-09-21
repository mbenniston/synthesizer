package synthesizer.WaveForms;

import synthesizer.Util.Tone;

public class Instrument {
    private VoiceCollection collection = new VoiceCollection();
    private Voice[] voices = new Voice[128];
    private double volumeScale = 1.0;

    public Instrument(Voice voicePrototype) throws CloneNotSupportedException {
        for (int i = 0; i < 128; i++) {
            Voice voice = voicePrototype.clone();
            voice.waveForm.setFrequency(Tone.getFrequencyFromTone(i));
            voices[i] = voice;
        }
    }

    public void PlayNote(int noteIndex, double currentTime) {
        collection.play(voices[noteIndex], currentTime);
    }

    public void StopNote(int noteIndex, double currentTime) {
        collection.stop(voices[noteIndex], currentTime);
    }

    public void update(double currentTime) {
        collection.update(currentTime);
    }

    public double sample(double currentTime) {
        return collection.sample(currentTime) * volumeScale;
    }

    public void printVoices(double currentTime) {
        boolean isVoicePlaying = false;
        for (Voice voice : voices) {
            if (voice.isPlaying(currentTime)) {
                isVoicePlaying = true;
                break;
            }
        }

        if (!isVoicePlaying)
            return;

        for (Voice voice : voices) {
            if (voice.isPlaying(currentTime)) {
                System.out.print("|");
            } else {
                System.out.print(" ");
            }
        }
        System.out.println();
    }

    public boolean isPlaying() {
        return collection.getActiveVoiceCount() > 0;
    }

    public void setVolumeScale(double volScale) {
        volumeScale = volScale;
    }
}
