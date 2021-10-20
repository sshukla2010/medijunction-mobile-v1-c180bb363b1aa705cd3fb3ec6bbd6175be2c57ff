package com.smileparser.medijunctions.bean;

import com.google.gson.annotations.SerializedName;

public class DoctorProfile {


    @SerializedName("Id")
    long Id;

    @SerializedName("CouponId")
    long CouponId;

    @SerializedName("Promocode")
    String PromoCode;

    @SerializedName("ImagePath")
    String ImagePath;

    @SerializedName("Name")
    String Name;

    @SerializedName("Degree")
    String Degree;

    @SerializedName("Speciality")
    String Speciality;

    @SerializedName("Rate")
    String Rate;

    @SerializedName("OldRate")
    String OldRate;


    @SerializedName("Language")
    String Language;

    @SerializedName("Qty")
    long Qty = 1;

    @SerializedName("OrganizationName")
    String OrganizationName;

    @SerializedName("DiscountDesc")
    String DiscountDesc;

    @SerializedName("CouponDescription")
    String CouponDescription;

    @SerializedName("Validity")
    String Validity;

    @SerializedName("IsAddCoupon")
    boolean IsAddCoupon = false;

    @SerializedName("IsConvertToMediCoin")
    boolean IsConvertToMediCoin = false;

    @SerializedName("Status")
    String Status;

    boolean isSelect = false;


    public DoctorProfile() {
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public long getCouponId() {
        return CouponId;
    }

    public void setCouponId(long couponId) {
        CouponId = couponId;
    }

    public String getPromoCode() {
        return PromoCode;
    }

    public void setPromoCode(String promoCode) {
        PromoCode = promoCode;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDegree() {
        return Degree;
    }

    public void setDegree(String degree) {
        Degree = degree;
    }

    public String getSpeciality() {
        return Speciality;
    }

    public void setSpeciality(String speciality) {
        Speciality = speciality;
    }

    public String getRate() {
        return Rate;
    }

    public void setRate(String rate) {
        Rate = rate;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    public long getQty() {
        return Qty;
    }

    public void setQty(long qty) {
        Qty = qty;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getOrganizationName() {
        return OrganizationName;
    }

    public void setOrganizationName(String organizationName) {
        OrganizationName = organizationName;
    }

    public String getOldRate() {
        return OldRate;
    }

    public void setOldRate(String oldRate) {
        OldRate = oldRate;
    }

    public String getDiscountDesc() {
        return DiscountDesc;
    }

    public void setDiscountDesc(String discountDesc) {
        DiscountDesc = discountDesc;
    }

    public String getCouponDescription() {
        return CouponDescription;
    }

    public void setCouponDescription(String couponDescription) {
        CouponDescription = couponDescription;
    }

    public String getValidity() {
        return Validity;
    }

    public void setValidity(String validity) {
        Validity = validity;
    }

    public boolean isAddCoupon() {
        return IsAddCoupon;
    }

    public void setAddCoupon(boolean addCoupon) {
        IsAddCoupon = addCoupon;
    }

    public boolean isConvertToMediCoin() {
        return IsConvertToMediCoin;
    }

    public void setConvertToMediCoin(boolean convertToMediCoin) {
        IsConvertToMediCoin = convertToMediCoin;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
