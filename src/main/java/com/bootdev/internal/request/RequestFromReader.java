package com.bootdev.internal.request;

import java.io.Reader;

public class RequestFromReader {
    public static Request requestFromReader(Reader reader) {
        Request request = new Request();
        char[] charBuff = new char[8];
        byte[] buffer = new byte[1024];
        int bufferLength = 0;
        try {
            while (request.getParserState() != ParserState.DONE) //keep running until we get and parse the request
            {
                int n = reader.read(charBuff);
                if (n == -1) {
                    break;
                }

                for (int i = 0; i < n; i++) {
                    if (bufferLength >= buffer.length) {
                        throw new IllegalArgumentException("Request line too long");
                    }
                    buffer[bufferLength++] = (byte) charBuff[i]; //convert char into byte buffer
                }

                //how much is read
                int consumed = RequestParser.parseRequestLine(request, buffer, bufferLength);

                if (consumed > 0) //we parsed the data
                {
                    System.arraycopy(
                            buffer,
                            consumed,
                            buffer,
                            0,
                            bufferLength - consumed
                    );
                    bufferLength -= consumed; //update the position we are at right now.
                }
            }

            if (request.getParserState() != ParserState.DONE) {
                throw new IllegalArgumentException("Incomplete Request");
            }
            return request;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
