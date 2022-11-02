package server;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

import helper.ValuesGenerator;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HostServiceTest {

    @Mock private HostReceiveFromUser hostReceiveFromUserMock;
    @Mock private HostSendAllUsers hostSendAllUsersMock;
    @Mock private DatagramSocket socket;
    @InjectMocks private HostService hostServiceInjected;

    @Test()
    public void whenHostSendAllUsersShouldBeforeReceiveFromUser() {
        InOrder inOrder = inOrder(hostReceiveFromUserMock, hostSendAllUsersMock);

        when(hostReceiveFromUserMock.receiveMassage(any(), any())).thenReturn(ValuesGenerator.getDatagramPacket());

        hostServiceInjected.execute();

        inOrder.verify(hostReceiveFromUserMock, times(1)).receiveMassage(any(), any());
        inOrder.verify(hostSendAllUsersMock, times(1)).sendAll(anySet(), any(), any(), any());
    }

    @Test()
    public void whenHostServiceCloseShouldCloseSocket() throws IOException {
        hostServiceInjected.close();
        verify(socket, times(1)).close();
    }
}