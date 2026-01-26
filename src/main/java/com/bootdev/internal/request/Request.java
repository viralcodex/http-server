package com.bootdev.internal.request;

import com.bootdev.internal.headers.Headers;
import com.bootdev.internal.headers.HeadersResult;

import java.nio.charset.StandardCharsets;

public class Request {
    private RequestLine requestLine;
    private final Headers headers;
    private byte[] body = new byte[0];

    private ParserState parserState = ParserState.INITIALIZED;
    private final RequestParser requestParser;

    public Request() {
        this.headers = Headers.newHeaders();
        this.requestParser = new RequestParser();
    }

    public RequestLine getRequestLine() {
        return this.requestLine;
    }

    public Headers getHeaders() {
        return this.headers;
    }

    public byte[] getBody() {
        return this.body;
    }

    public ParserState getParserState() {
        return this.parserState;
    }

    public void setRequestLine(RequestLine rl) {
        this.requestLine = rl;
        this.parserState = ParserState.PARSING_HEADERS;
    }

    public void setHeaders() {
        this.parserState = ParserState.PARSING_BODY;
    }

    public int parse(byte[] buffer, int length) {
        int totalBytesParsed = 0; //offset

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
            case PARSING_BODY -> {
                return parseBodyState(buffer, offset, length);
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
                String line = new String(buffer, offset, i - offset, StandardCharsets.US_ASCII);
                RequestLine rl = requestParser.parseRequestLineStrict(line);
                setRequestLine(rl);
                return (i - offset) + 2;
            }
        }
        return 0; //need more data
    }

    //parsing head state through headers parser
    private int parseHeadersState(byte[] buffer, int offset, int length) {
        HeadersResult headersResult = this.headers.parseHeaders(buffer, offset, length);
        if (headersResult.getError() != null) {
            throw new IllegalArgumentException(headersResult.getError());
        }

        if (headersResult.getIsDone()) {
            setHeaders();
        }
        return headersResult.getBytesConsumed();
    }

    //parsing body after parsing headers
    private int parseBodyState(byte[] buffer, int offset, int length) {
        String contentLengthValue = headers.getHeader("content-length");

        if (contentLengthValue == null) //no header = no body
        {
            this.parserState = ParserState.DONE;
            return 0;
        }

        int contentLength = 0;
        //get the content length value
        try {
            contentLength = Integer.parseInt(contentLengthValue);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Content-Length");
        }

        int remaining = contentLength - body.length; //how many bytes more to parse

        if (remaining < 0) {
            throw new IllegalArgumentException("Body larger than Content-Length");
        }
        /*
         * How this works:
         *
         * We gradually accumulate the body across multiple buffer reads.
         * Example: Content-Length is 100 bytes, but we receive data in chunks:
         *
         * 1st call: buffer has 40 bytes
         *    - body.length = 0, remaining = 100 - 0 = 100
         *    - toCopyLength = min(100, 40) = 40
         *    - Create newBody[0 + 40] = 40 bytes
         *    - Copy existing body (0 bytes) + 40 new bytes
         *    - body.length now = 40
         *
         * 2nd call: buffer has 50 bytes
         *    - body.length = 40, remaining = 100 - 40 = 60
         *    - toCopyLength = min(60, 50) = 50
         *    - Create newBody[40 + 50] = 90 bytes
         *    - Copy previous 40 bytes + 50 new bytes
         *    - body.length now = 90
         *
         * 3rd call: buffer has 20 bytes
         *    - body.length = 90, remaining = 100 - 90 = 10
         *    - toCopyLength = min(10, 20) = 10 (only take what we need!)
         *    - Create newBody[90 + 10] = 100 bytes
         *    - Copy previous 90 bytes + 10 new bytes
         *    - body.length now = 100 (DONE!)
         */
        int toCopyLength = Math.min(remaining, length); //we cannot take more that contentLength
        byte[] newBody = new byte[body.length + toCopyLength];
        System.arraycopy(this.body, 0, newBody, 0, body.length); //copying complete body[] into newBody
        System.arraycopy(buffer, offset, newBody, body.length, toCopyLength); //copying the buffer[] into newBody after body[]

        body = newBody; //update body to hold the updated data

        if (body.length > contentLength) {
            throw new IllegalArgumentException("Invalid Body-length");
        }

        if (body.length == contentLength) {
            parserState = ParserState.DONE;
        }

        return toCopyLength;
    }
}
