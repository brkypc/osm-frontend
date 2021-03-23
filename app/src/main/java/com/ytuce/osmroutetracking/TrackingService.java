package com.ytuce.osmroutetracking;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class TrackingService extends Service {

    private final String TAG = "TrackingService";

    // permission keys
    private final int LOCATION_ACCESS_GRANTED = 0;
    private final int LOCATION_PERMISSION_NOT_GRANTED = -1;
    private final int LOCATION_PROVIDER_NOT_AVAILABLE = -2;

    // intent keys
    public final String START_TRACKING = "start";
    public final String STOP_TRACKING = "stop";
    public final String INTENT_STRING_EXTRA = "extra";

    // notification keys ('status' in setNotification)
    private final String NOTIFICATION_TRACKING = "tracking";
    private final String NOTIFICATION_TRACKING_STOPPED = "stop";
    private final String NOTIFICATION_READY = "ready";

    private ArrayList<TrackingItem> points = null;
    private int locationPermissionStatus;
    private boolean gpsProviderActive;
    private boolean networkProviderActive;
    private Location moreAccurateLocation;

    public TrackingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e(TAG, "Service created");

        Context context = getApplicationContext();

        String extra;
        if (intent == null) {
            extra = "null";
        } else {
            extra = intent.getStringExtra(INTENT_STRING_EXTRA);
            Log.e(TAG, "extra = " + extra);
        }
        if (extra != null && extra.equals(STOP_TRACKING)) {
            stopTracking(context);
        } else {

            refreshData();
            locationPermissionStatus = checkLocationAccess(context);
            if (locationPermissionStatus == LOCATION_ACCESS_GRANTED) {
                trackLocation(context);
            } else if (locationPermissionStatus == LOCATION_PERMISSION_NOT_GRANTED) {
                Log.e(TAG, "Location permission not granted");
            } else if (locationPermissionStatus == LOCATION_PROVIDER_NOT_AVAILABLE) {
                Log.e(TAG, "Location services is not available (permission granted)");
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void refreshData() {
        if (points == null) {
            points = new ArrayList<>();
        }
        locationPermissionStatus = LOCATION_PERMISSION_NOT_GRANTED;
        gpsProviderActive = false;
        networkProviderActive = false;
        moreAccurateLocation = null;
    }

    private int checkLocationAccess(Context context) {
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        if (context.checkCallingOrSelfPermission(locationPermission) == PackageManager.PERMISSION_GRANTED) {

            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            try {
                gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception e) {
                e.printStackTrace();
            }

            gpsProviderActive = gpsEnabled;
            networkProviderActive = networkEnabled;

            if (gpsEnabled || networkEnabled) {
                return LOCATION_ACCESS_GRANTED;
            } else {
                return LOCATION_PROVIDER_NOT_AVAILABLE;
            }
        } else {
            return LOCATION_PERMISSION_NOT_GRANTED;
        }
    }

    private void trackLocation(Context context) {

        setNotification(NOTIFICATION_TRACKING, context);

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                TrackingItem item = new TrackingItem(location);
                points.add(item);
                Log.e(TAG, item.toString());
                // TODO control location accuracy
                // https://github.com/y20k/trackbook/blob/9086f4801f0f042e0ae58b9d957c3efdce3fdb0f/app/src/main/java/org/y20k/trackbook/TrackerService.kt#L298
                // https://github.com/y20k/trackbook/blob/9086f4801f0f042e0ae58b9d957c3efdce3fdb0f/app/src/main/java/org/y20k/trackbook/helpers/LocationHelper.kt#L80
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (gpsProviderActive) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener);
        }

        if (networkProviderActive) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, locationListener);
        }

    }

    private void stopTracking(Context context) {
        setNotification(NOTIFICATION_TRACKING_STOPPED, context);
        // TODO save to database
    }

    private void setNotification(@NotNull String status, Context context) {

        String contentTitle = "Konum takibi";
        String contentText = "";
        String buttonText = "";
        PendingIntent buttonPendingIntent = null;
        boolean ongoing; // set true if you want to disable swipe to delete

        if (status.equals(NOTIFICATION_TRACKING)) {
            contentTitle = "Konum takibi aktif";
            contentText = "Hareketiniz kaydediliyor";
            buttonText = "Bitir";
            ongoing = true;

            Intent stopTrackingIntent = new Intent(context, TrackingService.class);
            stopTrackingIntent.putExtra(INTENT_STRING_EXTRA, STOP_TRACKING);
            stopTrackingIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            buttonPendingIntent = PendingIntent.getService(context, 0, stopTrackingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else /* if (status.equals(NOTIFICATION_TRACKING_STOPPED)) */ {
            contentTitle = "Konum takibi durduruldu";
            contentText = "Rota sonlandirildi";
            buttonText = "Yeniden başla";
            ongoing = false;

            Intent startTrackingIntent = new Intent(context, TrackingService.class);
            startTrackingIntent.putExtra(INTENT_STRING_EXTRA, START_TRACKING);
            startTrackingIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            buttonPendingIntent = PendingIntent.getService(context, 0, startTrackingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", "CHANNEL_NAME", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("CHANNEL_DESCRIPTION");
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_menu_compass)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setNotificationSilent()
                .setOngoing(ongoing) // disable swipe to delete
                .addAction(R.drawable.osm_ic_follow_me, buttonText, buttonPendingIntent);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(0, notificationBuilder.build());
    }

}