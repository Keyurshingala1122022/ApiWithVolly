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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.apiwithvolley.databinding.ActivityVerifyBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.TimeZone;

public class VerifyActivity extends BaseActivity {

    ActivityVerifyBinding bind;

    @SuppressLint("HardwareIds")
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

    private void initUI() {
        tempId = getIntent().getStringExtra("temp_id");
        log("temp id: " + tempId);

        if (tempId == null) {
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

        bind.ll.setClickable(false);
        bind.pb.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject body = new JSONObject();
        try {
            JSONObject deviceInfo = new JSONObject();

            deviceInfo.put("device_reg_id", "");
            deviceInfo.put("device_platform", "Android");
            deviceInfo.put("device_model_name", Build.MODEL);
            deviceInfo.put("device_vendor_name", Build.MANUFACTURER);
            deviceInfo.put("device_os_version", String.valueOf(Build.VERSION.SDK_INT));
            deviceInfo.put("device_udid", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            deviceInfo.put("device_resolution", dm.widthPixels + "x" + dm.heightPixels);

            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            deviceInfo.put("device_carrier", tm.getNetworkOperatorName());
            deviceInfo.put("device_country_code", getResources().getConfiguration().locale.getCountry());
            deviceInfo.put("device_language", Locale.getDefault().getLanguage());
            deviceInfo.put("$device_local_code", Locale.getDefault().getISO3Language());
            deviceInfo.put("device_default_time_zone", TimeZone.getDefault().getID());
            deviceInfo.put("device_library_version", "");
            deviceInfo.put("device_application_version", BuildConfig.VERSION_NAME);
            deviceInfo.put("device_type", isTablet(VerifyActivity.this) ? "Tablet" : "Phone");
            deviceInfo.put("device_registration_date", DateFormat.format("yyyy-MM-dd hh:mm:ss a", Build.TIME).toString());
            deviceInfo.put("is_active", "1");


            body.put("user_reg_temp_id", tempId);
            body.put("token", token);
            body.put("device_info", deviceInfo);

            log(body.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL + "verifyUser", body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                bind.ll.setClickable(true);
                bind.pb.setVisibility(View.GONE);

                try {
                    String message = response.getString("message");
                    tos(message);

                    int code = response.getInt("code");
                    if (code == 200) {
                        startActivity(new Intent(VerifyActivity.this, SignInActivity.class));
                        finishAffinity();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                log(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                bind.ll.setClickable(true);
                bind.pb.setVisibility(View.GONE);
                error.printStackTrace();
                log(error.getMessage());
            }
        });

        queue.add(request);
    }
}