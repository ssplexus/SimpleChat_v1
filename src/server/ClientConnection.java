package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ClientConnection implements Runnable
{
    public static List<ClientConnection> clients = new ArrayList<>();

    public BufferedReader in;
    public PrintWriter out;

    private String name;
    private Socket client;

    ClientConnection(Socket client)
    {
        name = "";
        in = null;
        out = null;
        this.client = client;
        clients.add(this);
    }

    public void run()
    {

        try
        {
            in = new BufferedReader(new InputStreamReader(
                    client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);

            String msg;
            name = in.readLine();
            out.println("Hi, " + name);
            while (!(msg = in.readLine()).equalsIgnoreCase("bye"))
            {
                for(ClientConnection c: clients)
                {
                    if(!c.equals(this))
                    {
                        System.out.println(name + " says: " + msg);
                        c.out.println(name + " says: " + msg);
                    }
                }
            }
        }
        catch (NullPointerException | IOException e)
        {
            System.out.println(name + "'s connection unexpected error!");
        }
        finally
        {
            clients.remove(this);
            try
            {
                in.close();
                out.close();
                client.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                System.out.println("Can't close client socket!");
            }
            System.out.println(name + " left the chat");
        }

    }
}

