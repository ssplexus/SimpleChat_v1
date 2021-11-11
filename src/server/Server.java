package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static int port = 3333;

    public static void main (String[] args)
    {
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.err.println(e);
            System.exit(1);
        }


        Socket client = null;
        while(true)
        {
            try
            {
                client = server.accept();
            }
            catch (IOException e)
            {
                System.err.println("Accept failed.");
                System.err.println(e);
                System.exit(1);
            }

            try
            {
                new Thread(new ClientConnection(client)).start();
            }
            catch (ThreadDeath e)
            {
                System.out.println("Client's thread error!");
            }

        }
    }
}
