package com.bootdev.chunkreader;

import java.io.IOException;
import java.io.Reader;

public class ChunkReader extends Reader {
    private final String data;
    private final int chunkSize;
    private int pos = 0;


    public ChunkReader(String data, int chunkSize) {
        this.data = data;
        this.chunkSize = chunkSize;
    }

    @Override
    public int read(char[] cbuf, int off, int length) {
        if (this.pos >= data.length()) return -1; //can't access position outside the buffer size
        int endIndex = Math.min(Math.min(chunkSize, length), data.length() - this.pos);
        data.getChars(this.pos, this.pos + endIndex, cbuf, off);
        this.pos += endIndex;
        return endIndex;
    }

    @Override
    public void close() throws IOException {
    }
}
