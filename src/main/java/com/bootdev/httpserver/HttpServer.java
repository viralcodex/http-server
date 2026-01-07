package com.bootdev.httpserver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class HttpServer {
    public void run() {
        try (InputStream inputStream = new FileInputStream("messages.txt")) {
            //have to read in 8 bytes chunks from the file until EOF
            byte[] buffer = new byte[8]; //buffer to hold 8 bytes
            StringBuilder currentLine = new StringBuilder();
            while (true) {
                int bytesRead = inputStream.read(buffer);
                if (bytesRead == -1) //end of file
                {
                    break;
                }
                String chunk = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                String[] parts = chunk.split("\n"); //split on end of line

                //add these parts(not the last one as it will be of next line)
                for (int i = 0; i < parts.length - 1; i++) {
                    currentLine.append(parts[i]);
                    System.out.printf("read: %s\n", currentLine);
                    currentLine.setLength(0); //reset currentLine
                }
                currentLine.append(parts[parts.length - 1]);
            }
            if (!currentLine.isEmpty()) //after EOF if data is there in it, flush it
            {
                System.out.printf("read: %s\n", currentLine);
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}