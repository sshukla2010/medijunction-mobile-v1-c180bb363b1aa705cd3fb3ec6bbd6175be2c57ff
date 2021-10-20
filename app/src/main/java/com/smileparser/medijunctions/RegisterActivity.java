package com.smileparser.medijunctions;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.rilixtech.CountryCodePicker;
import com.smileparser.medijunctions.bean.ApiResponse;
import com.smileparser.medijunctions.bean.User;
import com.smileparser.medijunctions.customviews.LoadingDialog;
import com.smileparser.medijunctions.interfaces.ApiInterface;
import com.smileparser.medijunctions.utils.ApiClient;
import com.smileparser.medijunctions.utils.Constants;
import com.smileparser.medijunctions.utils.Global;
import com.smileparser.medijunctions.utils.SPreferenceManager;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edt_firstname, edt_lastname, edt_email, edt_Password, edt_mobile, edt_pincode, edt_emailOtp, edt_MobileOtp;
    Button btn_register, btn_cancel, btn_verifyEmail, btn_verifyMobile, btn_sendEmailOtp, btn_sendMobileOtp;
    public String Error = "";
    SharedPreferences mPrefs;
    private CountryCodePicker codePicker;
    private RadioGroup radioUserType;
    private RelativeLayout layoutVeryfyEmail, layoutVeryfyMobile;
    boolean isEmailVerified;
    boolean isMobileVerifired;
    String tmpPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Error = getApplicationContext().getString(R.string.error_msg_require_field);
        codePicker = (CountryCodePicker) findViewById(R.id.register_countrycodepicker);
        edt_firstname = findViewById(R.id.edt_register_firstname);
        edt_lastname = findViewById(R.id.edt_register_lastname);
        edt_email = findViewById(R.id.edt_register_email);
        edt_Password = findViewById(R.id.edt_register_password);
        edt_mobile = findViewById(R.id.register_edittext_mobilenumber);
        edt_pincode = findViewById(R.id.register_edittext_pincode);
        edt_emailOtp = findViewById(R.id.edtEmailOtp);
        edt_MobileOtp = findViewById(R.id.edtMobileOtp);

        btn_register = findViewById(R.id.btn_register_submit);
        btn_cancel = findViewById(R.id.btn_register_cancel);
        btn_verifyEmail = findViewById(R.id.btn_verify_email);
        btn_verifyMobile = findViewById(R.id.btn_verify_mobile);
        btn_sendEmailOtp = findViewById(R.id.btn_send_emailotp);
        btn_sendMobileOtp = findViewById(R.id.btn_send_mobileotp);

        radioUserType = (RadioGroup) findViewById(R.id.register_radiogroup_usertype);

        layoutVeryfyEmail = findViewById(R.id.layoutVeryfyEmail);
        layoutVeryfyMobile = findViewById(R.id.layoutVeryfyMobile);

        codePicker.registerPhoneNumberTextView(edt_mobile);

        edt_firstname.setOnClickListener(this);
        edt_lastname.setOnClickListener(this);
        edt_email.setOnClickListener(this);
        edt_Password.setOnClickListener(this);
        edt_mobile.setOnClickListener(this);
        edt_pincode.setOnClickListener(this);
        edt_emailOtp.setOnClickListener(this);
        edt_MobileOtp.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_verifyEmail.setOnClickListener(this);
        btn_verifyMobile.setOnClickListener(this);
        btn_sendEmailOtp.setOnClickListener(this);
        btn_sendMobileOtp.setOnClickListener(this);


        mPrefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        edt_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                isEmailVerified = false;
                if (Global.IsNotNull(s.toString().trim())) {
                    String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
                    Pattern pattern = Pattern.compile(EMAIL_PATTERN);
                    Matcher matchEmail = pattern.matcher(edt_email.getText().toString());
                    if (matchEmail.matches()) {
                        layoutVeryfyEmail.setVisibility(View.VISIBLE);
                    } else {
                        layoutVeryfyEmail.setVisibility(View.GONE);

                    }
                    btn_verifyEmail.setVisibility(View.VISIBLE);
                    edt_emailOtp.setVisibility(View.GONE);
                    edt_emailOtp.setText(null);
                    btn_sendEmailOtp.setVisibility(View.GONE);

                    tmpPassword = "";
                    isEmailVerified = false;
                }
            }
        });
        edt_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (Global.IsNotNull(s.toString().trim())) {
                    if (codePicker.isValid()) {
                        layoutVeryfyMobile.setVisibility(View.VISIBLE);
                    } else {
                        layoutVeryfyMobile.setVisibility(View.GONE);
                    }

                    btn_verifyMobile.setVisibility(View.VISIBLE);
                    edt_MobileOtp.setVisibility(View.GONE);
                    edt_MobileOtp.setText(null);
                    btn_sendMobileOtp.setVisibility(View.GONE);
                }
                tmpPassword = "";
                isMobileVerifired = false;
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.edt_register_firstname:
                edt_firstname.setError(null);
                break;
            case R.id.edt_register_lastname:
                edt_lastname.setError(null);
                break;
            case R.id.edt_register_email:
                edt_email.setError(null);
                break;
            case R.id.edt_register_password:
                edt_Password.setError(null);
                break;
            case R.id.edtEmailOtp:
                edt_emailOtp.setError(null);
                break;
            case R.id.edtMobileOtp:
                edt_MobileOtp.setError(null);
                break;
            case R.id.register_edittext_mobilenumber:
                edt_mobile.setError(null);
                break;
            case R.id.register_edittext_pincode:
                edt_pincode.setError(null);
                break;
            case R.id.btn_register_submit:
                register();
                break;
            case R.id.btn_register_cancel:
                onBackPressed();
                break;
            case R.id.btn_verify_email:
                if (Global.IsNotNull(edt_email.getText().toString().trim())) {
                    sendEmailOtp(edt_email.getText().toString().trim());
                }
                break;
            case R.id.btn_verify_mobile:
                if (Global.IsNotNull(edt_mobile.getText().toString().trim())) {
                    senMobileOtp(edt_mobile.getText().toString().trim());
                }
                break;
            case R.id.btn_send_emailotp:
                if (Global.IsNotNull(edt_emailOtp.getText().toString().trim())) {
                    verifyEmailOtp(edt_email.getText().toString().trim(), edt_emailOtp.getText().toString().trim());
                } else {
                    edt_emailOtp.setError(Error);
                }
                break;
            case R.id.btn_send_mobileotp:
                if (Global.IsNotNull(edt_MobileOtp.getText().toString().trim())) {
                    verifyMobileOtp(edt_mobile.getText().toString().trim(), edt_MobileOtp.getText().toString().trim());
                } else {
                    edt_MobileOtp.setError(Error);
                }
                break;
            default:
                break;
        }
    }

    private void sendEmailOtp(String email) {
        if (Global.isInternetOn(this)) {
            LoadingDialog.showLoadingDialog(RegisterActivity.this, "Please wait...");
            try {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("Email", email);

                Call<ApiResponse> apiResponseCall = apiInterface.sendEmailOTP(map);

                apiResponseCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        try {
                            LoadingDialog.cancelLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (Global.IsNotNull(response.body())) {
                            if (response.body().isResponse()) {
                                Toast.makeText(RegisterActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                tmpPassword = response.body().getResult().getAsJsonObject().get("EmailOTP").getAsString();
                                btn_verifyEmail.setVisibility(View.GONE);
                                edt_emailOtp.setVisibility(View.VISIBLE);
                                edt_emailOtp.requestFocus();
                                btn_sendEmailOtp.setVisibility(View.VISIBLE);
                            } else {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    Toast.makeText(RegisterActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (Global.IsNotNull(response.errorBody())) {
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = null;
                            try {
                                mJson = parser.parse(response.errorBody().string());
                                Gson gson = new Gson();
                                ApiResponse errorResponse = gson.fromJson(mJson, ApiResponse.class);

                                Toast.makeText(RegisterActivity.this, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }

                        } else {
                            Toast.makeText(RegisterActivity.this, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        }

                        try {
                            LoadingDialog.cancelLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                        try {
                            LoadingDialog.cancelLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(RegisterActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                try {
                    LoadingDialog.cancelLoading();
                } catch (Exception em) {
                    em.printStackTrace();
                }
                e.getMessage();
            }

        } else {
            Toast.makeText(this, "" + getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    private void verifyEmailOtp(String email, String otp) {

        if(Global.IsNotNull(tmpPassword))
        {
            String otpstr = md5(otp);
            if(Global.IsNotNull(otpstr))
            {
                if(otpstr.equals(tmpPassword))
                {
                    layoutVeryfyEmail.setVisibility(View.GONE);
                    btn_verifyEmail.setVisibility(View.VISIBLE);
                    edt_emailOtp.setVisibility(View.GONE);
                    btn_sendEmailOtp.setVisibility(View.GONE);
                    showEmailOrMobileDialog("Email");
                    isEmailVerified = true;
                    tmpPassword = "";
                }
                else
                {
                    Toast.makeText(this,"OTP not matched!", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this, getString(R.string.somethingwrong) + " try again", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
        }


    }

    private void senMobileOtp(String mobile) {
        if (Global.isInternetOn(this)) {
            LoadingDialog.showLoadingDialog(RegisterActivity.this, "Please wait...");

            try {

                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("Mobile", mobile);

                Call<ApiResponse> apiResponseCall = apiInterface.sendMobileOTP(map);

                apiResponseCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        try {
                            LoadingDialog.cancelLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (Global.IsNotNull(response.body())) {
                            if (response.body().isResponse()) {
                                Toast.makeText(RegisterActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                tmpPassword = response.body().getResult().getAsJsonObject().get("MobileOTP").getAsString();
                                btn_verifyMobile.setVisibility(View.GONE);
                                edt_MobileOtp.setVisibility(View.VISIBLE);
                                edt_MobileOtp.requestFocus();
                                btn_sendMobileOtp.setVisibility(View.VISIBLE);
                            } else {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    Toast.makeText(RegisterActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (Global.IsNotNull(response.errorBody())) {
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = null;
                            try {
                                mJson = parser.parse(response.errorBody().string());
                                Gson gson = new Gson();
                                ApiResponse errorResponse = gson.fromJson(mJson, ApiResponse.class);

                                Toast.makeText(RegisterActivity.this, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }

                        } else {
                            Toast.makeText(RegisterActivity.this, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        }

                        try {
                            LoadingDialog.cancelLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                        try {
                            LoadingDialog.cancelLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(RegisterActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                try {
                    LoadingDialog.cancelLoading();
                } catch (Exception em) {
                    em.printStackTrace();
                }
                e.getMessage();
            }
        } else {
            Toast.makeText(this, "" + getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    private void verifyMobileOtp(String mobile, String otp) {

        if(Global.IsNotNull(tmpPassword))
        {
            String otpstr = md5(otp);
            if(Global.IsNotNull(otpstr))
            {
                if(otpstr.equals(tmpPassword))
                {
                    layoutVeryfyMobile.setVisibility(View.GONE);
                    btn_verifyMobile.setVisibility(View.VISIBLE);
                    edt_MobileOtp.setVisibility(View.GONE);
                    btn_sendMobileOtp.setVisibility(View.GONE);
                    showEmailOrMobileDialog("Mobile");
                    isMobileVerifired = true;
                    tmpPassword = "";
                }
                else
                {
                    Toast.makeText(this,"OTP not matched!", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this, getString(R.string.somethingwrong) + " try again", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
        }
    }


    public boolean validate() {
        Matcher matchEmail, matchFirstname, matchMiddleName, matchLastname;
        boolean valid = true;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);

        Pattern ps = Pattern.compile("^[a-zA-Z ]+$");
        matchFirstname = ps.matcher(edt_firstname.getText().toString());
        matchLastname = ps.matcher(edt_lastname.getText().toString());


        if (!edt_email.getText().toString().isEmpty()) {
            //edt_Email.setError("This field is required");
            //valid=false;
            matchEmail = pattern.matcher(edt_email.getText().toString());
            if (!matchEmail.matches()) {
                edt_email.setError("Email is invalid");
                valid = false;
            }
        } else {
            edt_email.setError("This field is required");
            valid = false;
        }
        if (edt_firstname.getText().toString().isEmpty()) {
            edt_firstname.setError("This field is required");
            valid = false;
        }
        if (!codePicker.isValid()) {
            edt_mobile.setError("Mobile Number is not valid");
            valid = false;
        }
        /*if(!(edt_mobile.getText().toString().length()>=10 && edt_mobile.getText().toString().length()<=13))
        {
            edt_mobile.setError("Mobile Number is not valid");
            valid=false;
        }*/
        if (edt_mobile.getText().toString().isEmpty()) {
            edt_mobile.setError("This field is required");
            valid = false;
        }

        if (edt_Password.getText().toString().isEmpty()) {
            edt_Password.setError("This field is required");
            valid = false;
        }

        if (edt_pincode.getText().toString().isEmpty()) {
            edt_pincode.setError("This field is required");
            valid = false;
        } else if (Global.IsNotNull(edt_pincode.getText().toString())) {
            if (edt_pincode.getText().toString().length() != 6) {
                edt_pincode.setError("Pincode must be 6 charater");
                valid = false;
            }
        }


        return valid;
    }


    public void register() {
        if (validate()) {
            if (!isEmailOrMobileVarifired()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("You have to verify at least one of them Email OR Mobile");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            } else {
                if (Global.isInternetOn(this)) {
                    LoadingDialog.showLoadingDialog(RegisterActivity.this, "Please wait...");
                    String[] userdata = new String[9];

                    userdata[0] = edt_firstname.getText().toString();
                    if (Global.IsNotNull(edt_lastname.getText().toString())) {
                        userdata[1] = edt_lastname.getText().toString();
                    } else {
                        userdata[1] = "";
                    }
                    userdata[2] = edt_email.getText().toString();
                    userdata[3] = edt_Password.getText().toString();
                    userdata[4] = edt_mobile.getText().toString(); //String.valueOf(codePicker.getPhoneNumber().getNationalNumber());
                    userdata[5] = edt_pincode.getText().toString();
                    RadioButton radioButton = (RadioButton) radioUserType.findViewById(radioUserType.getCheckedRadioButtonId());
                    if (radioButton != null) {
                        if (radioButton.getText().toString().equals(getString(R.string.hint_patient))) {
                            userdata[6] = getString(R.string.hint_patient);
                        } else {
                            userdata[6] = getString(R.string.hint_doctor);
                        }
                    }
                    userdata[7] = codePicker.getSelectedCountryNameCode();
                    userdata[8] = Global.getAndroidSecureId(RegisterActivity.this);
                    new getUser().execute(userdata);
                } else {
                    Toast.makeText(this, "" + getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                }
           }
            //startActivity(new Intent(LoginActivity.this,Dashboard.class));
        }
    }

    private void showEmailOrMobileDialog(String data) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your " + data + " has been verified sucessfully.Please continue process for sign up");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private boolean isEmailOrMobileVarifired() {
        if (isEmailVerified) {
            return true;
        } else if (isMobileVerifired) {
            return true;
        } else {
            return false;
        }
    }

    private class getUser extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                HashMap<String, String> map = new HashMap<String, String>();

                map.put("FirstName", params[0]);
                map.put("LastName", params[1]);
                map.put("Email", params[2]);
                map.put("Password", params[3]);
                map.put("CountryCode",params[7]);
                map.put("Mobile", params[4]);
                map.put("Pincode", params[5]);
                map.put("Usertype", params[6]);
                map.put(Constants.HEADER_KEYS.DEVICEID,params[8]);


                Call<ApiResponse> apiResponseCall = apiInterface.getSignupUser(map);

                apiResponseCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        try {
                            LoadingDialog.cancelLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (Global.IsNotNull(response.body())) {
                            if (response.body().isResponse()) {
                                Gson gson = new Gson();

                                User user = gson.fromJson(response.body().getResult(), User.class);
                                if (Global.IsNotNull(user)) {
                                    String userstring = Global.getJsonString(user);
                                    SPreferenceManager sPreferenceManager = SPreferenceManager.getInstance(RegisterActivity.this);
                                    sPreferenceManager.setStringValue(SPreferenceManager.PREF_USERDETAILS, userstring);
                                    sPreferenceManager.setStringValue(SPreferenceManager.PREF_TOKEN,user.getToken());
                                    sPreferenceManager.setStringValue(SPreferenceManager.PREF_DEVICEID,params[8]);
                                    sPreferenceManager.setStringValue(SPreferenceManager.PREF_USERID,user.getId());

                                    Intent home = new Intent(RegisterActivity.this, Dashboard.class);
                                    home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(home);
                                    overridePendingTransition(0, 0);
                                    Toast.makeText(RegisterActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    Toast.makeText(RegisterActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (Global.IsNotNull(response.errorBody())) {
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = null;
                            try {
                                mJson = parser.parse(response.errorBody().string());
                                Gson gson = new Gson();
                                ApiResponse errorResponse = gson.fromJson(mJson, ApiResponse.class);

                                Toast.makeText(RegisterActivity.this, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }

                        } else {
                            Toast.makeText(RegisterActivity.this, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        }

                        try {
                            LoadingDialog.cancelLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                        try {
                            LoadingDialog.cancelLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(RegisterActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }

            return null;
        }

    }


    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
