package com.ytuce.osmroutetracking.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static RetrofitClient instance = null;
    private Api api;

    private RetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:8080/" /*EnvironmentVariables.REST_API_BASE_URL*/)
                .addConverterFactory(GsonConverterFactory.create()).build();
        api = retrofit.create(Api.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public Api getApi() {
        return api;
    }
}
