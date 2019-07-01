package com.example.candogan.todoexercise;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ToDoAppService extends Service {
    public ToDoAppService() {
    }

    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    float[] distance = new float[2];

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");

        return null;
    }

    @Override
    public void onCreate() {
        initFireBase();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            checkDistance();
        }
        sendLastLocToFirebase();
    }

    double selectedLat;
    double selectedLng;
    double myLat;
    double myLng;
    int radius;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkDistance() {

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("MarkerInfo").child("LastSelectedLoc").child("latitude").getValue(double.class) != null &&
                        dataSnapshot.child("MarkerInfo").child("LastSelectedLoc").child("longitude").getValue(double.class) != null &&
                        dataSnapshot.child("MarkerInfo").child("CircleInfo").getValue(double.class) != null) {
                    selectedLat = dataSnapshot.child("MarkerInfo").child("LastSelectedLoc").child("latitude").getValue(double.class);
                    selectedLng = dataSnapshot.child("MarkerInfo").child("LastSelectedLoc").child("longitude").getValue(double.class);
                    myLat = dataSnapshot.child("MarkerInfo").child("MyLastKnownLocation").child("latitude").getValue(double.class);
                    myLng = dataSnapshot.child("MarkerInfo").child("MyLastKnownLocation").child("longitude").getValue(double.class);
                    radius = dataSnapshot.child("MarkerInfo").child("CircleInfo").getValue(int.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Location.distanceBetween(myLat, myLng,
                selectedLat, selectedLng, distance);
        if (!(distance[0] > radius)) {
            String CHANNEL_ID = "TodoApp";
            String CHANNEL_NAME = "ArriveInfo";
            int NOTIFICATION_ID = 55;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableVibration(true);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("To Do App")
                    .setContentText("Congratulations you arrived the area!!")
                    .setSmallIcon(R.drawable.ic_add)
                    .setAutoCancel(true)
                    .build();
            manager.notify(NOTIFICATION_ID, notification);
        }

    }

    Handler handler;
    Runnable runnable;
    LocationManager lm;
    Location location;
    double latitude;
    double longitude;

    private void sendLastLocToFirebase() {

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

         handler =new Handler(Looper.getMainLooper());
         runnable=new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(runnable,5000);
                latitude=location.getLatitude();
                longitude=location.getLongitude();
                myRef.child("MarkerInfo").child("MyLastKnownLocation").child("latitude").setValue(latitude);
                myRef.child("MarkerInfo").child("MyLastKnownLocation").child("longitude").setValue(longitude);
            }
        };
        handler.post(runnable);


      /*  handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                distanceMessage();
                handler.postDelayed(runnable, 5 * SECOND);
            }
        };
        handler.post(runnable);*/

    }
    private void initFireBase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


}
