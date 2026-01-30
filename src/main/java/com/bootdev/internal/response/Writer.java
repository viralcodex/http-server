package com.bootdev.internal.response;

import com.bootdev.internal.headers.Headers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Writer {
    private enum WriterState {
        INIT,
        STATUS_WRITTEN,
        HEADERS_WRITTEN,
        CHUNKING,
        CHUNKING_DONE,
        BODY_WRITTEN,
        TRAILERS_WRITTEN,
    }

    private WriterState state = WriterState.INIT;
    private final OutputStream outputStream;

    public Writer(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void writeStatus(StatusCode statusCode) throws IOException {
        if (state != WriterState.INIT) {
            throw new IllegalStateException("Status line must be written first");
        }

        ResponseWriter.writeStatusLine(outputStream, statusCode);

        state = WriterState.STATUS_WRITTEN;
    }

    public void writeHeaders(Headers headers) throws IOException {
        if (state != WriterState.STATUS_WRITTEN) {
            throw new IllegalStateException("Headers line must be written after status line");
        }

        ResponseWriter.writeHeaders(outputStream, headers);
        state = WriterState.HEADERS_WRITTEN;
    }

    public int writeBody(byte[] body) throws IOException {
        if (state != WriterState.HEADERS_WRITTEN) {
            throw new IllegalStateException("Body line must be written after headers");
        }
        int n = body.length;
        outputStream.write(body);
        state = WriterState.BODY_WRITTEN;
        return n; //how many bytes written
    }

    public int writeChunkedBody(byte[] buffer) throws IOException
    {
        if(state != WriterState.HEADERS_WRITTEN && state != WriterState.CHUNKING)
        {
            throw new IllegalStateException("Chunked body must be written after headers");
        }
        int n = buffer.length;

        if(n == 0)
        {
            return 0;
        }

        //<n>\r\n
        String length = Integer.toHexString(n) + "\r\n";
        outputStream.write(length.getBytes(StandardCharsets.US_ASCII));

        //<data of length n>\r\n
        outputStream.write(buffer);
        outputStream.write("\r\n".getBytes(StandardCharsets.US_ASCII));

        state = WriterState.CHUNKING;
        return n;
    }

    public int writeChunkedBodyDone() throws  IOException
    {
        if(state != WriterState.CHUNKING && state != WriterState.HEADERS_WRITTEN)
        {
            throw new IllegalStateException("Chunked body not started");
        }

        outputStream.write("0\r\n".getBytes(StandardCharsets.US_ASCII));
        state = WriterState.CHUNKING_DONE;
        return 0;
    }

    public void writeTrailers(Headers trailers) throws IOException {
        if(state != WriterState.CHUNKING_DONE)
        {
            throw new IllegalStateException("Trailers line must be written after chunked body is completely finished");
        }

        for(Map.Entry<String, String> entry : trailers.entrySet()) {
            String trailer = entry.getKey() + ": " + entry.getValue() + "\r\n";
            outputStream.write(trailer.getBytes(StandardCharsets.US_ASCII));
        }

        outputStream.write("\r\n".getBytes(StandardCharsets.US_ASCII));
        state = WriterState.TRAILERS_WRITTEN;
    }
}
