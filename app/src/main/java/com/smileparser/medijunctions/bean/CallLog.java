package com.smileparser.medijunctions.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CallLog {

    @SerializedName("UserId")
    @Expose
    private String userId;
    @SerializedName("CallerName")
    @Expose
    private String callerName;
    @SerializedName("PatientName")
    @Expose
    private String patientName;
    @SerializedName("CallAt")
    @Expose
    private String callAt;
    @SerializedName("ImagePath")
    @Expose
    private String imagePath;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getCallAt() {
        return callAt;
    }

    public void setCallAt(String callAt) {
        this.callAt = callAt;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
