package com.addventure.loanadda;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.addventure.loanadda.R;

public class NotApproveLoanActivity extends ApproveLoanActivity {
    RelativeLayout callClickRl,docuploadClickRl;
    Toolbar toolbar;
    TextView titleTv,rateusClick,websiteClick;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getStatusbarcolor();
        setContentView(R.layout.activity_not_approve_loan);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        titleTv = (TextView)findViewById(R.id.title);
        rateusClick = (TextView)findViewById(R.id.rateusClick);
        websiteClick = (TextView)findViewById(R.id.websiteClick);
        callClickRl = (RelativeLayout)findViewById(R.id.callClick);
        docuploadClickRl = (RelativeLayout)findViewById(R.id.docuploadClick);
        titleTv.setText(getResources().getString(R.string.failed));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
               /* Intent intent=new Intent(NotApproveLoanActivity.this,MainActivity.class);
                startActivity(intent);*/
                finish();
            }
        });

        callClickRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getcallClick();

            }
        });

        docuploadClickRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(NotApproveLoanActivity.this,DocuploadActivity.class);
                startActivity(i);
            }
        });

        websiteClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.loanadda.com"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        rateusClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRatingpage();
            }
        });
    }

    @Override
    public void onBackPressed() {
       /* super.onBackPressed();
        Intent intent=new Intent(NotApproveLoanActivity.this,MainActivity.class);
        startActivity(intent);*/
        finish();
    }
}
