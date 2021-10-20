package com.smileparser.medijunctions.interfaces;


import com.smileparser.medijunctions.bean.ApiResponse;
import com.smileparser.medijunctions.bean.CheckAppVersionRequest;
import com.smileparser.medijunctions.bean.DeleteSync;
import com.smileparser.medijunctions.bean.RegisterPatient;
import com.smileparser.medijunctions.bean.VitalResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by hardik on 25/4/18.
 */

public interface ApiInterface {

    //http://medijunctions.com/api/User/LoginUser
    @POST("User/LoginUser")
    Call<ApiResponse> getLoginUser(@Body HashMap<String, String> keyset);

    @POST("User/AddPatientVitals")
    Call<VitalResponse> getpatientVitals(@Body HashMap<String, String> keyset);

    @POST("User/AddPatientECGVitals")
    @Multipart
    Call<ApiResponse> uploadECGVitals(@Part MultipartBody.Part file,
                                      @Part("PatientId") RequestBody appointmentId,
                                      @Part("LoggedInUserId") RequestBody LoggedInUserId,
                                      @Part("PatientVitalId") RequestBody PatientVitalId);

    @POST("User/AddPatientStethoscopeVitals")
    @Multipart
    Call<ApiResponse> uploadStethoscopeVitals(@Part MultipartBody.Part file,
                                              @Part("PatientId") RequestBody appointmentId,
                                              @Part("LoggedInUserId") RequestBody LoggedInUserId,
                                              @Part("PatientVitalId") RequestBody PatientVitalId);


    @POST("User/SignupUser")
    Call<ApiResponse> getSignupUser(@Body HashMap<String, String> keyset);

    @POST("User/ForgotPassword")
    Call<ApiResponse> forgotPassword(@Body HashMap<String, String> keyset);

    //https://medijunction.co.in/api/User/SendMobileOTP
    @POST("User/SendMobileOTP")
    Call<ApiResponse> sendMobileOTP(@Body HashMap<String, String> keyset);

    //https://medijunction.co.in/api/User/SendEmailOTP
    @POST("User/SendEmailOTP")
    Call<ApiResponse> sendEmailOTP(@Body HashMap<String, String> keyset);

    //https://medijunction.co.in/api/User/VerifyUserMobileOtp
    @POST("User/VerifyUserMobileOtp")
    Call<ApiResponse> verifyUserMobileOtp(@Body HashMap<String, String> keyset);

    //https://medijunction.co.in/api/User/VerifyUserEmailOtp
    @POST("User/VerifyUserEmailOtp")
    Call<ApiResponse> verifyUserEmailOtp(@Body HashMap<String, String> keyset);

    //https://medijunction.co.in/api/User/CheckAPKVersionForUpdate
    @POST("User/CheckAPKVersionForUpdate")
    Call<ApiResponse> checkAPKVersionForUpdate(@Body CheckAppVersionRequest checkAppVersionRequest);


    // New Api With header


    //http://medijunctions.com/api/User/RegisterPatient
    @POST("User/RegisterPatient")
    Call<ApiResponse> registerPatientApi(@HeaderMap Map<String, String> header,@Body List<RegisterPatient> registerPatient);

    //https://medijunction.co.in//api/User/GetActiveDoctorList
    @POST("User/GetActiveDoctorList")
    Call<ApiResponse> getActiveDoctorList(@HeaderMap Map<String, String> header, @Body HashMap<String, Object> keyset);

    //https://medijunction.co.in/api/User/CouponList
    @POST("User/CouponList")
    Call<ApiResponse> getCouponsList(@HeaderMap Map<String, String> header,@Body HashMap<String, Object> keyset);

    //http://medijunctions.com/api/User/PatientSync
    @POST("User/PatientSync")
    Call<ApiResponse> patientSync(@HeaderMap Map<String, String> header,@Body List<RegisterPatient> registerPatient);

    //http://medijunctions.com/api/User/AllergyList
    @POST("User/AllergyList")
    Call<ApiResponse> getAllergyList(@HeaderMap Map<String, String> header);

    //http://medijunctions.com/api/User/HealthConditionList
    @POST("User/HealthConditionList")
    Call<ApiResponse> getHealthConditionList(@HeaderMap Map<String, String> header);

    //http://medijunctions.com/api/User/SearchPatient
    @POST("User/SearchPatient")
    Call<ApiResponse> getRegisteredPatientList(@HeaderMap Map<String, String> header,@Body HashMap<String, String> keyset);

    @POST("User/EditPatient")
    Call<ApiResponse> getRegisteredEditPatient(@HeaderMap Map<String, String> header,@Body HashMap<String, String> keyset);

    //https://medijunctions.com/api/User/DeletePatient
    @POST("User/DeletePatient")
    Call<ApiResponse> deletePatient(@HeaderMap Map<String, String> header,@Body HashMap<String, String> keyset);

    //https://medijunctions.com/api/User/DeletePatientSync
    @POST("User/DeletePatientSync")
    Call<ApiResponse> deletePatientSync(@HeaderMap Map<String, String> header,@Body List<DeleteSync> deleteSyncList);


    ////////////////////////////////////////////////////////////////////////////////

    //https://medijunction.co.in/api/User/AddCoupon
    @POST("User/AddCoupon")
    Call<ApiResponse> addCoupon(@HeaderMap Map<String, String> header,@Body HashMap<String, Object> keyset);

    //https://medijunction.co.in/api/User/MyCouponList
    @POST("User/MyCouponList")
    Call<ApiResponse> myCouponList(@HeaderMap Map<String, String> header,@Body HashMap<String, String> keyset);

    //https://medijunction.co.in/api/User/VirtualLogin
    @POST("User/VirtualLogin")
    Call<ApiResponse> virtualLogin(@HeaderMap Map<String, String> header,@Body HashMap<String, String> keyset);

    @POST("User/SpecialityList")
    Call<ApiResponse> getSpecialityList(@HeaderMap Map<String, String> header);

    //https://medijunction.co.in/api/User/LanguageList
    @POST("User/LanguageList")
    Call<ApiResponse> getLanguageList(@HeaderMap Map<String, String> header);

    //https://medijunction.co.in/api/User/DoctorList
    @POST("User/DoctorList")
    Call<ApiResponse> getDoctorList(@HeaderMap Map<String, String> header,@Body HashMap<String, Object> keyset);

    //https://medijunction.co.in/api/User/AddDrCoupon
    @POST("User/AddDrCoupon")
    Call<ApiResponse> addDrCoupon(@HeaderMap Map<String, String> header,@Body HashMap<String, Object> keyset);

    //https://medijunction.co.in/api/User/CaseHistory
    @POST("User/CaseHistory")
    Call<ApiResponse> caseHistory(@HeaderMap Map<String, String> header,@Body HashMap<String, String> keyset);

    //https://medijunction.co.in/api/User/SearchCases
    @POST("User/SearchCases")
    Call<ApiResponse> searchCases(@HeaderMap Map<String, String> header,@Body HashMap<String, String> keyset);

    @POST("User/AddLabDocuments")
    @Multipart
    Call<ApiResponse> uploadDocument(@HeaderMap Map<String, String> header,@Part MultipartBody.Part file,
                                     @Part("AppointmentId") RequestBody appointmentId,
                                     @Part("LoggedInUserId") RequestBody loggedInUserId);

    @POST("User/ReConsultation")
    Call<ApiResponse> submitReconsultation(@HeaderMap Map<String, String> header,@Body HashMap<String, String> keyset);

    //https://devgv.medijunction.co.in/api/User/MissedCallLog
    @POST("User/MissedCallLog")
    Call<ApiResponse> getMissedCallLog(@HeaderMap Map<String, String> header,@Body HashMap<String, String> keyset);

}
