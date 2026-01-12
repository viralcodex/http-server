package com.bootdev.httpserver;

import com.bootdev.internal.request.Request;
import com.bootdev.internal.request.RequestFromReader;

import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpServer {
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(42069)) {
            System.out.println("Listening on port 42069");
            while (true) //keep running until a connection is accepted
            {
                Socket socket = serverSocket.accept();
                System.out.println("Connection accepted");
                handleConnection(socket);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleConnection(Socket socket) {
        try (socket) {
            InputStreamReader inputStreamReader = new InputStreamReader(
                    socket.getInputStream(), StandardCharsets.UTF_8
            );
            RequestFromReader requestFromReader = new RequestFromReader();
            Request request = requestFromReader.requestFromReader(inputStreamReader);

            System.out.println("Request Line:");
            System.out.println("- Method: " + request.getRequestLine().getMethod());
            System.out.println("- Target: " + request.getRequestLine().getRequestTarget());
            System.out.println("- Version: " + request.getRequestLine().getHttpVersion());
            System.out.println("Headers: ");
            for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                System.out.printf("- %s: %s\n", entry.getKey(), entry.getValue());
            }
            System.out.println("Connection closed");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}