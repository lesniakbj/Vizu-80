package src.server;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Used to update the Visual z80 Client
 * 
 * @author Brendan Lesniak
 * @version 0.0.a
 */
public class UpdateServer
{
    private static final int DEFAULT_SOCKET = 13130;
    private static final String CURRENT_VERSION = src.cpu.Z80Core.getVersion();
    
    private String inputLine;
    
    public static void main(String[] args) throws IOException
    {
        UpdateServer server = new UpdateServer();  
    }
    
    UpdateServer()
    {
        try
        (
            ServerSocket serverSocket = new ServerSocket(DEFAULT_SOCKET);
            Socket clientSocket = serverSocket.accept();
            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
        ) {
            while((inputLine = input.readLine()) != null)
            {
                output.println(inputLine);
                output.println("MESSAGE RECIEVED");
            }
        } 
        catch(IOException e) 
        {
            System.out.println(e);
        }
    }
}
