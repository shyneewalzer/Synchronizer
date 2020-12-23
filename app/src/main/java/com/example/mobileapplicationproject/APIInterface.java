package com.example.mobileapplicationproject;

import com.example.mobileapplicationproject.model.DriverScan;
import com.example.mobileapplicationproject.model.Edit;
import com.example.mobileapplicationproject.model.EditAccount;
import com.example.mobileapplicationproject.model.GetProfile;
import com.example.mobileapplicationproject.model.Login;
import com.example.mobileapplicationproject.model.Post;
import com.example.mobileapplicationproject.model.ResponesTravel;
import com.example.mobileapplicationproject.model.Travel;

import java.sql.Driver;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface APIInterface {
        @GET("list/account/login/profile")
        Call<GetProfile> getProfile(@Header("Authorization") String token);

        @POST("accounts/register")
        Call<Post> createPost(@Body Post post);

        @POST("accounts/login")
        Call<Login> loginUser(@Body Login login);

        @POST("accounts/create/travel")
        Call<ResponesTravel> travelPost(@Header("Authorization") String token, @Body Travel travel);

        @POST("driver/create/passenger")
        Call<DriverScan> driverScan(@Header("Authorization") String token, @Body DriverScan driverScan);

        @PATCH("accounts/update/profile")
        Call<Edit> edit(@Header("Authorization") String token, @Body Edit edit);

        @PATCH("update/account")
        Call<EditAccount> editAccount(@Header("Authorization") String token, @Body EditAccount editAccount);
}
