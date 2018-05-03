package SocketProgrammingTest;

import com.generalprogramming.SocketProgramming.MultithreadedServer;
import com.generalprogramming.SocketProgramming.TCPClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultiThreadedServerTest {
    private TCPClient client1;
    private TCPClient client2;
    private TCPClient client3;
    private TCPClient client4;
    private MultithreadedServer server;

    @Before
    public void setUp() throws Exception {
        client1 = new TCPClient();
        client2 = new TCPClient();
        client3 = new TCPClient();
        client4 = new TCPClient();
        server = new MultithreadedServer();
    }


    @Test
    public void maxConnectionTest() throws Exception{
        //start server
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MultithreadedServer.main(new String[]{});
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();

        //start clients
        ExecutorService service = Executors.newCachedThreadPool();
        final List<TCPClient> clients = Arrays.asList(client1, client2, client3, client4);

        service.submit(new Runnable() {
            @Override
            public void run() {
                client1.forTestAndDebug();
            }
        });
        service.submit(new Runnable() {
            @Override
            public void run() {
                client2.forTestAndDebug();
            }
        });
        service.submit(new Runnable() {
            @Override
            public void run() {
                client3.forTestAndDebug();
            }
        });
        service.submit(new Runnable() {
            @Override
            public void run() {
                client4.forTestAndDebug();
            }
        });

        service.shutdown();
        service.awaitTermination(1, TimeUnit.HOURS);

        //serverThread.join();
    }
}
