package com.example.femi.emergent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import Fragments.NewsFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String CONTACTFRAGMENT_TAG = "CTAG";
    private final String NEWSFRAGMENT_TAG = "NTAG";
    private int MY_PERMISSIONS_REQUEST_CALL_PHONE;
    private  FragmentTransaction ft;

    @BindView(R.id.drawer) DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle toggle;
    @BindView(R.id.navigation_view) NavigationView navigationView;
    @BindView(R.id.toolbar_image) ImageView toolbar_image;
    @BindView(R.id.title) CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.toolbar) Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


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


        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);
    }

    @Override
    public void onBackPressed(){
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
               Intent intent = new Intent(this, HomeActivity.class);
               startActivity(intent);
               break;
           }
           case R.id.inform: {
               ft = getSupportFragmentManager().beginTransaction();
               ft.replace(R.id.container, new NewsFragment(), NEWSFRAGMENT_TAG);
               ft.commit();
               toolbar_image.setImageResource(R.drawable.inform);
               toolbarLayout.setTitle(getString(R.string.inform));
               break;
           }
           case R.id.map: {
               Intent intent = new Intent(this, MapsActivity.class);
               startActivity(intent);
               break;
           }
           case R.id.about: {
               new LibsBuilder()
                       .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                       .withAboutIconShown(true)
                       .withAboutVersionShown(true)
                       .withAboutDescription("Emergent was built by Oluwafemi Azeez.<br /> " +
                               "Shoot me an email: <a href='mailto:azeezfemi17937@yahoo.com'>azeezfemi17937@yahoo.com</a> <br /> " +
                               "This app is open sourced at <a href ='https://github.com/kantologist/Emergent'> github.com/kantologist/Emergent</a> <br />" +
                               "Other open sourced libraries used are listed below.")
                       .start(this);
           }
           default:
               break;
       }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
