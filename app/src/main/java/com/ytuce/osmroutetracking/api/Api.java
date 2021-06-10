package com.ytuce.osmroutetracking.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {

    @GET("/getAllPoints")
    Call<List<Results>> getAllPoints();

    @GET("/getPointsOfClient")
    Call<List<Results>> getUserTracking(@Query("clientid") int id);

    @POST("/pushRouting")
    Call<Results> pushTracking(@Body Results items);

    @POST("/addRoute")
    Call<Results> addRoute(@Query("clientid") int clientId, @Query("trackingid") int trackingId);

    @GET("/getoRoutesClosePoint")
    Call<List<Results>> getRoutesClosePoint(
            @Query("latitude") double latitude, @Query("longitude") double longitude);

    @GET("/getoRoutesClosePointTimeInterval")
    Call<List<Results>> getRoutesClosePointTimeInterval(
            @Query("latitude") double latitude, @Query("longitude") double longitude,
            @Query("start") long startTime, @Query("end") long endTime);

}
