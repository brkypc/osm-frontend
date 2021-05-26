package com.ytuce.osmroutetracking;

import android.location.Location;

public class TrackingItem {

    private double latitude;
    private double longitude;
    private long time;
    private int clientId;
    private int trackingId;

    public TrackingItem(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        time = location.getTime();
        clientId = -1;
        trackingId = -1;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
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

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "TrackingItem{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", time=" + time +
                ", clientId=" + clientId +
                ", trackingId=" + trackingId +
                '}';
    }
}
