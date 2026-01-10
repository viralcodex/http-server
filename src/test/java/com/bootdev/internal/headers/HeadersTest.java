package com.bootdev.internal.headers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HeadersTest {

    private static final String SINGLE_HEADER = "host: localhost:42069\r\n\r\n";
    private static final String SINGLE_HEADER_2 = "user-agent: curl/7.81.0\r\n\r\n";
    private static final String SINGLE_HEADER_WTH_EXTRA_SPACES = "   host:    localhost:42069   \r\n\r\n";

    private static final String INVALID_HEADER = "       host : localhost:42069       \r\n\r\n";
    private static final String INVALID_HEADER_WITH_INVALID_KEY = "h©st: localhost:42069\r\n\r\n";
    private static final String SEPARATOR = "\r\n";

    @Test
    public void validSingleHeader() {
        byte[] buffer = SINGLE_HEADER.getBytes();
        Headers header = Headers.newHeaders();
        HeadersResult result = header.parse(buffer, buffer.length);

        assertNull(result.getError());
        assertEquals(23, result.getBytesConsumed());
        assertFalse(result.getIsDone(), String.valueOf(Boolean.FALSE));
        assertEquals("localhost:42069", header.get("host"));
    }

    @Test
    public void validDone() {
        byte[] buffer = SEPARATOR.getBytes();
        Headers header = Headers.newHeaders();
        HeadersResult result = header.parse(buffer, buffer.length);

        assertTrue(result.getIsDone(), String.valueOf(Boolean.TRUE));
        assertEquals(2, result.getBytesConsumed());
    }

    @Test
    void validSingleHeaderWithExtraWhitespace() {
        Headers headers = Headers.newHeaders();
        byte[] buffer = SINGLE_HEADER_WTH_EXTRA_SPACES.getBytes();

        HeadersResult r = headers.parse(buffer, buffer.length);

        assertNull(r.getError());
        assertEquals("localhost:42069", headers.get("host"));
    }

    @Test
    void validTwoHeadersWithExistingHeaders() {
        Headers headers = Headers.newHeaders();

        // First header
        byte[] data1 = SINGLE_HEADER.getBytes();
        HeadersResult r1 = headers.parse(data1, data1.length);

        assertNull(r1.getError());
        assertFalse(r1.getIsDone());
        assertEquals("localhost:42069", headers.get("host"));

        // Second header (parsed in a separate call)
        byte[] buffer = SINGLE_HEADER_2.getBytes();
        HeadersResult request = headers.parse(buffer, buffer.length);

        assertNull(request.getError());
        assertFalse(request.getIsDone());
        assertEquals("curl/7.81.0", headers.get("user-agent"));

        // Ensure BOTH headers exist
        assertEquals(2, headers.size());
    }

    @Test
    public void invalidSpacingHeader() {
        byte[] buffer = INVALID_HEADER.getBytes();
        Headers header = Headers.newHeaders();

        assertThrows(IllegalArgumentException.class, () -> header.parse(buffer, buffer.length));
    }

    @Test
    public void invalidHeaderKey() {
        byte[] buffer = INVALID_HEADER_WITH_INVALID_KEY.getBytes();
        Headers header = Headers.newHeaders();

        assertThrows(IllegalArgumentException.class, () -> header.parse(buffer, buffer.length));
    }

    @Test
    void validMultipleHeaderValues() {
        Headers headers = Headers.newHeaders();

        // First header already present
        headers.put("set-person", "lane-loves-go");

        byte[] buffer = "Set-Person: prime-loves-zig\r\n\r\n".getBytes();

        HeadersResult r = headers.parse(buffer, buffer.length);

        assertNull(r.getError());
        assertFalse(r.getIsDone());
        assertEquals(
                "lane-loves-go, prime-loves-zig",
                headers.get("set-person")
        );
    }

}
