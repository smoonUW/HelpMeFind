package com.example.helpmefind;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    // Map Reference
    private GoogleMap myMap;
    // LocationClient Reference
    private FusedLocationProviderClient myFusedLPClient;
    // Identify a Reference for this particular permission
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 8675309;

    private ArrayList<Resource> resources;

    private Marker selectedMarker = null;

    MapView mapViewActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        //MapView mapViewActivity = (MapView) getActivity();
        mapViewActivity = (MapView) getActivity();

        resources = mapViewActivity.getResourceArrayList();

        if (resources != null){
            for (int i = 0; i < resources.size(); i++){
                Log.i("DisplayMyStuff", resources.get(i).toString() );
            }
        }

        setupLocationClient();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {

            mapFragment.getMapAsync(this);
            displayMyLocation();
        }
    }

    private void setupLocationClient(){
        // LocationServices API:
        // https://developers.google.com/android/reference/com/google/android/gms/location/LocationServices
        myFusedLPClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    private void displayMyLocation() {
        // Check if permission granted
        int permission = ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        // If not, ask for it
        if (permission == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        // if permission granted, display market at current location
        else {
            myFusedLPClient.getLastLocation()
                    .addOnCompleteListener(getActivity(), task-> {
                        Location myLastKnownLocation = task.getResult();
                        if (task.isSuccessful() && myLastKnownLocation != null){

                            LatLng myLatLng = new LatLng(myLastKnownLocation.getLatitude(), myLastKnownLocation.getLongitude());

                            //setUpClusterer(myLatLng, 17);
                            Double selectedRadius = mapViewActivity.getSelectedRadius();
                            float zoom = 1;
                            if (selectedRadius <= 1) {
                                zoom = 15;
                            } else if (selectedRadius <= 5){
                                zoom = 12;
                            } else if (selectedRadius <= 10){
                                zoom = 10;
                            }
                            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,zoom));
                            setupResourceMarkers();
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
                displayMyLocation();
            }
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        // This could be interesting if we wanted to do anything with it.
        //https://developers.google.com/maps/documentation/android-sdk/events#indoor_map_events

        Log.i("MARKER", "Clicked");
        // If another marker had been selected before this marker was clicked
        if (selectedMarker != null){
            // We are going to replace the previous selected marker with a new marker containing
            // same information, just with a different icon (the non-selected icon color)
            IconGenerator oldIconFactory = new IconGenerator(getActivity().getApplicationContext());
            oldIconFactory.setStyle(IconGenerator.STYLE_RED);
            String title = selectedMarker.getTitle();
            String snippet = selectedMarker.getSnippet();
            LatLng position = selectedMarker.getPosition();
            // Remove the old marker
            selectedMarker.remove();

            // What's going on here is a bit hackey.
            // As of now, we are storing the entire toString value of a resource as a marker
            // snippet. Since we aren't able to grab Resource.getType() from the marker itself,
            // we use its snippet as a 'key' to find the matching resource from the list of all
            // resources.
            String type ="Type Not Found";

            for (Resource r : resources){
                if (r.toString().equals(snippet)) {
                    type = r.getType();
                    break;
                }
            }

            // Once we've found the type, we will create a new marker whose icon has the type embedded
            // as text inside it. (This is done in the adddIcon function.)
            Marker oldMarker = addIcon(oldIconFactory, type,position);
            oldMarker.setTitle(title);
            oldMarker.setSnippet(snippet);
            // TODO: Not sure why this line of code was here, so i'm commenting it out for now.
            //selectedMarker = null;
        }
        selectedMarker = marker;
        IconGenerator iconFactory = new IconGenerator(getActivity().getApplicationContext());
        iconFactory.setStyle(IconGenerator.STYLE_GREEN);
        String title = selectedMarker.getTitle();
        String snippet = selectedMarker.getSnippet();
        LatLng position = selectedMarker.getPosition();
        selectedMarker.remove();
        String type = "Type Not Found";
        for (Resource r : resources){
            if (r.toString().equals(snippet)) {
                type = r.getType();
                mapViewActivity.setSelectedResource(r);
                break;
            }
        }
        selectedMarker = addIcon(iconFactory, type,position);
        selectedMarker.setTitle(title);
        selectedMarker.setSnippet(snippet);
        selectedMarker.showInfoWindow();
        return false;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        myMap.setOnMarkerClickListener(this);
        myMap.setOnMapClickListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if (selectedMarker != null){
            IconGenerator iconFactory = new IconGenerator(getActivity().getApplicationContext());
            iconFactory.setStyle(IconGenerator.STYLE_RED);
            String title = selectedMarker.getTitle();
            String snippet = selectedMarker.getSnippet();
            LatLng position = selectedMarker.getPosition();
            selectedMarker.remove();
            String type = "Type Not Found";

            for (Resource r : resources){
                if (r.toString().equals(snippet)) {
                    type = r.getType();
                    break;
                }
            }

            Marker marker = addIcon(iconFactory, type,position);
            marker.setTitle(title);
            marker.setSnippet(snippet);
            selectedMarker = null;
            mapViewActivity.setSelectedResource(null);
        }
    }

    private void setupResourceMarkers() {
        // So long as an intent populated our resources list,
        if (resources != null){

            IconGenerator iconFactory = new IconGenerator(getActivity().getApplicationContext());
            iconFactory.setStyle(IconGenerator.STYLE_RED);

            // Iterate through the resources received
            for (int i = 0; i < resources.size(); i++){
                LatLng myLatLng = resources.get(i).getLatLon();
                for (int j = 0; j < i; j++){
                    // If more than one resource has the same lat and lng, then offset them by a
                    // small random amount.
                    if (myLatLng.equals(resources.get(j). getLatLon())){
                        myLatLng = new LatLng(myLatLng.latitude+ (Math.random()-.5)/1500,
                                myLatLng.longitude+(Math.random()-.5)/1500);
                        break;
                    }
                }
                Resource r = resources.get(i);

                String text = "";
                switch(r.getType()) {
                    case "microwave" :
                        text = "microwave";
                        break;

                    case "lactation" :
                        text = "lactation";
                        break;
                    case "restroom" :
                        text = "restroom";
                        break;

                    case "refrigerator" :
                        text = "refrigerator";
                        break;
                }

                Marker resourceMarker = addIcon(iconFactory, text, myLatLng);
                resourceMarker.setTitle(r.getName());
                resourceMarker.setSnippet(r.toString());

            }
            mapViewActivity.setSelectedResource(null);
        }
    }


    // Function Directly Taken From:
    // https://www.geeksforgeeks.org/how-to-add-custom-marker-to-google-maps-in-android/
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }



    //https://developers.google.com/maps/documentation/android-sdk/utility/marker-clustering#:~:text=%20Here%20is%20a%20summary%20of%20the%20steps,since%20ClusterManager%20implements%20%20the%20listener.%20More%20
    public class MyMarker implements ClusterItem {
        private final LatLng position;
        private final String title;
        private final String snippet;

        public MyMarker(double lat, double lng, String title, String snippet) {
            position = new LatLng(lat, lng);
            this.title = title;
            this.snippet = snippet;
        }

        @Override
        public LatLng getPosition() {
            return position;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getSnippet() {
            return snippet;
        }
    }

    // Declare a variable for the cluster manager.
    private ClusterManager<MyMarker> clusterManager;

    private void setUpClusterer(LatLng currLocation, int zoom) {
        // Position the map.
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLocation, zoom));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = new ClusterManager<MyMarker>(getActivity().getApplicationContext(), myMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        myMap.setOnCameraIdleListener(clusterManager);
        myMap.setOnMarkerClickListener(clusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {

        // So long as an intent populated our resources list,
        if (resources != null) {
            // Iterate through the resources received
            for (int i = 0; i < resources.size(); i++) {
                LatLng myLatLng = resources.get(i).getLatLon();
                for (int j = 0; j < i; j++) {
                    if (myLatLng.equals(resources.get(j).getLatLon())) {
                        myLatLng = new LatLng(myLatLng.latitude + (Math.random() - .5) / 1500,
                                myLatLng.longitude + (Math.random() - .5) / 1500);
                        break;
                    }
                }
                MyMarker resourceMarker = new MyMarker(myLatLng.latitude, myLatLng.longitude, "Title " + resources.get(i).getName(), "Description " + resources.get(i).toString());
                clusterManager.addItem(resourceMarker);
            }
        }
    }

    private Marker addIcon(IconGenerator iconFactory, CharSequence text, LatLng position) {

        Bitmap myIcon  = iconFactory.makeIcon(text);

        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(myIcon)).
                position(position).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        Marker marker = myMap.addMarker(markerOptions);
        return marker;
    }
}