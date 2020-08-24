package com.adel.audit.djangoauth;

import android.content.Context;
import android.content.SharedPreferences;
import com.adel.audit.djangoauth.data.Property;
import com.adel.audit.djangoauth.data.User;
import com.adel.audit.djangoauth.listeners.OnCompleteListener;
import com.adel.audit.djangoauth.listeners.OnRequestListener;
import com.adel.audit.djangoauth.tasks.AuthTask;

import java.util.ArrayList;
import java.util.List;

public class DjangoAuth {
    private static DjangoAuth instance = null;
    private OnCompleteListener mOnCompleteListener;
    private String mRequestMethod;
    private Context mContext;
    private String mUrl = "";
    private String mPath = "";
    private ArrayList<Property> mProperties;


    private DjangoAuth() {
    }

    public static DjangoAuth getInstance() {
        if (instance == null) {
            instance = new DjangoAuth();
        }
        return instance;
    }

    //    function used to sign up with email and password
    public DjangoAuth signUpWithEmailPassword() {
        AuthTask authTask = new AuthTask(mUrl, mPath, mRequestMethod, new OnRequestListener() {
            @Override
            public void onRequest(List<Property> properties) {

            }
        });
        authTask.execute(mProperties);
        return instance;
    }

    //    function used to login with email and password
    public DjangoAuth login() {

        AuthTask authTask = new AuthTask(mUrl, mPath, mRequestMethod, new OnRequestListener() {
            @Override
            public void onRequest(List<Property> properties) {
                mOnCompleteListener.onComplete(properties);
            }
        });
        authTask.execute(mProperties);
        return instance;
    }

    //    function used to register token to local storage
    private void registerToken(String token) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("token", Context.MODE_PRIVATE)
                .edit();
        editor.putString("token", token);
        editor.apply();
    }

    //    function to get user properties
    public User getUser() {
        SharedPreferences tokenPreference = mContext.getSharedPreferences("token", Context.MODE_PRIVATE);
        String token = tokenPreference.getString("token", null);
        mProperties.add(new Property("token", token));
        AuthTask authTask = new AuthTask(mUrl, mPath, mRequestMethod, new OnRequestListener() {
            @Override
            public void onRequest(List<Property> properties) {
                mOnCompleteListener.onComplete(properties);
            }
        });
        authTask.execute(mProperties);
        return null;
    }

    public DjangoAuth setContext(Context context) {
        mContext = context;
        return instance;
    }

    public DjangoAuth setUrl(String url) {
        mUrl = url;
        return instance;
    }

    public DjangoAuth setPath(String path) {
        mPath = path;
        return instance;
    }

    public DjangoAuth setRequestMethod(String requestMethod) {
        mRequestMethod = requestMethod;
        return instance;
    }

    public DjangoAuth setProperties(ArrayList<Property> properties) {
        mProperties = properties;
        return instance;
    }

    public DjangoAuth build() {
        return instance;
    }

    public DjangoAuth addOnCompeteListener(OnCompleteListener onCompleteListener) {
        mOnCompleteListener = onCompleteListener;
        return instance;
    }

    public DjangoAuth addOnFailedListener(String error) {
        return instance;
    }

}

