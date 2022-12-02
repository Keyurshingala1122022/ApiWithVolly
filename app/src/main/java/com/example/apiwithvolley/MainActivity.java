package com.example.apiwithvolley;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.apiwithvolley.databinding.ActivityMainBinding;
import com.example.apiwithvolley.service.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    ActivityMainBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        ininUI();
    }

    private void ininUI() {
        bind.tvLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
    }

    private void logOut() {
        bind.pb.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL + "logout", response -> {
            bind.pb.setVisibility(View.GONE);

            try {
                JSONObject jsonObject = new JSONObject(response);

                String message = jsonObject.getString("message");
                tos(message);

                if (jsonObject.getInt("code") == 200) {
                    App.removeAll();
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    finishAffinity();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            log(response);
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                bind.pb.setVisibility(View.GONE);
                error.printStackTrace();
                log(error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + App.getString(App.TOKEN));

                return params;
            }
        };

        queue.add(stringRequest);
    }
}