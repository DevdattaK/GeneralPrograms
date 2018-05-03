package com.generalprogramming.SocketProgramming;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;


public class TCPServer {
    private static BlockingQueue<Socket> queuedSocketRequests = new ArrayBlockingQueue<Socket>(5);


    public static void main(String[] args) throws IOException {
        if(args.length != 1){
            System.out.println("Incorrect number of arguments at command line");
            System.exit(1);
        }

        ServerSocket serverSocket = null;

        try{
            Integer portNumber = Integer.parseInt(args[0]);
            serverSocket = new ServerSocket(portNumber);

            System.out.println("Server listening on port# : " + args[0]);

            Socket newServerSocketForClientConnection = serverSocket.accept();

            BufferedReader socketReader = new BufferedReader(new InputStreamReader(newServerSocketForClientConnection.getInputStream()));
            PrintWriter socketWriter = new PrintWriter(newServerSocketForClientConnection.getOutputStream(), true);

            String clientMsg;
            String serverMsg;

            while((clientMsg = socketReader.readLine()) != null){
                if(clientMsg.equalsIgnoreCase("quit")){
                    break;
                }

                serverMsg = reverseString(clientMsg);
                socketWriter.println("Server : " + serverMsg);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            serverSocket.close();
        }
    }

    private static String reverseString(String clientMsg) {
        synchronized (clientMsg) {
            StringBuffer stringBuffer = new StringBuffer();

            for (int i = clientMsg.length()-1; i >= 0 ; i--){
                stringBuffer.append(clientMsg.charAt(i));
            }

            return stringBuffer.toString();
        }
    }
}
