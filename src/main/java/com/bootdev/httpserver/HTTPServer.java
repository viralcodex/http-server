package com.bootdev.httpserver;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class HttpServer {
    public void run() {
        try(ServerSocket serverSocket = new ServerSocket(42069))
        {
            System.out.println("Listening on port 42069");
            while(true) //keep running until a connection is accepted
            {
                Socket socket = serverSocket.accept();
                System.out.println("Connection accepted");
                handleConnection(socket);
            }
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    public void handleConnection(Socket socket) {
        try{
            InputStream inputStream = socket.getInputStream();
            BlockingQueue<String> queue = LinesReader.getLines(inputStream);

            //print the lines from the queue
            while(true)
            {
                String currentLine = queue.take();
                if(LinesReader.isEOF(currentLine))
                {
                    break; //EOF
                }
                System.out.printf("read: %s\n", currentLine);
            }
            //close after processing
            socket.close();
            System.out.println("Connection closed");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}