package com.milbar.logic.security.wrappers;

public interface ProgressListener {
    
    void initializeProgress(long total);

    void onProgressChanged(long change);
    
}