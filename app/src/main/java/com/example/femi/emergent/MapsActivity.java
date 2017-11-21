package com.example.femi.emergent;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import Models.Event;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
        OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private GoogleMap mMap;
    @BindView(R.id.add_event) FloatingActionButton fab;
    Map<Marker, Event> eventMarker;
    private Realm realm;
    private GoogleApiClient mGoogleApiClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // add event with plus button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // implement add event
                Intent intent = new Intent(getApplication(), AddEventActivity.class);
                startActivity(intent);
            }
        });
        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

    }

    private void getdeviceLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            onMapReady(mMap);
            if (mLastKnownLocation == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                getdeviceLocation();
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
                }
            }
        }
    }
//
//    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng local;
        if (mLastKnownLocation != null){
            local = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        } else {
            local = new LatLng(6.46, 3.4);
        }
//        mMap.addMarker(new MarkerOptions().position(local));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(local, 15));
        eventMarker = new HashMap<Marker, Event>();
        RealmResults<Event> events = realm.where(Event.class).findAll();
//        Toast.makeText(this, "events are: "+ events.size(), Toast.LENGTH_SHORT).show();
        for (Event event: events){
            addMarkers(event);
        }
        mMap.setOnMarkerClickListener(this);
    }

    private void addMarkers(Event event){
        if(null != mMap){
            Marker markerEvent = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(event.getLat(), event.getLon()))
                    .title(event.getTitle())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            markerEvent.showInfoWindow();
            eventMarker.put(markerEvent, event);
        }
    }

    @Override
    public boolean onMarkerClick (Marker marker){
        final Event event = eventMarker.get(marker);
        if (event != null){
            // implement pop up
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.marker_dialog);
            dialog.setTitle(event.getTitle());
//            getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(lp);


            TextView title = (TextView) dialog.findViewById(R.id.title);
            title.setText(event.getTitle());
            ImageView image = (ImageView) dialog.findViewById(R.id.image);
            if (event.getImage() != null) {
                Glide.with(this)
                        .load(event.getImage())
                        .centerCrop()
                        .into(image);
            }
            TextView desc = (TextView) dialog.findViewById(R.id.desc);
            desc.setText(event.getDescription());
            Button dialogButton = (Button) dialog.findViewById(R.id.dialog_ok);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            Button direct = (Button) dialog.findViewById(R.id.direction);
            direct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)",
                            event.getLat(), event.getLon(), "way to help");
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                    Intent intent = new Intent(Intent.ACTION_VIEW,
//                            Uri.parse("http://maps.google.com/maps?saddr="
//                                    +mLastKnownLocation.getLatitude()+","
//                                    +mLastKnownLocation.getLongitude()+"&daddr="
//                                    +event.getLat()+","
//                                    +event.getLon()));
                    startActivity(intent);
                }
            });
            dialog.show();
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getdeviceLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "You need internet to get your current location", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        getdeviceLocation();
    }
}
