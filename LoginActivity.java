package com.addventure.loanadda;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.StaleDataException;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.addventure.loanadda.Model.SMSData;
import com.addventure.loanadda.Utility.AppConfig;
import com.addventure.loanadda.Utility.Network;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.droidsonroids.gif.GifTextView;


public class LoginActivity extends BaseActivity  {

    EditText contactEt;
    TextView englishTv,hindiTv;
    Button loginBtn;
    String contactStr;
    SharedPreferences loginSp;
    public static final String loginPrefrence = "loginSp";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    Locale mylocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleBar();

        setContentView(R.layout.activity_login);
        contactEt = (EditText) findViewById(R.id.contact);
        englishTv = (TextView) findViewById(R.id.english);
        hindiTv = (TextView) findViewById(R.id.hindi);
        loginBtn = (Button) findViewById(R.id.login);


        contactEt.getBackground().mutate().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactStr = contactEt.getText().toString().trim();
                loginSp = getApplicationContext().getSharedPreferences(loginPrefrence, MODE_PRIVATE);
                SharedPreferences.Editor editor = loginSp.edit();
                editor.putString("contactNo", contactStr);
                editor.commit();
                editor.apply();
                if (contactStr.isEmpty()) {
                    contactEt.setError(getResources().getString(R.string.mobilenumbererror));
                } else if (contactStr.length() != 10) {
                    contactEt.setError(getResources().getString(R.string.mobilenumberlengtherror));
                } else {
                    /*Intent intent=new Intent(LoginActivity.this,OtpVerifyActivity.class);
                    startActivity(intent);*/

                    if (Network.isNetworkCheck(getApplicationContext())) {

                        getLoginformserver();


                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.internet_check), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        englishTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLanguage("en");
            }
        });

        hindiTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLanguage("hi");
            }
        });
    }




    private void getLoginformserver() {

        //showProgressDialog();
        progressDialog.show();

        //String login_url = AppConfig.BaseUrl + "otpverifynew";
        String login_url="http://192.168.1.99/app/otpverifynew";

        JSONObject js = new JSONObject();

        try {

            js.put("contact_no", contactStr);
            Log.d("jsosoonn",js.toString());

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, login_url, js,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //progressGif.setVisibility(View.GONE);
                            progressDialog.dismiss();
                            //hideProgressDialog();
                            //Toast.makeText(getApplicationContext(),""+response,Toast.LENGTH_SHORT).show();
                            Log.d("response", response.toString());
                            try {
                                String message=response.getString("message");
                                Log.d("message",message);
                                Intent i=new Intent(LoginActivity.this,OtpVerifyActivity.class);
                                i.putExtra("contactNo",contactStr);
                                startActivity(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    //hideProgressDialog();
                    Toast.makeText(getApplicationContext(),""+error,Toast.LENGTH_SHORT).show();
                    Log.d("errorrr", error.toString());

                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };

            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(jsonObjReq);
            AppController.getInstance().getRequestQueue().getCache().remove(login_url);
        }

        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.SEND_SMS);
        int receiveSMS = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS);
        int readSMS = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.RECEIVE_MMS);
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_SMS);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    protected void setLanguage(String language) {
        mylocale=new Locale(language);
        Resources resources=getResources();
        DisplayMetrics dm=resources.getDisplayMetrics();
        Configuration conf= resources.getConfiguration();
        conf.locale=mylocale;
        resources.updateConfiguration(conf,dm);
        Intent refreshIntent=new Intent(LoginActivity.this,LoginActivity.class);
        finish();
        startActivity(refreshIntent);
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
