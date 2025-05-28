package com.example.automatascoffeemobilejava.data;


import com.example.automatascoffeemobilejava.data.requests.DataRequest;
import com.example.automatascoffeemobilejava.data.requests.LoginRequest;
import com.example.automatascoffeemobilejava.data.responses.DataResponse;
import com.example.automatascoffeemobilejava.data.responses.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface API {
    @POST("api/mobile/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/mobile/data")
    Call<DataResponse> data(@Body DataRequest request);

}
