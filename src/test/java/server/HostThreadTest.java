package server;

import java.net.DatagramSocket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HostThreadTest {

    @Mock(name = "socket")
    private DatagramSocket socketMock;
    @Mock(name = "hostReceiveFromUser")
    private HostReceiveFromUser hostReceiveFromUserMock;
    @Mock(name = "hostSendAllUsers")
    private HostSendAllUsers hostSendAllUsersMock;
    @InjectMocks
    private HostThread hostInjected;

    @Test
    public void whenHostSendInterruptedShouldEndIt() throws InterruptedException {
        Thread hostThread = new Thread(hostInjected);

        hostThread.start();
        wait(100);
        hostThread.interrupt();
        wait(100);
        assertFalse(hostThread.isAlive());
    }

    @Test
    public void whenHostSendInterruptedShouldCloseAllClosable() throws InterruptedException {
        whenHostSendInterruptedShouldEndIt();
        verify(socketMock, times(1)).close();
    }

    @Test()
    public void whenHostSendAllUsersShouldBeforeReceiveFromUser() {
        InOrder inOrder = inOrder(hostReceiveFromUserMock, hostSendAllUsersMock);

        Thread hostThread = new Thread(hostInjected);
        doAnswer(ignore -> {
            hostThread.interrupt();

            return null;
        }).when(hostSendAllUsersMock).sendAll(anySet(), any(), any(), any());
        hostThread.start();

        inOrder.verify(hostReceiveFromUserMock, timeout(100).times(1)).receiveMassage(any(), any());
        inOrder.verify(hostSendAllUsersMock, timeout(100).times(1)).sendAll(anySet(), any(), any(), any());
    }
}
