package com.example.apiwithvolley;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Patterns;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.apiwithvolley.databinding.ActivitySignupBinding;
import com.example.apiwithvolley.service.App;
import com.example.apiwithvolley.service.VolleyMultipartRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends BaseActivity {

    private static final int SELECT_IMG = 111;
    ActivitySignupBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        if (App.getUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        }

        bind.ifvProfile.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
            startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_IMG);
        });

        bind.rg.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rbMale:
                    gender = 1;
                    break;
                case R.id.rbFemale:
                    gender = 2;
                    break;
            }
        });

        bind.btnSignUp.setOnClickListener(v -> {
            if (isDataValid()) signup();
        });

        bind.tvSignIn.setText(Html.fromHtml("Not a member?  <font color='blue'>Sign In</font>."));
        bind.tvSignIn.setOnClickListener(v -> startActivity(new Intent(SignUpActivity.this, SignInActivity.class)));

        /*if (BuildConfig.DEBUG) {
            bind.etEmail.setText(testEmail);
            bind.etPass.setText(testPass);
            bind.rbMale.setChecked(true);
            bind.etCountry.setText("India");
        }*/
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMG) {
                Uri imgUri = data.getData();
                if (null != imgUri) {
                    try {
                        bind.ifvProfile.setImageURI(imgUri);
                        profile = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void signup() {
        bind.ll.setClickable(false);
        bind.pb.setVisibility(View.VISIBLE);

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, BASE_URL + "signup", response -> {
            bind.ll.setClickable(true);
            bind.pb.setVisibility(View.GONE);

            String resultResponse = new String(response.data);
            try {
                JSONObject jsonObject = new JSONObject(resultResponse);

                String message = jsonObject.getString("message");
                tos(message);

                Integer code = jsonObject.getInt("code");
                String tempId = jsonObject.getJSONObject("data").getString("user_reg_temp_id");
                if (code == 200) {
                    startActivity(
                            new Intent(SignUpActivity.this, VerifyActivity.class)
                                    .putExtra("temp_id", tempId)
//                                    .putExtra("email", email)
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
                log(e.getMessage());
            }
            log(resultResponse);
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                bind.ll.setClickable(true);
                bind.pb.setVisibility(View.GONE);
                error.printStackTrace();
                log(error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", firstname);
                params.put("last_name", lastname);
                params.put("email_id", email);
                params.put("password", pass);
                params.put("gender", gender.toString());
                params.put("country", country);

                Map<String, String> body = new HashMap<>();
                body.put("request_data", new Gson().toJson(params));
                return body;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("profile_img", new DataPart("1.jpg", BitmapToByte(profile), "image/jpeg"));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(multipartRequest);
    }

    String firstname = "";
    String lastname = "";
    String email = "";
    String pass = "";
    Integer gender = -1;
    String country = "";
    Bitmap profile;

    private boolean isDataValid() {
        firstname = bind.etFirstName.getText().toString().trim();
        lastname = bind.etLastName.getText().toString().trim();
        email = bind.etEmail.getText().toString().trim();
        pass = bind.etPass.getText().toString().trim();
        country = bind.etCountry.getText().toString().trim();

        if (email.equals("")) {
            tos("email is empty!");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tos("email is invalid!");
            return false;
        }

        if (pass.equals("")) {
            tos("password is empty!");
            return false;
        }

        if (!(gender == 1 || gender == 2)) {
            tos("gender not selected");
            return false;
        }

        if (country.equals("")) {
            tos("country is empty!");
            return false;
        }

        if (profile == null) {
            tos("profile not selected");
            return false;
        }

        return true;
    }


}