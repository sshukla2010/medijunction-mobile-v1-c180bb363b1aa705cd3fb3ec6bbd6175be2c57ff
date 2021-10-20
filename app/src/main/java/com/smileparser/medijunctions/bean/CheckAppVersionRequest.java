package com.smileparser.medijunctions.bean;

import com.google.gson.annotations.SerializedName;

public class CheckAppVersionRequest {

    @SerializedName("APKVersionName")
    String APKVersionName;

    @SerializedName("APKVersionCode")
    int APKVersionCode;

    public String getAPKVersionName() {
        return APKVersionName;
    }

    public void setAPKVersionName(String APKVersionName) {
        this.APKVersionName = APKVersionName;
    }

    public int getAPKVersionCode() {
        return APKVersionCode;
    }

    public void setAPKVersionCode(int APKVersionCode) {
        this.APKVersionCode = APKVersionCode;
    }
}
