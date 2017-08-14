package com.example.femi.emergent;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import Fragments.ContactFragment;
import Fragments.NewsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String CONTACTFRAGMENT_TAG = "CTAG";
    private final String NEWSFRAGMENT_TAG = "NTAG";
    private int MY_PERMISSIONS_REQUEST_CALL_PHONE;
    private  FragmentTransaction ft;

    private DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    ImageView toolbar_image;
    CollapsingToolbarLayout toolbarLayout;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        PhoneCallListener phoneCallListener = new PhoneCallListener();
//        TelephonyManager telephonyManager = (TelephonyManager) this
//                .getSystemService(Context.TELEPHONY_SERVICE);
//        telephonyManager.listen(phoneCallListener, PhoneStateListener.LISTEN_CALL_STATE);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + getString(R.string.emergency_number)));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    return;
                }
                startActivity(callIntent);
            }
        });
        if (savedInstanceState == null){
            ft = getSupportFragmentManager().beginTransaction();
                    ft.add(R.id.container, new NewsFragment(), NEWSFRAGMENT_TAG)
                    .commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);
    }

    @Override
    public void onBackPressed(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();
       switch (id){
           case R.id.home: {
//               ft = getSupportFragmentManager().beginTransaction();
//               ft.replace(R.id.container, new ContactFragment(), CONTACTFRAGMENT_TAG);
//               ft.commit();
//               toolbar_image = (ImageView)findViewById(R.id.toolbar_image);
//               toolbar_image.setImageResource(R.drawable.report);
//               toolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.title);
//               toolbarLayout.setTitle(getString(R.string.report));
//               fab.setImageResource(R.drawable.ic_call);
//               break;
               Intent intent = new Intent(this, HomeActivity.class);
               startActivity(intent);
               break;
           }
           case R.id.inform: {
               ft = getSupportFragmentManager().beginTransaction();
               ft.replace(R.id.container, new NewsFragment(), NEWSFRAGMENT_TAG);
               ft.commit();
               toolbar_image = (ImageView)findViewById(R.id.toolbar_image);
               toolbar_image.setImageResource(R.drawable.inform);
               toolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.title);
               toolbarLayout.setTitle(getString(R.string.inform));
               break;
           }
           case R.id.map: {
               Intent intent = new Intent(this, MapsActivity.class);
               startActivity(intent);
               break;
           }
           default:
               break;
       }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


//    private class PhoneCallListener extends PhoneStateListener {
//
//        private boolean isPhonecalling = false;
//
//        private String LOG_TAG = "LOGGING 123";
//
//
//        @Override
//        public void onCallStateChanged(int state, String incomingNumber) {
//
//            if (TelephonyManager.CALL_STATE_RINGING == state) {
//
//                Log.i(LOG_TAG, "Ringing, number: " + incomingNumber);
//            }
//
//            if (TelephonyManager.CALL_STATE_OFFHOOK == state ) {
//                Log.i(LOG_TAG, "offhok");
//
//                isPhonecalling = true;
//            }
//
//            if (TelephonyManager.CALL_STATE_IDLE == state) {
//                Log.i(LOG_TAG, "IDLE");
//
//                if (isPhonecalling) {
//                    Log.i(LOG_TAG, "restart app");
//
//                    Intent i  = getBaseContext().getPackageManager()
//                            .getLaunchIntentForPackage(
//                                    getBaseContext().getPackageName());
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(i);
//
//                    isPhonecalling = false;
//                }
//            }
//        }
//    }

}
