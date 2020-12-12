package com.example.mobileapplicationproject;

import com.example.mobileapplicationproject.model.GetProfile;
import com.example.mobileapplicationproject.model.Login;
import com.example.mobileapplicationproject.model.Post;
import com.example.mobileapplicationproject.model.Travel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface APIInterface {
        @GET("list/account/login/profile")
        Call<GetProfile> getProfile(@Header("Authorization") String token);

        @POST("accounts/register")
        Call<Post> createPost(@Body Post post);

        @POST("accounts/login")
        Call<Login> loginUser(@Body Login login);

        @POST("accounts/create/travel")
        Call<Travel> Createtravel (@Header("Authorization")  String token, @Body Travel travel);
}
