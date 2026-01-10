package com.bootdev.internal.headers;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Headers extends HashMap<String, String> {
    private static final String allowedCharacters = "!#$%&'*+-.^_`|~";
    public static Headers newHeaders() {
        return new Headers();
    }

    public HeadersResult parse(byte[] buffer, int bufferLength) {
        for (int i = 0; i < bufferLength - 1; i++) {
            if (buffer[i] == '\r' && buffer[i + 1] == '\n') {
                if (i == 0) //empty header
                {
                    return new HeadersResult(2, true, null); //return early
                }

                String headerLine = new String(buffer, 0, i, StandardCharsets.US_ASCII);
                parseHeaderLine(headerLine);

                //coz adding \r\n in the parsing as well
                return new HeadersResult(i + 2, false, null);
            }
        }
        //no CRLF -> need more data to parse
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
        if(!isValidHeaderName(key))
        {
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
