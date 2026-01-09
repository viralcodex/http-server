package com.bootdev.internal.request;

import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RequestTest {

    @Test
    void goodGetRequestLine() {
        Request request = RequestParser.requestFromReader(new StringReader("GET / HTTP/1.1\r\n" + "Host: localhost:42069\r\n\r\n"));
        assertNotNull(request);
        assertEquals("GET", request.getRequestLine().getMethod());
        assertEquals("1.1", request.getRequestLine().getHttpVersion());
        assertEquals("/", request.getRequestLine().getRequestTarget());
    }

    @Test
    void getGoodRequestLineWithPath() {
        Request request = RequestParser.requestFromReader(new StringReader("GET /coffee HTTP/1.1\r\n\r\n"));

        assertEquals("GET", request.getRequestLine().getMethod());
        assertEquals("/coffee", request.getRequestLine().getRequestTarget());
        assertEquals("1.1", request.getRequestLine().getHttpVersion());
    }

    @Test
    void invalidNumberOfPartsInRequestLine() {
        assertThrows(IllegalArgumentException.class, () -> RequestParser.requestFromReader(new StringReader("/coffee HTTP/1.1\r\n\r\n")));
    }

    @Test
    void invalidMethodOutOfOrder() {
        assertThrows(IllegalArgumentException.class, () ->
                RequestParser.requestFromReader(
                        new StringReader("GeT / HTTP/1.1\r\n\r\n")
                )
        );
    }

    @Test
    void invalidHttpVersion() {
        assertThrows(IllegalArgumentException.class, () ->
                RequestParser.requestFromReader(
                        new StringReader("GET / HTTP/2.0\r\n\r\n")
                )
        );
    }
}
