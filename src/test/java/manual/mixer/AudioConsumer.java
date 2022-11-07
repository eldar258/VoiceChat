package manual.mixer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

public class AudioConsumer {

    private static final double MIXDOWN_VOLUME = 1.0 / SoundSettings.NUM_PRODUCERS;

    private final List<QueuedBlock> finished = new ArrayList<>();
    private final short[] mixBuffer = new short[SoundSettings.BUFFER_SIZE_FRAMES];
    private final byte[] audioBuffer = new byte[SoundSettings.BUFFER_SIZE_FRAMES * 2];

    private final Thread thread;
    private final AtomicLong position = new AtomicLong();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final ConcurrentLinkedQueue<QueuedBlock> scheduledBlocks = new ConcurrentLinkedQueue<>();


    public AudioConsumer() {
        thread = new Thread(this::run);
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        running.set(false);
    }

    public long position() {
        return position.get();
    }

    public void mix(long when, short[] block) {
        scheduledBlocks.add(new QueuedBlock(when, Arrays.copyOf(block, block.length)));
    }

    private void run() {
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        try (Mixer mixer = AudioSystem.getMixer(mixerInfo[SoundSettings.INDEX_SPEAKER])) {
            Line.Info[] lineInfo = mixer.getSourceLineInfo();
            try (SourceDataLine line = (SourceDataLine) mixer.getLine(lineInfo[0])) {
                line.open(new AudioFormat(SoundSettings.SAMPLE_RATE, 16, 1, true, false), SoundSettings.BUFFER_SIZE_FRAMES);
                line.start();
                while (running.get())
                    processSingleBuffer(line);
                line.stop();
            }
        } catch (LineUnavailableException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void processSingleBuffer(SourceDataLine line) {

        Arrays.fill(mixBuffer, (short) 0);
        long bufferStartAt = position.get();

        for (QueuedBlock block : scheduledBlocks) {

            int blockFrames = block.data().length;

            if (block.when() + blockFrames <= bufferStartAt) {
                finished.add(block);
                continue;
            }

            if (bufferStartAt + SoundSettings.BUFFER_SIZE_FRAMES <= block.when()) continue;

            int blockOffset = Math.max(0, (int) (bufferStartAt - block.when()));
            int blockMaxFrames = blockFrames - blockOffset;
            int bufferOffset = Math.max(0, (int) (block.when() - bufferStartAt));
            int bufferMaxFrames = SoundSettings.BUFFER_SIZE_FRAMES - bufferOffset;
            for (int i = 0; i < blockMaxFrames && i < bufferMaxFrames; i++)
            {
                int bufferIndex = bufferOffset + i;
                int blockIndex = blockOffset + i;
                mixBuffer[bufferIndex] += (short) (block.data()[blockIndex] * MIXDOWN_VOLUME);
            }
        }

        scheduledBlocks.removeAll(finished);
        finished.clear();
        ByteBuffer.wrap(audioBuffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(mixBuffer);
        line.write(audioBuffer, 0, audioBuffer.length);
        position.addAndGet(SoundSettings.BUFFER_SIZE_FRAMES);
    }
}
