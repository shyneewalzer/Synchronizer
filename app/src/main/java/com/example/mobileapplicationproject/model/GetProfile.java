package com.example.mobileapplicationproject.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class GetProfile {

    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("middlename")
    @Expose
    private String middlename;
    @SerializedName("age")
    @Expose
    private String age;
    @SerializedName("contactnumber")
    @Expose
    private String contactnumber;
    @SerializedName("image")
    @Expose
    private Object image;
    @SerializedName("profile_owner")
    @Expose
    private Integer profileOwner;
    @SerializedName("address_id")
    @Expose
    private Integer addressId;
    @SerializedName("house_lot_number")
    @Expose
    private String houseLotNumber;
    @SerializedName("barangay")
    @Expose
    private String barangay;
    @SerializedName("city")
    @Expose
    private String city;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getContactnumber() {
        return contactnumber;
    }

    public void setContactnumber(String contactnumber) {
        this.contactnumber = contactnumber;
    }

    public Object getImage() {
        return image;
    }

    public void setImage(Object image) {
        this.image = image;
    }

    public Integer getProfileOwner() {
        return profileOwner;
    }

    public void setProfileOwner(Integer profileOwner) {
        this.profileOwner = profileOwner;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public String getHouseLotNumber() {
        return houseLotNumber;
    }

    public void setHouseLotNumber(String houseLotNumber) {
        this.houseLotNumber = houseLotNumber;
    }

    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
