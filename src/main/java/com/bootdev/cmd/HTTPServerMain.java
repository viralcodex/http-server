package com.bootdev.cmd;

import com.bootdev.internal.headers.Headers;
import com.bootdev.internal.request.Request;
import com.bootdev.internal.response.ResponseWriter;
import com.bootdev.internal.response.StatusCode;
import com.bootdev.internal.response.Writer;
import com.bootdev.internal.server.Handler;
import com.bootdev.internal.server.Server;

import static com.bootdev.constants.Constants.BAD_REQUEST_HTML;
import static com.bootdev.constants.Constants.INTERNAL_SERVER_ERROR_HTML;
import static com.bootdev.constants.Constants.SUCCESS_HTML;

public class HTTPServerMain {
    private static final int PORT = 42069;


    public static void main(String[] args) {
        try {
            Handler handler = HTTPServerMain::handleRequest;
            Server server = Server.serve(PORT, handler);

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

    private static void handleRequest(Writer writer, Request request) throws Exception {
        String path = request.getRequestLine().getRequestTarget();
        Headers headers;
        byte[] body;
        StatusCode statusCode;

        if ("/yourproblem".equals(path)) {
            statusCode = StatusCode.BAD_REQUEST;
            body = BAD_REQUEST_HTML;
        } else if ("/myproblem".equals(path)) {
            statusCode = StatusCode.INTERNAL_SERVER_ERROR;
            body = INTERNAL_SERVER_ERROR_HTML;
        } else {
            statusCode = StatusCode.OK;
            body = SUCCESS_HTML;
        }

        headers = ResponseWriter.getDefaultHeaders(body.length);
        headers.set("content-type", "text/html");

        //write to the stream in order
        writer.writeStatus(statusCode);
        writer.writeHeaders(headers);
        writer.writeBody(body);
    }
}
