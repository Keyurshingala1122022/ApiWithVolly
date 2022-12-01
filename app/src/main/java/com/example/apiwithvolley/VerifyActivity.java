package com.example.apiwithvolley;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.apiwithvolley.databinding.ActivityVerifyBinding;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class VerifyActivity extends BaseActivity {

    ActivityVerifyBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityVerifyBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());


        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String tempId;
    String email;

    private void initUI() {
        tempId = getIntent().getStringExtra("temp_id");
        email = getIntent().getStringExtra("email");
        if (tempId == null || email ==null) {
            tos("Something went wrong");
            return;
        }

        bind.btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify();
            }
        });
    }

    private void verify() {
        String token = bind.etOtp.getText().toString().trim();
        if (token.equals("")) {
            tos("otp is empty");
            return;
        }

        String verifyUrl = "http://192.168.0.108/ask_question_poll/api/public/api/verifyUser";
//        String verifyUrl = "http://192.168.0.108/ask_question_poll/api/public/api/verifyOtpForUser";

        bind.ll.setClickable(false);
        bind.pb.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, verifyUrl, response -> {
            bind.ll.setClickable(true);
            bind.pb.setVisibility(View.GONE);

//            {"code":201,"message":"Trying to get property 'device_info' of non-object","cause":"","data":""}
            try {
                JSONObject jsonObject = new JSONObject(response);

                String message = jsonObject.getString("message");
                tos(message);

                Integer code = jsonObject.getInt("code");
                if (code == 200) {
                    startActivity(new Intent(VerifyActivity.this, SignInActivity.class));
                    finishAffinity();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            log(response);
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                bind.ll.setClickable(true);
                bind.pb.setVisibility(View.GONE);
                error.printStackTrace();
                log(error.getMessage());
            }
        }) {
            @SuppressLint("HardwareIds")
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("device_reg_id", "");
                params.put("device_platform", "Android");
                params.put("device_model_name", Build.MODEL);
                params.put("device_vendor_name", Build.MANUFACTURER);
                params.put("device_os_version", String.valueOf(Build.VERSION.SDK_INT));
                params.put("device_udid", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                params.put("device_resolution", dm.widthPixels + "x" + dm.heightPixels);

                TelephonyManager tm = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
                params.put("device_carrier", tm.getNetworkOperatorName());
                params.put("device_country_code", tm.getNetworkCountryIso());
                params.put("device_language", Locale.getDefault().getLanguage());
                params.put("device_local_code", Locale.getDefault().getISO3Language());
                params.put("device_default_time_zone", TimeZone.getDefault().toString());
                params.put("device_library_version", "");
                params.put("device_application_version", BuildConfig.VERSION_NAME);
                params.put("device_type", isTablet(VerifyActivity.this) ? "Tablet" : "Phone");
                params.put("device_registration_date", DateFormat.format("yyyy-MM-dd hh:mm:ss a", Build.TIME).toString());
                params.put("is_active", "1");

                Map<String, String> body = new HashMap<>();
                body.put("user_reg_temp_id", tempId);
                body.put("token", token);
//                body.put("device_info", params.toString());
                body.put("device_info", g.toJson(params));


//                Map<String, String> body = new HashMap<>();
//                body.put("email_id", email);
//                body.put("token", token);

                return body;
            }
        };

        queue.add(stringRequest);
    }
}