package com.ytuce.osmroutetracking.service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.ytuce.osmroutetracking.R;
import com.ytuce.osmroutetracking.activity.MainActivity;
import com.ytuce.osmroutetracking.api.Results;
import com.ytuce.osmroutetracking.api.RetrofitClient;
import com.ytuce.osmroutetracking.utility.LocalObjectStorage;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingService extends Service {

    private static final String TAG = "TrackingService";

    public interface StatusListener {
        void onServiceStart();
        void onServiceStop();
        void onServiceIdle();
    }

    // permission keys
    private static final int LOCATION_ACCESS_GRANTED = 0;
    private static final int LOCATION_PERMISSION_NOT_GRANTED = -1;
    private static final int LOCATION_PROVIDER_NOT_AVAILABLE = -2;

    // intent keys
    public static final String START_TRACKING = "start";
    public static final String STOP_TRACKING = "stop";
    public static final String IDLE_TRACKING = "idle";
    public static final String INTENT_STRING_EXTRA = "extra";

    private static final String SHARED_PREFS_STATUS_FILE = "service_prefs";
    private static final String STATUS_KEY = "status";
    private static final String SHARED_PREFS_ID_FILE = "client_id";
    private static final String ID_KEY = "id";

    public static final String SHARED_PREFS_POINTS_FILE = "points";

    private static String currentStatus = "";
    private ArrayList<TrackingItem> points = null;
    private int locationPermissionStatus;
    private boolean gpsProviderActive;
    private boolean networkProviderActive;
    private Location moreAccurateLocation;
    private static List<StatusListener> statusListeners;

    private LocationManager locationManager;
    private LocationListener locationListener = null;

    public TrackingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Context context = getApplicationContext();

        if (currentStatus.equals("")) {
            SharedPreferences preferences = this.getSharedPreferences(SHARED_PREFS_STATUS_FILE, MODE_PRIVATE);
            currentStatus = preferences.getString(STATUS_KEY, IDLE_TRACKING);
            Log.e(TAG, "status updated from file");
        }

        if (points != null) {
            Log.e(TAG, "points.size() = " + points.size());
        } else {
            Log.e(TAG, "points = null");

            LocalObjectStorage<TrackingItem> storage = new LocalObjectStorage<>();
            points = storage.getList(SHARED_PREFS_POINTS_FILE, context, TrackingItem.class);

            Log.e(TAG, "points.size() after read from file = " + points.size());
        }

        Log.e(TAG, "Service created");

        String extra;
        if (intent == null) {
            extra = "null";
        } else {
            extra = intent.getStringExtra(INTENT_STRING_EXTRA);
            Log.e(TAG, "extra = " + extra);
        }
        if (extra != null && extra.equals(STOP_TRACKING)) {
            stopTracking(context);
        } else if ((extra != null && extra.equals(START_TRACKING)) || currentStatus.equals(START_TRACKING)) {

            refreshData();
            locationPermissionStatus = checkLocationAccess(context);
            if (locationPermissionStatus == LOCATION_ACCESS_GRANTED) {
                trackLocation(context);
            } else if (locationPermissionStatus == LOCATION_PERMISSION_NOT_GRANTED) {
                Log.e(TAG, "Location permission not granted");
                Toast.makeText(context, "Konum izinleri verilmemis", Toast.LENGTH_LONG).show();
            } else if (locationPermissionStatus == LOCATION_PROVIDER_NOT_AVAILABLE) {
                Log.e(TAG, "Location services is not available (permission granted)");
                Toast.makeText(context, "Konum servisleri acik degil", Toast.LENGTH_LONG).show();
            }
        } else if (extra != null && extra.equals(IDLE_TRACKING)) {
            Log.e(TAG, "set idle");
            refreshData();
            idleTracking(context);
        } else {
            Log.e(TAG, "extra = null");
            setNotification(STOP_TRACKING, context);
        }


        return super.onStartCommand(intent, flags, startId);
    }

    public static String getServiceStatus() {
        return currentStatus;
    }

    private void refreshData() {
        if (points == null) {
            points = new ArrayList<>();
        }
        locationPermissionStatus = LOCATION_PERMISSION_NOT_GRANTED;
        gpsProviderActive = false;
        networkProviderActive = false;
        moreAccurateLocation = null;
        if (statusListeners == null) {
            statusListeners = new ArrayList<>();
        }
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

        setNotification(START_TRACKING, context);
        currentStatus = START_TRACKING;

        triggerListeners(START_TRACKING);

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                TrackingItem item = new TrackingItem(location);
                points.add(item);
                Log.e(TAG, item.toString());

                LocalObjectStorage<TrackingItem> storage = new LocalObjectStorage<>();
                storage.putElement(item, SHARED_PREFS_POINTS_FILE, context);

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
        setNotification(STOP_TRACKING, context);
        currentStatus = STOP_TRACKING;

        LocalObjectStorage<TrackingItem> storage = new LocalObjectStorage<>();
        storage.deleteList(SHARED_PREFS_POINTS_FILE, context);

        if (locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }

        triggerListeners(STOP_TRACKING);
        // TODO save to database


        int clientId = getClientID(context);
        Random random = new Random();
        int trackingId = random.nextInt();

        for (TrackingItem point : points) {
            point.setClientId(clientId);
            point.setTrackingId(trackingId);

            Call<Results> call = RetrofitClient.getInstance().getApi().pushTracking(new Results(point));
            call.enqueue(new Callback<Results>() {
                @Override
                public void onResponse(Call<Results> call, Response<Results> response) {
                    Log.e(TAG, "push successful");
                }

                @Override
                public void onFailure(Call<Results> call, Throwable t) {
                    Log.e(TAG, t.getMessage());
                }
            });
        }

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            Call<Results> call = RetrofitClient.getInstance().getApi().addRoute(clientId, trackingId);
            call.enqueue(new Callback<Results>() {
                @Override
                public void onResponse(Call<Results> call, Response<Results> response) {
                    Log.e(TAG, "add_route called");
                }

                @Override
                public void onFailure(Call<Results> call, Throwable t) {
                    Log.e(TAG, t.getMessage());
                }
            });
        }, 2000);

        points = new ArrayList<>();
    }

    private void idleTracking(Context context) {
        setNotification(IDLE_TRACKING, context);
        currentStatus = IDLE_TRACKING;

        triggerListeners(IDLE_TRACKING);
    }

    private void setNotification(@NotNull String status, Context context) {

        String contentTitle = "Konum takibi";
        String contentText = "";
        String buttonText = "";
        PendingIntent buttonPendingIntent = null;
        boolean ongoing; // set true if you want to disable swipe to delete

        if (status.equals(START_TRACKING)) {
            contentTitle = "Konum takibi aktif";
            contentText = "Hareketiniz kaydediliyor";
            buttonText = "Bitir";
            ongoing = true;

            buttonPendingIntent = getStopperIntent(context);
        } else if (status.equals(STOP_TRACKING)) {
            contentTitle = "Konum takibi durduruldu";
            contentText = "Rota sonlandirildi";
            buttonText = "Yeniden başla";
            ongoing = false;

            buttonPendingIntent = getStarterIntent(context);
        } else /* if status IDLE */ {
            contentTitle = "Konum servisi hazir";
            contentText = "Konumunuz kaydedilmiyor";
            buttonText = "Basla";
            ongoing = false;

            buttonPendingIntent = getStarterIntent(context);
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
                .setOngoing(ongoing) // disable (or enable) swipe to delete
                .addAction(R.drawable.osm_ic_follow_me, buttonText, buttonPendingIntent);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private PendingIntent getStarterIntent(Context context) {
        Intent startTrackingIntent = new Intent(context, TrackingService.class);
        startTrackingIntent.putExtra(INTENT_STRING_EXTRA, START_TRACKING);
        startTrackingIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getService(context, 0, startTrackingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getStopperIntent(Context context) {
        Intent stopTrackingIntent = new Intent(context, TrackingService.class);
        stopTrackingIntent.putExtra(INTENT_STRING_EXTRA, STOP_TRACKING);
        stopTrackingIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getService(context, 0, stopTrackingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void addStatusListener(boolean getCurrent, StatusListener listener) {

        if (statusListeners == null) {
            statusListeners = new ArrayList<>();
        }

        statusListeners.add(listener);

        Log.e(TAG, "listener added");

        if (getCurrent) {
            triggerListener(listener, currentStatus);
            Log.e(TAG, "listener triggered with status = " + currentStatus);
        }
    }

    public static void addStatusListener(StatusListener listener) {

        if (statusListeners == null) {
            statusListeners = new ArrayList<>();
        }

        statusListeners.add(listener);
    }

    private void triggerListeners(String status) {

        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_STATUS_FILE, MODE_PRIVATE);
        preferences.edit().putString(STATUS_KEY, status).apply();

        if (statusListeners == null || statusListeners.size() == 0) {
            return;
        }

        for (StatusListener statusListener : statusListeners) {
            triggerListener(statusListener, status);
        }
    }

    private static void triggerListener(StatusListener listener, String status) {
        switch (currentStatus) {
            case START_TRACKING:
                listener.onServiceStart();
                break;
            case STOP_TRACKING:
                listener.onServiceStop();
                break;
            case IDLE_TRACKING:
                listener.onServiceIdle();
                break;
        }
    }

    private int getClientID(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS_ID_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int clientID = preferences.getInt(ID_KEY, new Random().nextInt());
        editor.putInt(ID_KEY, clientID);
        editor.apply();
        return clientID;
    }

}