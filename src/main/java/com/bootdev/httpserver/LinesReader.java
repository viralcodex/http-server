package com.bootdev.httpserver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Using threads creates a reusable method to get stream of lines and then close the thread
 */
public class LinesReader {

    private static final String EOF = "__EOF__";
    public static BlockingQueue<String> getLines(InputStream inputStream)
    {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        Thread producer = new Thread(() -> {
            try (InputStream in = inputStream) {
                //have to read in 8 bytes chunks from the file until EOF
                byte[] buffer = new byte[8]; //buffer to hold 8 bytes
                StringBuilder currentLine = new StringBuilder();
                while (true) {
                    int bytesRead = in.read(buffer);
                    if (bytesRead == -1) //end of file
                    {
                        break;
                    }
                    String chunk = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                    String[] parts = chunk.split("\n", -1); //split on end of line

                    //add these parts(not the last one as it will be of next line)
                    for (int i = 0; i < parts.length - 1; i++) {
                        currentLine.append(parts[i]);
                        queue.put(currentLine.toString());
                        currentLine.setLength(0); //reset currentLine
                    }
                    currentLine.append(parts[parts.length - 1]);
                }
                if (!currentLine.isEmpty()) //after EOF if data is there in it, flush it
                {
                    queue.put(currentLine.toString());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    queue.put(EOF); //after all lines put the sentinel in the queue to give EOF
                } catch (Exception ignored){}
            }
        });

        producer.start(); //run the thread
        return queue;
    }

    static boolean isEOF(String line)
    {
        return EOF.equals(line);
    }
}
