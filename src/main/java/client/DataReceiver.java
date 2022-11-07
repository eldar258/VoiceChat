package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class DataReceiver {

    public byte[][] receive(DatagramSocket socket, byte[] emptyBuffer) {
        DatagramPacket datagramPacket = new DatagramPacket(emptyBuffer, emptyBuffer.length);
        try {
            socket.receive(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[][] result = new byte[2][];
        result[0] = new byte[1];

        var data = datagramPacket.getData();
        result[0][0] = data[0];
        result[1] = Arrays.copyOfRange(data, 1, data.length);

        return result;
    }
}
