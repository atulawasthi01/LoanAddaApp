package com.addventure.loanadda;

import android.*;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.addventure.loanadda.Model.Perfios;
import com.addventure.loanadda.Utility.AppConfig;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ApproveLoanActivity extends BaseActivity {
    RelativeLayout webviewLayout,mainviewLayout;
    Toolbar toolbar;
    TextView titleTv,amountTv,pancardnoTv,panstatusTv,panfnameTv,panmnameTv,panlnameTv,aadharnoTv,
            aadharstatusTv,aadharnameTv,aadhardobTv,aadharaddressTv,aadharmobileTv;
    TextView cibilscoreTv;
    EditText otpEt;
    Spinner bankSp;
    Button submitBtn;
    WebView webView;
    String disbursal_amount;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 123;
    SharedPreferences plSp;
    public static final String plPrefrence = "plSp";
    String FirstName,MiddleName,LastName,panStatus;
    String contactStr;
    SharedPreferences loginSp;
    public static final String loginPrefrence = "loginSp";
    String aadharOtp;
    String[] bankArr;
    String[] bankValueArr;
    List<Perfios> perfiosList;
    String bankvalStr;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;
    public String htmlText;
    public File perfiosFile;
    DecimalFormat myFormatter;
    String path;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getStatusbarcolor();
        setContentView(R.layout.activity_loan_approve);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        titleTv = (TextView)findViewById(R.id.title);
        webviewLayout = (RelativeLayout)findViewById(R.id.webviewLayout);
        mainviewLayout = (RelativeLayout)findViewById(R.id.mainviewLayout);
        amountTv = (TextView)findViewById(R.id.amount);
        pancardnoTv = (TextView)findViewById(R.id.pancardno);
        panstatusTv = (TextView)findViewById(R.id.status);
        panfnameTv = (TextView)findViewById(R.id.fname);
        panmnameTv = (TextView)findViewById(R.id.mname);
        panlnameTv = (TextView)findViewById(R.id.lname);
        aadharnoTv = (TextView)findViewById(R.id.aadharno);
        aadharstatusTv = (TextView)findViewById(R.id.aadharstatus);
        aadharnameTv = (TextView)findViewById(R.id.name);
        aadhardobTv = (TextView)findViewById(R.id.dob);
        aadharmobileTv = (TextView)findViewById(R.id.mobile);
        aadharaddressTv = (TextView)findViewById(R.id.address);
        otpEt = (EditText)findViewById(R.id.otpEt);
        cibilscoreTv = (TextView)findViewById(R.id.cibilscore);
        bankSp = (Spinner)findViewById(R.id.bankSp);
        submitBtn = (Button)findViewById(R.id.submitBtn);
        webView = (WebView)findViewById(R.id.webView);
        bankArr = getResources().getStringArray(R.array.bankArr);
        bankValueArr = getResources().getStringArray(R.array.bankValueArr);
        permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);
        mainviewLayout.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);

        perfiosList = new ArrayList<Perfios>();
        for (int i=0;i<bankArr.length;i++) {
            Perfios perfios=new Perfios(bankArr[i],bankValueArr[i]);
            perfiosList.add(perfios);
        }

        try {
            ArrayAdapter<Perfios> adapter = new ArrayAdapter<Perfios>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, perfiosList);
            bankSp.setAdapter(adapter);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        bankSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Perfios perfios = (Perfios) adapterView.getSelectedItem();
                bankvalStr = perfios.getBankValue();
                Log.d("bankvalStr",bankvalStr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getMinistatement();
            }
        });

        //Log.d("FirstName",PersonalloanActivity.FirstName);

        /*callClickRl = (RelativeLayout)findViewById(R.id.callClick);
        docuploadClickRl = (RelativeLayout)findViewById(R.id.docuploadClick);*/
        titleTv.setText(getResources().getString(R.string.congratulation));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
               /* Intent intent=new Intent(ApproveLoanActivity.this,MainActivity.class);
                startActivity(intent);*/
                finish();
            }
        });


        loginSp = getSharedPreferences(loginPrefrence, MODE_PRIVATE);
        contactStr = loginSp.getString("contactNo", "");


        Log.d("disbursal_amount",AppController.disbersal_amount);
        //myFormatter = new DecimalFormat("#,##,####");
        myFormatter = new DecimalFormat("#,###,###,###");
        String disbersalStr=myFormatter.format(Integer.parseInt(AppController.disbersal_amount));

        amountTv.setText(" ₹ "+disbersalStr);

        aadharnoTv.setText(AppController.aadharcardStr);

        getPanVerify();

        /*callClickRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getcallClick();

            }
        });

        docuploadClickRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(ApproveLoanActivity.this,DocuploadActivity.class);
                startActivity(i);
            }
        });*/

        otpEt.addTextChangedListener(watcher);

        getCibilScore();
    }

    TextWatcher watcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            aadharOtp=otpEt.getText().toString();

            if (aadharOtp.length()==6) {
                //getAadharOtp();
                new getAdrOtp().execute();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    private class getAdrOtp extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*pDialog = new ProgressDialog(ApproveLoanActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();*/
            progressDialog.show();

        }


        @Override
        protected String doInBackground(Void... arg0) {

            String rresponse = null;
            String url_aadharotp=AppConfig.BaseUrl+"aadharverfiy";
            final String message;
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url_aadharotp);

            JSONObject json = new JSONObject();
            try {
                json.put("contact_no", contactStr);
                json.put("iifl_aadhar_number", AppController.aadharcardStr);
                json.put("iifl_aadhar_otp", aadharOtp);
                Log.d("jsosoonnaadharotp", json.toString());

            } catch (Exception ex) {

            }

            message=json.toString();

            try {
                httpPost.setEntity(new StringEntity(message, "UTF8"));
                httpPost.setHeader("Content-type", "application/json");
                HttpResponse resp = httpClient.execute(httpPost);
                HttpEntity entity = resp.getEntity();
                rresponse = EntityUtils.toString(entity);
                Log.d("AadharotpRes:", rresponse);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return rresponse;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            Log.d("result",result);
            //Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
            try {
                JSONObject jsonObject=new JSONObject(result);
                JSONObject jsonObjectBody=jsonObject.getJSONObject("body");
                String email=jsonObjectBody.getString("email");
                String firstname=jsonObjectBody.getString("firstname");
                String lastname=jsonObjectBody.getString("lastname");
                String middlename=jsonObjectBody.getString("middlename");
                String add3=jsonObjectBody.getString("add3");
                String dob=jsonObjectBody.getString("dob");
                String mobile=jsonObjectBody.getString("mobile");
                String pincode=jsonObjectBody.getString("pincode");
                String state=jsonObjectBody.getString("state");
                JSONObject jsonObject1=jsonObject.getJSONObject("head");
                String success_user_status=jsonObject1.getString("success_user_status");

                if (success_user_status.equals("0")) {
                    aadharstatusTv.setText("Verified");
                }
                else if (success_user_status.equals("1")) {
                    aadharstatusTv.setText("Not Verified");
                }

                Log.d("firstname",firstname);
                Log.d("middlename",middlename);
                Log.d("lastname",lastname);
                Log.d("success_user_status",success_user_status);

                aadharnameTv.setText(firstname+" "+middlename+" "+lastname);
                aadharaddressTv.setText(add3+" "+pincode+" "+state);
                aadhardobTv.setText(dob);
                aadharmobileTv.setText(mobile);
            }

            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void getCibilScore() {

        String login_url = AppConfig.BaseUrl + "cibilstart";

        JSONObject js = new JSONObject();

        try {

            js.put("contact_no", contactStr);
            Log.d("jsosoonncibil",js.toString());

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, login_url, js,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hideProgressDialog();
                            //Toast.makeText(getApplicationContext(),""+response,Toast.LENGTH_SHORT).show();
                            Log.d("cibilResponse", response.toString());

                            try {

                                String cibilScore=response.getString("cibil_score");
                                cibilscoreTv.setText(cibilScore);



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
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

            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(jsonObjReq);
            AppController.getInstance().getRequestQueue().getCache().remove(login_url);
        }

        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getMinistatement() {

        if (ActivityCompat.checkSelfPermission(ApproveLoanActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ApproveLoanActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(ApproveLoanActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(ApproveLoanActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(ApproveLoanActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(ApproveLoanActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,true);
            editor.commit();


        } else {

            getPerfios();
            //getWebviewUrl();
        }
    }



    public void getPerfios() {

        //showProgressDialog();
        progressDialog.show();

        String perfios_url= AppConfig.BaseUrl+"perfioscall";
        //String perfios_url="http://192.168.1.99/app/perfioscall";

        JSONObject js = new JSONObject();

        try {
            js.put("contact_no", contactStr);
            js.put("InstitutionID",bankvalStr);
            Log.d("jsonperfios",js.toString());

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, perfios_url, js,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //hideProgressDialog();
                            progressDialog.dismiss();
                            //Toast.makeText(getApplicationContext(),""+response,Toast.LENGTH_SHORT).show();
                            Log.d("perfiosres", response.toString());
                            try {
                                //String hhh=response.getString("body");
                                htmlText=response.getString("body");
                                Log.d("htmlText",htmlText);


                                path = Environment.getExternalStorageDirectory().getPath();
                                //fileName = DateFormat.format("dd_MM_yyyy_hh_mm_ss", System.currentTimeMillis()).toString();
                                fileName="perfios";
                                fileName = fileName + ".html";
                                File file = new File(path, fileName);
                                //String html = "<html><head><title>Title</title></head><body>This is random text.</body></html>";

                                try {
                                    FileOutputStream out = new FileOutputStream(file);
                                    byte[] data = htmlText.getBytes();
                                    out.write(data);
                                    out.close();
                                    Log.d("FileSave : ", ""+ file.getPath());
                                    String separator = "/";
                                    String sdcardPath = Environment.getExternalStorageDirectory().getPath() + separator+fileName;
                                    Log.d("sdcardPath",sdcardPath);
                                    perfiosFile = new File(Environment.getExternalStorageDirectory()
                                            .getAbsolutePath() + File.separator + fileName);
                                    Log.d("fileName",""+perfiosFile);

                                    mainviewLayout.setVisibility(View.GONE);
                                    webviewLayout.setVisibility(View.VISIBLE);
                                    toolbar.setVisibility(View.GONE);
                                    /*Intent i=new Intent(ApproveLoanActivity.this,PerfiosWebActivity.class);
                                    i.putExtra("perfiosFile",perfiosFile);
                                    startActivity(i);*/
                                    webView.getSettings().setJavaScriptEnabled(true);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                webView.setWebChromeClient(new WebChromeClient() {
                                    public void onProgressChanged(WebView view, int progress)
                                    {
                                        setTitle("Loading...");
                                        setProgress(progress * 100);

                                        if(progress == 100)
                                            setTitle(R.string.app_name);
                                    }
                                });

                                webView.setWebViewClient(new WebViewClient() {
                                    @Override
                                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
                                    {
                                        // Handle the error

                                        Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView view, String url)
                                    {
                                        view.loadUrl(url);
                                        return true;
                                    }
                                });

                                webView.loadUrl("file://" + perfiosFile);
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //hideProgressDialog();
                    progressDialog.dismiss();
                    Log.d("errorrr", error.toString());

                    if (error instanceof TimeoutError) {
                        Toast.makeText(getApplicationContext(),"Oops. Timeout error!",Toast.LENGTH_SHORT).show();
                    }
                    else if (error instanceof NoConnectionError) {
                        Toast.makeText(getApplicationContext(),"Oops. No Connection error!",Toast.LENGTH_SHORT).show();
                    }
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };


            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(jsonObjReq);
            AppController.getInstance().getRequestQueue().getCache().remove(perfios_url);
        }

        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getPanVerify() {

        progressDialog.show();

        String pan_url = AppConfig.BaseUrl + "panverfiy";

        JSONObject js = new JSONObject();

        try {

            js.put("contact_no", contactStr);
            js.put("pan_card",AppController.pencardStr);
            Log.d("jsosoonnpan",js.toString());

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, pan_url, js,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            //Toast.makeText(getApplicationContext(),""+response,Toast.LENGTH_SHORT).show();
                            Log.d("panverres", response.toString());
                            //Toast.makeText(getApplicationContext(),"Successfully Verified!",Toast.LENGTH_SHORT).show();
                            try {
                                JSONObject jsonObject=response.getJSONObject("body");
                                panStatus=jsonObject.getString("status");
                                FirstName=jsonObject.getString("FirstName");
                                MiddleName=jsonObject.getString("MiddleName");
                                LastName=jsonObject.getString("LastName");

                                pancardnoTv.setText(AppController.pencardStr);
                                panfnameTv.setText(FirstName);
                                panmnameTv.setText(MiddleName);
                                panlnameTv.setText(LastName);
                                if (panStatus.equals("Y")) {
                                    panstatusTv.setText("Verified");
                                }
                                else if (panStatus.equals("N")) {
                                    panstatusTv.setText("Not Verify");
                                }
                                if (FirstName.equals("") && LastName.equals("")) {
                                    panfnameTv.setText("ANSHUMAN");
                                    panlnameTv.setText("MISHRA");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    //Toast.makeText(getApplicationContext(),""+error,Toast.LENGTH_SHORT).show();
                    Log.d("errorrrpan", error.toString());
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };

            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(jsonObjReq);
            AppController.getInstance().getRequestQueue().getCache().remove(pan_url);
        }

        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                //selectImage();

                getPerfios();
                //getWebviewUrl();

            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(ApproveLoanActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(ApproveLoanActivity.this);
                    builder.setTitle("Need Storage Permission");
                    builder.setMessage("This app needs storage permission");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                            ActivityCompat.requestPermissions(ApproveLoanActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(getBaseContext(),"Unable to get Permission",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(ApproveLoanActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                //selectImage();
                getPerfios();
                //getWebviewUrl();
                //saveHtmlFile();
            }
        }
    }

    @Override
    public void onBackPressed() {
       /* super.onBackPressed();
        Intent intent=new Intent(ApproveLoanActivity.this,MainActivity.class);
        startActivity(intent);*/
        finish();
    }
}
