package com.generalprogramming.SocketProgramming;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class MultithreadedServer {
    private static final int MAX_QUEUE_SIZE = 1;
    private static BlockingQueue<Socket> queuedSockets = new ArrayBlockingQueue<>(MAX_QUEUE_SIZE);                  //max queued connections.
    private static final int MAX_THREAD_RUNNING = 2;
    private static Semaphore semaphoreForMaxConnectionsAllowed = new Semaphore(MAX_THREAD_RUNNING);              //max active connections being served.

    private static void handleClientConnectionRequest(final Socket newSocketForClientConnection, final Semaphore maxConnectionSemaphore,
                                                      final Object mainThreadListeningLock) {
        new Thread(new Runnable() {

            @Override
            public void run() {

                try (
                        BufferedReader socketReader = new BufferedReader(new InputStreamReader(newSocketForClientConnection.getInputStream()));
                        PrintWriter socketWriter = new PrintWriter(newSocketForClientConnection.getOutputStream(), true)
                ) {

                    maxConnectionSemaphore.acquire();

                    String serverMsg;
                    String clientMsg;

                    while ((clientMsg = socketReader.readLine()) != null) {
                        if (clientMsg.equalsIgnoreCase("quit")) {
                            maxConnectionSemaphore.release();
                            synchronized (mainThreadListeningLock){
                                mainThreadListeningLock.notifyAll();
                            }
                            break;
                        }
                        serverMsg = reverseString(clientMsg);

                        socketWriter.println(serverMsg);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("Closing client upon client's request.");
                }
            }
        }).start();
    }

    private static String reverseString(String clientMsg) {
        synchronized (clientMsg) {
            StringBuffer stringBuffer = new StringBuffer();

            for (int i = clientMsg.length() - 1; i >= 0; i--) {
                stringBuffer.append(clientMsg.charAt(i));
            }

            return stringBuffer.toString();
        }
    }

    public static void main(String[] args) throws IOException {
        boolean shouldContinue = true;

        if (args.length != 1) {
            System.out.println("Incorrect number of arguments at command line");
            System.exit(1);
        }

        ServerSocket serverSocket = null;

        try {
            Integer portNumber = Integer.parseInt(args[0]);
            serverSocket = new ServerSocket(portNumber);
            Object mainThreadListeningLock = new Object();

            System.out.println("Server listening on port# : " + args[0]);

            //main thread...
            while (shouldContinue) {
                Socket newServerSocketForClientConnection = null;

                //if queue is not full yet, then we can accept the connection request. Worst case (if not monitors available through
                //semaphore), it will be queued.
                newServerSocketForClientConnection = queuedSockets.poll();

                if (newServerSocketForClientConnection == null) {
                    newServerSocketForClientConnection = serverSocket.accept();

                    processConnection(newServerSocketForClientConnection, mainThreadListeningLock);
                } else {
                    //i.e. queue has a socket request pending.
                    synchronized (mainThreadListeningLock) {
                        System.out.println("Semaphore Permits available .." + semaphoreForMaxConnectionsAllowed.availablePermits());
                        while (semaphoreForMaxConnectionsAllowed.availablePermits() == 0) {
                            //i.e. there exists a connectionRequest in queue, but there are no resources available to respect the request
                            System.out.println("Server thread is not accepting any more connections until resources to respect the connections are free.");
                            mainThreadListeningLock.wait();
                        }
                        processConnection(newServerSocketForClientConnection, mainThreadListeningLock);
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }

    private static void processConnection(Socket newServerSocketForClientConnection, Object processConnection) {

        if (semaphoreForMaxConnectionsAllowed.availablePermits() > 0) {
            handleClientConnectionRequest(newServerSocketForClientConnection, semaphoreForMaxConnectionsAllowed, processConnection);
        } else {

            if (queuedSockets.offer(newServerSocketForClientConnection)) {
                System.out.println("connectionRequest queued because no more space on server. QueuedSocketList size : " + queuedSockets.size());
            } else {
                System.out.println("No space available for client connections and can not be queued too. Please try again later.");
            }
        }

    }
}
