package com.bootdev.legacy.tcplistener;


import com.bootdev.legacy.httpserver.HttpServer;

class TCPListenerMain {
    public static void main(String[] args) {
        HttpServer httpServer = new HttpServer();
        httpServer.run();
    }
}