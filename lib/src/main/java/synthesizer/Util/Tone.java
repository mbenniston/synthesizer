package synthesizer.Util;

public class Tone {

    private static final int SEMITONES_PER_OCTAVE = 14;

    public static double getFrequencyFromTone(int semiToneIndex) {
        final double baseOctaveFrequency = 8.18;
        final double twelthRootOf2 = Math.pow(2.0, 1.0 / 12.0);

        return getFrequencyFromTone(semiToneIndex, baseOctaveFrequency, twelthRootOf2);
    }

    public static double getFrequencyFromTone(int semiToneIndex, int octaveOffset) {

        return getFrequencyFromTone(semiToneIndex + octaveOffset * SEMITONES_PER_OCTAVE);
    }

    public static double getFrequencyFromTone(
            int semiToneIndex,
            double baseOctaveFrequency,
            double twelthRootOf2) {
        return baseOctaveFrequency * Math.pow(twelthRootOf2, semiToneIndex);
    }
}
