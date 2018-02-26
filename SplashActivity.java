package com.addventure.loanadda;

import android.*;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.addventure.loanadda.LocationUtil.MyLocationUsingHelper;
import com.addventure.loanadda.LocationUtil.MyLocationUsingLocationAPI;
import com.addventure.loanadda.LocationUtil.PermissionUtils;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class SplashActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,ActivityCompat.OnRequestPermissionsResultCallback,
        PermissionUtils.PermissionResultCallback {
    Context context=this;
    Dialog dialog;
    private static int SPLASH_TIME_OUT = 3000;
    RelativeLayout mainLayout,updateClick,cancelClick;
    String versionName;
    int versionCode;

    private static final String TAG = MyLocationUsingHelper.class.getSimpleName();

    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int REQUEST_CHECK_SETTINGS = 2000;
    private Location mLastLocation;
    SharedPreferences locationSp;
    public static final String locationPrefrence = "location";


    // Google client to interact with Google API

    private GoogleApiClient mGoogleApiClient;

    double latitude;
    double longitude;

    // list of permissions

    ArrayList<String> permissions=new ArrayList<>();
    PermissionUtils permissionUtils;

    boolean isPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleBar();
        setContentView(R.layout.activity_splash);
        versionName=getVersionName();
        versionCode=getVersionCode();
        Log.d("versionName",versionName);
        Log.d("versionCode",""+versionCode);



        getForcefullUpdate();

        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }
    }

    private void getForcefullUpdate() {

        String appupdateUrl=AppConfig.BaseUrl+"findversion";

        JSONObject js = new JSONObject();
        try {
            js.put("version", versionName);
            Log.d("jsosoonn",js.toString());

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, appupdateUrl, js,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hideProgressDialog();
                            Log.d("otpresponse", response.toString());
                            //Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                            try {
                                String status=response.getString("status");

                                if (status.equals("Y")) {

                                    getcurrentLocation();

                                   /* new Handler().postDelayed(new Runnable() {

                                        @Override
                                        public void run() {
                                            // This method will be executed once the timer is over
                                            // Start your app main activity
                                           *//* Intent i = new Intent(SplashActivity.this, MyLocationUsingLocationAPI.class);
                                            startActivity(i);
                                            finish();*//*

                                           getcurrentLocation();
                                        }
                                    }, SPLASH_TIME_OUT);*/
                                }

                                else if (status.equals("N")) {

                                    Intent i=new Intent(SplashActivity.this,ForceUpdateActivity.class);
                                    startActivity(i);

                                 /*   dialog = new Dialog(context);
                                    dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.forceupdate_dialog);
                                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                    dialog.show();
                                        updateClick = (RelativeLayout) dialog.findViewById(R.id.updateRl);
                                        cancelClick = (RelativeLayout) dialog.findViewById(R.id.cancelRl);

                                    updateClick.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                getRatingpage();
                                                dialog.dismiss();

                                            }
                                        });

                                    cancelClick.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                        public void onClick(View view) {

                                            dialog.dismiss();

                                        }
                                    });*/
                                }

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

    private void getcurrentLocation() {

        permissionUtils=new PermissionUtils(SplashActivity.this);

        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionUtils.check_permission(permissions,"Need GPS permission for getting your location",1);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                getLocation();

                if (mLastLocation != null) {
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();
                    getAddress();

                } else {

                    showToast("Couldn't get the location. Make sure location is enabled on the device");

                }
            }
        }, SPLASH_TIME_OUT);

    }

    public String getVersionName() {
        PackageManager pm = this.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException ex) {}
        return "";
    }

    public int getVersionCode() {
        PackageManager pm =getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {}
        return 0;
    }

    private void getLocation() {

        if (isPermissionGranted) {

            try
            {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }
            catch (SecurityException e)
            {
                e.printStackTrace();
            }
        }
    }

    public Address getAddress(double latitude, double longitude) {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public void getAddress() {

        Address locationAddress=getAddress(latitude,longitude);

        if(locationAddress!=null) {

            String address = locationAddress.getAddressLine(0);
            String address1 = locationAddress.getAddressLine(1);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String country = locationAddress.getCountryName();
            String postalCode = locationAddress.getPostalCode();

            locationSp = getApplicationContext().getSharedPreferences(locationPrefrence, MODE_PRIVATE);
            SharedPreferences.Editor editor = locationSp.edit();
            editor.putString("address",address);
            editor.putString("address1",address1);
            editor.putString("city",city);
            editor.putString("state",state);
            editor.putString("country",country);
            editor.putString("postalCode",postalCode);
            editor.commit();
            editor.apply();

          /*  Log.d("address",address);
            Log.d("address1",address1);
            Log.d("city",city);
            Log.d("state",state);
            Log.d("country",country);
            Log.d("postalCode",postalCode);*/

            String currentLocation;

            if(!TextUtils.isEmpty(address)) {

                currentLocation=address;

                if (!TextUtils.isEmpty(address1))
                    currentLocation+="\n"+address1;

                if (!TextUtils.isEmpty(city)) {

                    currentLocation+="\n"+city;

                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation+=" - "+postalCode;
                }
                else {

                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation+="\n"+postalCode;
                }

                if (!TextUtils.isEmpty(state))
                    currentLocation+="\n"+state;

                if (!TextUtils.isEmpty(country))
                    currentLocation+="\n"+country;


                //Toast.makeText(getApplicationContext(),currentLocation,Toast.LENGTH_SHORT).show();

                Intent i=new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);

            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(SplashActivity.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    private boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this,resultCode,
                        PLAY_SERVICES_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // redirects to utils
        permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);

    }


    @Override
    public void PermissionGranted(int request_code) {

        Log.i("PERMISSION","GRANTED");
        isPermissionGranted=true;

    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Log.i("PERMISSION PARTIALLY","GRANTED");

    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.i("PERMISSION","DENIED");

    }

    @Override
    public void NeverAskAgain(int request_code) {
        Log.i("PERMISSION","NEVER ASK AGAIN");

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        getLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

    }

    public void showToast(String message)
    {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
