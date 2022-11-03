package client;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import helper.ValuesGenerator;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.Before;
import org.junit.Test;

public class DataSenderTest {

    private DatagramSocket socketMock;
    private InetAddress address;
    private byte[] expected;
    private DataSender dataSender;

    @Before
    public void setup() throws UnknownHostException {
        socketMock = mock(DatagramSocket.class);
        address = ValuesGenerator.getRandomInetAddress();
        expected = ValuesGenerator.getRandomByteArray();
        dataSender = new DataSender();
    }

    @Test
    public void whenDataSenderSendDataShouldSocketSendCorrectData() throws IOException {
        int port = 6666;

        doAnswer(invocationOnMock -> {
            DatagramPacket datagramPacket = invocationOnMock.getArgument(0);

            assertArrayEquals(expected, datagramPacket.getData());
            assertEquals(address, datagramPacket.getAddress());
            assertEquals(port, datagramPacket.getPort());
            return null;
        }).when(socketMock).send(any(DatagramPacket.class));

        dataSender.sendData(socketMock, address, port, expected);
        verify(socketMock, times(1)).send(any());
    }

    @Test
    public void whenInDataSenderSocketThrowAExceptionShouldTheExceptionPST() throws IOException {
        IOException ioExceptionSpy = spy(IOException.class);
        doThrow(ioExceptionSpy).when(socketMock).send(any());

        dataSender.sendData(socketMock, address, 6666, expected);

        verify(socketMock, times(1)).send(any());
        verify(ioExceptionSpy, atLeast(1)).printStackTrace();
    }
}
