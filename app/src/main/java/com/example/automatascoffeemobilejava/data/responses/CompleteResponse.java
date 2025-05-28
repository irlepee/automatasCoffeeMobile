package com.example.automatascoffeemobilejava.data.responses;

public class CompleteResponse {
    String status;
    String message;

    public CompleteResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
