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

    @GET("/getAllPoints")
    Call<List<Results>> getAllPoints();

    @GET("/getPointsOfClient")
    Call<List<Results>> getUserTracking(@Query("clientid") int id);

    @POST("/pushRouting")
    Call<Results> pushTracking(@Body Results items);
}
