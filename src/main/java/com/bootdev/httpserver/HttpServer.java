package com.bootdev.httpserver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class HttpServer {
    public void run(){
        try(InputStream inputStream = new FileInputStream("messages.txt"))
        {
            //have to read in 8 bytes chunks from the file until EOF
            byte[] buffer = new byte[8]; //buffer to hold 8 bytes
            while(true){
                int bytesRead = inputStream.read(buffer);
                if( bytesRead == -1) //end of file
                {
                    System.out.println("End of File, exiting...");
                    System.exit(0);
                }
                String chunk = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                System.out.printf("read: %s\n", chunk);
            }
        } catch (FileNotFoundException e)
        {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}