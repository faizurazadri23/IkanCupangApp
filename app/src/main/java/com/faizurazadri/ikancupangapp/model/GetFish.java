package com.faizurazadri.ikancupangapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetFish {

    @SerializedName("data")
    private List<Fish> fishList;

    public List<Fish> getFishList() {
        return fishList;
    }

    public void setFishList(List<Fish> fishList) {
        this.fishList = fishList;
    }
}
