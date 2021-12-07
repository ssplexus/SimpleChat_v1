package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
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
            server = new Socket(host, port); // подключение к серверу
            out = new PrintWriter(server.getOutputStream(), true); // поток отправки сообщений на сервер
            in = new BufferedReader(new InputStreamReader(server.getInputStream())); // поток чтения из сокета
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in)); // поток стандартного ввода

            System.out.print("Enter nick: ");
            out.println(stdIn.readLine()); // представляемся

            // Создаём поток чтения ответа от сервера
            Thread reader = new Thread()
            {
                String read = "";
                @Override
                public void run() {
                    while (true) {
                        try
                        {
                            read = in.readLine();
                            if(read == null) break; // если поток чтения разорвался, то выход из цикла потока
                            System.out.println(read); // выводим ответ от сервера
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        if (read == null) break;
                    }
                }
            };
            reader.start(); // запуск потока
            String outMsg = "";
            while (reader.isAlive()) // работа чата пока поток чтения жив
            {
                // Если отправили серверу "bye", то поток клиента на стороне сервера завершает работу и
                // readLine для потока чтения из сокета возвращает null, поток чтения завершается,
                // происходит выход из цикла и завершения работы клиента
                if(outMsg.equalsIgnoreCase("bye"))
                    reader.join(); // ожидаем отключения
                else
                {
                    outMsg = stdIn.readLine(); // ввод сообщения в чат
                    out.println(outMsg);
                }
            }
        }
        catch (ConnectException exception)
        {
            System.out.println("Connection error!");
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
                if(in != null) in.close();
                if(out != null) out.close();
                if(server != null) server.close();
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
