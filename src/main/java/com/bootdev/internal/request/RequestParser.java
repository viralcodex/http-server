package com.bootdev.internal.request;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class RequestParser {

    //this function will wait until we get a proper request line i.e. ending with \r\n,
    // until then we defer and return 0 that we need more data to parse
    public static int parseRequestLine(Request request, byte[] buffer, int bufferLength) {
        for (int i = 0; i < bufferLength - 1; i++) {
            if (buffer[i] == '\r' && buffer[i + 1] == '\n') //found full request line, parse it and return the result
            {
                RequestLine requestLine = parseRequestLineStrict(new String(buffer, 0, i, StandardCharsets.US_ASCII));
                request.markDone(requestLine);
                return i + 2; //<data> + \r\n
            }
        }
        return 0; //more data needed to correctly parse the request
    }

    private static RequestLine parseRequestLineStrict(String line) {

        String[] details = line.split(" ");

        if (details.length != 3) {
            throw new IllegalArgumentException("Incorrect request line");
        }

        String method = details[0];
        String requestTarget = details[1];
        String httpVersion = details[2];

        if (!method.matches("^[A-Z]+$")) {
            throw new IllegalArgumentException("Invalid request method");
        }

        if (!httpVersion.contains("HTTP/")) {
            throw new IllegalArgumentException("Invalid HTTP version");
        }

        String version = httpVersion.substring("HTTP/".length());
        if (!version.equals("1.1")) {
            throw new IllegalArgumentException("Unsupported HTTP version (only 1.1 is supported)");
        }
        return new RequestLine(version, requestTarget, method);
    }
}
