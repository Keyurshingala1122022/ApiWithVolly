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
import com.example.apiwithvolley.databinding.ActivitySignInBinding;
import com.example.apiwithvolley.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class SignInActivity extends BaseActivity {

    ActivitySignInBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        bind.btnSignIn.setOnClickListener(v -> {
            if (isDataValid()) signIn();
        });

        if (BuildConfig.DEBUG) {
            bind.etEmail.setText(testEmail);
            bind.etPass.setText(testPass);
        }
    }

    private void signIn() {
        String signInUrl = "http://192.168.0.108/ask_question_poll/api/public/api/loginForUser";

        bind.ll.setClickable(false);
        bind.pb.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, signInUrl, response -> {
            bind.ll.setClickable(true);
            bind.pb.setVisibility(View.GONE);

            try {
                JSONObject jsonObject = new JSONObject(response);

                String name = jsonObject.getString("message");
                tos(name);

                JSONObject data = jsonObject.getJSONObject("data");

                User user = g.fromJson(data.getString("user_details"), User.class);

                if (jsonObject.getInt("code") == 200) {
                    App.putString("token", data.getString("token"));
                    App.putUser(user);

                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
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
                params.put("device_type", isTablet(SignInActivity.this) ? "Tablet" : "Phone");
                params.put("device_registration_date", DateFormat.format("yyyy-MM-dd hh:mm:ss a", Build.TIME).toString());
                params.put("is_active", "0");

                Map<String, String> body = new HashMap<>();
                body.put("email_id", email);
                body.put("password", pass);
                body.put("device_info", params.toString());
//                body.put("device_info", new Gson().toJson(params));

                // returning our params.
                return body;
            }
        };

        queue.add(stringRequest);
    }

    String email = "";
    String pass = "";

    private boolean isDataValid() {
        email = bind.etEmail.getText().toString().trim();
        pass = bind.etPass.getText().toString().trim();

        if (email.equals("")) {
            tos("email is empty");
            return false;
        }

        if (pass.equals("")) {
            tos("Password is empty");
            return false;
        }

        return true;
    }
}