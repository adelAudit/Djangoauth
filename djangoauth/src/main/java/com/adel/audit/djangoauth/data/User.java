package com.adel.audit.djangoauth.data;

public class User {
    private String mUsername;
    private String mEmail;
    private String mToken;

    public User(String username, String email, String token) {
        mUsername = username;
        mEmail = email;
        mToken = token;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getToken() {
        return mToken;
    }

}
