package com.example.femi.emergent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

import Models.Event;
import Utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class AddEventActivity extends AppCompatActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    @BindView(R.id.add_event_toolbar) Toolbar toolbar;
    @BindView(R.id.add_event_cancel) Button cancel;
    @BindView(R.id.add_event_submit) Button submit;
    @BindView(R.id.edit_event_title) EditText title;
    @BindView(R.id.edit_event_description) EditText desc;
    @BindView(R.id.event_image) ImageView image;
    private GoogleApiClient mGoogleApiClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private boolean imageSet = false;
    static final int REQUEST_TAKE_PHOTO = 1;
    // utilities object
    Utils util = new Utils();
    private LocationRequest mLocationRequest;
    private Uri photoPath;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(util.dispatchTakePictureIntent(getApplicationContext()), REQUEST_TAKE_PHOTO);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitEvent();
            }
        });
//        getdeviceLocation();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

    }

    private void setEditingEnabled(boolean enabled) {
        title.setEnabled(enabled);
        desc.setEnabled(enabled);
        if (enabled) {
            submit.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
        } else {
            submit.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
        }
    }

    private void submitEvent(){
        final String titleText = title.getText().toString();
        final String description = desc.getText().toString();
        final String author = desc.getText().toString();
        if (photoPath == null ) {
            Toast.makeText(this, "Take a photo first", Toast.LENGTH_SHORT).show();
        } else {
            setEditingEnabled(false);

            Toast.makeText(this, "Posting events", Toast.LENGTH_SHORT).show();
            File photo = new File("" + photoPath);
            mStorageReference = FirebaseStorage.getInstance().getReference("event_photos/" + photo.getName());

            image.setDrawingCacheEnabled(true);
            image.buildDrawingCache();
            Bitmap bitmap = image.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            mStorageReference.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downLoadUrl = taskSnapshot.getDownloadUrl();
                            String imageurl = downLoadUrl.toString();
                            Event event = new Event(imageurl, titleText, description,
                                    mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude(),
                                    1);
                            writeEvents(event);
                            setEditingEnabled(true);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddEventActivity.this, "Photo was not added successfully", Toast.LENGTH_SHORT).show();
                        }
                    });

        }


    }

    private void writeEvents(Event event) {
        mDatabaseReference.child("event").push().setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "event written successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "event not written successfully", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Timber.e(e);
            }
        });

        Timber.d("transaction successful.");

        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            photoPath = Uri.parse(util.getCurrentPhotoPath());
            Glide.with(getApplicationContext())
                    .load(util.getCurrentPhotoPath())
                    .centerCrop()
                    .into(image);
            imageSet = true;
        }
    }



    private void getdeviceLocation()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if(mLocationPermissionGranted){
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            if (mLastKnownLocation == null){
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                getdeviceLocation();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getdeviceLocation();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getdeviceLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "You need internet to get your current location", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        getdeviceLocation();
    }
}
