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

public class MultithreadedServer {
    private static BlockingQueue<Socket> queuedSockets = new ArrayBlockingQueue<>(1);                  //max queued connections.
    private static Semaphore semaphoreForMaxConnectionsAllowed = new Semaphore(2);              //max active connections being served.

    private static void handleClientConnectionRequest(final Socket newSocketForClientConnection, final Semaphore maxConnectionSemaphore) {
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

                    SocketAddress clientSocket = (InetSocketAddress) newSocketForClientConnection.getRemoteSocketAddress();

                    while ((clientMsg = socketReader.readLine()) != null) {
                        if (clientMsg.equalsIgnoreCase("quit")) {
                            maxConnectionSemaphore.release();
                            break;
                        }

                        System.out.println("client with socket " + clientSocket + " sent MSG : " + clientMsg);
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
            int connectionNumber = 0;

            System.out.println("Server listening on port# : " + args[0]);

            //main thread...
            while (shouldContinue) {
                Socket newServerSocketForClientConnection = null;
                newServerSocketForClientConnection = queuedSockets.poll();

                if (newServerSocketForClientConnection == null) {
                    newServerSocketForClientConnection = serverSocket.accept();

                    connectionNumber++;
                    System.out.println("Created new socket upon client request. ConnectionCOunt = " + connectionNumber);

                    processConnection(newServerSocketForClientConnection);
                } else {
                    //i.e. queue has a socket request pending.
                    System.out.println("Picking queued socket..");
                    processConnection(newServerSocketForClientConnection);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }

    private static void processConnection(Socket newServerSocketForClientConnection) {

        if (semaphoreForMaxConnectionsAllowed.availablePermits() > 0) {
            handleClientConnectionRequest(newServerSocketForClientConnection, semaphoreForMaxConnectionsAllowed);
        } else {
            //System.out.println("Since exceeded max connection limit, adding in queue.");
            if (queuedSockets.offer(newServerSocketForClientConnection)) {
                System.out.println("connectionRequest queued because no more space on server. QueuedSocketList size : " + queuedSockets.size());
            }else{
                System.out.println("No space available for client connections. Can not be queued too.");
            }

        }

    }
}
