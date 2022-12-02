package com.example.apiwithvolley;

import android.os.Bundle;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.example.apiwithvolley.databinding.ActivityCacheBinding;
import com.example.apiwithvolley.service.CacheRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class CacheActivity extends BaseActivity {

    ActivityCacheBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityCacheBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        CacheRequest cacheRequest = new CacheRequest(Request.Method.GET, "https://jsonplaceholder.typicode.com/posts", new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    final String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    JSONObject jsonObject = new JSONObject(jsonString);
                    log(jsonObject.toString(5));

                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                    log("error1 : " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                log("error2 : " + error);
            }
        });

        Volley.newRequestQueue(this).add(cacheRequest);
    }
}