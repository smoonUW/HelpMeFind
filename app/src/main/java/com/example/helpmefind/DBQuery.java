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
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;
    private FusedLocationProviderClient flpc;
    private LatLng currLoc;
    private final double MILES_TO_LAT = 0.01449275;
    private final double EARTH_RADIUS = 3959;

    public DBQuery(LatLng currLoc) {
        this.currLoc = currLoc;
    }

    private ArrayList<Resource> read(int radius, String[] types) {
        CollectionReference res = db.collection("resources");
        double minLat = currLoc.latitude - MILES_TO_LAT*radius;
        double maxLat = currLoc.latitude + MILES_TO_LAT*radius;
        double[] longBounds = calcLongBounds(radius);
        Query distQ = res.whereLessThan("latitude", maxLat).whereGreaterThan("latitude", minLat).whereLessThan("longitude",longBounds[1]).whereGreaterThan("longitude", longBounds[0]);
        ArrayList<Resource> resources = new ArrayList<Resource>();
        for (String type : types) {
            distQ.whereEqualTo("type", type).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            resources.add(document.toObject(Resource.class));
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }
        return resources;
    }

    private double[] calcLongBounds(int radius) {
        double[] bounds = new double[2];
        double rootTerm = Math.sqrt(Math.sin(radius/(2*EARTH_RADIUS))/Math.pow(Math.cos(currLoc.latitude),2));
        bounds[0] = currLoc.longitude + 2*Math.asin((-1)*rootTerm);
        bounds[1] = currLoc.longitude + 2*Math.asin(rootTerm);
        Log.i("LOWER LONG BOUND:", String.valueOf(bounds[0]));
        Log.i("UPPER LONG BOUND:", String.valueOf(bounds[1]));
        return bounds;
    }
}
