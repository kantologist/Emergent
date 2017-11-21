package com.example.femi.emergent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.leakcanary.LeakCanary;

import java.io.File;

import Utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

import static java.security.AccessController.getContext;

public class HomeActivity extends AppCompatActivity {
    private int MY_PERMISSIONS_REQUEST_CALL_PHONE;
    static final int REQUEST_TAKE_PHOTO = 1;
    // utilities object
    Utils util = new Utils();
    @BindView(R.id.send) LinearLayout send;
    @BindView(R.id.news) LinearLayout news;
    @BindView(R.id.call) LinearLayout call;
    @BindView(R.id.help) LinearLayout help;
    @BindView(R.id.main_toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(LeakCanary.isInAnalyzerProcess(this)){
            return;
        }
        LeakCanary.install(getApplication());
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

//        setSupportActionBar(toolbar);
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
}
