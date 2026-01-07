package com.bootdev.httpserver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class HTTPServerMain {
    public static void main(String[] args) {
        HttpServer httpServer = new HttpServer();
        httpServer.run();
    }
}