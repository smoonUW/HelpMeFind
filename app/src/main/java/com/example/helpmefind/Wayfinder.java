package com.example.helpmefind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationBarView;

import java.io.Serializable;

public class Wayfinder extends AppCompatActivity implements SensorEventListener {

    NavigationBarView bottomNavigationView;
    private ImageView mPointer;
    private TextView resourceName;
    private TextView distance;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;
    ////// Location Stuff Below...
    LocationManager locationManager;
    LocationListener locationListener;
    Location destLoc;
    Location userLoc;
    float bearTo;
    float heading;
    Resource selectedResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wayfinder);
        
        bottomNavigationView = findViewById(R.id.bottomnav);
        bottomNavigationView.setOnItemSelectedListener(bottomnavFunction);

        Intent intent = getIntent();
        selectedResource = (Resource) intent.getSerializableExtra("selectedResource");
        distance = (TextView) findViewById(R.id.distance);
        resourceName = (TextView) findViewById(R.id.resourceName);
        resourceName.setText(selectedResource.getName());
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mPointer = (ImageView) findViewById(R.id.pointer);

        destLoc = new Location("service provider");
        destLoc.setLatitude(selectedResource.getLatitude());
        destLoc.setLongitude(selectedResource.getLongitude());
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateLocationInfo(location);
                distance.setText(String.valueOf(location.distanceTo(destLoc)) + " meters");
            }
        };
        if (Build.VERSION.SDK_INT < 23) {
            startListening();
        }
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                userLoc = location;
                if (userLoc == null) {
                    userLoc = new Location("service provider");
                    userLoc.setLatitude(43.07216);  // Union South Lat
                    userLoc.setLongitude(-89.40765);  // Union South Long
                }
                Log.e("Error: Location is ", String.valueOf(userLoc));
                bearTo=userLoc.bearingTo(destLoc);
                if (bearTo < 0 || bearTo > 360) {
                    if (bearTo < 0) bearTo += 360;
                    else bearTo -= 360;
                }
                if (location != null) {
                    updateLocationInfo(location);

                }
            }
        }
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        bearTo=userLoc.bearingTo(destLoc);
        if (bearTo < 0 || bearTo > 360) {
            if (bearTo < 0) bearTo += 360;
            else bearTo -= 360;
        }
        GeomagneticField geoField = new GeomagneticField( Double.valueOf( userLoc.getLatitude() ).floatValue(), Double
                .valueOf( userLoc.getLongitude() ).floatValue(),
                Double.valueOf( userLoc.getAltitude() ).floatValue(),
                System.currentTimeMillis() );

        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegrees = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            azimuthInDegrees -= geoField.getDeclination();  // magnetic north to true north;
            float resourceDirection = bearTo - azimuthInDegrees;
            if (resourceDirection < 0) {
                resourceDirection += 360;
            }
            mCurrentDegree = resourceDirection;
            RotateAnimation ra = new RotateAnimation(
                    mCurrentDegree,
                    -azimuthInDegrees,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(250);

            ra.setFillAfter(true);

            mPointer.startAnimation(ra);
            mCurrentDegree = -azimuthInDegrees;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    /// Location functions
    public void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        }
    }

    public void updateLocationInfo(Location location) {
        userLoc = location;
        bearTo=userLoc.bearingTo(destLoc);
        if (bearTo < 0 || bearTo > 360) {
            if (bearTo < 0) bearTo += 360;
            else bearTo -= 360;
        }
        Log.i("LocationInfo", location.toString());
    }
    private NavigationBarView.OnItemSelectedListener bottomnavFunction = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.back) {
                Intent intent = new Intent(Wayfinder.this, FilterSearch.class);
                startActivity(intent);
                return true;
            }
            else if (item.getItemId() == R.id.arrived) {
                Intent intent = new Intent(Wayfinder.this, Feedback.class);
                intent.putExtra("selectedResource", (Serializable) selectedResource);
                startActivity(intent);
                return true;
            }
            return true;
        }
    };
}