package manual.mixer;

import java.util.concurrent.ThreadLocalRandom;

public class ToneGenerator {

    private static final double[] NOTES = {261.63, 311.13, 392.00};
    private static final double[] OCTAVES = {1.0, 2.0, 4.0, 8.0};
    private static final double[] LENGTHS = {0.05, 0.25, 1.0, 2.5, 5.0};

    private double phase;
    private int framesProcessed;
    private final double length;
    private final double frequency;

    public ToneGenerator() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        length = LENGTHS[rand.nextInt(LENGTHS.length)];
        frequency = NOTES[rand.nextInt(NOTES.length)] * OCTAVES[rand.nextInt(OCTAVES.length)];
    }

    public void fill(short[] block) {
        for (int i = 0; i < block.length; i++) {
            double sample = Math.sin(phase * 2.0 * Math.PI);
            block[i] = (short) (sample * Short.MAX_VALUE);
            phase += frequency / SoundSettings.SAMPLE_RATE;
        }
        framesProcessed += block.length;
    }

    public boolean done() {
        return framesProcessed >= length * SoundSettings.SAMPLE_RATE;
    }
}
