package org.dgl.commons.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PushedNotifier {

    private final String appKey;
    private final String appSecret;
    private final String targetType;
    private final HttpClient httpclient;
    private Exception lastException;

    public PushedNotifier(String appKey, String appSecret) {
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.targetType = "app";
        this.httpclient = HttpClients.createDefault();
        this.lastException = null;
    }

    public boolean push(String message) {
        boolean success = false;
        HttpPost httppost = new HttpPost("https://api.pushed.co/1/push");
        List<NameValuePair> params = new ArrayList(2);
        params.add(new BasicNameValuePair("app_key", this.appKey));
        params.add(new BasicNameValuePair("app_secret", this.appSecret));
        params.add(new BasicNameValuePair("target_type", this.targetType));
        params.add(new BasicNameValuePair("content", message));
        BufferedReader reader = null;
        try {
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = this.httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                reader = new BufferedReader(new InputStreamReader(entity.getContent()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("\"type\":\"shipment_successfully_sent\"")) {
                        success = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            this.lastException = e;
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {}
            return success;
        }
    }

    public Exception getLastException() {
        return this.lastException;
    }
}