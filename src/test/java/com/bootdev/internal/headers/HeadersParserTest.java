package com.bootdev.internal.headers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HeadersParserTest {

    private static final String SINGLE_HEADER = "host: localhost:42069\r\n\r\n";
    private static final String SINGLE_HEADER_2 = "user-agent: curl/7.81.0\r\n\r\n";
    private static final String SINGLE_HEADER_WTH_EXTRA_SPACES = "   host:    localhost:42069   \r\n\r\n";

    private static final String INVALID_HEADER = "       host : localhost:42069       \r\n\r\n";
    private static final String INVALID_HEADER_WITH_INVALID_KEY = "h©st: localhost:42069\r\n\r\n";
    private static final String SEPARATOR = "\r\n";

    @Test
    public void validSingleHeader() {
        byte[] buffer = SINGLE_HEADER.getBytes();
        HeadersParser header = HeadersParser.newHeaders();
        HeadersResult result = header.parseHeaders(buffer, 0, buffer.length);

        assertNull(result.getError());
        assertEquals(23, result.getBytesConsumed());
        assertFalse(result.getIsDone(), String.valueOf(Boolean.FALSE));
        assertEquals("localhost:42069", header.get("host"));
    }

    @Test
    public void validDone() {
        byte[] buffer = SEPARATOR.getBytes();
        HeadersParser header = HeadersParser.newHeaders();
        HeadersResult result = header.parseHeaders(buffer, 0, buffer.length);

        assertTrue(result.getIsDone(), String.valueOf(Boolean.TRUE));
        assertEquals(2, result.getBytesConsumed());
    }

    @Test
    void validSingleHeaderWithExtraWhitespace() {
        HeadersParser headersParser = HeadersParser.newHeaders();
        byte[] buffer = SINGLE_HEADER_WTH_EXTRA_SPACES.getBytes();

        HeadersResult r = headersParser.parseHeaders(buffer, 0, buffer.length);

        assertNull(r.getError());
        assertEquals("localhost:42069", headersParser.get("host"));
    }

    @Test
    void validTwoHeadersWithExistingHeaders() {
        HeadersParser headersParser = HeadersParser.newHeaders();

        // First header
        byte[] data1 = SINGLE_HEADER.getBytes();
        HeadersResult r1 = headersParser.parseHeaders(data1, 0, data1.length);

        assertNull(r1.getError());
        assertFalse(r1.getIsDone());
        assertEquals("localhost:42069", headersParser.get("host"));

        // Second header (parsed in a separate call)
        byte[] buffer = SINGLE_HEADER_2.getBytes();
        HeadersResult request = headersParser.parseHeaders(buffer, 0, buffer.length);

        assertNull(request.getError());
        assertFalse(request.getIsDone());
        assertEquals("curl/7.81.0", headersParser.get("user-agent"));

        // Ensure BOTH headers exist
        assertEquals(2, headersParser.size());
    }

    @Test
    public void invalidSpacingHeader() {
        byte[] buffer = INVALID_HEADER.getBytes();
        HeadersParser header = HeadersParser.newHeaders();

        assertThrows(IllegalArgumentException.class, () -> header.parseHeaders(buffer, 0, buffer.length));
    }

    @Test
    public void invalidHeaderKey() {
        byte[] buffer = INVALID_HEADER_WITH_INVALID_KEY.getBytes();
        HeadersParser header = HeadersParser.newHeaders();

        assertThrows(IllegalArgumentException.class, () -> header.parseHeaders(buffer, 0, buffer.length));
    }

    @Test
    void validMultipleHeaderValues() {
        HeadersParser headersParser = HeadersParser.newHeaders();

        // First header already present
        headersParser.put("set-person", "lane-loves-go");

        byte[] buffer = "Set-Person: prime-loves-zig\r\n\r\n".getBytes();

        HeadersResult r = headersParser.parseHeaders(buffer, 0, buffer.length);

        assertNull(r.getError());
        assertFalse(r.getIsDone());
        assertEquals(
                "lane-loves-go, prime-loves-zig",
                headersParser.get("set-person")
        );
    }

}
