package com.smileparser.medijunctions.bean;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class CaseData {

  @SerializedName(value = "AppointmentId")
  private String appointmentId;
  @SerializedName(value = "CaseNo")
  private String caseNo;
  @SerializedName(value = "CHAMP_NAME")
  private String champName;
  @SerializedName(value = "USER_NAME")
  private String userName;
  @SerializedName(value = "CreatedDate")
  private String createdDate;
  @SerializedName(value = "Fees")
  private double fees;
  @SerializedName(value = "InjectionFees")
  private double injectionFees;
  @SerializedName(value = "Discount")
  private double discount;


  public String getAppointmentId() {
    return appointmentId;
  }

  public void setAppointmentId(String appointmentId) {
    this.appointmentId = appointmentId;
  }

  public String getCaseNo() {
    return caseNo;
  }

  public void setCaseNo(String caseNo) {
    this.caseNo = caseNo;
  }

  public String getChampName() {
    return champName;
  }

  public void setChampName(String champName) {
    this.champName = champName;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }

  public double getFees() {
    return fees;
  }

  public void setFees(double fees) {
    this.fees = fees;
  }

  public double getInjectionFees() {
    return injectionFees;
  }

  public void setInjectionFees(double injectionFees) {
    this.injectionFees = injectionFees;
  }

  public double getDiscount() {
    return discount;
  }

  public void setDiscount(double discount) {
    this.discount = discount;
  }
}
