package com.ytuce.osmroutetracking.api;

import com.ytuce.osmroutetracking.EnvironmentVariables;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {
    String BASE_URL = EnvironmentVariables.REST_API_BASE_URL;

    @GET("postgis_30_sample/public/users_locations")
    Call<List<Results>> getAllPoints();

    // id should be set to: $eq.clientid exaple: $eq.2
    @GET("postgis_30_sample/public/users_locations")
    Call<List<Results>> getUserTracking(@Query("clientid") String id);
}
