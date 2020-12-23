package com.example.mobileapplicationproject.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class DriverScan {
    @SerializedName("travel_id")
    @Expose
    private Integer travelId;

    public Integer getTravelId() {
        return travelId;
    }

    public void setTravelId(Integer travelId) {
        this.travelId = travelId;
    }

}
