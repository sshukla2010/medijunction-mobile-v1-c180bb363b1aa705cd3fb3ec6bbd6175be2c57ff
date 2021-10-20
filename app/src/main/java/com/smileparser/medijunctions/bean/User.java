package com.smileparser.medijunctions.bean;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

/**
 * Created by hardik on 25/4/18.
 */

public class User {

/*
{
    "Message": "Login Successfully",
    "Response": true,
    "Result": {
        "Id": "000-000-000-000"(Guid),
        "Name": "A b",
        "Email":"a@gmail.com",
        "Mobile":"9898666666",
         "Pincode":"361008"
    }
}
 */

    @SerializedName("Id")
    String Id;

    @SerializedName("Name")
    String UserName;


    @SerializedName("FirstName")
    String FirstName;

    @SerializedName("LastName")
    String LastName;

    @SerializedName("Password")
    String Password;

    @SerializedName("Email")
    String Email;

    @SerializedName("Mobile")
    String Mobile;

    @SerializedName("Pincode")
    String Pincode;

    @SerializedName("TotalPatient")
    int TotalPatient;

    @SerializedName("Usertype")
    String Usertype;

    @SerializedName("CountryCode")
    public String CountryCode;

    @SerializedName("DeviceId")
    public String DeviceId;

    @SerializedName("Token")
    public String Token;


    public User() {
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
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

    public String getPincode() {
        return Pincode;
    }

    public void setPincode(String pincode) {
        Pincode = pincode;
    }

    public int getTotalPatient() {
        return TotalPatient;
    }

    public void setTotalPatient(int totalPatient) {
        TotalPatient = totalPatient;
    }

    public String getUsertype() {
        return Usertype;
    }

    public void setUsertype(String usertype) {
        Usertype = usertype;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String countryCode) {
        CountryCode = countryCode;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}
