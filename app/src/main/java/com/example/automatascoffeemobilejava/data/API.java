package com.example.automatascoffeemobilejava.data;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

//POST es el metodo que se utiliza para hacer la llamada a la API

//Call<LoginResponse> login(@Body LoginRequest request);

//Call es el metodo que se utiliza para hacer la llamada a la API
//LoginResponse indica el tipo de repsuesta que se espera de la API (una clase a aparte)
//LoginRequest es el objeto que se va a enviar a la API (una clase a aparte)
//request es el objeto de tipo LoginRequest que se va a enviar a la API

public interface API {
    @POST("api/mobile/login")
    Call<LoginResponse> login(@Body LoginRequest request);

}
