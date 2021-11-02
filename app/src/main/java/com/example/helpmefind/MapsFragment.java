package com.example.helpmefind;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsFragment extends Fragment {

    // Map Reference
    private GoogleMap myMap;
    // LocationClient Reference
    private FusedLocationProviderClient myFusedLPClient;
    // Identify a Reference for this particular permission
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 8675309;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            myMap = googleMap;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        setupLocationClient();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {

            mapFragment.getMapAsync(callback);
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

                            myMap.addMarker(new MarkerOptions()
                                    .position(myLatLng));

                            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,17));
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
}