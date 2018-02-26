
package com.addventure.loanadda;

import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.TextView;



public class AboutusActivity extends BaseActivity {
    Toolbar toolbar;
    TextView titleTv;
    WebView aboutusWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getStatusbarcolor();
        setContentView(R.layout.activity_aboutus);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleTv = (TextView)findViewById(R.id.title);
        aboutusWebview = (WebView)findViewById(R.id.aboutusWeb);
        titleTv.setText(getResources().getString(R.string.aboutusttl));
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

        aboutusWebview.getSettings().setJavaScriptEnabled(true);
        aboutusWebview.loadUrl("file:///android_asset/aboutus.html");
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
