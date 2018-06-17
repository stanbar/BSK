package com.milbar.logic.security.wrappers;

import com.milbar.logic.abstracts.ArrayDestroyer;
import com.milbar.logic.abstracts.Destroyable;

public class Password implements Destroyable {
    
    private char[] secret;
    
    public Password(char[] secret) {
        this.secret = secret;
    }
    
    public Password(byte[] secret) {
        String string = new String(secret);
        this.secret = string.toCharArray();
    }

    public char[] getSecret() {
        return secret;
    }
    
    @Override
    public void destroy() {
        ArrayDestroyer.destroy(secret);
    }
}
