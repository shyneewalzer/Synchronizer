package com.example.mobileapplicationproject.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class ResponesTravel {

    @SerializedName("destination")
    @Expose
    private String destination;
    @SerializedName("isCompanion")
    @Expose
    private Integer isCompanion;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("travel_id")
    @Expose
    private Integer travelId;
    @SerializedName("time_created")
    @Expose
    private String timeCreated;
    @SerializedName("date_created")
    @Expose
    private String dateCreated;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getIsCompanion() {
        return isCompanion;
    }

    public void setIsCompanion(Integer isCompanion) {
        this.isCompanion = isCompanion;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTravelId() {
        return travelId;
    }

    public void setTravelId(Integer travelId) {
        this.travelId = travelId;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
