package client;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import helper.ValuesGenerator;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import org.junit.Test;

public class DataSenderTest {

    @Test
    public void whenDataSenderSendDataShouldSocketSendCorrectData() throws IOException {
        DatagramSocket socketMock = mock(DatagramSocket.class);
        byte[] expected = ValuesGenerator.getRandomByteArray();

        InetAddress address = ValuesGenerator.getRandomInetAddress();
        doAnswer(invocationOnMock -> {
            DatagramPacket datagramPacket = invocationOnMock.getArgument(0);

            assertArrayEquals(expected, datagramPacket.getData());
            assertEquals(address, datagramPacket.getAddress());
            return null;
        }).when(socketMock).send(any(DatagramPacket.class));

        DataSender dataSender = new DataSender();
        dataSender.sendData(socketMock, address, expected);
        verify(socketMock, times(1)).send(any());
    }
}
