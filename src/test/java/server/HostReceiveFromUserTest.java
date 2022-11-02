package server;

import helper.ValuesGenerator;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HostReceiveFromUserTest {

    @Test
    public void whenReceiveMsgShouldGetMsg() throws IOException {
        byte[] expected = ValuesGenerator.getRandomByteArray();
        HostReceiveFromUser hostReceiveFromUser = new HostReceiveFromUser();

        DatagramSocket socketMock = mock(DatagramSocket.class);
        ArgumentCaptor<DatagramPacket> argumentCaptor = ArgumentCaptor.forClass(DatagramPacket.class);

        doAnswer(invocationOnMock -> {
            DatagramPacket datagramPacket = invocationOnMock.getArgument(0);
            datagramPacket.setData(expected);

            return null;
        }).when(socketMock).receive(any(DatagramPacket.class));

        byte[] result = hostReceiveFromUser.receiveMassage(socketMock);
        assertArrayEquals(expected, result);
    }
}
