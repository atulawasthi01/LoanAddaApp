package com.addventure.loanadda;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class SocialLoginActivity extends AppCompatActivity implements
        View.OnClickListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    Button fbLoginBtn,gmailLoginBtn;
    SharedPreferences socialSp;
    public static final String socialPrefrence = "socialSp";
    private CallbackManager callbackManager;
    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "MainActivity";
    private static final int PROFILE_PIC_SIZE = 400;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    public static String personName, personPhotoUrl, personGooglePlusProfile, email;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_social_login);
        fbLoginBtn = (Button) findViewById(R.id.fbLogin);
        gmailLoginBtn = (Button) findViewById(R.id.gmailLogin);

        fbLoginBtn.setOnClickListener(this);
        gmailLoginBtn.setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fbLogin:
                getFacebookLogin();
                break;
            case R.id.gmailLogin:
                AppController.gmailclick=true;
                signInWithGplus();
                break;
        }

    }

    private void getFacebookLogin() {

        AppController.fbclick=true;
        LoginManager.getInstance().logInWithReadPermissions(SocialLoginActivity.this, Arrays.asList("public_profile", "email", "user_friends"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>()
                {
                    @Override
                    public void onSuccess(LoginResult loginResult)
                    {

                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        if (response != null) {
                                            try {

                                                //Toast.makeText(getApplicationContext(),"facebook login",Toast.LENGTH_SHORT).show();

                                                AppController.fbid = object.getString("id");
                                                AppController.fbname = object.getString("name");
                                                AppController.fbemail = object.getString("email");

                                                Log.d("emailfb", AppController.fbemail);
                                                Log.d("idfb", AppController.fbid);
                                                Log.d("namefb", AppController.fbname);

                                                socialSp = getApplicationContext().getSharedPreferences(socialPrefrence, MODE_PRIVATE);
                                                SharedPreferences.Editor editor = socialSp.edit();
                                                editor.putString("fbname",AppController.fbname);
                                                editor.putString("fbemail",AppController.fbemail);
                                                editor.commit();
                                                editor.apply();


                                                Intent intent=new Intent(SocialLoginActivity.this,LoginActivity.class);
                                                startActivity(intent);


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email");
                        request.setParameters(parameters);
                        request.executeAndWait();
                    }

                    @Override
                    public void onCancel()
                    {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception)
                    {
                        // App code
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (requestCode != RESULT_OK) {
                mSignInClicked = false;
            }
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;

            resolveSignInError();

        }
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                AppController.gmlname = currentPerson.getDisplayName();
                personPhotoUrl = currentPerson.getImage().getUrl();
                personGooglePlusProfile = currentPerson.getUrl();
                AppController.gmlemail = Plus.AccountApi.getAccountName(mGoogleApiClient);
                Toast.makeText(getApplicationContext(),""+AppController.gmlname+" "+AppController.gmlemail,Toast.LENGTH_SHORT).show();

                Log.e(TAG, "Name: " + AppController.gmlname + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + AppController.gmlemail
                        + ", Image: " + personPhotoUrl);



            } else {
                Toast.makeText(getApplicationContext(), "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mSignInClicked = false;
        //Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
        getProfileInformation();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {

        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {

                resolveSignInError();
            }
        }

    }
}
