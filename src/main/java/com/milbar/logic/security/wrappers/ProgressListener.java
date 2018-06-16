package com.milbar.logic.security.wrappers;


/**
 * @author gcurtis
 * @link https://github.com/box/box-java-sdk/blob/master/src/main/java/com/box/sdk/ProgressInputStream.java
 * Project name: https://github.com/box/box-java-sdk
 *
 * The listener interface for monitoring the progress of a long-running API call.
 */
public interface ProgressListener {
    
    void initializeProgress(long total);

    void onProgressChanged(long change);
    
}