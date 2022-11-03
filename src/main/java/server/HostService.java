package server;

import data.AddressWithPort;
import helper.Consts;
import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashSet;
import java.util.Set;

public class HostService implements Closeable {

    private final HostReceiveFromUser hostReceiveFromUser;
    private final HostSendAllUsers hostSendAllUsers;
    private final DatagramSocket socket;
    private final Set<AddressWithPort> users = new HashSet<>();
    private final byte[] buf;

    public HostService(HostReceiveFromUser hostReceiveFromUser, HostSendAllUsers hostSendAllUsers,
            DatagramSocket socket) {
        this.hostReceiveFromUser = hostReceiveFromUser;
        this.hostSendAllUsers = hostSendAllUsers;
        this.socket = socket;
        this.buf = new byte[Consts.BUFFER_SIZE];
    }

    public void execute() {
        DatagramPacket packet = hostReceiveFromUser.receiveMassage(socket, buf);
        var address = packet.getAddress();
        users.add(new AddressWithPort(address, packet.getPort()));
        hostSendAllUsers.sendAll(users, address, socket, packet.getData());
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
