package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class HostReceiveFromUser {

    public DatagramPacket receiveMassage(DatagramSocket socketMock, byte[] emptyBuffer) {
        DatagramPacket datagramPacket = new DatagramPacket(emptyBuffer, emptyBuffer.length);

        try {
            socketMock.receive(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return datagramPacket;
    }
}
