package com.smileparser.medijunctions.bean;

import com.google.gson.annotations.SerializedName;

public class CheckAppVeriosnResponse {


    @SerializedName("IsForceUpdate")
    boolean IsForceUpdate;

    @SerializedName("IsUpdate")
    boolean IsUpdate;

    @SerializedName("UpdateDetails")
    String UpdateDetails;


    public boolean isForceUpdate() {
        return IsForceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        IsForceUpdate = forceUpdate;
    }

    public boolean isUpdate() {
        return IsUpdate;
    }

    public void setUpdate(boolean update) {
        IsUpdate = update;
    }

    public String getUpdateDetails() {
        return UpdateDetails;
    }

    public void setUpdateDetails(String updateDetails) {
        UpdateDetails = updateDetails;
    }
}
