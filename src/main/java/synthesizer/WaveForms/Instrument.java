package synthesizer.WaveForms;

import synthesizer.Util.Tone;

/**
 * Represents an instrument containing 128 individual playable notes.
 */
public class Instrument implements Cloneable {
    private final VoiceCollection collection = new VoiceCollection();
    private final Voice[] voices;
    private double volumeScale = 1.0;

    public Instrument clone() throws CloneNotSupportedException {
        Voice[] newVoices = new Voice[128];
        for (int i = 0; i < 128; i++) {
            newVoices[i] = voices[i].clone();
        }
        Instrument instrument = new Instrument(newVoices);
        instrument.setVolumeScale(volumeScale);
        return instrument;
    }

    public Instrument(Voice voicePrototype) {
        voices = new Voice[128];
        for (int i = 0; i < 128; i++) {
            Voice voice;

            try {
                voice = voicePrototype.clone();
            } catch (CloneNotSupportedException e) {
                voice = StandardVoices.createDefaultVoice();
            }

            voice.waveForm.setFrequency(Tone.getFrequencyFromTone(i));
            voices[i] = voice;
        }
    }

    public Instrument(Voice[] voices) {
        this.voices = voices;
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
