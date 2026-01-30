package com.bootdev.internal.server;

import com.bootdev.internal.request.Request;
import com.bootdev.internal.request.RequestFromReader;
import com.bootdev.internal.response.ResponseWriter;
import com.bootdev.internal.response.StatusCode;
import com.bootdev.internal.response.Writer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.bootdev.constants.Constants.BAD_REQUEST_HTML;
import static com.bootdev.constants.Constants.INTERNAL_SERVER_ERROR_HTML;

public class Server {

    private final ServerSocket serverSocket;
    private final AtomicBoolean closed = new AtomicBoolean(false); //track if server is closed
    private final Handler handler;

    public Server(ServerSocket serverSocket, Handler handler) {
        this.serverSocket = serverSocket;
        this.handler = handler;
    }

    public static Server serve(int port, Handler handler) throws Exception {
        ServerSocket serverSock = new ServerSocket(port);
        Server server = new Server(serverSock, handler);

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
            Request request;
            Writer writer = new Writer(outputStream);
            try {
                request = new RequestFromReader().requestFromReader(new InputStreamReader(conn.getInputStream()));
            } catch (IOException e) {
                writer.writeStatus(StatusCode.BAD_REQUEST);
                handlerBadRequest(writer);
                return;
            }

            handler.handle(writer, request); //handle the incoming req and write response accordingly

        } catch (Exception e) {
            try (OutputStream outputStream = conn.getOutputStream()) {
                Writer writer = new Writer(outputStream);
                writer.writeStatus(StatusCode.INTERNAL_SERVER_ERROR);
                handlerInternalServerError(writer);
            } catch (Exception ignored) {
            }
        }
    }

    public void close() throws IOException {
        closed.set(true); //server closed
        serverSocket.close();
    }

    // --- utils to write HTML to response body ---

    private void handlerBadRequest(Writer w) throws Exception {
        byte[] body = BAD_REQUEST_HTML;
        w.writeHeaders(DefaultHtmlHeaders(body.length));
        w.writeBody(body);
    }

    private void handlerInternalServerError(Writer w) throws Exception {
        byte[] body = INTERNAL_SERVER_ERROR_HTML;
        w.writeHeaders(DefaultHtmlHeaders(body.length));
        w.writeBody(body);
    }

    private com.bootdev.internal.headers.Headers DefaultHtmlHeaders(int contentLength) {
        var headers = ResponseWriter.getDefaultHeaders(contentLength);
        headers.set("content-type", "text/html");
        return headers;
    }
}