package com.bootdev.internal.request;

public class Request {
    private final RequestLine requestLine;

    public Request(RequestLine requestLine) {
        this.requestLine = requestLine;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }
}
