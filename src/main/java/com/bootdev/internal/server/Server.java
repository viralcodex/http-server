package com.bootdev.internal.server;

import com.bootdev.internal.headers.Headers;
import com.bootdev.internal.response.ResponseWriter;
import com.bootdev.internal.response.StatusCode;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {

    private final ServerSocket serverSocket;
    private final AtomicBoolean closed = new AtomicBoolean(false); //track if server is closed

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static Server serve(int port) throws Exception {
        ServerSocket serverSock = new ServerSocket(port);
        Server server = new Server(serverSock);

        Thread listenerThread = new Thread(server::listen); //start the server
        listenerThread.start();

        return server;
    }

    public void listen() {
        while (!closed.get()) {
            try {
                Socket conn = serverSocket.accept();
                System.out.println("Connection accepted");

                new Thread(() -> {
                    try {
                        handle(conn);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();

            } catch (Exception e) {
                if (!closed.get()) {
                    System.err.println("Accept error: " + e.getMessage());
                }
            }
        }
    }

    public void handle(Socket conn) throws IOException {
        try (conn; OutputStream outputStream = conn.getOutputStream()) { //try with resources
            ResponseWriter.WriteStatusLine(outputStream, StatusCode.OK);

            Headers headers = ResponseWriter.getDefaultHeaders(0);
            ResponseWriter.writeHeaders(outputStream, headers);
            outputStream.flush();
            conn.shutdownOutput();
        } catch (Exception e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    public void close() throws IOException {
        closed.set(true); //server closed
        serverSocket.close();
    }
}