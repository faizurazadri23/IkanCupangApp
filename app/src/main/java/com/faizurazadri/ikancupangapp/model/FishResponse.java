package com.faizurazadri.ikancupangapp.model;

import com.google.gson.annotations.SerializedName;

public class FishResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Fish fish;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Fish getFish() {
        return fish;
    }

    public void setFish(Fish fish) {
        this.fish = fish;
    }
}
