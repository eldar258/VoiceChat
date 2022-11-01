package server;

import data.AddressWithPort;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

public class HostSendAllUsersTest {

    private final static int PORT = 6666;

    @Test
    public void whenSendAllUsersShouldSocketSendAll() throws IOException {
        Set<AddressWithPort> usersSet = new HashSet<>();
        InetAddress senderAddress = InetAddress.getByAddress(new byte[]{1, 1, 1, 1});
        usersSet.add(new AddressWithPort(senderAddress, PORT));
        int usersNumber = 100;
        for (byte i = 1; i <= usersNumber; i ++){
            InetAddress address = InetAddress.getByAddress(new byte[]{i, i, i, i});
            usersSet.add(new AddressWithPort(address, PORT));
        }

        DatagramSocket socketMock = mock(DatagramSocket.class);
        final byte expectedUsersNumberWithoutSender = (byte) (usersNumber - 1);
        byte[] dataForSend = getRandomByteArray();

        doAnswer(invocationOnMock -> {
            var packet = invocationOnMock.getArgument(0, DatagramPacket.class);
            byte[] massageForSend = packet.getData();

            if (!packet.getAddress().equals(senderAddress)) {

                assertEquals(dataForSend.length + 1, massageForSend.length);
                assertEquals(expectedUsersNumberWithoutSender, massageForSend[0]);
                final byte[] onlyMassage = Arrays.copyOfRange(massageForSend, 1, massageForSend.length);
                assertArrayEquals(dataForSend, onlyMassage);
            } else {
                fail();
            }
            return null;
        }).when(socketMock).send(any(DatagramPacket.class));

        HostSendAllUsers hostSendAllUsers = new HostSendAllUsers();
        hostSendAllUsers.sendAll(usersSet, senderAddress, socketMock, dataForSend);

        verify(socketMock, times(usersNumber - 1)).send(any(DatagramPacket.class));
    }

    private static byte[] getRandomByteArray() {
        Random random = new Random();
        int n = random.nextInt(1000);
        return new byte[++n];
    }
}
