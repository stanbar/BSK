package com.milbar.logic.login;

public class UserCredentials {
    
    //todo implement safe password storing method
    
    private String username, password;
    
    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void destroy() {
        username = null;
        password = null;
    }
}
