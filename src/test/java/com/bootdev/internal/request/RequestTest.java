package com.bootdev.internal.request;

import com.bootdev.chunkreader.ChunkReader;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RequestTest {
    private static final String GET_WITH_NO_PATH = "GET / HTTP/1.1\r\n" + "Host: localhost:42069\r\n\r\n";
    private static final String GET_WITH_PATH = "GET /coffee HTTP/1.1\r\n\r\n";
    private static final String MALFORMED_REQUEST = "/coffee HTTP/1.1\r\n\r\n";
    private static final String MALFORMED_REQUEST_2 = "GeT / HTTP/1.1\r\n\r\n";
    private static final String MALFORMED_REQUEST_3 = "GeT / HTTP/2.0\r\n\r\n";

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 4, 8, 16, 32})
    void goodGetRequestLine(int chunkSize) {
        ChunkReader reader = new ChunkReader(GET_WITH_NO_PATH, chunkSize);
        Request request = RequestFromReader.requestFromReader(reader);
        assertNotNull(request);
        assertEquals("GET", request.getRequestLine().getMethod());
        assertEquals("1.1", request.getRequestLine().getHttpVersion());
        assertEquals("/", request.getRequestLine().getRequestTarget());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 4, 8, 16, 32})
    void getGoodRequestLineWithPath(int chunkSize) {
        ChunkReader reader = new ChunkReader(GET_WITH_PATH, chunkSize);
        Request request = RequestFromReader.requestFromReader(reader);
        assertEquals("GET", request.getRequestLine().getMethod());
        assertEquals("/coffee", request.getRequestLine().getRequestTarget());
        assertEquals("1.1", request.getRequestLine().getHttpVersion());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 4, 8, 16, 32})
    void invalidNumberOfPartsInRequestLine(int chunkSize) {
        ChunkReader reader = new ChunkReader(MALFORMED_REQUEST, chunkSize);
        assertThrows(IllegalArgumentException.class, () -> RequestFromReader.requestFromReader(reader));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 4, 8, 16, 32})
    void invalidMethodOutOfOrder(int chunkSize) {
        ChunkReader reader = new ChunkReader(MALFORMED_REQUEST_2, chunkSize);
        assertThrows(IllegalArgumentException.class, () -> RequestFromReader.requestFromReader(reader));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 4, 8, 16, 32})
    void invalidHttpVersion(int chunkSize) {
        ChunkReader reader = new ChunkReader(MALFORMED_REQUEST_3, chunkSize);
        assertThrows(IllegalArgumentException.class, () -> RequestFromReader.requestFromReader(reader));
    }
}
