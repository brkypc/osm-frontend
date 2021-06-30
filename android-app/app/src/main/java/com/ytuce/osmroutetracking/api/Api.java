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

    @GET("/getRoutesClosePointOfClient")
    Call<List<Results>> getRoutesClosePoint(
            @Query("latitude") double latitude, @Query("longitude") double longitude,
            @Query("clnt") int client);

    @GET("/getRoutesClosePointTimeIntervalOfClient")
    Call<List<Results>> getRoutesClosePointTimeInterval(
            @Query("latitude") double latitude, @Query("longitude") double longitude,
            @Query("start") long startTime, @Query("end") long endTime,
            @Query("clnt") int client);

    @GET("/getRoutesInsideAreaOfClient")
    Call<List<Results>> getRoutesInsideArea(
            @Query("latitude1") double latitude1, @Query("longitude1") double longitude1,
            @Query("latitude2") double latitude2, @Query("longitude2") double longitude2,
            @Query("clnt") int client);

    @GET("/getRoutesInsideAreaTimeIntervalOfClient")
    Call<List<Results>> getRoutesInsideAreaTimeInterval(
            @Query("latitude1") double latitude1, @Query("longitude1") double longitude1,
            @Query("latitude2") double latitude2, @Query("longitude2") double longitude2,
            @Query("start") long startTime, @Query("end") long endTime,
            @Query("clnt") int client);

}
