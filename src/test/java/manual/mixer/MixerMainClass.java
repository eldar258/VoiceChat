package manual.mixer;

import java.util.Scanner;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class MixerMainClass {


    public static void main(String[] args) {
        printAllDevices();

        System.out.print("Press enter to exit...");
        AudioConsumer consumer = new AudioConsumer();
        consumer.start();
        for (int i = 0; i < SoundSettings.NUM_PRODUCERS; i++) new AudioProducer(consumer).start();

        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        consumer.stop();
    }

    private static void printAllDevices() {
        var infos = AudioSystem.getMixerInfo();
        int count = 0;
        for (var el0 : infos) {
            System.out.println(count + "_" + el0);

            var mixer = AudioSystem.getMixer(el0);

            for (var el2 : mixer.getSourceLineInfo()) {
                System.out.println(count + "__" + el2);
                Line line;
                try {
                    line = AudioSystem.getLine(el2);
                } catch (LineUnavailableException e) {
                    throw new RuntimeException(e);
                }
                if (line instanceof SourceDataLine) {
                    System.out.println(count + "___/|\\SPEAKER/|\\");
                }
            }
            for (var el2 : mixer.getTargetLineInfo()) {
                System.out.println(count + "__" + el2);

                Line line;
                try {
                    line = AudioSystem.getLine(el2);
                } catch (LineUnavailableException e) {
                    throw new RuntimeException(e);
                }
                if (line instanceof TargetDataLine) {
                    System.out.println(count + "___/|\\MICROPHONE/|\\");
                }
            }

            count++;
        }
    }
}
