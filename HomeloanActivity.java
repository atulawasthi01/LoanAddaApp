package com.addventure.loanadda;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.addventure.loanadda.Model.Loan;
import com.addventure.loanadda.Utility.AppConfig;
import com.addventure.loanadda.Utility.Network;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.droidsonroids.gif.GifTextView;


public class HomeloanActivity extends BaseActivity  implements
        View.OnClickListener
        ,SeekBar.OnSeekBarChangeListener {

private String TAG = "PersonalloanActivity";
        Toolbar toolbar;
        RelativeLayout callClickRl,docuploadClickRl,workexpLayout,residentLayout;
        TextView titleTv, incomeTv, emiTv, workexpTv, loanreqTv;
        AutoCompleteTextView companyTextView,cityAutoTextView;
        SeekBar incomeSeekBar, emiSeekBar, workexpSeekBar, loanreqSeekBar;
        EditText fullnameEt, pancardEt, aadharcardEt, residentEt, officeEt, dateofbirthTv;
        TextView residenttypeTv;
        Button offersBtn;
private DatePickerDialog dobDatePickerDialog;
private SimpleDateFormat dateFormatter;
        Calendar startDt;
        String datefromStr="", residentspStr="", cityspStr="", fullnameStr="", pencardStr="",  residentaddressStr="", officeaddressStr="",
        dateofbirthStr="", incomeStr="0", emiStr="0", workexpStr="0", loanreqStr="0", companyspStr="";
        public static String aadharcardStr="";
        int yearDob, monthDob, dayDob;
        Date dateDob;
        String[] cityArr, residentArr;
        String contactStr="APOLLO SINDOORI HOTELS LIMITED";
        SharedPreferences loginSp;
public static final String loginPrefrence = "loginSp";
        List<Loan> companyList,cityList;
        String companyResponse;

        String statusStr;
public String disbursal_amount;
public boolean isResidentialtype=false;
        int i,resPos;
        String cityposStr,resposStr;
final CharSequence[] restypeArrrestypeArr = {"Own-Home","Rented-Home"};
private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 123;
        DecimalFormat myFormatter;
        String incomeSt,emiSt,loanreqSt;
    public static String FirstName,MiddleName,LastName,panStatus;
    SharedPreferences plSp;
    public static final String plPrefrence = "plSp";
    SharedPreferences locationSp;
    public static final String locationPrefrence = "location";
    String addressStr;
    int timestamp=1802729536;


@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getStatusbarcolor();
        setContentView(R.layout.activity_personalloan);
        findviewbyId();
        titleTv.setText(getResources().getString(R.string.homeloanttl));
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
       /* initVolleyCallback();
        mVolleyService = new VolleyService(mResultCallback,this);
        mVolleyService.getDataVolley("GETCALL",AppConfig.BaseUrl+"fetchcompanylist");*/
        Log.d("incomeStr",incomeStr);

        dateofbirthTv.setInputType(InputType.TYPE_NULL);

        fullnameEt.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        pancardEt.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        aadharcardEt.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        residentEt.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        officeEt.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);

        /*fullnameStr = fullnameEt.getText().toString();
        pencardStr = pancardEt.getText().toString();
        aadharcardStr = aadharcardEt.getText().toString();
        residentaddressStr = residentEt.getText().toString();
        officeaddressStr = officeEt.getText().toString();
        Log.d("fullnameStr",fullnameStr);
        Log.d("pencardStr",pencardStr);
        Log.d("aadharcardStr",aadharcardStr);
        Log.d("residentaddressStr",residentaddressStr);
        Log.d("officeaddressStr",officeaddressStr);*/


        loginSp = getSharedPreferences(loginPrefrence, MODE_PRIVATE);
        contactStr = loginSp.getString("contactNo", "");

    locationSp = getSharedPreferences(locationPrefrence,MODE_PRIVATE);
    addressStr=locationSp.getString("address","");
    cityspStr=locationSp.getString("city","");

    residentEt.setText(addressStr);
    cityAutoTextView.setText(cityspStr);




        myFormatter = new DecimalFormat("#,##,###");

        dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        setDateTimeField();

        dateofbirthTv.setOnClickListener(this);
        offersBtn.setOnClickListener(this);
        incomeSeekBar.setOnSeekBarChangeListener(this);
        emiSeekBar.setOnSeekBarChangeListener(this);
        workexpSeekBar.setOnSeekBarChangeListener(this);
        loanreqSeekBar.setOnSeekBarChangeListener(this);
        residenttypeTv.setOnClickListener(this);
        callClickRl.setOnClickListener(this);
        docuploadClickRl.setOnClickListener(this);
        /*companyTextView.setOnItemClickListener(this);
        cityAutoTextView.setOnItemClickListener(this);*/

    incomeSeekBar.setMax(200000);
    incomeSeekBar.setProgress(0);
    emiSeekBar.setMax(100000);
    emiSeekBar.setProgress(0);
    workexpSeekBar.setMax(20);
    workexpSeekBar.setProgress(0);
    loanreqSeekBar.setMax(5000000);
    loanreqSeekBar.setProgress(0);

        cityArr = getResources().getStringArray(R.array.cityArray);
        residentArr = getResources().getStringArray(R.array.residentArr);

        companyList = new ArrayList<Loan>();
        cityList = new ArrayList<Loan>();

        if (Network.isNetworkCheck(getApplicationContext())) {
        getcityList();
        }
        else {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.internet_check), Toast.LENGTH_SHORT).show();

        }

        try
        {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(HomeloanActivity.this, R.layout.company_layout, OtpVerifyActivity.companyArr);
                companyTextView.setThreshold(1);//will start working from first character
                companyTextView.setAdapter(adapter);
        }
        catch (NullPointerException e) {
                e.printStackTrace();
        }

    companyTextView.setVisibility(View.GONE);
    workexpLayout.setVisibility(View.GONE);
    residentLayout.setVisibility(View.GONE);
    officeEt.setVisibility(View.GONE);





        //new CompanyListTask().execute();
        //getcompanyList();



            /*final ProgressDialog dialog = ProgressDialog.show(PersonalloanActivity.this, "Please wait", "Doing long task...", true);
            dialog.setCancelable(true);
            //dialog = CustomProgressDialog.show(this, "", "");
            new Thread() {
                @Override
                public void run() {
                    try{
                        getcompanyList();
                        //TODO Write here your method logic
                        sleep(5000);
                    } catch (Exception e) {
                        Log.i("your_app_tag", e.toString());
                        dialog.dismiss();
                    }
                    //Dismiss dialog, and notify handler to done this task
                    dialog.dismiss();
                    //longTaskHandler.sendEmptyMessage(0);
                }
            }.start();*/

    }


    public class getAdr extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /*pDialog = new ProgressDialog(HomeloanActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();*/

            progressDialog.show();

        }


        @Override
        protected String doInBackground(Void... arg0) {

            String rresponse = null;
            final String message;
            String url_aadhar=AppConfig.BaseUrl+"aadharverfiy";
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url_aadhar);

            JSONObject json = new JSONObject();
            try {
                json.put("contact_no", contactStr);
                json.put("iifl_aadhar_number", AppController.aadharcardStr);
                json.put("iifl_aadhar_otp", "");
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
                Log.d("AdharResponse:", rresponse);

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
            Intent intent = new Intent(HomeloanActivity.this, ApproveLoanActivity.class);
            startActivity(intent);

        }
    }

    public void findviewbyId() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleTv = (TextView) findViewById(R.id.title);
        incomeTv = (TextView) findViewById(R.id.income);
        emiTv = (TextView) findViewById(R.id.emi);
        workexpTv = (TextView) findViewById(R.id.workexp);
        loanreqTv = (TextView) findViewById(R.id.loanreq);
        dateofbirthTv = (EditText) findViewById(R.id.dateofbirth);
        incomeSeekBar = (SeekBar) findViewById(R.id.incomeseekbar);
        emiSeekBar = (SeekBar) findViewById(R.id.emiseekbar);
        workexpSeekBar = (SeekBar) findViewById(R.id.workexpseekbar);
        loanreqSeekBar = (SeekBar) findViewById(R.id.loanreqseekbar);
        fullnameEt = (EditText) findViewById(R.id.fullname);
        companyTextView = (AutoCompleteTextView) findViewById(R.id.companySpinner);
        //companySpinner = (Spinner) findViewById(R.id.companySpinner);
        pancardEt = (EditText) findViewById(R.id.pancard);
        aadharcardEt = (EditText) findViewById(R.id.aadhar);
        residentEt = (EditText) findViewById(R.id.residentEt);
        officeEt = (EditText) findViewById(R.id.officeEt);
        residenttypeTv = (TextView) findViewById(R.id.residentSpinner);
        cityAutoTextView = (AutoCompleteTextView) findViewById(R.id.citySpinner);
        offersBtn = (Button) findViewById(R.id.offersBtn);
        callClickRl = (RelativeLayout) findViewById(R.id.callClick);
        docuploadClickRl = (RelativeLayout) findViewById(R.id.docuploadClick);
        workexpLayout = (RelativeLayout) findViewById(R.id.workexpLayout);
        residentLayout = (RelativeLayout) findViewById(R.id.residentLayout);

    }

public void setDateTimeField() {


        dateofbirthTv.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        newCalendar.add(Calendar.YEAR, -30);
        dobDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        startDt = Calendar.getInstance();
        startDt.set(year, monthOfYear, dayOfMonth);

        dateofbirthStr = (dateFormatter.format(startDt.getTime()));
        Log.d("datefromStr", "" + datefromStr);
        yearDob = year;
        monthDob = monthOfYear;
        dayDob = dayOfMonth;
        Log.d("yearDob", "" + yearDob);
        Log.d("monthDob", "" + monthDob);
        dateofbirthTv.setText(dateofbirthStr);
        Log.d("dayDob", "" + dayDob);
        try {
        dateDob = dateFormatter.parse(dateofbirthStr);
            timestamp= (int) dateDob.getTime();
            Log.d("timestamp",""+timestamp);
        Log.d("dateDob", "" + dateDob);
        } catch (ParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        }
        }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        }


        @Override
public void onClick(View view) {
        switch (view.getId()) {

       case R.id.dateofbirth:
        dobDatePickerDialog.show();
        break;

        case R.id.offersBtn:

        fullnameStr = fullnameEt.getText().toString();
        AppController.pencardStr = pancardEt.getText().toString();
        AppController.aadharcardStr = aadharcardEt.getText().toString();
        residentaddressStr = residentEt.getText().toString();
        officeaddressStr = officeEt.getText().toString();
        companyspStr = companyTextView.getText().toString();
        //cityspStr = cityAutoTextView.getText().toString();
        Log.d("fullnameStr",fullnameStr);
        Log.d("pencardStr",AppController.pencardStr);
        Log.d("aadharcardStr",AppController.aadharcardStr);
        Log.d("residentaddressStr",residentaddressStr);
        Log.d("officeaddressStr",officeaddressStr);
        Log.d("dateofbirthStr",dateofbirthStr);
        Log.d("companyspStr",companyspStr);
        //Log.d("cityspStr",cityspStr);


               if (fullnameStr.isEmpty()) {
        fullnameEt.setError(getResources().getString(R.string.fullnameerror));
        }
        else if (dateofbirthStr.isEmpty()) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.dateofbirtherror), Toast.LENGTH_SHORT).show();
        }
        else if (AppController.pencardStr.isEmpty()) {
        pancardEt.setError(getResources().getString(R.string.pencarderror));
        }
        else if (AppController.aadharcardStr.isEmpty()) {
        aadharcardEt.setError(getResources().getString(R.string.aadharcarderror));
        }
        else if (incomeStr.equals("0")) {
        Toast.makeText(getApplicationContext(),getResources().getString(R.string.monthlyincomeerror),Toast.LENGTH_SHORT).show();
        }
       /* else if (emiStr.equals("0")) {
        Toast.makeText(getApplicationContext(),getResources().getString(R.string.emierror),Toast.LENGTH_SHORT).show();
        }*/

        else if (loanreqStr.equals("0")) {
        Toast.makeText(getApplicationContext(),getResources().getString(R.string.loanreqerror),Toast.LENGTH_SHORT).show();
        }
        else if (cityspStr.isEmpty()) {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.cityerror),Toast.LENGTH_SHORT).show();
        }
        else if (residentaddressStr.isEmpty()) {
        residentEt.setError(getResources().getString(R.string.residanterror));
        }
        else {

                if (Network.isNetworkCheck(getApplicationContext())) {

                getHomeLoan();
        }
        else
            {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.internet_check), Toast.LENGTH_SHORT).show();
        }

        }

        break;
        case R.id.residentSpinner:
        isResidentialtype=true;
        getResidentialtypeList();
        break;
        case R.id.callClick:
        getcallClick();
        break;
        case R.id.docuploadClick:
        Intent intent=new Intent(HomeloanActivity.this,DocuploadActivity.class);
        startActivity(intent);
        break;
        }
}




private void getResidentialtypeList() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Residential Type");
        builder.setItems(restypeArrrestypeArr, new DialogInterface.OnClickListener() {
public void onClick(DialogInterface dialog, int item) {
        // Do something with the selection
        resPos=item;
        resposStr= (String) restypeArrrestypeArr[item];
        residenttypeTv.setText(restypeArrrestypeArr[item]);
        }
        });
        AlertDialog alert = builder.create();
        alert.show();
        }

     public void getHomeLoan() {

        //showProgressDialog();
         progressDialog.show();

        String homeloan_url = AppConfig.BaseUrl + "quickdisbursalnew";
        //String homeloan_url =  "https://www.loanadda.com/app/quickdisbursalnew";
        Log.d("homeloan_url",homeloan_url);

        JSONObject js = new JSONObject();
        try {
        js.put("loan_type", "quickHomelLoan");
        js.put("user_type", "salaried");
        js.put("lead_customer_name", fullnameStr);
        js.put("lead_date_of_birth", "" + dateofbirthStr);
        js.put("contact_no", contactStr);
        js.put("lead_aadhar_card", AppController.aadharcardStr);
        js.put("lead_loan_amount", loanreqStr);
        js.put("lead_location",cityspStr);
        js.put("lead_property_location","lllllllll");
        js.put("lead_monthly_income", incomeStr);
        js.put("lead_monthly_obligation", emiStr);
        js.put("lead_name_of_organisation", companyspStr);
        js.put("lead_pan_card", AppController.pencardStr);
        js.put("lead_resident_type", resposStr);
        js.put("lead_total_work_experience", workexpStr);
        js.put("lead_user_office_address", officeaddressStr);
        js.put("lead_user_resident_address", residentaddressStr);
        Log.d("homeloanreq", js.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, homeloan_url, js,
        new Response.Listener<JSONObject>() {
@Override
public void onResponse(JSONObject response) {
        //hideProgressDialog();
       progressDialog.dismiss();
        try {
        //Toast.makeText(getApplicationContext(), "" + response, Toast.LENGTH_SHORT).show();
        Log.d("hloanresponse", response.toString());
        statusStr = response.getString("status");
        AppController.disbersal_amount = response.getString("disbursal_amount");

            if (statusStr.equals("N") && AppController.disbersal_amount.equals("00")) {
                Intent intent = new Intent(HomeloanActivity.this, NotApproveLoanActivity.class);
                startActivity(intent);
            }
            else if (statusStr.equals("Y") && !AppController.disbersal_amount.equals("00")) {
        /*Intent intent = new Intent(HomeloanActivity.this, ApproveLoanActivity.class);
        intent.putExtra("disbursal_amount", disbursal_amount);
        startActivity(intent);*/
            new getAdr().execute();
        }

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
        Toast.makeText(getApplicationContext(), "" + error, Toast.LENGTH_SHORT).show();
        Log.d("errorrr",error.toString());
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
        AppController.getInstance().getRequestQueue().getCache().remove(homeloan_url);

        } catch (JSONException e) {
        e.printStackTrace();
        }
     }


        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                switch (seekBar.getId()) {
                        case R.id.incomeseekbar:
                                //double value = min + (progress * step);
                                int stepSize = 1000;
                                progress = ((int) Math.round(progress / stepSize)) * stepSize;
                                //incomeStr = NumberFormat.getIntegerInstance().format(progress);
                                incomeStr = String.valueOf(progress);
                                incomeSt = myFormatter.format(progress);
                                incomeTv.setText("₹ " + incomeSt);
                                break;
                        case R.id.emiseekbar:
                                int stepSizeemi = 1000;
                                progress = ((int) Math.round(progress / stepSizeemi)) * stepSizeemi;
                                emiStr = String.valueOf(progress);
                                emiSt = myFormatter.format(progress);
                                emiTv.setText("₹ " + emiSt);
                                break;
                        case R.id.workexpseekbar:
                                int stepSizeworkexp = 1;
                                progress = ((int) Math.round(progress / stepSizeworkexp)) * stepSizeworkexp;
                                workexpStr = String.valueOf(progress);
                                workexpTv.setText("" + progress);
                                break;
                        case R.id.loanreqseekbar:
                                int stepSizeloanreq = 1000;
                                progress = ((int) Math.round(progress / stepSizeloanreq)) * stepSizeloanreq;
                                loanreqStr = String.valueOf(progress);
                                loanreqSt = myFormatter.format(progress);
                                loanreqTv.setText("₹ " + loanreqSt);
                                break;
                }
        }


         public void getcityList() {

    progressDialog.show();
                //showProgressDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.BaseUrl + "fetchcitylist",
        new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

            progressDialog.dismiss();
        //hideProgressDialog();
        //Toast.makeText(PersonalloanActivity.this, response, Toast.LENGTH_LONG).show();
        Log.d("cityresponse", response);
        JSONArray jsonArray = null;
        try {
        jsonArray = new JSONArray(response);
        cityArr = new String[jsonArray.length()];
        for ( i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        String cityName = jsonObject.getString("name");
        cityArr[i] = cityName;
        cityposStr=cityArr[i];
        Log.d("cityArr", Arrays.toString(cityArr));
                                /*Loan loan=new Loan();
                                loan.setCompany(companyName);
                                companyList.add(loan);*/
        }


        }
        catch (JSONException e) {
        e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(HomeloanActivity.this, R.layout.company_layout, cityArr);
            cityAutoTextView.setThreshold(1);//will start working from first character
            cityAutoTextView.setAdapter(adapter);




                       /* ArrayAdapter cityAdptr = new ArrayAdapter(PersonalloanActivity.this, android.R.layout.simple_spinner_item, companyArr);
                        cityAdptr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        companySpinner.setAdapter(cityAdptr);*/
        }
        },
        new Response.ErrorListener() {
@Override
public void onErrorResponse(VolleyError error) {
        //hideProgressDialog();
    progressDialog.dismiss();
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



@Override
public void onStartTrackingTouch(SeekBar seekBar) {

        }

@Override
public void onStopTrackingTouch(SeekBar seekBar) {

        }

@Override
public void onBackPressed() {
        finish();
        }

}
