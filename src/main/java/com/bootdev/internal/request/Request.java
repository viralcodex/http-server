package com.bootdev.internal.request;

public class Request {
    private RequestLine requestLine;
    private ParserState parserState = ParserState.INITIALIZED;

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public ParserState getParserState(){
        return this.parserState;
    }

    public void markDone(RequestLine rl)
    {
        this.requestLine = rl;
        this.parserState = ParserState.DONE;
    }
}
