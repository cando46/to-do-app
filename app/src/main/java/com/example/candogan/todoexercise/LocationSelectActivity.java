package com.example.candogan.todoexercise;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationSelectActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener {

    private MapView mapView;
    private GoogleMap mMap;
    private Circle mCircle;
    private Marker mMarker;
    private CircleOptions locationCircle;
    private MarkerOptions locationMarker;
    private SeekBar seekBarSetRadius;
    Handler handler;
    Runnable runnable;

    private final static int CIRCLE_RADIUS = 150;
    private final static int SEEKBAR_MAX = 750;
    private final static int SECOND = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_select);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapview_location_select);
        mapFragment.getMapAsync(this);
        seekBarSetRadius = findViewById(R.id.seekBar_location_radius);
        setCircleRadiusWithSeekBar();
        checkDistance();
    }


    private void setCircleRadiusWithSeekBar() {
        seekBarSetRadius.setMax(SEEKBAR_MAX);
        seekBarSetRadius.setProgress(CIRCLE_RADIUS);
        seekBarSetRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCircle.setRadius(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    double longitude;
    double latitude;
    float[] distance = new float[2];

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        mMap.setMyLocationEnabled(true);
        LatLng myLocation = new LatLng(latitude, longitude);
        locationCircle = new CircleOptions().center(myLocation)
                .radius(CIRCLE_RADIUS);
        locationMarker = new MarkerOptions()
                .draggable(true)
                .position(myLocation);

        mCircle = mMap.addCircle(locationCircle);
        mMarker = mMap.addMarker(locationMarker);
        mMap.setOnMarkerDragListener(this);
//     LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        mCircle.setCenter(marker.getPosition());
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    private void distanceMessage() {
        if (isOutside()) {

            Toast.makeText(getBaseContext(), "Outside, distance from center: " + distance[0] +
                    " Distance to the circle: " + (int)(distance[0] - mCircle.getRadius()) + " radius: " +
                    mCircle.getRadius(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), "Inside, distance from center: " + distance[0] +
                    " radius: " + mCircle.getRadius(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isOutside() {
        Location.distanceBetween(latitude, longitude,
                mCircle.getCenter().latitude, mCircle.getCenter().longitude, distance);
        return (distance[0] > mCircle.getRadius());
    }

    private void checkDistance() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                distanceMessage();
                handler.postDelayed(runnable, 5 * SECOND);
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(runnable);
    }

}