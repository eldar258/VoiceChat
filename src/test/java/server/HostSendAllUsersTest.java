package server;

import data.AddressWithPort;
import helper.ValuesGenerator;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

public class HostSendAllUsersTest {

    private final static int PORT = 6666;

    private Set<AddressWithPort> usersSet;
    private InetAddress senderAddress;
    int usersNumber;
    DatagramSocket socketMock;

    @Before
    public void beforeSetup() throws IOException{
        usersSet = new HashSet<>();
        senderAddress = InetAddress.getByAddress(new byte[]{1, 1, 1, 1});
        usersSet.add(new AddressWithPort(senderAddress, PORT));
        usersNumber = 100;
        for (byte i = 1; i <= usersNumber; i ++){
            InetAddress address = InetAddress.getByAddress(new byte[]{i, i, i, i});
            usersSet.add(new AddressWithPort(address, PORT));
        }

        socketMock = mock(DatagramSocket.class);
    }

    @Test
    public void whenSendAllUsersShouldSocketSendAll() throws IOException {

        final byte expectedUsersNumberWithoutSender = (byte) (usersNumber - 1);
        byte[] dataForSend = ValuesGenerator.getRandomByteArray();

        doAnswer(invocationOnMock -> {
            var packet = invocationOnMock.getArgument(0, DatagramPacket.class);
            byte[] sendedMassage = packet.getData();

            if (!packet.getAddress().equals(senderAddress)) {

                assertEquals(dataForSend.length + 1, sendedMassage.length);
                assertEquals(expectedUsersNumberWithoutSender, sendedMassage[0]);
                final byte[] onlyMassage = Arrays.copyOfRange(sendedMassage, 1, sendedMassage.length);
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

    @Test
    public void whenUsersForSendEqualsZeroShouldDoNotSend() throws IOException {
        HostSendAllUsers hostSendAllUsers = new HostSendAllUsers();

        var addressWithPortOptional = usersSet.stream().findAny();
        usersSet.clear();
        usersSet.add(addressWithPortOptional.orElseThrow());
        assertEquals(1, usersSet.size());
        hostSendAllUsers.sendAll(usersSet, senderAddress, socketMock, ValuesGenerator.getRandomByteArray());

        verify(socketMock, times(0)).send(any());
    }

    @Test
    public void whenSocketSendAndMustThrowExceptionShouldThrowException() throws IOException {
        HostSendAllUsers hostSendAllUsers = new HostSendAllUsers();
        IOException ioExceptionMock = spy(IOException.class);
        doThrow(ioExceptionMock).when(socketMock).send(any());

        hostSendAllUsers.sendAll(usersSet, senderAddress, socketMock, ValuesGenerator.getRandomByteArray());

        verify(socketMock, atLeastOnce()).send(any());
        verify(ioExceptionMock, atLeastOnce()).printStackTrace();
    }
}
