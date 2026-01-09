package com.bootdev.internal.request;

import java.io.IOException;
import java.io.Reader;

public class RequestParser {

    public static Request requestFromReader(Reader reader) {
        try{
            String s = readAll(reader);
            String[] lines = s.split("\r\n"); //break all lines in the request

            if(lines.length == 0)
            {
                throw new IllegalArgumentException("Empty request");
            }

            RequestLine requestLine = parseRequestLine(lines[0]); //send the first line that has the request details

            return new Request(requestLine);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static RequestLine parseRequestLine(String line) {

        String[] details = line.split(" ");

        if(details.length != 3)
        {
            throw new IllegalArgumentException("Incorrect request line");
        }

        String method = details[0];
        String requestTarget = details[1];
        String httpVersion = details[2];

        if(!method.matches("^[A-Z]+$"))
        {
            throw new IllegalArgumentException("Invalid request method");
        }

        if(!httpVersion.contains("HTTP/"))
        {
            throw new IllegalArgumentException("Invalid HTTP version");
        }

        String version = httpVersion.substring("HTTP/".length());
        if(!version.equals("1.1"))
        {
            throw new IllegalArgumentException("Unsupported HTTP version (only 1.1 is supported)");
        }
        return new RequestLine(version, requestTarget, method);
    }

    private static String readAll(Reader reader) throws IOException
    {
       StringBuilder sb = new StringBuilder();
       char[] buffer = new char[1024];
       int n;
       while((n = reader.read(buffer)) != -1)
       {
           sb.append(buffer, 0, n);
       }
       return sb.toString();
    }
}
