package client;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import helper.ValuesGenerator;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DataReceiverTest {

    @Mock private DatagramSocket socketMock;
    private DataReceiver dataReceiver;

    @Before
    public void setup() {
        dataReceiver = new DataReceiver();
    }

    @Test
    public void whenReceive_thenSocketCallReceiveOnce() throws IOException {
        dataReceiver.receive(socketMock);

        verify(socketMock, times(1)).receive(any(DatagramPacket.class));
    }

    @Test
    public void whenReceiveSocketThrowException_thenPST() throws IOException {
        IOException ioExceptionSpy = spy(IOException.class);
        doThrow(ioExceptionSpy).when(socketMock).receive(any());

        verify(ioExceptionSpy, atLeastOnce()).printStackTrace();
    }

    @Test
    public void whenReceive_thenFirstByteIsUsersNumberInResult() throws IOException {
        byte userNumber = ValuesGenerator.getRandomByte();
        byte[][] expected = new byte[2][];
        expected[0] = new byte[1];
        expected[0][0] = userNumber;
        expected[1] = ValuesGenerator.getRandomByteArray();
        doAnswer(invocationOnMock -> {
            DatagramPacket datagramPacket = invocationOnMock.getArgument(0);
            byte[] data = new byte[expected[1].length + 1];
            ByteBuffer.wrap(data).put(expected[0][0]).put(expected[1]);
            datagramPacket.setData(data);

            return null;
        }).when(socketMock).receive(any(DatagramPacket.class));

        byte[][] result = dataReceiver.receive(socketMock);

        assertArrayEquals(expected, result);
    }
}
