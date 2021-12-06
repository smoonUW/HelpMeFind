package com.example.helpmefind;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.List;

public class Resource implements Serializable {
    private String name;
    private String type;
    private double latitude;
    private double longitude;
    private String address;
    private List<String> comments;

    public Resource(String name, String type, double latitude, double longitude, String address, List<String> comments){
        this.name = name;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.comments = comments;
    }
    public Resource() {
        // necessary to have no-arg constructor for firestore query
    }

    protected Resource(Parcel in) {
        name = in.readString();
        type = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        address = in.readString();
    }


    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String toString(){
        return name+"\n"+type+"\n"+latitude+", "+longitude+"\n"+address;
    }


    public LatLng getLatLon(){
        return new LatLng(latitude, longitude);
    }


    public List<String> getComments() { return comments; }
}
