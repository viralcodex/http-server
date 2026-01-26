package com.bootdev.internal.request;

import com.bootdev.internal.headers.HeadersParser;
import com.bootdev.internal.headers.HeadersResult;

import java.nio.charset.StandardCharsets;

public class Request {
    private RequestLine requestLine;
    private ParserState parserState = ParserState.INITIALIZED;
    private final RequestParser requestParser;
    private final HeadersParser headersParser;

    public Request() {
        this.headersParser = HeadersParser.newHeaders();
        this.requestParser = new RequestParser();
    }

    public RequestLine getRequestLine() {
        return this.requestLine;
    }

    public HeadersParser getHeaders() {
        return this.headersParser;
    }

    public ParserState getParserState() {
        return this.parserState;
    }

    public void setRequestLine(RequestLine rl) {
        this.requestLine = rl;
        this.parserState = ParserState.PARSING_HEADERS;
    }

    public void markDone() {
        this.parserState = ParserState.DONE;
    }

    public int parse(byte[] buffer, int length) {
        int totalBytesParsed = 0;

        while (parserState != ParserState.DONE) {
            int n = parseSingle(buffer, totalBytesParsed, length - totalBytesParsed);

            if (n == 0) {
                break; //need more data to parse the request
            }

            totalBytesParsed += n;
        }

        return totalBytesParsed;
    }

    private int parseSingle(byte[] buffer, int offset, int length) {
        switch (parserState) {
            case INITIALIZED -> {
                return parseRequestLineState(buffer, offset, length);
            }
            case PARSING_HEADERS -> {
                return parseHeadersState(buffer, offset, length);
            }
        }
        return 0;
    }

    //this function will wait until we get a proper request line i.e. ending with \r\n,
    private int parseRequestLineState(byte[] buffer, int offset, int length) {
        if (buffer == null || length < 2) {
            return 0; // need more data
        }
        for (int i = offset; i < offset + length - 1; i++) {
            if (buffer[i] == '\r' && buffer[i + 1] == '\n') {
                String line = new String(
                        buffer, offset, i - offset, StandardCharsets.US_ASCII
                );
                RequestLine rl = requestParser.parseRequestLineStrict(line);
                setRequestLine(rl);
                return (i - offset) + 2;
            }
        }
        return 0; //need more data
    }

    //parsing head state through headers parser
    private int parseHeadersState(byte[] buffer, int offset, int length) {
        HeadersResult headersResult = this.headersParser.parseHeaders(buffer, offset, length);
        if (headersResult.getError() != null) {
            throw new IllegalArgumentException(headersResult.getError());
        }

        if (headersResult.getIsDone()) {
            markDone();
        }
        return headersResult.getBytesConsumed();
    }
}
