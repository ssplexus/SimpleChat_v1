package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Класс клиента
 */
public class Client
{
    private static int port = 3333;
    private static String host = "localhost";

    private PrintWriter out = null;
    private BufferedReader in = null;

    public void run()
    {
        Socket server = null;

        try
        {
            server = new Socket(host, port);
            out = new PrintWriter(server.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Enter nick: ");
            out.println(stdIn.readLine());

            Thread reader = new Thread()
            {
                String read = "";
                @Override
                public void run() {
                    while (true) {
                        try
                        {
                            read = in.readLine();
                            if(read == null) break;
                            System.out.println(read);
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        if (read == null) break;
                    }
                }
            };
            reader.start();
            String outMsg = "";
            while (reader.isAlive())
            {
                if(outMsg.equalsIgnoreCase("bye"))
                    reader.join();
                else
                {
                    outMsg = stdIn.readLine();
                    out.println(outMsg);
                }
            }
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
            System.out.println("Unexpected error!");
            System.exit(1);
        }
        finally
        {
            try
            {
                in.close();
                out.close();
                server.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                System.out.println("Can't close client socket!");
            }
        }
    }

    public static void main (String[] args)
    {
        new Client().run();
    }
}
