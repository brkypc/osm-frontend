package com.ytuce.osmroutetracking.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.ytuce.osmroutetracking.R;
import com.ytuce.osmroutetracking.TrackingAdaptor;
import com.ytuce.osmroutetracking.api.Results;
import com.ytuce.osmroutetracking.api.RetrofitClient;
import com.ytuce.osmroutetracking.map.ClientFilterTileSource;
import com.ytuce.osmroutetracking.map.MapserverTileSource;
import com.ytuce.osmroutetracking.map.TileSourceFactory;
import com.ytuce.osmroutetracking.map.TrackingFilterTileSource;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.modules.SqlTileWriter;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

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

    public static final String MAP_MODE_INTENT_NAME = "map";
    public static final String ID_LIST_INTENT_NAME = "ids";
    public static final String FLAGS_INTENT_NAME = "flags";

    public static final String CLIENT_FILTER = "clnt";
    public static final String TRACKING_FILTER = "trck";
    public static final String NO_FILTER = "no";

    public static final int FLAG_SHOW_MY_ROUTES = 0b1;

    private final String TAG = "MapWithListActivity";

    private MapView map = null;
    private RecyclerView trackingRecyclerView;

    private ArrayList<Integer> ids;
    private MapserverTileSource tileSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent currentIntent = getIntent();
        String mapMode = currentIntent.getStringExtra(MAP_MODE_INTENT_NAME);
        int flags = currentIntent.getIntExtra(FLAGS_INTENT_NAME, 0);

        Context context = getApplicationContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        setContentView(R.layout.activity_map_with_list);

        map = (MapView) findViewById(R.id.mapView_onMapWithList);
        trackingRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_points);

        if (mapMode.equals(CLIENT_FILTER)) {
            ids = (ArrayList<Integer>) currentIntent.getSerializableExtra(ID_LIST_INTENT_NAME);
            tileSource = new TileSourceFactory().build(TileSourceFactory.CLIENT_FILTER, ids);
        } else if (mapMode.equals(TRACKING_FILTER)) {
            ids = (ArrayList<Integer>) currentIntent.getSerializableExtra(ID_LIST_INTENT_NAME);
            tileSource = new TileSourceFactory().build(TileSourceFactory.TRACKING_FILTER, ids);
            Log.e(TAG, "tile source = tracking filter");
        } else /* if (mapMode.equals(NO_FILTER)) */ {
            tileSource = new TileSourceFactory().build();
            Log.e(TAG, "tile source = default (no filter)");
        }
        map.setTileSource(tileSource);

        if (ids == null) {
            Log.e(TAG, "ids = null");
            ids = new ArrayList<>();
        }

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

        clearMapCache(false);

        // when tracking selection changes
        TrackingAdaptor.TrackingSelectionListener trackingSelectionListener = new TrackingAdaptor.TrackingSelectionListener() {
            @Override
            public void addTracking(int trackingId) {
                ids.add(trackingId);
                updateMapserverAdd(trackingId);
            }

            @Override
            public void deleteTracking(int trackingId) {
                for (int i = 0; i < ids.size(); i++) {
                    if (ids.get(i) == trackingId) {
                        ids.remove(i);
                    }
                }
                updateMapserverRemove(trackingId);
            }
        };

        // set recyclerview adaptor
        TrackingAdaptor adaptor = new TrackingAdaptor(context, trackingSelectionListener);
        trackingRecyclerView.setAdapter(adaptor);
        trackingRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if ((flags & FLAG_SHOW_MY_ROUTES) == FLAG_SHOW_MY_ROUTES) {

            Log.e(TAG, "FLAG = show my routes");

            // call api
            getClientPoints(adaptor, context, getClientID(context));
        }
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

    public void getClientPoints(TrackingAdaptor adaptor, Context context, int clientId) {

        Call<List<Results>> call = RetrofitClient.getInstance().getApi()
                .getUserTracking(clientId);

        /*
        Call<List<Results>> call = RetrofitClient.getInstance().getApi()
                .getUserTracking("$eq.2");
        */

        call.enqueue(new Callback<List<Results>>() {
            @Override
            public void onResponse(Call<List<Results>> call, Response<List<Results>> response) {
                List<Results> points = response.body();
                points = makeTrackingListUnique(points);

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

    private void updateMapserverAdd(int id) {
        Log.e(TAG, "selected ids on next lines:");
        for (Integer selectedTrackingId : ids) {
            Log.e(TAG, String.valueOf(selectedTrackingId));
        }

        if (tileSource instanceof TrackingFilterTileSource) {
            ((TrackingFilterTileSource) tileSource).addTracking(id);
        } else if (tileSource instanceof ClientFilterTileSource) {
            ((ClientFilterTileSource) tileSource).addClient(id);
        }

        clearMapCache(true);
    }

    private void updateMapserverRemove(int id) {
        Log.e(TAG, "selected ids on next lines:");
        for (Integer selectedTrackingId : ids) {
            Log.e(TAG, String.valueOf(selectedTrackingId));
        }

        if (tileSource instanceof TrackingFilterTileSource) {
            ((TrackingFilterTileSource) tileSource).removeTracking(id);
        } else if (tileSource instanceof ClientFilterTileSource) {
            ((ClientFilterTileSource) tileSource).removeClient(id);
        }

        clearMapCache(true);
    }

    public int getClientID(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS_ID_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int clientID = preferences.getInt(ID_KEY, new Random().nextInt());
        editor.putInt(ID_KEY, clientID);
        editor.apply();

        Log.e(TAG, "clientId = " + clientID);

        return clientID;
    }

    /**
     * @param list List fetched from api
     * @return A list that contains only unique client ids
     */
    private List<Results> makeClientListUnique(List<Results> list) {

        List<Results> results = new ArrayList<>();
        Set<Integer> ids = new HashSet<>();

        for (Results item : list) {
            if (!ids.contains(item.getClientId())) {
                results.add(item);
                ids.add(item.getClientId());
            }
        }

        return results;
    }

    /**
     * @param list List fetched from api
     * @return A list that contains only unique tracking ids
     */
    private List<Results> makeTrackingListUnique(List<Results> list) {

        List<Results> results = new ArrayList<>();
        Set<Integer> ids = new HashSet<>();

        for (Results item : list) {
            if (!ids.contains(item.getTrackingId())) {
                results.add(item);
                ids.add(item.getTrackingId());
            }
        }

        return results;
    }

    private void clearMapCache(boolean zoom) {
        SqlTileWriter sqlTileWriter = new SqlTileWriter();
        sqlTileWriter.purgeCache();

        if (zoom) {
            map.invalidate();
            map.getController().zoomOut(900L);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    map.getController().zoomIn();
                }
            }, 1500);
        }
    }
}
