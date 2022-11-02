package server;

import helper.ValuesGenerator;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HostReceiveFromUserTest {

    private final static int PORT = 6666;

    private DatagramSocket socketMock;

    @Before
    public void beforeSetup() {
        socketMock = mock(DatagramSocket.class);
    }

    @Test
    public void whenReceiveMsgShouldGetMsg() throws IOException {
        byte[] expected = ValuesGenerator.getRandomByteArray();
        HostReceiveFromUser hostReceiveFromUser = new HostReceiveFromUser();

        InetAddress expectedAddress = InetAddress.getByAddress(new byte[]{1, 1, 1, 1});
        doAnswer(invocationOnMock -> {
            DatagramPacket datagramPacket = invocationOnMock.getArgument(0);
            datagramPacket.setData(expected);
            datagramPacket.setAddress(expectedAddress);

            return null;
        }).when(socketMock).receive(any(DatagramPacket.class));

        byte[] emptyBuffer = new byte[expected.length];
        DatagramPacket resultPacket = hostReceiveFromUser.receiveMassage(socketMock, emptyBuffer);

        assertArrayEquals(expected, resultPacket.getData());
        assertEquals(expectedAddress, resultPacket.getAddress());

        verify(socketMock, only()).receive(any());
    }

    @Test
    public void whenSocketThrowExceptionShouldCallPrintStackTrace() throws IOException {
        IOException ioExceptionMock = spy(IOException.class);
        doThrow(ioExceptionMock).when(socketMock).receive(any(DatagramPacket.class));

        HostReceiveFromUser hostReceiveFromUser = new HostReceiveFromUser();
        hostReceiveFromUser.receiveMassage(socketMock, ValuesGenerator.getRandomByteArray());

        verify(socketMock, only()).receive(notNull());
        verify(ioExceptionMock, times(1)).printStackTrace();
    }
}
