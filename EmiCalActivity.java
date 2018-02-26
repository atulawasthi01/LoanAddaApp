package com.addventure.loanadda;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.addventure.loanadda.Utility.AppConfig;
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

public class EmiCalActivity extends BaseActivity {
    RelativeLayout emiresLayout;
    Toolbar toolbar;
    ImageView shareIv;
    TextView titleTv,emiresTv,ttlintrestresTv,ttlamuntresTv;
    EditText loanAmountEt,rolEt,tenureEt;
    Button emiBtn;
    public String loanamountStr,rolStr,tenureStr,emiStr,totalAmountPaidStr,totalInterestPaidStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getStatusbarcolor();
        setContentView(R.layout.activity_emi_cal);
        emiresLayout = (RelativeLayout) findViewById(R.id.emiresLayout);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        shareIv = (ImageView)findViewById(R.id.share);
        titleTv = (TextView)findViewById(R.id.title);
        emiresTv = (TextView)findViewById(R.id.emires);
        ttlintrestresTv = (TextView)findViewById(R.id.ttlintrestres);
        ttlamuntresTv = (TextView)findViewById(R.id.ttlamuntres);
        loanAmountEt = (EditText)findViewById(R.id.loanAmount);
        rolEt = (EditText)findViewById(R.id.rol);
        tenureEt = (EditText)findViewById(R.id.tenure);
        emiBtn = (Button) findViewById(R.id.emiBtn);

        titleTv.setText(getResources().getString(R.string.emicalttl));
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

        shareIv.setVisibility(View.VISIBLE);
        shareIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEmishare();
            }
        });

        emiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loanamountStr=loanAmountEt.getText().toString();
                rolStr=rolEt.getText().toString();
                tenureStr=tenureEt.getText().toString();

                if (loanamountStr.isEmpty()) {
                    loanAmountEt.setError(getResources().getString(R.string.loanamounterror));
                }
                else if (rolStr.isEmpty()) {
                    rolEt.setError(getResources().getString(R.string.rolerror));
                }
                else if (tenureStr.isEmpty()) {
                    tenureEt.setError(getResources().getString(R.string.tenureerror));
                }
                else {
                    getEmicalculator();
                }

            }
        });
    }

    private void getEmishare() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, "Loan Amount: ₹ "+totalAmountPaidStr+"\nEmi: ₹ "+emiStr+" \nLoan Outstanding Including Interest: ₹ "+totalInterestPaidStr
        +" \nhttps://www.loanadda.com/");
        startActivity(Intent.createChooser(share, "Share using"));
    }


    private void getEmicalculator() {

        //showProgressDialog();
        progressDialog.show();

        String emi_url = AppConfig.BaseUrl + "emi";

        JSONObject js = new JSONObject();

        try {

            js.put("loan_amount", loanamountStr);
            js.put("rate_of_interest", rolStr);
            js.put("tenure", tenureStr);
            Log.d("jsemi", js.toString());

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, emi_url, js,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            //hideProgressDialog();
                            //Toast.makeText(getApplicationContext(),""+response,Toast.LENGTH_SHORT).show();
                            Log.d("emiresponse", response.toString());

                            try {

                                emiStr=response.getString("emi");
                                totalAmountPaidStr=response.getString("totalAmountPaid");
                                totalInterestPaidStr=response.getString("totalInterestPaid");
                                emiresTv.setText("₹ "+emiStr);
                                ttlintrestresTv.setText("₹ "+totalAmountPaidStr);
                                ttlamuntresTv.setText("₹ "+totalInterestPaidStr);
                                emiresLayout.setVisibility(View.VISIBLE);



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                           /* try {
                                jsonObject.getJSONObject(String.valueOf(response));
                                String message=jsonObject.getString("message");
                                Log.d("message",message);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }*/

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
            AppController.getInstance().getRequestQueue().getCache().remove(emi_url);

         } catch (JSONException e) {
                e.printStackTrace();
            }
    }
}
