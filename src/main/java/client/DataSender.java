package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DataSender {

    public void sendData(DatagramSocket socket, InetAddress address, int port, byte[] data) {
        DatagramPacket datagramPacket = new DatagramPacket(data, data.length, address, port);

        try {
            socket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
