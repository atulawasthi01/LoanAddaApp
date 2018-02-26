package com.addventure.loanadda;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.StaleDataException;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.addventure.loanadda.Model.SMSData;
import com.addventure.loanadda.Utility.AppConfig;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OtpVerifyActivity extends BaseActivity {
    Toolbar toolbar;
    TextView titleTv;
    EditText otpEtone,otpEttwo,otpEtthree,otpEtfour;
    Button verifiedBtn;
    String otpStrone,otpStrtwo,otpStrthree,otpStrfour,otpStr,contactStr;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    SharedPreferences profileSp;
    public static final String profilePrefrence = "profileSp";
    public static String[] companyArr;
    BroadcastReceiver receiver;
    String numOtp;
    List<String> smsBodyList,smsAddressList,smsAmountList,smsDateList,mergeList;
    Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleBar();
        setContentView(R.layout.activity_otp_verify);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        titleTv = (TextView)findViewById(R.id.title);
        otpEtone = (EditText) findViewById(R.id.otpetone);
        /*otpEttwo = (EditText) findViewById(R.id.otpettwo);
        otpEtthree = (EditText) findViewById(R.id.otpetthree);
        otpEtfour = (EditText) findViewById(R.id.otpetfour);*/
        verifiedBtn = (Button) findViewById(R.id.verified);
        titleTv.setText(getResources().getString(R.string.verifyotpttl));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });

        otpEtone.getBackground().mutate().setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);
        /*otpEttwo.getBackground().mutate().setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);
        otpEtthree.getBackground().mutate().setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);
        otpEtfour.getBackground().mutate().setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);*/

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase("otp")) {
                    final String message = intent.getStringExtra("message");
                    Log.d("otpmsg",message);


                    numOtp = message.replaceAll("\\D+","");
                    Log.d("numOtp",numOtp);
                    otpEtone.setText(numOtp);

                    if (!numOtp.equals("")) {
                        getOtpVerify();
                    }
                }
            }
        };

        getcompanyList();


        verifiedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent i=new Intent(OtpVerifyActivity.this,MainActivity.class);
                startActivity(i);*/

                getOtpVerify();

                /*if (Network.isNetworkCheck(getApplicationContext())) {

                }
                else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.internet_check), Toast.LENGTH_LONG).show();
                }*/
            }
        });

        /*if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
        }*/
        otpEtone.addTextChangedListener(watcher);
       /* otpEttwo.addTextChangedListener(watcher);
        otpEtthree.addTextChangedListener(watcher);
        otpEtfour.addTextChangedListener(watcher);*/

        if (com.addventure.loanadda.Utility.Network.isNetworkCheck(getApplicationContext())) {
            getSmsfromPhone();
        }
        else {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.internet_check),Toast.LENGTH_SHORT).show();
        }
    }

    TextWatcher watcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           /* otpStrone=otpEtone.getText().toString();
            otpStrtwo=otpEttwo.getText().toString();
            otpStrthree=otpEtthree.getText().toString();
            otpStrfour=otpEtfour.getText().toString();
            if (otpStrone.length()==1) {
                otpEttwo.requestFocus();

            }
            if (otpStrtwo.length()==1) {
                otpEtthree.requestFocus();

            }
            if (otpStrthree.length()==1) {
                otpEtfour.requestFocus();

            }*/

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };



    private void getOtpVerify() {
     /* otpStrone=otpEtone.getText().toString().trim();
        otpStrtwo=otpEttwo.getText().toString().trim();
        otpStrthree=otpEtthree.getText().toString().trim();
        otpStrfour=otpEtfour.getText().toString().trim();*/
        numOtp=otpEtone.getText().toString().trim();
        //showProgressDialog();
        //otpStr=otpStrone+otpStrtwo+otpStrthree+otpStrfour;
        progressDialog.show();

        contactStr = getIntent().getStringExtra("contactNo");
        //String login_url = AppConfig.BaseUrl + "otpverifynewdone";
        String login_url="http://192.168.1.99/app/otpverifynewdone";

        JSONObject js = new JSONObject();
        try {
            js.put("contact_no", contactStr);
            js.put("lead_otp",numOtp);
            Log.d("jsosoonn",js.toString());

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, login_url, js,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            //hideProgressDialog();
                            Log.d("otpresponse", response.toString());
                            //Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(OtpVerifyActivity.this,MainActivity.class);
                            startActivity(i);
                            finish();
                            try {
                                String message=response.getString("message");
                                JSONObject object=response.getJSONObject("data");
                                String cusName=object.getString("customer_name");
                                String compName=object.getString("company_name");
                                String userStatus=object.getString("user_status");
                                String loanType=object.getString("loan_type");
                                String loanAmount=object.getString("loan_amount");
                                //String dobStr=object.getString("lead_date_of_birth");
                                String loanStatus=object.getString("loan_status");
                                String rmName=object.getString("rm_name");
                                String rmContact=object.getString("rm_contact");
                                String cityStr=object.getString("city");

                                profileSp = getApplicationContext().getSharedPreferences(profilePrefrence, MODE_PRIVATE);
                                SharedPreferences.Editor editor = profileSp.edit();
                                editor.putString("cusName",cusName);
                                editor.putString("compName",compName);
                                editor.putString("userStatus",userStatus);
                                editor.putString("loanType",loanType);
                                editor.putString("loanAmount",loanAmount);
                                //editor.putString("dobStr",dobStr);
                                editor.putString("loanStatus",loanStatus);
                                editor.putString("rmName",rmName);
                                editor.putString("rmContact",rmContact);
                                editor.putString("cityStr",cityStr);
                                editor.commit();
                                editor.apply();

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
                   /* VolleyLog.d("errorrr", "Error: " + error.getMessage());
                    Log.d("errorrr", error.getMessage());*/

                }
            }) {

                /**
                 * Passing some request headers
                 */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };

            AppController.getInstance().addToRequestQueue(jsonObjReq);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getcompanyList() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.BaseUrl + "fetchcompanylist",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("companyRes",response);
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            companyArr = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String companyName = jsonObject.getString("name");
                                companyArr[i] = companyName;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("errorcompany", error.toString());
                    }
                }) {
           /* @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<String, String>();
                params.put(Utility.KEY_MOBILENUMBER,mobileNumber);
                //  params.put(Utility.KEY_OTP, otp);
                return params;
            }*/
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void getSmsfromPhone() {

        try {


            Uri uri = Uri.parse("content://sms/inbox");
            c = getContentResolver().query(uri, null, null, null, null);
            startManagingCursor(c);
            smsBodyList = new ArrayList<String>();
            smsAddressList = new ArrayList<String>();
            smsAmountList = new ArrayList<String>();
            smsDateList = new ArrayList<String>();

            if (c.moveToFirst()) {

                for (int i = 0; i < c.getCount(); i++) {

                    Pattern regEx = Pattern.compile("(?i)(?:(?:RS|INR|MRP)\\.?\\s?)(\\d+(:?\\,\\d+)?(\\,\\d+)?(\\.\\d{1,2})?)");
                    Matcher m = regEx.matcher(c.getString(c.getColumnIndexOrThrow("body")).toString());
                    SMSData sms = new SMSData();
                    sms.setBody(c.getString(c.getColumnIndexOrThrow("body")).toString());
                    sms.setNumber(c.getString(c.getColumnIndexOrThrow("address")).toString());
                    sms.setDate(c.getString(c.getColumnIndexOrThrow("date")).toString());

                    {
                        if (m.find()) {

                            if (sms.getBody().contains("debited") || sms.getBody().contains("credited")) {

                                sms.setBody(c.getString(c.getColumnIndexOrThrow("body")).toString());
                                sms.setNumber(c.getString(c.getColumnIndexOrThrow("address")).toString());
                                sms.setDate(c.getString(c.getColumnIndexOrThrow("date")).toString());
                            /*Log.d("smsbody", sms.getBody());
                            Log.d("smsnumber",sms.getNumber());
                            Log.d("smsdate",sms.getDate());*/

                                smsBodyList.add(sms.getBody());
                                smsAddressList.add(sms.getNumber());
                                smsDateList.add(sms.getDate());

                            /*Log.d("smsBodyList",smsBodyList.toString());
                            Log.d("smsAddressList",smsAddressList.toString());
                            Log.d("smsDateList",smsDateList.toString());*/

                                String amount = (m.group(0).replaceAll("inrr", ""));
                            /*amount = amount.replaceAll("rs", "");
                            amount = amount.replaceAll("inr", "");*/
                                amount = amount.replaceAll(" ", "");
                                amount = amount.replaceAll(",", "");
                                //Log.d("amountStr",amount);

                                smsAmountList.add(amount);
                                //Log.d("smsAmountList",smsAmountList.toString());

                                // Marge all three arraylist

                                //List<List<String>> mergedList = Arrays.asList(smsAddressList, smsDateList, smsAmountList);


                                mergeList = concatList(smsDateList, smsAmountList);

                                //Log.d("mergeList",mergeList.toString());




                           /* String bodyList = smsBodyList.toString();
                            smsBodyStr = bodyList.substring(1, bodyList.length() - 1).replace("[", "").replace("]", "").replace(", ", ",");

                            String addressList = smsAddressList.toString();
                            smsAddressStr = addressList.substring(1, addressList.length() - 1).replace("[", "").replace("]", "").replace(", ", ",");

                            String amountList = smsAmountList.toString();
                            smsAmountStr = amountList.substring(1, amountList.length() - 1).replace("[", "").replace("]", "").replace(", ", ",");

                            String dateList = smsDateList.toString();
                            smsDateStr = dateList.substring(1, dateList.length() - 1).replace("[", "").replace("]", "").replace(", ", ",");


                            Log.d("smsBodyStr",smsBodyStr);
                            Log.d("smsAddressStr",smsAddressStr);
                            Log.d("smsAmountStr",smsAmountStr);
                            Log.d("smsDateStr",smsDateStr);*/

                            }
                        }
                    }

                    c.moveToNext();
                }
            }

            c.close();
        }

        catch (StaleDataException e) {
            e.printStackTrace();
        }

        //String sms_url = AppConfig.BaseUrl + "gettransactionsms";
        String sms_url="http://192.168.1.99/app/gettransactionsms";
        JSONObject js = new JSONObject();

        try {

            js.put("contact_no", contactStr);
            js.put("smsdata",mergeList.toString());
            Log.d("jsonsmsreq",js.toString());

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, sms_url, js,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Toast.makeText(getApplicationContext(),""+response,Toast.LENGTH_SHORT).show();
                            Log.d("responsesms", response.toString());

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(getApplicationContext(),""+error,Toast.LENGTH_SHORT).show();
                    Log.d("errorrrsms", error.toString());

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
            AppController.getInstance().getRequestQueue().getCache().remove(sms_url);
        }

        catch (JSONException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public List<String> concatList(final List<String> list1, final List<String> list2) {

        final Iterator<String> i1 = list1.iterator();
        final Iterator<String> i2 = list2.iterator();

        final List<String> combined = new ArrayList<>();
        while (i1.hasNext() && i2.hasNext()) {
            combined.add(i1.next() + "  "+ i2.next());
        }

        return combined;
    }



    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (c != null) {
            c.close();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (c != null) {
            c.close();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public static int extractNumberFromAnyAlphaNumeric(String alphaNumeric) {
        alphaNumeric = alphaNumeric.length() > 0 ? alphaNumeric.replaceAll("\\D+", "") : "";
        int num = alphaNumeric.length() > 0 ? Integer.parseInt(alphaNumeric) : 0; // or -1
        return num;
    }
}
