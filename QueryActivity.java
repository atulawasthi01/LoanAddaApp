package com.addventure.loanadda;

import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.addventure.loanadda.Utility.AppConfig;
import com.addventure.loanadda.Utility.Network;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class QueryActivity extends BaseActivity {
    Toolbar toolbar;
    TextView titleTv;
    EditText nameEt,emailEt,phoneEt,messageEt;
    Spinner loantypeSp;
    Button submitBtn;
    String nameStr,emailStr,phoneStr,messageStr,loantypeStr;
    String[] loantypeArr=new String[] {"Home Loan","Personal Loan"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getStatusbarcolor();
        setContentView(R.layout.activity_query);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        titleTv = (TextView) findViewById(R.id.title);
        emailEt = (EditText) findViewById(R.id.email);
        nameEt = (EditText) findViewById(R.id.name);
        phoneEt = (EditText) findViewById(R.id.phone);
        messageEt = (EditText) findViewById(R.id.message);
        loantypeSp = (Spinner) findViewById(R.id.loantype);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        setSupportActionBar(toolbar);
        titleTv.setText(getResources().getString(R.string.query));
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

        nameEt.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        emailEt.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        phoneEt.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,loantypeArr);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        loantypeSp.setAdapter(aa);

        loantypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loantypeStr = loantypeSp.getItemAtPosition(i).toString();
                Log.d("loantypeStr",loantypeStr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nameStr = nameEt.getText().toString();
                emailStr = emailEt.getText().toString();
                phoneStr = phoneEt.getText().toString();
                messageStr = messageEt.getText().toString();

                if (nameStr.equals("")) {
                    nameEt.setError(getResources().getString(R.string.nameerror));
                } else if (emailStr.equals("")) {
                    emailEt.setError(getResources().getString(R.string.emailerror));
                } else if (phoneStr.equals("")) {
                    phoneEt.setError(getResources().getString(R.string.phoneerror));
                } else if (messageStr.equals("")) {
                    messageEt.setError(getResources().getString(R.string.messageerror));
                } else {
                    if (Network.isNetworkCheck(getApplicationContext())) {
                        postQuery();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.internet_check), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void postQuery() {

        progressDialog.show();

        //String emi_url = AppConfig.BaseUrl + "query";
        String emi_url="http://192.168.1.99/app/query";

        JSONObject js = new JSONObject();

        try {
            js.put("name", nameStr);
            js.put("email", emailStr);
            js.put("phone", phoneStr);
            js.put("loantype",loantypeStr);
            js.put("message",messageStr);

            Log.d("jsquery", js.toString());

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, emi_url, js,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            //hideProgressDialog();
                            Toast.makeText(getApplicationContext(),"Query has been Submitted.",Toast.LENGTH_SHORT).show();
                            Log.d("queryresponse", response.toString());

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //hideProgressDialog();
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),""+error,Toast.LENGTH_SHORT).show();
                    VolleyLog.d("errorrr", error.getMessage());
                    //Log.d("errorrr", error.getMessage());

                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };

            AppController.getInstance().addToRequestQueue(jsonObjReq);
            AppController.getInstance().getRequestQueue().getCache().remove(emi_url);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
