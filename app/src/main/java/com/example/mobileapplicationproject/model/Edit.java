package com.example.mobileapplicationproject.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Edit {

    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("middleName")
    @Expose
    private String middleName;
    @SerializedName("age")
    @Expose
    private String age;
    @SerializedName("contact")
    @Expose
    private String contact;
    @SerializedName("position")
    @Expose


    private String position;


    public String getFirstName() {
        return firstName;
    }
    public Edit(String firstName, String lastName, String middleName, String age, String contact) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.age = age;
        this.contact = contact;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

}
