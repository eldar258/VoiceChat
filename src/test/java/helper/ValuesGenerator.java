package helper;

import java.net.DatagramPacket;
import java.util.Random;

public class ValuesGenerator {
    public static byte[] getRandomByteArray() {
        Random random = new Random();
        int n = random.nextInt(1000);
        byte[] result = new byte[++n];
        random.nextBytes(result);
        return result;
    }

    public static DatagramPacket getDatagramPacket() {
        byte[] buf = new byte[100];
        return new DatagramPacket(buf, buf.length);
    }
}
