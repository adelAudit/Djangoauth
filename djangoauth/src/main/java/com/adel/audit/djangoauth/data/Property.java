package com.adel.audit.djangoauth.data;

public class Property {
    private String mKey;
    private String mValue;

    public Property(String key, String value) {
        mKey = key;
        mValue = value;
    }

    public String key() {
        return mKey;
    }

    public String value() {
        return mValue;
    }
}
