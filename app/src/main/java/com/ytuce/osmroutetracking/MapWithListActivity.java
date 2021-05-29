package com.ytuce.osmroutetracking;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.ytuce.osmroutetracking.api.Results;
import com.ytuce.osmroutetracking.api.RetrofitClient;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapWithListActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 2001;
    private static final String SHARED_PREFS_ID_FILE = "client_id";
    private static final String ID_KEY = "id";
    private final String TAG = "MapWithListActivity";

    private MapView map = null;
    private RecyclerView trackingRecyclerView;

    private List<Integer> selectedTrackingIds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "onCreate start");

        Context context = getApplicationContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        setContentView(R.layout.activity_map_with_list);

        map = (MapView) findViewById(R.id.mapView_onMapWithList);
        trackingRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_points);

        selectedTrackingIds = new ArrayList<>();

        MapserverTileSource tileSource = new MapserverTileSource("mapserver", -25, 25, 256, ".png", MapserverTileSource.baseUrl, "YTU CE");
        map.setTileSource(tileSource);

        requestPermissions(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        map.setMultiTouchControls(true);

        // set starting point
        IMapController mapController = map.getController();
        mapController.setZoom(15.);
        GeoPoint pointOfDavutpasa = new GeoPoint(41.028736, 28.891083);
        mapController.setCenter(pointOfDavutpasa);

        // enable 'my location'
        MyLocationNewOverlay myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), map);
        myLocationOverlay.enableMyLocation();
        map.getOverlays().add(myLocationOverlay);

        // enable compass
        CompassOverlay compassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context), map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        // show scale bar
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(map);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(displayMetrics.widthPixels / 2, 10);
        map.getOverlays().add(scaleBarOverlay);

        // when tracking selection changes
        TrackingAdaptor.TrackingSelectionListener trackingSelectionListener = new TrackingAdaptor.TrackingSelectionListener() {
            @Override
            public void addTracking(int trackingId) {
                selectedTrackingIds.add(trackingId);
                updateMapserver();
            }

            @Override
            public void deleteTracking(int trackingId) {
                for (int i = 0; i < selectedTrackingIds.size(); i++) {
                    if (selectedTrackingIds.get(i) == trackingId) {
                        selectedTrackingIds.remove(i);
                    }
                }
                updateMapserver();
            }
        };

        // set recyclerview adaptor
        TrackingAdaptor adaptor = new TrackingAdaptor(context, trackingSelectionListener);
        trackingRecyclerView.setAdapter(adaptor);
        trackingRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // call api
        getClientPoints(adaptor, context);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (map != null) {
            map.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (map != null) {
            map.onPause();
        }
    }

    private void requestPermissions(String[] permissions) {
        // request permissions until they granted
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    public void getAllPoints(TrackingAdaptor adaptor) {

        Call<List<Results>> call = RetrofitClient.getInstance().getApi().getAllPoints();
        call.enqueue(new Callback<List<Results>>() {
            @Override
            public void onResponse(Call<List<Results>> call, Response<List<Results>> response) {
                List<Results> points = response.body();

                for (Results point : points) {
                    Log.e("MapWithListActivity", point.toString());
                }

                adaptor.setTrackingList(points);
                Log.e("MapWithListActivity", "adaptor set");
            }

            @Override
            public void onFailure(Call<List<Results>> call, Throwable t) {
                // show error
                Log.e("MapWithListActivity", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    public void getClientPoints(TrackingAdaptor adaptor, Context context) {

        Call<List<Results>> call = RetrofitClient.getInstance().getApi()
                .getUserTracking(getClientID(context));

        /*
        Call<List<Results>> call = RetrofitClient.getInstance().getApi()
                .getUserTracking("$eq.2");
        */

        call.enqueue(new Callback<List<Results>>() {
            @Override
            public void onResponse(Call<List<Results>> call, Response<List<Results>> response) {
                List<Results> points = response.body();

                for (Results point : points) {
                    Log.e("MapWithListActivity", point.toString());
                }

                adaptor.setTrackingList(points);
            }

            @Override
            public void onFailure(Call<List<Results>> call, Throwable t) {
                // show error
                Log.e("RestService", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void updateMapserver() {
        Log.e(TAG, "selected ids on next lines:");
        for (Integer selectedTrackingId : selectedTrackingIds) {
            Log.e(TAG, String.valueOf(selectedTrackingId));
        }
    }

    public int getClientID(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS_ID_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int clientID = preferences.getInt(ID_KEY, new Random().nextInt());
        editor.putInt(ID_KEY, clientID);
        editor.apply();
        return clientID;
    }
}
