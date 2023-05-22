# Synthesizer

Java library that implements basic audio synthesis.

Used in [Synthesizer visualizer](https://github.com/mbenniston/synthesizer-visualizer.git) for midi audio playback.

Features:

- Playback of audio samples
- Standard envelope implementation
- Sine waveform
- Saw waveform
- Triangle waveform
- Modulated waveform
- Additive waveform
- Noise waveform
- Sample waveform

Missing features:

- Robust audio playback
    - Picking specific audio device to playback from.
    - Choosing correct sample rate and block size for optimal latency and quality.

## Key classes / packages

- [WaveForms](/src/main/java/synthesizer/WaveForms), contains all implemented waveform types.
- [AudioPlayer](/src/main/java/synthesizer/Playback/AudioPlayer.java), for playing audio samples.
