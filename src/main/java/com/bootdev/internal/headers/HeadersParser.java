package com.bootdev.internal.headers;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class HeadersParser extends HashMap<String, String> {
    private static final String allowedCharacters = "!#$%&'*+-.^_`|~";

    public static HeadersParser newHeaders() {
        return new HeadersParser();
    }

    public HeadersResult parseHeaders(byte[] buffer, int offset, int length) {
        if (buffer == null || buffer.length == 0) {
            return new HeadersResult(0, false, null);
        }
        if (offset < 0 || length < 0 || offset + length > buffer.length) {
            throw new IllegalArgumentException("Invalid buffer offset or length");
        }

        for (int i = offset; i < offset + length - 1; i++) {
            if (buffer[i] == '\r' && buffer[i + 1] == '\n') {
                if (i == offset) //empty header
                {
                    return new HeadersResult(2, true, null); //return early
                }

                String headerLine = new String(buffer, offset, i - offset, StandardCharsets.US_ASCII);
                parseHeaderLine(headerLine);

                //coz adding \r\n in the parsing as well
                return new HeadersResult((i - offset) + 2, false, null);
            }
        }
        //no CRLF -> need more data to parseHeaders
        return new HeadersResult(0, false, null);
    }

    private void parseHeaderLine(String headerLine) {
        int colonIndex = headerLine.indexOf(':');

        if (colonIndex <= 0) {
            throw new IllegalArgumentException("Malformed Headers");
        }
        if (headerLine.charAt(colonIndex - 1) == ' ') {
            throw new IllegalArgumentException("Invalid spacing before colon");
        }

        String key = headerLine.substring(0, colonIndex).trim().toLowerCase();
        if (!isValidHeaderName(key)) {
            throw new IllegalArgumentException("Invalid header name");
        }
        String value = headerLine.substring(colonIndex + 1).trim();
        String existingValue = getOrDefault(key, null);
        put(key, existingValue == null ? value : existingValue.concat(", ").concat(value));
    }

    private boolean isValidHeaderName(String key) {
        if (key.isEmpty()) {
            return false;
        }
        for (char c : key.toCharArray()) {
            boolean isAllowed = allowedCharacters.indexOf(c) >= 0;
            if (!Character.isLetterOrDigit(c) && !isAllowed) {
                return false;
            }
        }
        return true;
    }
}
