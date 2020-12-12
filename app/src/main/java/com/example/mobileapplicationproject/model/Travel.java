package com.example.mobileapplicationproject.model;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class Travel {
    @SerializedName("destination")
    @Expose
    private String destination;
    @SerializedName("accEmail")
    @Expose
    private List<String> accEmail = null;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List<String> getAccEmail() {
        return accEmail;
    }

    public void setAccEmail(List<String> accEmail) {
        this.accEmail = accEmail;
    }

}
