package com.example.automatascoffeemobilejava.data;

import com.example.automatascoffeemobilejava.data.requests.DataRequest;
import com.example.automatascoffeemobilejava.data.requests.LoginRequest;
import com.example.automatascoffeemobilejava.data.requests.LogoutRequest;
import com.example.automatascoffeemobilejava.data.responses.DataResponse;
import com.example.automatascoffeemobilejava.data.responses.DeliveryResponse;
import com.example.automatascoffeemobilejava.data.responses.DetailsResponse;
import com.example.automatascoffeemobilejava.data.responses.LoginResponse;
import com.example.automatascoffeemobilejava.data.responses.LogoutResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {
    @POST("api/mobile/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/mobile/data")
    Call<DataResponse> data(@Body DataRequest request);

    @GET("api/purchase/delivery")
    Call<DeliveryResponse> pedidos(@Query("id") int id, @Query("max") int max);

    @GET("api/mobile/details")
    Call<DetailsResponse> getDetails(@Query("id") int idCompra);

    @POST("api/mobile/logout")
    Call<LogoutResponse> logout(@Body LogoutRequest request);
}