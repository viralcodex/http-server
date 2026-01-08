package com.bootdev.tcplistener;


import com.bootdev.httpserver.HttpServer;

class TCPListenerMain {
    public static void main(String[] args) {
        HttpServer httpServer = new HttpServer();
        httpServer.run();
    }
}