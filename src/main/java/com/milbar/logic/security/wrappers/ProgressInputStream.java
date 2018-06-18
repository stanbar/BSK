package com.milbar.logic.security.wrappers;


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

    public long getTotal() {
        return this.total;
    }

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
    public int read(byte[] b, int off, int len) throws IOException {
        int read = this.stream.read(b, off, len);
        this.totalRead += read;
        this.listener.onProgressChanged(totalRead);
        
        return read;
    }
}
