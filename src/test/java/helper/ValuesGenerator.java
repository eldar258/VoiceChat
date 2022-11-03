package helper;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class ValuesGenerator {

    private static final Random random = new Random();

    public static byte[] getRandomByteArray() {
        int n = random.nextInt(1000);
        byte[] result = new byte[++n];
        random.nextBytes(result);
        return result;
    }

    public static DatagramPacket getDatagramPacket() {
        byte[] buf = new byte[100];
        return new DatagramPacket(buf, buf.length);
    }

    public static InetAddress getRandomInetAddress() throws UnknownHostException {
        byte[] address = new byte[4];
        random.nextBytes(address);
        return InetAddress.getByAddress(address);
    }
}
