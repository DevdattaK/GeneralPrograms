package com.generalprogramming.SocketProgramming;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class StandaloneServer {

    public static void main(String[] args) throws IOException {
        int count = 0;
        while (count < 5) {
            System.out.println("Total connections : " + count);
            ServerSocket serverSocket = new ServerSocket(7777);
            Socket socket = serverSocket.accept();
            count++;
        }

    }
}
