package com.smileparser.medijunctions.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VitalResponse {
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Response")
    @Expose
    private Boolean response;
    @SerializedName("Result")
    @Expose
    private Result result;
    @SerializedName("ErrorCode")
    @Expose
    private Integer errorCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getResponse() {
        return response;
    }

    public void setResponse(Boolean response) {
        this.response = response;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public class Result {

        @SerializedName("PatientVitalsID")
        @Expose
        private Integer patientVitalsID;
        @SerializedName("PatientID")
        @Expose
        private String patientID;
        @SerializedName("IsActive")
        @Expose
        private Boolean isActive;
        @SerializedName("IsDelete")
        @Expose
        private Boolean isDelete;
        @SerializedName("PatientAge")
        @Expose
        private Object patientAge;
        @SerializedName("SystolicBP")
        @Expose
        private Object systolicBP;
        @SerializedName("DiastolicBP")
        @Expose
        private Object diastolicBP;
        @SerializedName("BloodGroup")
        @Expose
        private Object bloodGroup;
        @SerializedName("Height")
        @Expose
        private Object height;
        @SerializedName("Weight")
        @Expose
        private Object weight;
        @SerializedName("SPO2")
        @Expose
        private Object spo2;
        @SerializedName("Sugar")
        @Expose
        private Object sugar;
        @SerializedName("Temperature")
        @Expose
        private Double temperature;
        @SerializedName("RespirationRate")
        @Expose
        private Object respirationRate;
        @SerializedName("PulseRate")
        @Expose
        private Object pulseRate;
        @SerializedName("CreatedOn")
        @Expose
        private String createdOn;
        @SerializedName("CreatedBy")
        @Expose
        private String createdBy;
        @SerializedName("CreatedUserType")
        @Expose
        private Object createdUserType;
        @SerializedName("Complaints")
        @Expose
        private Object complaints;
        @SerializedName("CholesterolLDL")
        @Expose
        private Object cholesterolLDL;
        @SerializedName("CholesterolHDL")
        @Expose
        private Object cholesterolHDL;
        @SerializedName("TotalCholesterol")
        @Expose
        private Object totalCholesterol;
        @SerializedName("Comorbidity")
        @Expose
        private Object comorbidity;
        @SerializedName("DateDiseaseOnset")
        @Expose
        private Object dateDiseaseOnset;
        @SerializedName("RTPCRDate")
        @Expose
        private Object rTPCRDate;
        @SerializedName("RTPCR")
        @Expose
        private Object rtpcr;
        @SerializedName("RTPCRCTValue")
        @Expose
        private Object rTPCRCTValue;
        @SerializedName("HRCTDate")
        @Expose
        private Object hRCTDate;
        @SerializedName("HRCTScore")
        @Expose
        private Object hRCTScore;
        @SerializedName("CRP")
        @Expose
        private Object crp;
        @SerializedName("ESR")
        @Expose
        private Object esr;
        @SerializedName("Interleukln6")
        @Expose
        private Object interleukln6;
        @SerializedName("LDH")
        @Expose
        private Object ldh;
        @SerializedName("DDimer")
        @Expose
        private Object dDimer;
        @SerializedName("Ferritin")
        @Expose
        private Object ferritin;
        @SerializedName("StethoscopeFileName")
        @Expose
        private Object stethoscopeFileName;
        @SerializedName("StethoscopeFilePath")
        @Expose
        private Object stethoscopeFilePath;
        @SerializedName("ECGFileName")
        @Expose
        private Object eCGFileName;
        @SerializedName("ECGFilePath")
        @Expose
        private Object eCGFilePath;

        public Integer getPatientVitalsID() {
            return patientVitalsID;
        }

        public void setPatientVitalsID(Integer patientVitalsID) {
            this.patientVitalsID = patientVitalsID;
        }

        public String getPatientID() {
            return patientID;
        }

        public void setPatientID(String patientID) {
            this.patientID = patientID;
        }

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }

        public Boolean getIsDelete() {
            return isDelete;
        }

        public void setIsDelete(Boolean isDelete) {
            this.isDelete = isDelete;
        }

        public Object getPatientAge() {
            return patientAge;
        }

        public void setPatientAge(Object patientAge) {
            this.patientAge = patientAge;
        }

        public Object getSystolicBP() {
            return systolicBP;
        }

        public void setSystolicBP(Object systolicBP) {
            this.systolicBP = systolicBP;
        }

        public Object getDiastolicBP() {
            return diastolicBP;
        }

        public void setDiastolicBP(Object diastolicBP) {
            this.diastolicBP = diastolicBP;
        }

        public Object getBloodGroup() {
            return bloodGroup;
        }

        public void setBloodGroup(Object bloodGroup) {
            this.bloodGroup = bloodGroup;
        }

        public Object getHeight() {
            return height;
        }

        public void setHeight(Object height) {
            this.height = height;
        }

        public Object getWeight() {
            return weight;
        }

        public void setWeight(Object weight) {
            this.weight = weight;
        }

        public Object getSpo2() {
            return spo2;
        }

        public void setSpo2(Object spo2) {
            this.spo2 = spo2;
        }

        public Object getSugar() {
            return sugar;
        }

        public void setSugar(Object sugar) {
            this.sugar = sugar;
        }

        public Double getTemperature() {
            return temperature;
        }

        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }

        public Object getRespirationRate() {
            return respirationRate;
        }

        public void setRespirationRate(Object respirationRate) {
            this.respirationRate = respirationRate;
        }

        public Object getPulseRate() {
            return pulseRate;
        }

        public void setPulseRate(Object pulseRate) {
            this.pulseRate = pulseRate;
        }

        public String getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(String createdOn) {
            this.createdOn = createdOn;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public Object getCreatedUserType() {
            return createdUserType;
        }

        public void setCreatedUserType(Object createdUserType) {
            this.createdUserType = createdUserType;
        }

        public Object getComplaints() {
            return complaints;
        }

        public void setComplaints(Object complaints) {
            this.complaints = complaints;
        }

        public Object getCholesterolLDL() {
            return cholesterolLDL;
        }

        public void setCholesterolLDL(Object cholesterolLDL) {
            this.cholesterolLDL = cholesterolLDL;
        }

        public Object getCholesterolHDL() {
            return cholesterolHDL;
        }

        public void setCholesterolHDL(Object cholesterolHDL) {
            this.cholesterolHDL = cholesterolHDL;
        }

        public Object getTotalCholesterol() {
            return totalCholesterol;
        }

        public void setTotalCholesterol(Object totalCholesterol) {
            this.totalCholesterol = totalCholesterol;
        }

        public Object getComorbidity() {
            return comorbidity;
        }

        public void setComorbidity(Object comorbidity) {
            this.comorbidity = comorbidity;
        }

        public Object getDateDiseaseOnset() {
            return dateDiseaseOnset;
        }

        public void setDateDiseaseOnset(Object dateDiseaseOnset) {
            this.dateDiseaseOnset = dateDiseaseOnset;
        }

        public Object getRTPCRDate() {
            return rTPCRDate;
        }

        public void setRTPCRDate(Object rTPCRDate) {
            this.rTPCRDate = rTPCRDate;
        }

        public Object getRtpcr() {
            return rtpcr;
        }

        public void setRtpcr(Object rtpcr) {
            this.rtpcr = rtpcr;
        }

        public Object getRTPCRCTValue() {
            return rTPCRCTValue;
        }

        public void setRTPCRCTValue(Object rTPCRCTValue) {
            this.rTPCRCTValue = rTPCRCTValue;
        }

        public Object getHRCTDate() {
            return hRCTDate;
        }

        public void setHRCTDate(Object hRCTDate) {
            this.hRCTDate = hRCTDate;
        }

        public Object getHRCTScore() {
            return hRCTScore;
        }

        public void setHRCTScore(Object hRCTScore) {
            this.hRCTScore = hRCTScore;
        }

        public Object getCrp() {
            return crp;
        }

        public void setCrp(Object crp) {
            this.crp = crp;
        }

        public Object getEsr() {
            return esr;
        }

        public void setEsr(Object esr) {
            this.esr = esr;
        }

        public Object getInterleukln6() {
            return interleukln6;
        }

        public void setInterleukln6(Object interleukln6) {
            this.interleukln6 = interleukln6;
        }

        public Object getLdh() {
            return ldh;
        }

        public void setLdh(Object ldh) {
            this.ldh = ldh;
        }

        public Object getDDimer() {
            return dDimer;
        }

        public void setDDimer(Object dDimer) {
            this.dDimer = dDimer;
        }

        public Object getFerritin() {
            return ferritin;
        }

        public void setFerritin(Object ferritin) {
            this.ferritin = ferritin;
        }

        public Object getStethoscopeFileName() {
            return stethoscopeFileName;
        }

        public void setStethoscopeFileName(Object stethoscopeFileName) {
            this.stethoscopeFileName = stethoscopeFileName;
        }

        public Object getStethoscopeFilePath() {
            return stethoscopeFilePath;
        }

        public void setStethoscopeFilePath(Object stethoscopeFilePath) {
            this.stethoscopeFilePath = stethoscopeFilePath;
        }

        public Object getECGFileName() {
            return eCGFileName;
        }

        public void setECGFileName(Object eCGFileName) {
            this.eCGFileName = eCGFileName;
        }

        public Object getECGFilePath() {
            return eCGFilePath;
        }

        public void setECGFilePath(Object eCGFilePath) {
            this.eCGFilePath = eCGFilePath;
        }

    }
}
