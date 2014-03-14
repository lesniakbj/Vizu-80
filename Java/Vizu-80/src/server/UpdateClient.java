package src.server;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;


/**
 * Client used to check for updates
 * 
 * @author Brendan Lesniak
 * @version 0.0.a
 */
public class UpdateClient
{
    private static final int DEFAULT_SOCKET = 13130;
    private static final String HOST = "localhost";
    //private static final String CURRENT_VERSION = //src.cpu.Z80Core.getVersion(); Get saved file version

    private String userInput;
    
    public static void main(String[] args) throws IOException
    {
        UpdateClient client = new UpdateClient();
    }
    
    UpdateClient()
    {
        try
        (   
            Socket clientSocket = new Socket(HOST, DEFAULT_SOCKET);
            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedReader keyInput = new BufferedReader(new InputStreamReader(System.in));   
        )
        {
            while((userInput = keyInput.readLine()) != null)
            {
                output.println(userInput);
                System.out.println("Echo:" + input.readLine());
            }
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
    }

}
