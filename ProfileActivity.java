package com.addventure.loanadda;

import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ProfileActivity extends BaseActivity {

    RelativeLayout rmcontLayout;
    TextView calltextTv,verifytextTv,loantypetxtTv,amtappltxtTv,loanstatustxtTv,rmnametxtTv,rmcontacttxtTv,cusNameTv,companyNameTv;
    ImageView backIv;
    SharedPreferences profileSp;
    public static final String profilePrefrence = "profileSp";
    String cusName,compName,userStatus,loanType,loanAmount,dobStr,loanStatus,rmName,rmContact,cityStr;
    String contactStr;
    SharedPreferences loginSp;
    public static final String loginPrefrence = "loginSp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getStatusbarcolor();
        setContentView(R.layout.activity_profile);
        getFindviewid();
        getprofile();

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rmcontLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getcallClick();

            }
        });
    }

    private void getFindviewid() {

        rmcontLayout = (RelativeLayout) findViewById(R.id.rmcontLayout);
        cusNameTv = (TextView) findViewById(R.id.cusName);
        companyNameTv = (TextView) findViewById(R.id.companyName);
        calltextTv = (TextView) findViewById(R.id.calltext);
        verifytextTv = (TextView) findViewById(R.id.verifytext);
        loantypetxtTv = (TextView) findViewById(R.id.loantypetxt);
        //amtappltxtTv = (TextView) findViewById(R.id.amtappltxt);
        loanstatustxtTv = (TextView) findViewById(R.id.loanstatustxt);
        rmnametxtTv = (TextView) findViewById(R.id.rmnametxt);
        rmcontacttxtTv = (TextView) findViewById(R.id.rmcontacttxt);
        backIv = (ImageView) findViewById(R.id.back);

    }

    private void getprofile() {

        profileSp = getSharedPreferences(profilePrefrence, MODE_PRIVATE);
        cusName = profileSp.getString("cusName", "");
        compName = profileSp.getString("compName", "");
        userStatus = profileSp.getString("userStatus", "");
        loanType = profileSp.getString("loanType", "");
        loanAmount = profileSp.getString("loanAmount", "");
        //dobStr = profileSp.getString("dobStr", "");
        loanStatus = profileSp.getString("loanStatus", "");
        rmName = profileSp.getString("rmName", "");
        rmContact = profileSp.getString("rmContact", "");
        cityStr = profileSp.getString("cityStr", "");

        loginSp = getSharedPreferences(loginPrefrence, MODE_PRIVATE);
        contactStr = loginSp.getString("contactNo", "");

        cusNameTv.setText(cusName);
        companyNameTv.setText(compName);
        calltextTv.setText(contactStr);
        verifytextTv.setText(userStatus);
        loantypetxtTv.setText(loanType);
        //amtappltxtTv.setText("Apply");
        loanstatustxtTv.setText(loanStatus);
        rmnametxtTv.setText(rmName);
        rmcontacttxtTv.setText(rmContact);

        if (AppController.fbclick==true) {
            cusNameTv.setText(AppController.fbname);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
