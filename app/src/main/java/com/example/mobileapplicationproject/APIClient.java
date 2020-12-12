package com.example.mobileapplicationproject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class APIClient {

    public static Retrofit retrofit = null;

    static Retrofit getClient() {

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.100.79:6060/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

}
