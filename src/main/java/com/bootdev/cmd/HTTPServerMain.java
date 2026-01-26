package com.bootdev.cmd;

import com.bootdev.internal.server.Server;

public class HTTPServerMain {
    private static final int PORT = 42069;

    public static void main(String[] args) {
        try {
            Server server = Server.serve(PORT);

            System.out.println("Server started on port " + PORT);

            //graceful shutdown on ctrl + C
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down server...");
                try {
                    server.close();
                } catch (Exception ignored) {
                }
            }));

            Thread.currentThread().join();
        } catch (Exception e) {
            throw new RuntimeException("Error starting server: ", e);
        }
    }
}
