package com.bootdev.httpserver;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;

class HttpServer {
    public void run() {
        try{
            InputStream inputStream = new FileInputStream("messages.txt");
            BlockingQueue<String> queue = LinesReader.getLines(inputStream);

            //print the lines from the queue
            while(true)
            {
                String currentLine = queue.take();
                if(LinesReader.isEOF(currentLine))
                {
                    break; //EOF
                }
                System.out.printf("read: %s\n", currentLine);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}