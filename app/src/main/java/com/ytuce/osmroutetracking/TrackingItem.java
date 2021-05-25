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
