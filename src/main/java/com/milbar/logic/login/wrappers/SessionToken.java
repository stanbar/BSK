package com.milbar.logic.login.wrappers;

import java.util.Arrays;

public class SessionToken {
    
    private String username;
    private byte[] token;
    
    public SessionToken(String username, byte[] token) {
        this.username = username;
        this.token = token;
    }
    
    public byte[] getToken() {
        return token;
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        
        if (other instanceof SessionToken) {
            SessionToken otherToken = (SessionToken)other;
            return Arrays.equals(this.token, otherToken.token)
                    && this.username.equals(otherToken.username);
        }
        else
            return false;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + Arrays.hashCode(this.token);
        result = 31 * result + username.hashCode();
        return result;
    }
    
}
