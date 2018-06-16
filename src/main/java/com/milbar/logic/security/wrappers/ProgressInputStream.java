package com.milbar.logic.security.wrappers;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;


public class ProgressInputStream extends InputStream {
    private final InputStream stream;
    private final ProgressListener listener;
    
    private long total;
    private long totalRead;
    private int progress;
    
    public ProgressInputStream(InputStream stream, ProgressListener listener, long total) {
        this.stream = stream;
        this.listener = listener;
        this.listener.initializeProgress(total);
        this.total = total;
    }
    
    /**
     * Gets the total number of bytes that are expected to be read from the stream.
     * @return the total number of bytes.
     */
    public long getTotal() {
        return this.total;
    }
    
    /**
     * Sets the total number of bytes that are expected to be read from the stream.
     * @param total the total number of bytes
     */
    public void setTotal(long total) {
        this.total = total;
    }
    
    @Override
    public void close() throws IOException {
        this.stream.close();
    }
    
    @Override
    public int read() throws IOException {
        int read = this.stream.read();
        this.totalRead++;
        this.listener.onProgressChanged(totalRead);
        
        return read;
    }
    
    @Override
    public int read(@NotNull byte[] b, int off, int len) throws IOException {
        int read = this.stream.read(b, off, len);
        this.totalRead += read;
        this.listener.onProgressChanged(totalRead);
        
        return read;
    }
}
