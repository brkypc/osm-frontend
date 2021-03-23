package com.ytuce.osmroutetracking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button goMapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startTrackingService();

        goMapButton = (Button) findViewById(R.id.button_goMap);
    }

    public void goMap(View v) {
        startActivity(new Intent(this, MapActivity.class));
    }

    public void startTrackingService() {
        Intent trackingServiceIntent = new Intent(this, TrackingService.class);
        startService(trackingServiceIntent);
    }
}