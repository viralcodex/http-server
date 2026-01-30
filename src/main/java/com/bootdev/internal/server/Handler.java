package com.bootdev.internal.server;

import com.bootdev.internal.request.Request;
import com.bootdev.internal.response.Writer;

import java.io.IOException;

@FunctionalInterface
public interface Handler {
    void handle(Writer writer, Request request) throws Exception;
}
