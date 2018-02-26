package com.addventure.loanadda;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class PersonalloanEligibility extends BaseActivity {
    RelativeLayout salariedLayout,sempLayout;
    Button applyBtn;
    Toolbar toolbar;
    TextView titleTv,txtcriteriyaTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getStatusbarcolor();
        setContentView(R.layout.activity_homeloan_elgibility);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        titleTv = (TextView)findViewById(R.id.title);
        txtcriteriyaTv = (TextView)findViewById(R.id.txtcriteriya);
        txtcriteriyaTv.setText(getResources().getString(R.string.peligibilitycriteria));
        salariedLayout = (RelativeLayout) findViewById(R.id.salariedLayout);
        sempLayout = (RelativeLayout) findViewById(R.id.sempLayout);
        sempLayout.setVisibility(View.GONE);
        applyBtn = (Button) findViewById(R.id.applyBtn);
        titleTv.setText(getResources().getString(R.string.ploanelgttl));
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

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(PersonalloanEligibility.this,PersonalloanActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
