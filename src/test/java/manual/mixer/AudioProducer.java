package manual.mixer;

import java.util.concurrent.ThreadLocalRandom;

public class AudioProducer {

    final Thread thread;
    final AudioConsumer consumer;
    final short[] buffer = new short[SoundSettings.BUFFER_SIZE_FRAMES];

    public AudioProducer(AudioConsumer consumer) {
        this.consumer = consumer;
        thread = new Thread(this::run);
        thread.setDaemon(true);
    }

    public void start() {
        thread.start();
    }

    void run() {
        try {
            ThreadLocalRandom rand = ThreadLocalRandom.current();
            while (true) {
                long pos = consumer.position();
                ToneGenerator g = new ToneGenerator();

                pos += SoundSettings.BUFFER_SIZE_FRAMES + rand.nextInt(SoundSettings.BUFFER_SIZE_FRAMES);
                while (!g.done()) {
                    g.fill(buffer);
                    consumer.mix(pos, buffer);
                    pos += SoundSettings.BUFFER_SIZE_FRAMES;

                    double bufferLengthMillis = SoundSettings.BUFFER_SIZE_FRAMES * 1000.0 / SoundSettings.SAMPLE_RATE;
                    Thread.sleep((int) (bufferLengthMillis * 0.9));
                }

                Thread.sleep(1000 + rand.nextInt(2000));
            }
        } catch (Throwable t) {
            System.out.println(t.getMessage());
            t.printStackTrace();
        }
    }
}