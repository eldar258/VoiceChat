package helper;

import java.util.Random;

public class ValuesGenerator {
    public static byte[] getRandomByteArray() {
        Random random = new Random();
        int n = random.nextInt(1000);
        byte[] result = new byte[++n];
        random.nextBytes(result);
        return result;
    }
}
