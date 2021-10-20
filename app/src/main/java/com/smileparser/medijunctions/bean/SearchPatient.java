package com.smileparser.medijunctions.bean;

import com.google.gson.annotations.SerializedName;

public class SearchPatient {
    @SerializedName("Id")
    String Id;

    @SerializedName("Name")
    String Name;

    @SerializedName("Email")
    String Email;

    @SerializedName("Mobile")
    String Mobile;

    public SearchPatient() {
    }

    public SearchPatient(String id, String name, String email, String mobile) {
        Id = id;
        Name = name;
        Email = email;
        Mobile = mobile;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }
}
