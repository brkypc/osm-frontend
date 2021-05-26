package com.ytuce.osmroutetracking.api;

import com.ytuce.osmroutetracking.EnvironmentVariables;
import com.ytuce.osmroutetracking.TrackingItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    @GET("postgis_30_sample/public/users_locations")
    Call<List<Results>> getAllPoints();

    // id should be set to: $eq.clientid exaple: $eq.2
    @GET("postgis_30_sample/public/users_locations")
    Call<List<Results>> getUserTracking(@Query("clientid") String id);

    @POST("postgis_30_sample/public/users_locations")
    Call<Results> pushTracking(@Body Results item);
}
