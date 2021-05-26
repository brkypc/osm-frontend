package com.ytuce.osmroutetracking.api;

import com.google.gson.annotations.SerializedName;
import com.ytuce.osmroutetracking.TrackingItem;

public class Results {

    @SerializedName("clientid")
    private int clientid;

    @SerializedName("trackingid")
    private int trackingid;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("time")
    private long time;

    public Results(TrackingItem item) {
        clientid = item.getClientId();
        trackingid = item.getTrackingId();
        latitude = item.getLatitude();
        longitude = item.getLongitude();
        time = item.getTime();
    }

    public int getClientid() {
        return clientid;
    }

    public void setClientid(int clientid) {
        this.clientid = clientid;
    }

    public int getTrackingid() {
        return trackingid;
    }

    public void setTrackingid(int trackingid) {
        this.trackingid = trackingid;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Results{" +
                "clientid=" + clientid +
                ", trackingid=" + trackingid +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", time=" + time +
                '}';
    }
}
