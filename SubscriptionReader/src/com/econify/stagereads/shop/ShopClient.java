package com.econify.stagereads.shop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShopClient {

    static ShopClient mShopClient;

    HttpClient mClient = new DefaultHttpClient();

    public static ShopClient initShopClient() {
        if (mShopClient == null) {
            mShopClient = new ShopClient();
        }
        return mShopClient;
    }

    private ShopClient() {

    }

    public JSONArray getProducts() throws JSONException {
        String path = "http://srvr.stagereads.com/periodicals.json";
        HttpGet request = new HttpGet(path);
        String result = makeRequest(request);

        if (result != null) {
            JSONArray json = new JSONArray(result);

            return json;
        }

        return null;
    }

    public JSONArray getProductData(String url) throws JSONException {
        HttpGet request = new HttpGet(url);
        String result = makeRequest(request);

        if (result != null) {
            JSONArray json = new JSONArray(result);
            return json;
        }
        return null;
    }

    private String makeRequest(HttpUriRequest request) {
        HttpResponse response;
        try {
            HttpClient client = new DefaultHttpClient();
            response = client.execute(request);
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() != 200) {
                // TODO: error!
            }

            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }

            return total.toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
}