package server;

import java.io.IOException;

public record Host(Factory<HostService> hostServiceFactory, int port, int bufferSize) implements Runnable {

    @Override
    public void run() {
        Thread thisThread = Thread.currentThread();
        HostService hostService;
        hostService = hostServiceFactory.create();//new HostService(new HostReceiveFromUser(), new HostSendAllUsers(), new DatagramSocket(port));

        do {
            hostService.execute();
        } while (!thisThread.isInterrupted());

        try {
            hostService.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
