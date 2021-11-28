package com.example.helpmefind;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * How to pass an object through intents
 * http://aryo.lecture.ub.ac.id/android-passing-arraylist-of-object-within-an-intent/#:~:text=But%20Android%20has%20no%20custom%20object%20data%20type,rebuild%20the%20flatten%20object%20into%20the%20original%20object%E2%80%9D
 **/
public class Resource implements Parcelable {
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

    protected Resource(Parcel in) {
        name = in.readString();
        type = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        address = in.readString();
    }

    public static final Creator<Resource> CREATOR = new Creator<Resource>() {
        @Override
        public Resource createFromParcel(Parcel in) {
            return new Resource(in);
        }

        @Override
        public Resource[] newArray(int size) {
            return new Resource[size];
        }
    };

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

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return this.hashCode();
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // String name;
        dest.writeString(name);
        // String type;
        dest.writeString(type);
        // double latitude;
        dest.writeDouble(latitude);
        // double longitude;
        dest.writeDouble(longitude);
        // String address;
        dest.writeString(address);
    }
}
