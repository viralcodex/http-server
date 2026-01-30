package com.bootdev.cmd;

import com.bootdev.internal.headers.Headers;
import com.bootdev.internal.request.Request;
import com.bootdev.internal.response.ResponseWriter;
import com.bootdev.internal.response.StatusCode;
import com.bootdev.internal.response.Writer;
import com.bootdev.internal.server.Handler;
import com.bootdev.internal.server.Server;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Arrays;

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
        } else if ("/video".equals(path)) {
            handleVideoRequest(writer, request);
            return;
        } else if (path.startsWith("/httpbin/")) {
            handleChunkedRequest(writer, request);
            return;
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

    public static void handleChunkedRequest(Writer writer, Request request) throws Exception {
        String path = request.getRequestLine().getRequestTarget();
        String suffix = path.substring("/httpbin/".length());
        String targetUrl = "https://httpbin.org/" + suffix;

        HttpURLConnection connection = (HttpURLConnection) new URL(targetUrl).openConnection();

        connection.setRequestMethod("GET");

        InputStream inputStream = connection.getInputStream();

        Headers headers = ResponseWriter.getDefaultHeaders(connection.getContentLength());

        headers.remove("content-length");
        headers.set("transfer-encoding", "chunked");
        headers.set("content-type", "application/json");

        //trailer keys(announce them beforehand to the server)
        headers.set("trailer", "X-Content-SHA256, X-Content-Length");

        writer.writeStatus(StatusCode.OK);
        writer.writeHeaders(headers);

        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        int totalLength = 0;

        //write body chunks as they are received from httpbin
        byte[] buffer = new byte[1024];
        int n;

        while ((n = inputStream.read(buffer)) != -1) {
            System.out.println("Proxied bytes: " + n);
            byte[] chunk = Arrays.copyOf(buffer, n);
            totalLength += n;
            writer.writeChunkedBody(chunk);
        }
        writer.writeChunkedBodyDone();

        //write trailers in the response
        Headers trailers = Headers.newHeaders();
        trailers.set(
                "x-content-sha256",
                bytesToHex(sha256.digest())
        );
        trailers.set(
                "x-content-length",
                String.valueOf(totalLength)
        );
        writer.writeTrailers(trailers);
    }

    public static void handleVideoRequest(Writer writer, Request request) throws Exception {
        try {
            byte[] body = Files.readAllBytes(Path.of("assets/vim.mp4"));

            Headers headers = ResponseWriter.getDefaultHeaders(body.length);
            headers.set("content-type", "video/mp4");

            writer.writeStatus(StatusCode.OK);
            writer.writeHeaders(headers);
            writer.writeBody(body);
        } catch (Exception e) {
            throw new RuntimeException("Error opening video file: ", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}
