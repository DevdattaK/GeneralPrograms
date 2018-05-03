package com.generalprogramming.SocketProgramming;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Hello world!
 *
 */
public class TCPClient
{
    //for debug purpose.
    public void forTestAndDebug(){
        String hostName = "192.168.1.123";
        Integer portNumber = 4567;

        System.out.println("Client created to connect to host/port : " + hostName + "/" + portNumber);

        try(
                Socket clientSocket = new Socket(hostName, portNumber);
                BufferedReader socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))
        ){
            String serverMsg;
            String clientMsg;
            do{
                clientMsg = bufferedReader.readLine();  //blocking call.
                System.out.println("Client : " + clientMsg);

                socketWriter.println(clientMsg);        //auto-flush enabled on this writer.
                serverMsg = socketReader.readLine();

                System.out.println("Server (reverse of Client Msg): " + serverMsg);
            }while(serverMsg != null);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }


    public static void main( String[] args )
    {
        if(args.length != 2){
            System.out.println("Exiting the App as command-line args are not accurate");
            System.exit(1);
        }

        String hostName = args[0];
        Integer portNumber = Integer.parseInt(args[1]);

        System.out.println("Client created to connect to host/port : " + hostName + "/" + portNumber);

        try(
                Socket clientSocket = new Socket(hostName, portNumber);
                BufferedReader socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))
        ){
            String serverMsg;
            String clientMsg;
            do{
                clientMsg = bufferedReader.readLine();  //blocking call.
                System.out.println("Client : " + clientMsg);

                socketWriter.println(clientMsg);        //auto-flush enabled on this writer.
                serverMsg = socketReader.readLine();

                System.out.println("Server (reverse of Client Msg): " + serverMsg);
            }while(serverMsg != null);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
