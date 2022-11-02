package server;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HostTest {

    private Host host;
    private HostService hostServiceMock;

    @Before
    public void setup() {
        hostServiceMock = mock(HostService.class);
        Factory<HostService> hostServiceFactory = () -> hostServiceMock;
        host = new Host(hostServiceFactory, 666, 100);
    }

    @Test
    public void whenHostSendInterruptedShouldEndIt() throws InterruptedException {
        Thread hostThread = new Thread(host);

        hostThread.start();
        Thread.sleep(100);
        hostThread.interrupt();
        Thread.sleep(100);
        assertFalse(hostThread.isAlive());
    }

    @Test
    public void whenHostInterruptedShouldAtLeastOnceCallExecuteAndClose() throws IOException, InterruptedException {
        whenHostSendInterruptedShouldEndIt();

        verify(hostServiceMock, atLeastOnce()).execute();
        verify(hostServiceMock, times(1)).close();
    }

    @Test
    public void whenHostInterruptedAndWhenCloseShouldThrowException() throws IOException, InterruptedException {
        IOException ioExceptionSpy = spy(IOException.class);

        doThrow(ioExceptionSpy).when(hostServiceMock).close();

        whenHostInterruptedShouldAtLeastOnceCallExecuteAndClose();
        verify(ioExceptionSpy, atLeastOnce()).printStackTrace();
    }

    @Test
    public void whenHostDoNotInterruptedShouldKeepWorking() throws IOException {
        Thread thread = new Thread(host);
        thread.start();

        verify(hostServiceMock, timeout(100).atLeastOnce()).execute();
        verify(hostServiceMock, after(100).times(0)).close();
        assertFalse(thread.isInterrupted());
        assertTrue(thread.isAlive());
    }
}
