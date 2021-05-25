package com.ytuce.osmroutetracking.api;

import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestService {

    public static void getAllPoints() {

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
            }
        });
    }

}
