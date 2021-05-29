package com.ytuce.osmroutetracking.api;

import com.google.gson.annotations.SerializedName;
import com.ytuce.osmroutetracking.TrackingItem;

public class Results {

    @SerializedName("clientid")
    private int clientId;

    @SerializedName("trackingid")
    private int trackingId;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("time")
    private long time;

    public Results(TrackingItem item) {
        clientId = item.getClientId();
        trackingId = item.getTrackingId();
        latitude = item.getLatitude();
        longitude = item.getLongitude();
        time = item.getTime();
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(int trackingId) {
        this.trackingId = trackingId;
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
                "clientid=" + clientId +
                ", trackingid=" + trackingId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", time=" + time +
                '}';
    }
}
