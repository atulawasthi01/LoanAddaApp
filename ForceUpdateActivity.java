package com.addventure.loanadda;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.addventure.loanadda.LocationUtil.MyLocationUsingLocationAPI;

public class ForceUpdateActivity extends BaseActivity {

    Button updateBtn;
    ImageView cancelClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_force_update);
        updateBtn = (Button) findViewById(R.id.updateBtn);
        cancelClick = (ImageView) findViewById(R.id.cancel);

        cancelClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(ForceUpdateActivity.this, MyLocationUsingLocationAPI.class);
                startActivity(i);
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRatingpage();
            }
        });


    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
