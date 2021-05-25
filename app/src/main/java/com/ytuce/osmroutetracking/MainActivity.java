package com.ytuce.osmroutetracking;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ytuce.osmroutetracking.api.RestService;
import com.ytuce.osmroutetracking.api.Results;
import com.ytuce.osmroutetracking.api.RetrofitClient;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button goMapButton;
    private TextView serviceStatusTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EnvironmentVariables.set();

        goMapButton = findViewById(R.id.button_goMap);
        serviceStatusTextView = findViewById(R.id.textView_ServiceStatus);
        progressBar = findViewById(R.id.progressBar);

        TrackingService.StatusListener serviceStatusListener = new TrackingService.StatusListener() {
            @Override
            public void onServiceStart() {
                serviceStatusTextView.setText("Konum takibi aktif");
            }

            @Override
            public void onServiceStop() {
                serviceStatusTextView.setText("Konum takibi durduruldu");
            }

            @Override
            public void onServiceIdle() {
                serviceStatusTextView.setText("Konum kaydi icin hazir");
            }
        };

        if (!isServiceRunning()) {
            startTrackingService();
            Log.e(TAG, "service is not running");
        }

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            TrackingService.addStatusListener(true, serviceStatusListener);
            progressBar.setVisibility(View.GONE);
        }, 1000);

        getAllPoints();

    }

    public void goMap(View v) {
        startActivity(new Intent(this, MapWithListActivity.class));
    }

    private void startTrackingService() {
        Intent trackingServiceIntent = new Intent(this, TrackingService.class);
        trackingServiceIntent.putExtra(TrackingService.INTENT_STRING_EXTRA, TrackingService.IDLE_TRACKING);
        trackingServiceIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startService(trackingServiceIntent);
    }

    private boolean isServiceRunning() {

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningService : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (TrackingService.class.getName().equals(runningService.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    public void getAllPoints() {

        Call<List<Results>> call = RetrofitClient.getInstance().getApi().getAllPoints();
        call.enqueue(new Callback<List<Results>>() {
            @Override
            public void onResponse(Call<List<Results>> call, Response<List<Results>> response) {
                List<Results> points = response.body();
                for (Results point : points) {
                    Log.e("RestService", point.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Results>> call, Throwable t) {
                // show error
                Log.e("RestSerivece", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
}