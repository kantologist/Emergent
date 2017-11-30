package com.example.femi.emergent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.leakcanary.LeakCanary;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import Models.News;
import Models.Report;
import Utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

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
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private String mUseremail;
    private String mUserphone;
    private Uri photoPath;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=1;



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

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();



        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUseremail = mFirebaseUser.getEmail();
            mUserphone = mFirebaseUser.getPhoneNumber();
//            if (mFirebaseUser.getPhotoUrl() != null) {
//                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
//            }
        }


        Intent intent = getIntent();
        String report_key = intent.getStringExtra("report");
        if("report".equals(report_key)){
            startActivityForResult(util.dispatchTakePictureIntent(getApplicationContext()),
                    REQUEST_TAKE_PHOTO);
        }

        setSupportActionBar(toolbar);


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

    private void submitReport() {
        if (photoPath == null ) {
            Toast.makeText(this, "Take a photo first", Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, "Posting report", Toast.LENGTH_SHORT).show();
            File photo = new File("" + photoPath);
            mStorageReference = FirebaseStorage.getInstance().getReference("report_photos/" + photo.getName());
            InputStream is = null;
            try {
                is = getContentResolver().openInputStream(photoPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            mStorageReference.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downLoadUrl = taskSnapshot.getDownloadUrl();
                            String imageurl = downLoadUrl.toString();
                            Report report = new Report("", imageurl, mUseremail, mUserphone);
                            writeReport(report);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(HomeActivity.this, "Photo was not added successfully", Toast.LENGTH_SHORT).show();
                        }
                    });

        }


    }

    private void writeReport(Report report) {
        mDatabaseReference.child("report").push().setValue(report).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "reports written successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "reports not written successfully", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Timber.e(e);
            }
        });

        Timber.d("transaction successful.");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        submitReport();

                } else {
                    Toast.makeText(this,"You have to grant permission to read the" +
                            " storage to make a report", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
           photoPath = Uri.fromFile(new File(util.getCurrentPhotoPath()));
           Timber.d("permission request");
           if (ContextCompat.checkSelfPermission(this,
                   Manifest.permission.READ_EXTERNAL_STORAGE)
                   != PackageManager.PERMISSION_GRANTED) {
               Timber.d("permission granted");
               submitReport();
           } else {
               ActivityCompat.requestPermissions(this,
                       new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                       MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
               Timber.d("permission asking");
           }

       }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "could not connect to google", Toast.LENGTH_SHORT).show();
    }
}
