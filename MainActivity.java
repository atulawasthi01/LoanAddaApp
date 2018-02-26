package com.addventure.loanadda;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.StaleDataException;
import android.net.Network;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.addventure.loanadda.Adapter.CustomloanAdapter;
import com.addventure.loanadda.Adapter.NavigationDrawerListAdapter;
import com.addventure.loanadda.Adapter.SlidingImage_Adapter;
import com.addventure.loanadda.Model.Loantitle;
import com.addventure.loanadda.Model.NavDrawerItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.addventure.loanadda.Model.SMSData;
import com.addventure.loanadda.R;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import me.relex.circleindicator.CircleIndicator;


public class MainActivity extends BaseActivity {
    Toolbar toolbar;
    TextView titleTv;
    GridView loangridView;
    CustomloanAdapter customloanAdapter;
    List<Loantitle> loantitleList;
    protected ArrayList<NavDrawerItem> _items;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    android.support.v7.app.ActionBar actionBar;
    protected FrameLayout frameLayout;
    protected ListView mDrawerList;
    RelativeLayout mainviewLayout;
    protected static int position;
    private static boolean isLaunch = true;
    String[] itemnameArr;
    String[] loantitleArr;

    Integer[] loanttlimgArr=new Integer[] {
            R.drawable.hloan,
            R.drawable.ploan
    };

    Integer[] itemimgArr=new Integer[] {
            R.drawable.home,
            R.drawable.homeloaneligibility,
            R.drawable.personalloan,
            R.drawable.personalloaneligibility,
            R.drawable.homeloaneligibility,
            R.drawable.documentploade,
            R.drawable.ifsc,
            R.drawable.emi,
            R.drawable.aboutus,
            R.drawable.contactus,
            R.drawable.contactus,
            R.drawable.rateus,
            R.drawable.logout
    };

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static final Integer[] XMEN= {R.drawable.bannerone,R.drawable.bannertwo,R.drawable.bannerthree};
    private ArrayList<Integer> XMENArray = new ArrayList<Integer>();
    String contactStr;
    private ArrayList<Integer> ImagesArray = new ArrayList<Integer>();
    SharedPreferences loginSp;
    public static final String loginPrefrence = "loginSp";

    //public static String[] companyArr;



    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getStatusbarcolor();
            setContentView(R.layout.activity_main);

            navDrawer();
            init();

        loangridView = (GridView) findViewById(R.id.loangrid);
        loantitleList = new ArrayList<Loantitle>();
        loantitleArr = getResources().getStringArray(R.array.loantitleArr);
        for (int i=0;i<loantitleArr.length;i++) {
            Loantitle loantitle=new Loantitle(loantitleArr[i],loanttlimgArr[i]);
            loantitleList.add(loantitle);
        }

        customloanAdapter = new CustomloanAdapter(this,loantitleList);
        loangridView.setAdapter(customloanAdapter);

        loangridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (position==0) {
                    Intent i=new Intent(MainActivity.this,HomeloanActivity.class);
                    startActivity(i);
                }
                else if (position==1) {
                    Intent i=new Intent(MainActivity.this,PersonalloanActivity.class);
                    startActivity(i);
                }
            }
        });
    }


    private void init() {

        for(int i=0;i<XMEN.length;i++)
            XMENArray.add(XMEN[i]);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImage_Adapter(MainActivity.this,XMENArray));
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == XMEN.length) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };

        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

    }


    private void navDrawer() {

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        titleTv = (TextView) findViewById(R.id.title);
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mainviewLayout = (RelativeLayout)findViewById(R.id.mainviewLayout);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menuicon);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        itemnameArr = getResources().getStringArray(R.array.nav_drawer_lables);
        titleTv.setText(getResources().getString(R.string.loanaddattl));
        _items = new ArrayList<NavDrawerItem>();

        for (int i=0;i<itemnameArr.length;i++) {
            NavDrawerItem navDrawerItem=new NavDrawerItem(itemnameArr[i],itemimgArr[i]);
            _items.add(navDrawerItem);
        }

        loginSp = getSharedPreferences(loginPrefrence, MODE_PRIVATE);
        contactStr = loginSp.getString("contactNo", "");

        View header = (View) getLayoutInflater().inflate(R.layout.drawer_header, null);
        mDrawerList.addHeaderView(header);
        TextView contactTv = (TextView)header.findViewById(R.id.number);
        RelativeLayout profileLayout = (RelativeLayout)header.findViewById(R.id.profileLayout);
        contactTv.setText(contactStr);

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(i);
            }
        });

        mDrawerList.setAdapter(new NavigationDrawerListAdapter(this, _items));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                openActivity(position);
            }
        });


        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,						/* host Activity */
                mDrawerLayout, 				/* DrawerLayout object */
                R.drawable.menuicon,     /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,       /* "open drawer" description for accessibility */
                R.string.drawer_close)      /* "close drawer" description for accessibility */ {

            @Override
            public void onDrawerClosed(View drawerView) {
                /*getActionBar().setTitle(listArray[position]);*/
                invalidateOptionsMenu();
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
                super.onDrawerOpened(drawerView);
                //mDrawerLayout.setScrimColor(Color.WHITE);
            }


            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                mainviewLayout.setTranslationX(slideOffset * drawerView.getWidth());
                mDrawerLayout.bringChildToFront(drawerView);
                mDrawerLayout.requestLayout();
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }
        };

        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);


        if (isLaunch) {

            isLaunch = false;
            openActivity(0);
        }
    }

    protected void openActivity(int position) {

        //mDrawerLayout.closeDrawer(mDrawerList);

        switch (position - 1) {
            case 0:
               /* Intent i=new Intent(MainActivity.this,MainActivity.class);
                startActivity(i);*/
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case 1:
                Intent iHome=new Intent(MainActivity.this,HomeloanActivity.class);
                startActivity(iHome);
                break;
            case 2:
                Intent iPersonal=new Intent(MainActivity.this,PersonalloanActivity.class);
                startActivity(iPersonal);
                break;
            case 3:
                Intent iPerEl=new Intent(MainActivity.this,PersonalloanEligibility.class);
                startActivity(iPerEl);
                break;
            case 4:
                Intent iHomeEl=new Intent(MainActivity.this,HomeloanElgibility.class);
                startActivity(iHomeEl);
                break;
            case 5:
                Intent iDoc=new Intent(MainActivity.this,DocuploadActivity.class);
                startActivity(iDoc);
                break;
            case 6:
                Intent iIfsc=new Intent(MainActivity.this,IfscActivity.class);
                startActivity(iIfsc);
                break;
            case 7:
                Intent iEmi=new Intent(MainActivity.this,EmiCalActivity.class);
                startActivity(iEmi);
                break;
            case 8:
               Intent iAbout=new Intent(MainActivity.this,AboutusActivity.class);
               startActivity(iAbout);
               break;
            case 9:
                Intent icontact=new Intent(MainActivity.this,ContactusActivity.class);
                startActivity(icontact);
                break;
            case 10:
                Intent iQuery=new Intent(MainActivity.this,QueryActivity.class);
                startActivity(iQuery);
                break;
            case 11:
                getRatingpage();
                break;
            case 12:
                Intent ilogout=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(ilogout);
                break;

                default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                item.setEnabled(true);
                item.setTitle(Html.fromHtml("<font color='#ff3824'>Settings</font>"));
                return false;
            }
        });

        return super.onOptionsItemSelected(item);

    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view/*
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
       /* menu.findItem(R.id.action_settings).setVisible(!drawerOpen);*/
        return super.onPrepareOptionsMenu(menu);
    }




    @Override
    public void onBackPressed() {
        MainActivity.this.finishAffinity();
    }
}
