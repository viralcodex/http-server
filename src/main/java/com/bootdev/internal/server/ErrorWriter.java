package com.bootdev.internal.server;

import com.bootdev.internal.headers.Headers;
import com.bootdev.internal.response.ResponseWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ErrorWriter {

    private ErrorWriter() {}

    public static void write(OutputStream outputStream, HandlerError error) throws IOException {
        byte[] errorBody = error.message().getBytes(StandardCharsets.US_ASCII);

        ResponseWriter.writeStatusLine(outputStream, error.statusCode());

        Headers headers = ResponseWriter.getDefaultHeaders(errorBody.length);

        ResponseWriter.writeHeaders(outputStream, headers);
        outputStream.write(errorBody);
    }
}
