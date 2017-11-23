package com.example.femi.emergent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.leakcanary.LeakCanary;

import java.io.File;

import Utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

import static java.security.AccessController.getContext;

public class HomeActivity extends AppCompatActivity
        implements  GoogleApiClient.OnConnectionFailedListener{
    private int MY_PERMISSIONS_REQUEST_CALL_PHONE;
    static final int REQUEST_TAKE_PHOTO = 1;
    // utilities object
    Utils util = new Utils();
    @BindView(R.id.send) LinearLayout send;
    @BindView(R.id.news) LinearLayout news;
    @BindView(R.id.call) LinearLayout call;
    @BindView(R.id.help) LinearLayout help;
    @BindView(R.id.main_toolbar) Toolbar toolbar;
    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(LeakCanary.isInAnalyzerProcess(this)){
            return;
        }
        LeakCanary.install(getApplication());
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();


        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
//            mUsername = mFirebaseUser.getDisplayName();
//            if (mFirebaseUser.getPhotoUrl() != null) {
//                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
//            }
        }

        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Watchman");
//        toolbar.setSubtitle("Lagos State");
//        toolbar.setLogo(R.drawable.logo);


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + getString(R.string.emergency_number)));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HomeActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    return;
                }
                startActivity(callIntent);
            }
        });

     help.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
             startActivity(intent);
         }
     });
     news.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent intent = new Intent(getApplicationContext(), MainActivity.class);
             startActivity(intent);
         }
     });

     send.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             startActivityForResult(util.dispatchTakePictureIntent(getApplicationContext()), REQUEST_TAKE_PHOTO);
         }
     });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        for(int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spanString.length(), 0); //fix the color to white
            item.setTitle(spanString);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
//                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
           Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + Uri.encode("azeezfemi17937@yahoo.com")));
           intent.putExtra(Intent.EXTRA_SUBJECT, "Emergency");
           intent.putExtra(Intent.EXTRA_TEXT, "There is an emergency situation going on please help");
           Toast.makeText(getApplicationContext(), "There are clients "+ util.getCurrentPhotoPath(), Toast.LENGTH_SHORT).show();
           Uri uri = Uri.fromFile(new File(util.getCurrentPhotoPath()));
           intent.putExtra(Intent.EXTRA_STREAM, uri);
           try{
               startActivity(Intent.createChooser(intent, "Send email..."));
           } catch (android.content.ActivityNotFoundException ex){
               Toast.makeText(getApplicationContext(), "There are no email clients", Toast.LENGTH_SHORT).show();
           }

       }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "could not connect to google", Toast.LENGTH_SHORT).show();
    }
}
