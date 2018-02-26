package com.addventure.loanadda;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.addventure.loanadda.Model.Bank;
import com.addventure.loanadda.Model.Branch;
import com.addventure.loanadda.Model.City;
import com.addventure.loanadda.Model.State;
import com.addventure.loanadda.Utility.AppConfig;
import com.addventure.loanadda.Utility.Network;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IfscActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    Toolbar toolbar;
    TextView titleTv,addressTv,ifsccodeTv;
    Spinner bankautoSp,stateautoSp,cityautoSp,branchautoSp;
    Button ifscBtn;
    List<Bank> ifscbankList;
    List<State> ifscstateList;
    List<City> ifsccityList;
    List<Branch> ifscbranchList;
    int i;
    String selectbankStr,selectstateStr,selectcityStr,selectbranchStr;
    String ifscdbId,ifscBankname,ifscCode,micrCode,ifscBranch,ifscAddress,contactStr,cityStr,districtStr,stateStr;
    String ifsc_url = AppConfig.BaseUrl + "fetchbl";
    boolean isBank=false;
    boolean isState=false;
    boolean isCity=false;
    boolean isBranch=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getStatusbarcolor();
        setContentView(R.layout.activity_ifsc);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        titleTv = (TextView)findViewById(R.id.title);
        addressTv = (TextView)findViewById(R.id.addresstxt);
        ifsccodeTv = (TextView)findViewById(R.id.ifsccode);
        bankautoSp = (Spinner)findViewById(R.id.bankauto);
        stateautoSp = (Spinner)findViewById(R.id.stateauto);
        cityautoSp = (Spinner)findViewById(R.id.cityauto);
        branchautoSp = (Spinner)findViewById(R.id.branchauto);
        ifscBtn = (Button)findViewById(R.id.ifscBtn);


        titleTv.setText(getResources().getString(R.string.ifsccodettl));
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

        ifscbankList=new ArrayList<Bank>();
        ifscstateList=new ArrayList<State>();
        ifsccityList=new ArrayList<City>();
        ifscbranchList=new ArrayList<Branch>();

        bankautoSp.setOnItemSelectedListener(this);
        stateautoSp.setOnItemSelectedListener(this);
        cityautoSp.setOnItemSelectedListener(this);
        branchautoSp.setOnItemSelectedListener(this);

        if (Network.isNetworkCheck(getApplicationContext())) {

            getBankList(ifsc_url);
        }

        else {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.internet_check),Toast.LENGTH_SHORT).show();
        }
    }


    private void getBankList(String ifsc_url) {

        showProgressDialog();

        JSONObject js = new JSONObject();
        try {
            js.put("action", "list");
            Log.d("jsfetchbl", js.toString());

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, ifsc_url, js,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hideProgressDialog();
                            Log.d("ifscresponse", response.toString());

                            try {
                                JSONArray jsonArray=response.getJSONArray("banks");
                                for (int i=0;i<jsonArray.length();i++) {
                                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                                    String bankName=jsonObject.getString("bank");
                                    Bank bank=new Bank();
                                    bank.setBankName(bankName);
                                    ifscbankList.add(bank);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            /*ArrayAdapter dataAdapter = new ArrayAdapter(IfscActivity.this, android.R.layout.simple_spinner_item, ifscbankList);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            bankautoSp.setAdapter(dataAdapter);*/

                            ArrayAdapter<Bank> adapter = new ArrayAdapter<Bank>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, ifscbankList);
                            bankautoSp.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
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
            AppController.getInstance().getRequestQueue().getCache().remove(ifsc_url);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getstateList(String ifsc_url) {

        showProgressDialog();

        JSONObject js = new JSONObject();
        try {
            js.put("action", "stateByBank");
            js.put("bank", selectbankStr);
            Log.d("jsfetchblstate", js.toString());

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, ifsc_url, js,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hideProgressDialog();
                            //Toast.makeText(getApplicationContext(),""+response,Toast.LENGTH_SHORT).show();
                            Log.d("stateresponse", response.toString());

                            try {
                                JSONArray jsonArray=response.getJSONArray("states");
                                for (int i=0;i<jsonArray.length();i++) {
                                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                                    String stateName=jsonObject.getString("state");
                                    State state=new State();
                                    state.setStateName(stateName);
                                    ifscstateList.add(state);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            /*ArrayAdapter dataAdapter = new ArrayAdapter(IfscActivity.this, android.R.layout.simple_spinner_item, ifscbankList);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            bankautoSp.setAdapter(dataAdapter);*/

                            ArrayAdapter<State> adapter = new ArrayAdapter<State>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, ifscstateList);
                            stateautoSp.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
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

            if (isBank==true) {
                AppController.getInstance().getRequestQueue().getCache().remove(ifsc_url);
                isBank=false;

            }
            else {
                Toast.makeText(getApplicationContext(),"State Null",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getcityList(String ifsc_url, String selectstateStr, String selectbankStr) {

        showProgressDialog();

        JSONObject js = new JSONObject();
        Log.d("selectbankStr",selectbankStr);
        Log.d("selectstateStr",selectstateStr);
        try {
            js.put("action", "cityByBankState");
            js.put("bank", selectbankStr);
            js.put("state", selectstateStr);
            Log.d("jscity", js.toString());

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, ifsc_url, js,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hideProgressDialog();
                            Log.d("cityresponse", response.toString());

                            try {
                                JSONArray jsonArray=response.getJSONArray("city");
                                for (int i=0;i<jsonArray.length();i++) {
                                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                                    String cityName=jsonObject.getString("city");
                                    City city=new City();
                                    city.setCityName(cityName);
                                    ifsccityList.add(city);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            ArrayAdapter<City> adapter = new ArrayAdapter<City>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, ifsccityList);
                            cityautoSp.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
                    Toast.makeText(getApplicationContext(),""+error,Toast.LENGTH_SHORT).show();
                    Log.d("errorrr", error.getMessage());
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

            if (isState==true) {
                AppController.getInstance().getRequestQueue().getCache().remove(ifsc_url);
                isState=false;

            }
            else {
                Toast.makeText(getApplicationContext(),"City Null",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getBranchList(String ifsc_url, String selectcityStr, String selectstateStr, String selectbankStr) {

        showProgressDialog();

        JSONObject js = new JSONObject();
        try {
            js.put("action", "branchByBankStateCity");
            js.put("bank", selectbankStr);
            js.put("state", selectstateStr);
            js.put("city",selectcityStr);
            Log.d("jsfetchblbranch", js.toString());

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, ifsc_url, js,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hideProgressDialog();
                            Log.d("branchresponse", response.toString());

                            try {
                                JSONArray jsonArray=response.getJSONArray("branch");
                                for (int i=0;i<jsonArray.length();i++) {
                                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                                    String branchName=jsonObject.getString("branch");
                                    Branch branch=new Branch();
                                    branch.setBranchName(branchName);
                                    ifscbranchList.add(branch);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            ArrayAdapter<Branch> adapter = new ArrayAdapter<Branch>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, ifscbranchList);
                            branchautoSp.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
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
            if (isCity==true) {
                AppController.getInstance().getRequestQueue().getCache().remove(ifsc_url);
                isCity=false;

            }
            else {
                Toast.makeText(getApplicationContext(),"City Null",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getIfscCode(String ifsc_url, String selectcityStr, String selectstateStr, String selectbankStr, String selectbranchStr) {

        showProgressDialog();

        JSONObject js = new JSONObject();
        try {
            js.put("action", "fetchIFSC");
            js.put("bank", selectbankStr);
            js.put("state", selectstateStr);
            js.put("city",selectcityStr);
            js.put("branch",selectbranchStr);
            Log.d("jsfetchbl", js.toString());

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, ifsc_url, js,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hideProgressDialog();
                            //Toast.makeText(getApplicationContext(),""+response,Toast.LENGTH_SHORT).show();
                            Log.d("ifscresponse", response.toString());

                            try {
                                JSONArray jsonArray=response.getJSONArray("branchL");
                                for (int i=0;i<jsonArray.length();i++) {
                                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                                    ifscdbId=jsonObject.getString("ifscdb_id");
                                    ifscBankname=jsonObject.getString("bank");
                                    ifscCode=jsonObject.getString("ifsc");
                                    micrCode=jsonObject.getString("micr_code");
                                    ifscBranch=jsonObject.getString("branch");
                                    ifscAddress=jsonObject.getString("address");
                                    contactStr=jsonObject.getString("contact");
                                    cityStr=jsonObject.getString("city");
                                    districtStr=jsonObject.getString("district");
                                    stateStr=jsonObject.getString("state");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            ArrayAdapter<Branch> adapter = new ArrayAdapter<Branch>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, ifscbranchList);
                            branchautoSp.setAdapter(adapter);

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
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
            if (isBranch==true) {
                AppController.getInstance().getRequestQueue().getCache().remove(ifsc_url);
                isBranch=false;
            }
            else {
                Toast.makeText(getApplicationContext(),"Branch Null",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.bankauto:
                isBank=true;
                Bank bank = (Bank) adapterView.getSelectedItem();
                selectbankStr=bank.getBankName();
                ifscstateList.clear();
                if (Network.isNetworkCheck(getApplicationContext())) {
                    getstateList(ifsc_url);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.internet_check), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.stateauto:
                isState=true;
                State state = (State) adapterView.getSelectedItem();
                selectstateStr=state.getStateName();
                ifsccityList.clear();
                if (Network.isNetworkCheck(getApplicationContext())) {
                    getcityList(ifsc_url,selectstateStr,selectbankStr);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.internet_check), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.cityauto:
                isCity=true;
                City city = (City) adapterView.getSelectedItem();
                selectcityStr=city.getCityName();
                ifscbranchList.clear();
                if (Network.isNetworkCheck(getApplicationContext())) {
                    getBranchList(ifsc_url,selectcityStr,selectstateStr,selectbankStr);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.internet_check), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.branchauto:
                isBranch=true;
                //Toast.makeText(getApplicationContext(),""+i,Toast.LENGTH_SHORT).show();
                try {
                    Branch branch = (Branch) adapterView.getSelectedItem();
                    selectbranchStr=branch.getBranchName();
                    if (Network.isNetworkCheck(getApplicationContext())) {
                        getIfscCode(ifsc_url,selectcityStr,selectstateStr,selectbankStr,selectbranchStr);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.internet_check), Toast.LENGTH_LONG).show();
                    }
                }
                catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(),"Null Value Occer", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }


                ifscBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        addressTv.setText(ifscAddress);
                        ifsccodeTv.setText(ifscCode);

                    }
                });

                break;
        }
    }



    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}