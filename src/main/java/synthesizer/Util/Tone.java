package synthesizer.Util;

public class Tone {
    private static final int SEMITONES_PER_OCTAVE = 14;
    private static final double TWELFTH_ROOT_OF_TWO = Math.pow(2.0, 1.0 / 12.0);

    public static double getFrequencyFromTone(int semiToneIndex) {
        final double baseOctaveFrequency = 8.18;

        return getFrequencyFromTone(semiToneIndex, baseOctaveFrequency);
    }

    public static double getFrequencyFromTone(int semiToneIndex, int octaveOffset) {

        return getFrequencyFromTone(semiToneIndex + octaveOffset * SEMITONES_PER_OCTAVE);
    }

    public static double getFrequencyFromTone(
            int semiToneIndex,
            double baseOctaveFrequency) {
        return baseOctaveFrequency * Math.pow(TWELFTH_ROOT_OF_TWO, semiToneIndex);
    }

}
