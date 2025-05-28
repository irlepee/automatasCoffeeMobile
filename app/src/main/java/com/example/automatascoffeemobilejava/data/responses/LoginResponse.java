package com.example.automatascoffeemobilejava.data.responses;

public class LoginResponse {

    String status;
    boolean success;
    int id;

    public boolean isSuccess() {
        return success;
    }

    public int getId() {return id;}

    public String getStatus() {
        return status;
    }

}
