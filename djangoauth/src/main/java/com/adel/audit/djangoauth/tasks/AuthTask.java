package com.adel.audit.djangoauth.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;
import com.adel.audit.djangoauth.data.Property;
import com.adel.audit.djangoauth.data.RequestMethod;
import com.adel.audit.djangoauth.listeners.OnRequestListener;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AuthTask extends AsyncTask<ArrayList<Property>, Void, List<Property>> {
    String mRequestMethod;
    String mUrlHost;
    String mUrlPath;
    OnRequestListener mOnRequestListener;

    public AuthTask(String urlHost, String urlPath, String requestMethod, OnRequestListener onRequestListener) {
        mRequestMethod = requestMethod;
        mUrlHost = urlHost;
        mUrlPath = urlPath;
        mOnRequestListener = onRequestListener;
    }

    @SafeVarargs
    @Override
    protected final List<Property> doInBackground(ArrayList<Property>... arrayLists) {
        URL url;
        if (mUrlPath.equals("")) {
            url = createUrl(mUrlHost);
        } else if (mUrlHost.equals("")) {
            url = createUrl(mUrlPath);
        } else {
            url = createUrl(mUrlHost + mUrlPath);
        }
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url, mRequestMethod, arrayLists[0]);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return extractJson(jsonResponse);
    }

    @Override
    protected void onPostExecute(List<Property> properties) {

        if (properties != null) {
            mOnRequestListener.onRequest(properties);

        }
        super.onPostExecute(properties);
    }

    //    function used to create URL from string
    private URL createUrl(String url) {
        URL mUrl = null;
        try {
            mUrl = new URL(url);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mUrl;
    }

    //    function used to make http request
    private String makeHttpRequest(URL url, String requestMethod, ArrayList<Property> properties) throws IOException {
        String jsonResponse = "";
//        return empty response if the url is null or invalid
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
//            setup the url connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(requestMethod);
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            //          set this header where we need token
            for (Property property : properties) {
                if (property.key().equals("Authorization")) {
                    urlConnection.setRequestProperty(property.key(), "token" + " " + property.value());
                }
            }
            if (mRequestMethod.equals(RequestMethod.POST)) {
                urlConnection.setDoOutput(true);
//          write in stream where the operations are login or register
                //          get the output stream from the url connection
                outputStream = urlConnection.getOutputStream();
                writeToStream(outputStream, properties);
            }


//            connect to the server
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                //            get input stream from http connection
                inputStream = urlConnection.getInputStream();
                //            convert input stream to string
                jsonResponse = readFromStream(inputStream);

                extractJson(jsonResponse);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();

            if (inputStream != null)
                inputStream.close();

            if (outputStream != null)
                outputStream.close();
        }

        return jsonResponse;
    }

    //    function used to extract information from string json
    private ArrayList<Property> extractJson(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        StringBuilder sb = new StringBuilder(jsonResponse);
        jsonResponse = sb.deleteCharAt(0).toString();

//        this for loop used to delete some char from json string file
        for (int i = 0; i < jsonResponse.length(); i++) {
            char c = jsonResponse.charAt(i);
            if (c == '{' || c == '}' || c == ':' || c == ' ' || c == ',' || c == '[' || c == ']') {
                jsonResponse = sb.deleteCharAt(i).toString();
            }
        }

//        this for loop used to delete space between words
        for (int i = 0; i < jsonResponse.length(); i++) {
            char c = jsonResponse.charAt(i);
            if (c == ' ') {
                jsonResponse = sb.deleteCharAt(i).toString();
            }
        }

//        this for loop used to delete double quotation next to other double quotation
        for (int i = 1; i < jsonResponse.length(); i++) {
            char c = jsonResponse.charAt(i);
            char b;
            if (i < jsonResponse.length() - 1) {
                b = jsonResponse.charAt(i + 1);
                if (c == '"' && b == '"') {
                    jsonResponse = sb.deleteCharAt(i).toString();
                }
            }

        }

//        retrieve keys and values from string json
        String key = "";
        String value = "";
        StringBuilder stringBuilderKey = new StringBuilder(key);
        StringBuilder stringBuilderValue = new StringBuilder(value);
        int k = 0;
        boolean isFinish = false;
        ArrayList<Property> properties = new ArrayList<>();
        jsonResponse = sb.append(' ').toString();
        for (int i = 1; i < jsonResponse.length(); i++) {
            char c = jsonResponse.charAt(i);
            if (c == '"') {
                k++;
//                check if retrieve one key and one value
                if (k % 2 == 0) {
                    isFinish = true;
                }
            } else {
                if (isFinish) {
//                    add new object to properties list
                    properties.add(new Property(key, value));
//                    restart string builders to generate next property
                    key = "";
                    value = "";
                    stringBuilderKey.delete(0, stringBuilderKey.length());
                    stringBuilderValue.delete(0, stringBuilderValue.length());
                    isFinish = false;
                }
                if (k % 2 != 1) {
                    key = stringBuilderKey.append(c).toString();
                } else {
                    value = stringBuilderValue.append(c).toString();
                }
            }
        }
        return properties;
    }

    //    function used to write data to output stream
    private void writeToStream(OutputStream outputStream, ArrayList<Property> properties) throws IOException {
        OutputStreamWriter outputStreamWriter = null;
        try {

//            define an string builder
            StringBuilder stringBuilder = new StringBuilder();

//            build the string json file
            stringBuilder.append("{");
            for (Property property : properties) {
                stringBuilder.append("\"").append(property.key()).append("\"").append(":").append("\"")
                        .append(property.value()).append("\"").append(",");
            }
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
            stringBuilder.append("}");

//            define an output stream writer
            outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write(stringBuilder.toString());
            outputStreamWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStreamWriter != null)
                outputStreamWriter.close();
        }
    }

    //    function used to read data from input stream
    private String readFromStream(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }
}