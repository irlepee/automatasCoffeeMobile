package com.example.automatascoffeemobilejava.data;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface API {
    @POST("api/mobile/login")
    Call<LoginResponse> login(@Body LoginRequest request);

}
