package com.ytuce.osmroutetracking;

import android.content.Context;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseManager {

    private Connection connection;
    private final Context context;

    public DatabaseManager(Context context) {

        this.context = context;

        try {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection(
                    EnvironmentVariables.PGSQL_LOCALHOST,
                    EnvironmentVariables.PGSQL_USERNAME,
                    EnvironmentVariables.PGSQL_PASSWORD
            );
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Toast.makeText(context, "Veritabani baglantisi kurulamadi", Toast.LENGTH_LONG).show();
        }
    }

    public void pushPoints() {
        ArrayList<TrackingItem> points = new ArrayList<>();
        LocalObjectStorage<TrackingItem> storage = new LocalObjectStorage<>();
        points = storage.getList(TrackingService.SHARED_PREFS_POINTS_FILE, context, TrackingItem.class);

        // TODO push list to database

        closeConnection();
    }

    private void closeConnection() {
        try {
            connection.close();
            // statement.close(), etc...
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
