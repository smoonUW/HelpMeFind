package com.example.helpmefind;



import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DBQuery {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LatLng currLoc;
    private final double MILES_TO_LAT = 0.01449275;
    private final double EARTH_RADIUS = 3959;

    public DBQuery(LatLng currLoc) {
        this.currLoc = currLoc;
    }

    public ArrayList<Resource> read(double radius, ArrayList<String> types) {
        CollectionReference res = db.collection("resources");
        double minLat = currLoc.latitude - MILES_TO_LAT*radius;
        double maxLat = currLoc.latitude + MILES_TO_LAT*radius;
        double[] longBounds = calcLongBounds(radius);
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
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }
        return resources;
    }

    private double[] calcLongBounds(double radius) {
        double[] bounds = new double[2];
        double rootTerm = Math.sqrt(Math.sin(radius/(2*EARTH_RADIUS))/Math.pow(Math.cos(currLoc.latitude),2));
        bounds[0] = currLoc.longitude + 2*Math.asin((-1)*rootTerm);
        bounds[1] = currLoc.longitude + 2*Math.asin(rootTerm);
        Log.i("LOWER LONG BOUND:", String.valueOf(bounds[0]));
        Log.i("UPPER LONG BOUND:", String.valueOf(bounds[1]));
        return bounds;
    }
}
