package com.ytuce.osmroutetracking.map;

import android.util.Log;

import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;

public class MapClickListener implements MapEventsReceiver {

    private final static String TAG = "MapClickListener";

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        Log.e(TAG, "lat = " + p.getLatitude() + " long = " + p.getLongitude());
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }
}
