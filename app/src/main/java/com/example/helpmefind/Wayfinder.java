package com.example.helpmefind;
// The following links provided a lot of help and examples for this code:
// https://stackoverflow.com/questions/4308262/calculate-compass-bearing-heading-to-location-in-android
// https://www.techrepublic.com/article/pro-tip-create-your-own-magnetic-compass-using-androids-internal-sensors/
// https://stackoverflow.com/questions/7978618/rotating-an-imageview-like-a-compass-with-the-north-pole-set-elsewhere

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
    private ImageView pointer;
    private TextView resourceName;
    private TextView distance;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] orientation = new float[3];
    private float currentDegree = 0f;
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
        // "get" the bottom navigator
        bottomNavigationView = findViewById(R.id.bottomnav);
        bottomNavigationView.setOnItemSelectedListener(bottomnavFunction);
        // "get" the sent resource from the intent and convert/populate what is needed
        Intent intent = getIntent();
        selectedResource = (Resource) intent.getSerializableExtra("selectedResource");
        distance = (TextView) findViewById(R.id.distance);
        resourceName = (TextView) findViewById(R.id.resourceName);
        resourceName.setText(selectedResource.getName());
        // set up sensor manager and sensors
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        // "get" the pointer image
        pointer = (ImageView) findViewById(R.id.pointer);
        // Acquire the destination location from the intent info and set it up
        destLoc = new Location("service provider");
        destLoc.setLatitude(selectedResource.getLatitude());
        destLoc.setLongitude(selectedResource.getLongitude());
        // Setup up location manager and listener
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // see further down for update location view
                updateLocationInfo(location);
                // This updates the distance from current location to destination
                double meters = location.distanceTo(destLoc);
                int feet = (int) (meters*3.280839895);
                distance.setText(String.valueOf(feet) + " feet");
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
                // if permissions are set, get the last known location
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                // For some reason, location was always returning null on start up for our test device,
                // even if Google Maps was currently running.  This is a rough fix so that user location
                // is populated with SOMETHING until it is updated.
                userLoc = location;
                if (userLoc == null) {
                    userLoc = new Location("service provider");
                    userLoc.setLatitude(43.07216);  // Union South Lat
                    userLoc.setLongitude(-89.40765);  // Union South Long
                }
                // A test statement left here for debugging
                //Log.e("Error: Location is ", String.valueOf(userLoc));
                // This gets the bearing to the destination, correcting the value to
                // work properly with the compass direction
                bearTo=userLoc.bearingTo(destLoc);
                if (bearTo < 0 || bearTo > 360) {
                    if (bearTo < 0) bearTo += 360;
                    else bearTo -= 360;
                }
                // It it is somehow STILL null, update the location.
                if (location != null) {
                    updateLocationInfo(location);
                }
            }
        }
    }
    // register/unregister listeners as appropriate for onResume and onPause
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, magnetometer);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // The first thing to do on sensor changing is update the bearing
        bearTo=userLoc.bearingTo(destLoc);
        if (bearTo < 0 || bearTo > 360) {
            if (bearTo < 0) bearTo += 360;
            else bearTo -= 360;
        }
        // Produce a geofield object using the current user position, (for image update below)
        GeomagneticField geoField = new GeomagneticField( Double.valueOf( userLoc.getLatitude() ).floatValue(), Double
                .valueOf( userLoc.getLongitude() ).floatValue(),
                Double.valueOf( userLoc.getAltitude() ).floatValue(),
                System.currentTimeMillis() );
        // Update flags based on accel/mag
        if (event.sensor == accelerometer) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
            lastAccelerometerSet = true;
        } else if (event.sensor == magnetometer) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
            lastMagnetometerSet = true;
        }
        if (lastAccelerometerSet && lastMagnetometerSet) {
            // if both flags are set, get/produce a rotation matrix...
            SensorManager.getRotationMatrix(mR, null, lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(mR, orientation);
            // we want the azimuth, (rotation from north, basically)
            float azimuthInRadians = orientation[0];
            float azimuthInDegrees = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            azimuthInDegrees -= geoField.getDeclination();  // magnetic north to true north;
            // "currentdegree" will help us point to our resource when we rotate the image
            currentDegree = bearTo - azimuthInDegrees;
            if (currentDegree < 0) {
                currentDegree += 360;
            }
            // rotate the image based on currentdegree and the "opposite" of the azimuth
            RotateAnimation ra = new RotateAnimation(
                    currentDegree,
                    -azimuthInDegrees,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(250);

            ra.setFillAfter(true);

            pointer.startAnimation(ra);
            currentDegree = -azimuthInDegrees;
        }
    }
    // needs to be here for sensorEventListener interface
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    /// Location functions
    ///////

    // This starts our listener
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
    // This updates our user location and bearing
    public void updateLocationInfo(Location location) {
        userLoc = location;
        bearTo=userLoc.bearingTo(destLoc);
        if (bearTo < 0 || bearTo > 360) {
            if (bearTo < 0) bearTo += 360;
            else bearTo -= 360;
        }
        // debug statement left in for future testing
        //Log.i("LocationInfo", location.toString());
    }
    // This uses the bottom navigation view similar to the other activities, (nothing new here)
    private NavigationBarView.OnItemSelectedListener bottomnavFunction = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.back) {
                Intent intent = new Intent(Wayfinder.this, FilterSearch.class);
                startActivity(intent);
                return true;
            }
            // This makes just moves our resource object "down the line" to the next activity
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