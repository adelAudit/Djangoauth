package com.adel.audit.djangoauth.data;

public class AuthResult {
    private String mResponse;
    private String mUsername;
    private String mEmail;
    private String mToken;

    public AuthResult(String response, String username, String email, String token) {
        mResponse = response;
        mUsername = username;
        mEmail = email;
        mToken = token;
    }

    public AuthResult(String response) {
        mResponse = response;
    }

    public String getResponse() {
        return mResponse;
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

    public Boolean isSuccessful() {
        return !mResponse.equals("Error");
    }
}
