package server;

import data.AddressWithPort;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Set;

public class HostSendAllUsers {

    public void sendAll(Set<AddressWithPort> usersSet, InetAddress senderAddress, DatagramSocket socket, byte[] dataForSend) {
        int usersNumberWithoutSender = usersSet.size();
        if (--usersNumberWithoutSender == 0) return;
        for (var el : usersSet) {
            if (!el.address().equals(senderAddress)) {
                var dataForSendWithUsersNumber = new byte[dataForSend.length + 1];
                dataForSendWithUsersNumber[0] = (byte) usersNumberWithoutSender;
                System.arraycopy(dataForSend, 0, dataForSendWithUsersNumber, 1, dataForSend.length);

                DatagramPacket packet = new DatagramPacket(dataForSendWithUsersNumber, dataForSendWithUsersNumber.length,
                        el.address(), el.port());
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
