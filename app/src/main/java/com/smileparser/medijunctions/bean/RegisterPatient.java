package com.smileparser.medijunctions.bean;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable
public class RegisterPatient implements Serializable {

    @DatabaseField(generatedId = true)
    @SerializedName("Id")
    public int Id;

    @DatabaseField
    @SerializedName("DBId")
    public String DBId;

    @DatabaseField(canBeNull = true)
    @SerializedName("PatientCode")
    public String PatientCode;


    @DatabaseField(canBeNull = true)
    @SerializedName("ProfileImage")
    public String ProfileImage;

    @DatabaseField(canBeNull = true)
    @SerializedName("FirstName")
    public String FirstName;

    @DatabaseField(canBeNull = true)
    @SerializedName("MiddleName")
    public String MiddleName;

    @DatabaseField(canBeNull = true)
    @SerializedName("LastName")
    public String LastName;

    @DatabaseField(canBeNull = true)
    @SerializedName("FatherName")
    public String FatherName;

    @DatabaseField(canBeNull = true)
    @SerializedName("HusbandName")
    public String HusbandName;

    @DatabaseField(canBeNull = true)
    @SerializedName("BloodGroup")
    public String BloodGroup;

    @DatabaseField(canBeNull = true)
    @SerializedName("Gender")
    public String Gender;


    @DatabaseField(canBeNull = true)
    @SerializedName("DateOfBirth")
    public String DateOfBirth;

    @DatabaseField(canBeNull = true)
    @SerializedName("Address")
    public String Address;

    @DatabaseField(canBeNull = true)
    @SerializedName("Mobile")
    public String Mobile;

    @DatabaseField(canBeNull = true)
    @SerializedName("Email")
    public String Email;

    @SerializedName("Allergies")
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public SerializedList<Allergy> Allergies;

    @DatabaseField(canBeNull = true)
    @SerializedName("OtherAllergy")
    public String OtherAllergy;

    @SerializedName("HealthConditions")
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public SerializedList<HealthCondition> HealthConditions;

    @DatabaseField(canBeNull = true)
    @SerializedName("OtherHealth")
    public String OtherHealth;

    @DatabaseField(canBeNull = true)
    @SerializedName("IdProofNo1")
    public String IdProofNo1;

    @DatabaseField(canBeNull = true)
    @SerializedName("IdProofType1")
    public String IdProofType1;

    @DatabaseField(canBeNull = true)
    @SerializedName("IdProofType2")
    public String IdProofType2;

    @DatabaseField(canBeNull = true)
    @SerializedName("IdProofNo2")
    public String IdProofNo2;

    @DatabaseField(canBeNull = true)
    @SerializedName("UserId")
    public String UserId;

    @DatabaseField(canBeNull = false)
    @SerializedName("IsInserted")
    public boolean IsInserted;


    @SerializedName("FamilyCount")
    public int FamilyCount;

    @SerializedName("FamilyList")
    public String FamilyList;

    @SerializedName("CountryCode")
    public String CountryCode;

    public RegisterPatient() {
    }

    public RegisterPatient(int id, String DBId, String patientCode, String profileImage, String firstName, String middleName, String lastName, String fatherName, String husbandName, String bloodGroup, String gender, String dateOfBirth, String address, String mobile, String email, SerializedList<Allergy> allergies, String otherAllergy, SerializedList<HealthCondition> healthConditions, String otherHealth, String idProofNo1, String idProofType1, String idProofType2, String idProofNo2, String userId, boolean isInserted,String countrycode) {
        Id = id;
        this.DBId = DBId;
        PatientCode = patientCode;
        ProfileImage = profileImage;
        FirstName = firstName;
        MiddleName = middleName;
        LastName = lastName;
        FatherName = fatherName;
        HusbandName = husbandName;
        BloodGroup = bloodGroup;
        Gender = gender;
        DateOfBirth = dateOfBirth;
        Address = address;
        Mobile = mobile;
        Email = email;
        Allergies = allergies;
        OtherAllergy = otherAllergy;
        HealthConditions = healthConditions;
        OtherHealth = otherHealth;
        IdProofNo1 = idProofNo1;
        IdProofType1 = idProofType1;
        IdProofType2 = idProofType2;
        IdProofNo2 = idProofNo2;
        UserId = userId;
        IsInserted = isInserted;
        CountryCode = countrycode;
    }

    public RegisterPatient(int id, String DBId, String patientCode, String profileImage, String firstName, String middleName, String lastName, String fatherName, String husbandName, String bloodGroup, String gender, String dateOfBirth, String address, String mobile, String email, SerializedList<Allergy> allergies, String otherAllergy, SerializedList<HealthCondition> healthConditions, String otherHealth, String idProofNo1, String idProofType1, String idProofType2, String idProofNo2, String userId, boolean isInserted, int familyCount, String familyList,String countrycode) {
        Id = id;
        this.DBId = DBId;
        PatientCode = patientCode;
        ProfileImage = profileImage;
        FirstName = firstName;
        MiddleName = middleName;
        LastName = lastName;
        FatherName = fatherName;
        HusbandName = husbandName;
        BloodGroup = bloodGroup;
        Gender = gender;
        DateOfBirth = dateOfBirth;
        Address = address;
        Mobile = mobile;
        Email = email;
        Allergies = allergies;
        OtherAllergy = otherAllergy;
        HealthConditions = healthConditions;
        OtherHealth = otherHealth;
        IdProofNo1 = idProofNo1;
        IdProofType1 = idProofType1;
        IdProofType2 = idProofType2;
        IdProofNo2 = idProofNo2;
        UserId = userId;
        IsInserted = isInserted;
        FamilyCount = familyCount;
        FamilyList = familyList;
        CountryCode = countrycode;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getDBId() {
        return DBId;
    }

    public void setDBId(String DBId) {
        this.DBId = DBId;
    }

    public String getPatientCode() {
        return PatientCode;
    }

    public void setPatientCode(String patientCode) {
        PatientCode = patientCode;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public void setMiddleName(String middleName) {
        MiddleName = middleName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getFatherName() {
        return FatherName;
    }

    public void setFatherName(String fatherName) {
        FatherName = fatherName;
    }

    public String getHusbandName() {
        return HusbandName;
    }

    public void setHusbandName(String husbandName) {
        HusbandName = husbandName;
    }

    public String getBloodGroup() {
        return BloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        BloodGroup = bloodGroup;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public SerializedList<Allergy> getAllergies() {
        return Allergies;
    }

    public void setAllergies(SerializedList<Allergy> allergies) {
        Allergies = allergies;
    }

    public String getOtherAllergy() {
        return OtherAllergy;
    }

    public void setOtherAllergy(String otherAllergy) {
        OtherAllergy = otherAllergy;
    }

    public SerializedList<HealthCondition> getHealthConditions() {
        return HealthConditions;
    }

    public void setHealthConditions(SerializedList<HealthCondition> healthConditions) {
        HealthConditions = healthConditions;
    }

    public String getOtherHealth() {
        return OtherHealth;
    }

    public void setOtherHealth(String otherHealth) {
        OtherHealth = otherHealth;
    }

    public String getIdProofNo1() {
        return IdProofNo1;
    }

    public void setIdProofNo1(String idProofNo1) {
        IdProofNo1 = idProofNo1;
    }

    public String getIdProofType1() {
        return IdProofType1;
    }

    public void setIdProofType1(String idProofType1) {
        IdProofType1 = idProofType1;
    }

    public String getIdProofType2() {
        return IdProofType2;
    }

    public void setIdProofType2(String idProofType2) {
        IdProofType2 = idProofType2;
    }

    public String getIdProofNo2() {
        return IdProofNo2;
    }

    public void setIdProofNo2(String idProofNo2) {
        IdProofNo2 = idProofNo2;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public boolean isInserted() {
        return IsInserted;
    }

    public void setInserted(boolean inserted) {
        IsInserted = inserted;
    }

    public int getFamilyCount() {
        return FamilyCount;
    }

    public void setFamilyCount(int familyCount) {
        FamilyCount = familyCount;
    }

    public String getFamilyList() {
        return FamilyList;
    }

    public void setFamilyList(String familyList) {
        FamilyList = familyList;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String countryCode) {
        CountryCode = countryCode;
    }
}
