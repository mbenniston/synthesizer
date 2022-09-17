package synthesizer.WaveForms;

import java.util.ArrayList;

public class VoiceCollection {
    private ArrayList<Voice> voices = new ArrayList<>();

    public void play(Voice voice, double startTime) {
        if (!voices.contains(voice)) {
            voice.start(startTime);
            voices.add(voice);
        }
    }

    public void stop(Voice voice, double stopTime) {
        voice.stop(stopTime);
    }

    public void update(double currentTime) {
        ArrayList<Voice> toRemove = new ArrayList<>();

        for (Voice voice : voices) {
            if (voice.isStopped(currentTime)) {
                toRemove.add(voice);
            }
        }

        for (Voice voice : toRemove) {
            voices.remove(voice);
        }
    }

    public double sample(double time) {
        double totalAmplitude = 0.0;

        for (Voice voice : voices) {
            totalAmplitude += voice.sample(time);
        }

        if (totalAmplitude > 1) {
            totalAmplitude = 1;
        }
        if (totalAmplitude < -1) {
            totalAmplitude = -1;
        }

        return totalAmplitude;
    }

    public boolean hasVoice(Voice voice) {
        return voices.contains(voice);
    }

    public int getActiveVoiceCount() {
        return voices.size();
    }
}
