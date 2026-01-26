package com.bootdev.internal.request;

public enum ParserState {
    INITIALIZED,
    PARSING_HEADERS,
    PARSING_BODY,
    DONE
}
