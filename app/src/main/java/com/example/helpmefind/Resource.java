package com.example.helpmefind;

import android.location.Address;

public class Resource {
    private String name;
    private String type;
    private double latitude;
    private double longitude;
    private String address;

    public Resource(String name, String type, double latitude, double longitude, String address){
        this.name = name;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
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
}
