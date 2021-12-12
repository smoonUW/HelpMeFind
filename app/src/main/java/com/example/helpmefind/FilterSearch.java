package com.example.helpmefind;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;

public class FilterSearch extends AppCompatActivity {

    // LocationClient Reference
    private FusedLocationProviderClient myFusedLPClient;
    // Identify a Reference for this particular permission
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 8675309;
    // user selected search radius
    static double selectedRadius = -1;
    // user's current location
    static LatLng myLatLng;

    private final double MILES_TO_LAT = 0.01449275;
    private final double EARTH_RADIUS = 3959;
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_search);

        Spinner radiusMenu = findViewById(R.id.radiusDropdown);

        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(this, R.array.radii, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        radiusMenu.setAdapter(adapter);

        myFusedLPClient = LocationServices.getFusedLocationProviderClient(this);
        findMyLocation();

        radiusMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                // dumbest line of code I've ever written
                selectedRadius = Double.parseDouble(parent.getItemAtPosition(position).toString().split(" ")[0]);

                Log.i("selected radius", String.valueOf(selectedRadius));
            }

            public void onNothingSelected(AdapterView<?> parent)
            {
                // Do nothing?
            }
        });
    }

    public ArrayList<String> saveSelection() {
        CheckBox microwave = (CheckBox) findViewById(R.id.microwaveCheckBox);
        CheckBox bathroom = (CheckBox) findViewById(R.id.bathroomCheckBox);
        CheckBox fridge = (CheckBox) findViewById(R.id.refrigeratorCheckBox);
        CheckBox lactation = (CheckBox) findViewById(R.id.lactationCheckBox);

        ArrayList<String> types = new ArrayList<String>();
        if (microwave.isChecked()) {
            types.add("microwave");
        }
        if (bathroom.isChecked()) {
            types.add("bathroom");
        }
        if (fridge.isChecked()) {
            types.add("fridge");
        }
        if (lactation.isChecked()) {
            types.add("lactation");
        }

        return types;
    }

    public void goToMapView(View view) {
        ArrayList<String> types = saveSelection();

        if (selectedRadius == -1 || types.size() == 0) {
            Toast.makeText(this, "Please select at least one resource and a radius value", Toast.LENGTH_LONG).show();
        } else if (myLatLng == null) {
            Toast.makeText(this, "Please allow location access", Toast.LENGTH_LONG).show();
        } else {
            readDataToView(types, 1);
        }
    }

    private void findMyLocation() {
        // Check if permission granted
        int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        // If not, ask for it
        if (permission == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        // if permission granted, get current location
        else {
            myFusedLPClient.getLastLocation()
                    .addOnCompleteListener(this, task-> {
                        Location myLastKnownLocation = task.getResult();
                        if (task.isSuccessful() && myLastKnownLocation != null){
                            myLatLng = new LatLng(myLastKnownLocation.getLatitude(), myLastKnownLocation.getLongitude());
                        }
                    });
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findMyLocation();
            }
        }
    }

    private void readDataToView(ArrayList<String> types, int targetActivity) {
        CollectionReference res = db.collection("resources");
        double minLat = myLatLng.latitude - MILES_TO_LAT*selectedRadius;
        double maxLat = myLatLng.latitude + MILES_TO_LAT*selectedRadius;
        double[] longBounds = calcLongBounds(selectedRadius);
        Query latQ = res.whereLessThan("latitude", maxLat).whereGreaterThan("latitude", minLat);
        ArrayList<Resource> resources = new ArrayList<Resource>();
        for (String type : types) {
            latQ.whereEqualTo("type", type).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Resource r = document.toObject(Resource.class);
                            if (longBounds[0] <= r.getLongitude() && r.getLongitude() <= longBounds[1]) {
                                resources.add(r);
                                Log.i("ADDED RESOURCE:", r.getName());
                            }
                        }
                        Intent intent;
                        intent = new Intent(FilterSearch.this, MapView.class);
                        intent.putExtra("radius", selectedRadius);
                        intent.putExtra("list", (Serializable) resources);
                        startActivity(intent);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }

    private double[] calcLongBounds(double radius) {
        double[] bounds = new double[2];
        double rootTerm = Math.sqrt(Math.sin(radius/(2*EARTH_RADIUS))/Math.pow(Math.cos(myLatLng.latitude),2));
        bounds[0] = myLatLng.longitude + 2*Math.asin((-1)*rootTerm);
        bounds[1] = myLatLng.longitude + 2*Math.asin(rootTerm);
        return bounds;
    }
}