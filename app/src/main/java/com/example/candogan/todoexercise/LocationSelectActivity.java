package com.example.candogan.todoexercise;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LocationSelectActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener {

    private MapView mapView;
    private GoogleMap mMap;
    private Circle mCircle;
    private Marker mMarker;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;

    private final static int CIRCLE_RADIUS = 150;
    private final static int SEEKBAR_MAX = 750;
    private final static int SECOND = 1000;
    private final static String APP_PACKAGE = "com.example.candogan.todoexercise";
    private final static String APP_CHANEL_ID = APP_PACKAGE + ".APP_CHANNEL";
    private SeekBar seekBarSetRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_select);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapview_location_select);
        mapFragment.getMapAsync(this);
        seekBarSetRadius = findViewById(R.id.seekBar_location_radius);
        initFireBase();
        getDataFromFireBase();
        listenInsideOutside();
    }


    public void listenInsideOutside() {
        myRef.child("MarkerInfo").child("InsideOutside").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == "Inside"
                &&android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    pushNotification("Congratulations you arrived the area!!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void pushNotification(String message){
        String CHANNEL_ID = "TodoApp";
        String CHANNEL_NAME = "ArriveInfo";
        int NOTIFICATION_ID = 52;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableVibration(true);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("To Do App")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_add)
                .setAutoCancel(true)
                .build();
        manager.notify(NOTIFICATION_ID, notification);
    }
    int radius;

    private void getDataFromFireBase() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("MarkerInfo").child("LastSelectedLoc").child("latitude").getValue(double.class) != null &&
                        dataSnapshot.child("MarkerInfo").child("LastSelectedLoc").child("longitude").getValue(double.class) != null &&
                        dataSnapshot.child("MarkerInfo").child("CircleInfo").getValue(double.class) != null) {

                    double lat = dataSnapshot.child("MarkerInfo").child("LastSelectedLoc").child("latitude").getValue(double.class);
                    double lng = dataSnapshot.child("MarkerInfo").child("LastSelectedLoc").child("longitude").getValue(double.class);
                    LatLng latLng = new LatLng(lat, lng);
                    loadLastCirclePos(latLng);
                    radius = dataSnapshot.child("MarkerInfo").child("CircleInfo").getValue(int.class);
                    mCircle.setRadius(radius);
                    setCircleRadiusWithSeekBar();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LocationSelectActivity.this, "The read failed: "
                        + databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initFireBase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
    }

    private void setCircleRadiusWithSeekBar() {
        seekBarSetRadius.setMax(SEEKBAR_MAX);
        seekBarSetRadius.setProgress(radius);
        setSeekBarVisibility();

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
                myRef.child("MarkerInfo").child("CircleInfo").setValue(mCircle.getRadius());
            }
        });

    }

    double longitude;
    double latitude;
    float[] distance = new float[2];
    LocationManager lm;
    Location location;
    LatLng myLocation;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        } else {
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            try {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            } catch (Exception e) {
                e.printStackTrace();
            }
            myLocation = new LatLng(latitude, longitude);

           /* locationCircle = new CircleOptions().center(myLocation)
                    .radius(CIRCLE_RADIUS);
            locationMarker = new MarkerOptions()
                    .draggable(true)
                    .position(myLocation);

            mCircle = mMap.addCircle(locationCircle);
            mMarker = mMap.addMarker(locationMarker);*/
            mMap.setOnMarkerDragListener(this);
            mMap.setOnMapLongClickListener(this);
            mMap.setMyLocationEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12));
//     LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                   latitude=location.getLatitude();
                   longitude=location.getLongitude();
                }
            });
        }
    }


    Handler handler;
    Runnable runnable;

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        if (handler != null)
            handler.removeCallbacks(runnable);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        mCircle.setCenter(marker.getPosition());

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        myRef.child("MarkerInfo").child("LastSelectedLoc").setValue(marker.getPosition());
        checkDistance();
    }

    private void distanceMessage() {
        if (isOutside()) {
            Toast.makeText(getBaseContext(), "Outside, distance from center: " + distance[0] +
                    " Distance to the circle: " + (int) (distance[0] - mCircle.getRadius()) + " radius: " +
                    mCircle.getRadius(), Toast.LENGTH_LONG).show();
            myRef.child("MarkerInfo").child("InsideOutside").setValue("Outside");
        } else {
            Toast.makeText(getBaseContext(), "Inside, distance from center: " + distance[0] +
                    " radius: " + mCircle.getRadius(), Toast.LENGTH_LONG).show();
            myRef.child("MarkerInfo").child("InsideOutside").setValue("Inside");
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

    private CircleOptions locationCircle;
    private MarkerOptions locationMarker;

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();
        //setSeekBarVisibility();

        locationCircle = new CircleOptions().center(latLng)
                .radius(radius);
        locationMarker = new MarkerOptions()
                .draggable(true)
                .position(latLng);
        mCircle = mMap.addCircle(locationCircle);
        mMarker = mMap.addMarker(locationMarker);
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        myRef.child("MarkerInfo").child("LastSelectedLoc").setValue(latLng);
        checkDistance();

    }

    private void loadLastCirclePos(LatLng latLng) {
        mMap.clear();
        //setSeekBarVisibility();
        locationCircle = new CircleOptions().center(latLng)
                .radius(radius);
        locationMarker = new MarkerOptions()
                .draggable(true)
                .position(latLng);
        mCircle = mMap.addCircle(locationCircle);
        mMarker = mMap.addMarker(locationMarker);
    }

    private boolean isCircleCreated() {
        return (mCircle != null);
    }

    private void setSeekBarVisibility() {
        if (isCircleCreated())
            seekBarSetRadius.setVisibility(View.VISIBLE);
        else
            seekBarSetRadius.setVisibility(View.GONE);

    }
}