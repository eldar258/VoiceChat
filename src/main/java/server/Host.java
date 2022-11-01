package server;

import data.AddressWithPort;
import helper.Consts;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class Host extends Thread {
    private final DatagramSocket socket;
    private final int portClient;

    public Host(String portClient, String portHost) throws SocketException {
        this.portClient = Integer.parseInt(portClient);
        socket = new DatagramSocket(Integer.parseInt(portHost));
    }

    @Override
    public void run() {
        System.out.println("host started");
        byte[] buf = new byte[Consts.BUFFER_SIZE];

        Set<AddressWithPort> usersConnecting = new HashSet<>();
        while (!this.isInterrupted()) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                buf = packet.getData();
            } catch (IOException e) {
                e.printStackTrace();
                this.interrupt();
            }

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            usersConnecting.add(new AddressWithPort(address, port));
            byte numberUsers = (byte) usersConnecting.size();
            byte[] parameters = new byte[5];
            for (var el : usersConnecting) {
                if (!el.address().equals(address)) {
                    parameters = Arrays.copyOf(address.getAddress(), parameters.length);
                    parameters[4] = numberUsers;
                    byte[] sendBuf = joinArray(parameters, buf);
                    packet = new DatagramPacket(sendBuf, sendBuf.length, el.address(), portClient);
                    try {
                        socket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                        this.interrupt();
                    }
                }
            }
        }

        socket.close();
    }

    private byte[] joinArray(byte[] head, byte[] tail) {
        byte[] result = Arrays.copyOf(head, head.length + tail.length);
        System.arraycopy(tail, 0, result, head.length, tail.length);

        return result;
    }
}
