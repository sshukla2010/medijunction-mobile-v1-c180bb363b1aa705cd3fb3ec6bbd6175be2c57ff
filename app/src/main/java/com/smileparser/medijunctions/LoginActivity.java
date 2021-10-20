package com.smileparser.medijunctions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.smileparser.medijunctions.bean.ApiResponse;
import com.smileparser.medijunctions.bean.User;
import com.smileparser.medijunctions.customviews.LoadingDialog;
import com.smileparser.medijunctions.dialogs.DisplayMessagePopup;
import com.smileparser.medijunctions.dialogs.ForgotPasswordDialog;
import com.smileparser.medijunctions.interfaces.ApiInterface;
import com.smileparser.medijunctions.utils.ApiClient;
import com.smileparser.medijunctions.utils.Global;
import com.smileparser.medijunctions.utils.SPreferenceManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edt_UserName, edt_Password, edt_clinicid, edt_pincode;
    Button btn_Login;
    TextView btn_signUp,btn_forgotPassword;
    public String Error = "";
    SharedPreferences mPrefs;
    private static final int REQUEST_FOR_PHONESTATE = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Error = getApplicationContext().getString(R.string.error_msg_require_field);
        edt_UserName = (EditText) findViewById(R.id.edt_signin_username);
        edt_Password = (EditText) findViewById(R.id.edt_signin_password);
        edt_pincode = (EditText) findViewById(R.id.edt_signin_pincode);
        edt_clinicid = (EditText) findViewById(R.id.edt_signin_clinicid);
        btn_Login = (Button) findViewById(R.id.btn_login_submit);
        btn_signUp = findViewById(R.id.btn_login_singup);
        btn_forgotPassword = findViewById(R.id.btn_forgotPassword);
        edt_UserName.setOnClickListener(this);
        edt_Password.setOnClickListener(this);
        edt_pincode.setOnClickListener(this);
        edt_clinicid.setOnClickListener(this);
        btn_Login.setOnClickListener(this);
        btn_signUp.setOnClickListener(this);
        btn_forgotPassword.setOnClickListener(this);

        mPrefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        String t1 = "New User? ";
        String t2 = "Sign Up";

        t1 = Global.getColoredSpanned(t1,"#ffffff");
        t2 = Global.getColoredSpanned(t2,"#F88F21");

        if (Build.VERSION.SDK_INT >= 24) {
            btn_signUp.setText(Html.fromHtml(t1 + t2, HtmlCompat.FROM_HTML_MODE_LEGACY)); // for 24 API  and more
        } else {
            btn_signUp.setText(Html.fromHtml(t1 + t2)); // or for older API
        }
        //checkPermissionsForPhoneState();

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.edt_signin_username:

                edt_UserName.setError(null);

                break;

            case R.id.btn_login_submit:
               /* if(checkPermissionsForPhoneState())
                {
                    Login();
                }*/
               Login();
                break;

            case R.id.btn_login_singup:
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                break;
            case R.id.btn_forgotPassword:
                forgotPasWord();
                break;
        }
    }

    private void forgotPasWord() {
        ForgotPasswordDialog mBottomSheetDialog = new ForgotPasswordDialog(LoginActivity.this)
        {
            @Override
            public void onSubmit(String value) {
                super.onSubmit(value);
                if (Global.isInternetOn(LoginActivity.this)) {
                    LoadingDialog.showLoadingDialog(LoginActivity.this, "Please wait...");
                    String[] userdata = new String[1];
                    userdata[0] = value;
                    new forgotPassword().execute(userdata);
                    super.dismiss();
                }
                else
                {
                    Toast.makeText(LoginActivity.this, ""+ getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onSigupClick() {
                super.onSigupClick();
                btn_signUp.performClick();
                super.dismiss();
            }
        };
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.show();
    }

    private void displayforgotPasswordMessage(String message,boolean isSuccess)
    {
        DisplayMessagePopup displayMessagePopup = new DisplayMessagePopup(LoginActivity.this,message,isSuccess);
        displayMessagePopup.setCancelable(false);
        displayMessagePopup.show();
    }

    public void Login()
    {
        if (validation()) {
            if (Global.isInternetOn(this)) {
                LoadingDialog.showLoadingDialog(LoginActivity.this, "Please wait...");
                String[] userdata = new String[3];
                userdata[0] = edt_UserName.getText().toString();
                userdata[1] = edt_Password.getText().toString();
                userdata[2] = Global.getAndroidSecureId(LoginActivity.this);
              /*  try {
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                       if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                String deviceID = telephonyManager.getDeviceId();
                    if(Global.IsNotNull(deviceID))
                    {
                        userdata[2] = deviceID;
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }*/
                new getUser().execute(userdata);
            }
            else
            {
                Toast.makeText(this, ""+getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            }
            //startActivity(new Intent(LoginActivity.this,Dashboard.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }


    public boolean checkPermissionsForPhoneState()
    {
        Context context;
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity)LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_FOR_PHONESTATE);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public boolean validation()
    {
        Matcher matchEmail;
        boolean isValid = true;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);


        if(!Global.IsNotNull(edt_UserName.getText().toString().trim()))
        {
            edt_UserName.setError(Error);
            isValid = false;
        }
        /*else
        {
            if(edt_UserName.getText().toString().contains("@")) {
                matchEmail = pattern.matcher(edt_UserName.getText().toString());
                if (!matchEmail.matches()) {
                    edt_UserName.setError("Email is invalid");
                    isValid = false;
                }
            }
            else
            {

            }
        }*/

        if(!Global.IsNotNull(edt_Password.getText().toString().trim()))
        {
            edt_Password.setError(Error);
            isValid = false;
        }

       /* if(!Global.IsNotNull(edt_pincode.getText().toString().trim()))
        {
            edt_pincode.setError(Error);
            isValid = false;
        }

        if(!Global.IsNotNull(edt_clinicid.getText().toString().trim()))
        {
            edt_clinicid.setError(Error);
            isValid = false;
        }*/
        return isValid;
    }


    private class getUser extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... params) {
            try {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                HashMap<String, String> map = new HashMap<String, String>();

                map.put("Email", params[0]);
                map.put("Password", params[1]);
                map.put("DeviceId",params[2]);
                //map.put("IMEINo",params[2]);

                Call<ApiResponse> apiResponseCall = apiInterface.getLoginUser(map);

                apiResponseCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        try {
                            LoadingDialog.cancelLoading();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        if (Global.IsNotNull(response.body())) {
                            if (response.body().isResponse()) {
                                Gson gson = new Gson();

                                User user = gson.fromJson(response.body().getResult(), User.class);
                                if (Global.IsNotNull(user)) {
                                    String userstring = Global.getJsonString(user);
                                    SPreferenceManager sPreferenceManager = SPreferenceManager.getInstance(LoginActivity.this);
                                    sPreferenceManager.setStringValue(SPreferenceManager.PREF_USERDETAILS, userstring);
                                    sPreferenceManager.setStringValue(SPreferenceManager.PREF_TOKEN,user.getToken());
                                    sPreferenceManager.setStringValue(SPreferenceManager.PREF_DEVICEID,params[2]);
                                    sPreferenceManager.setStringValue(SPreferenceManager.PREF_USERID,user.getId());
                                    Intent home = new Intent(LoginActivity.this, Dashboard.class);
                                    home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(home);
                                    overridePendingTransition(0,0);
                                    finish();
                                    Toast.makeText(LoginActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    Toast.makeText(LoginActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (Global.IsNotNull(response.errorBody())) {
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = null;
                            try {
                                mJson = parser.parse(response.errorBody().string());
                                Gson gson = new Gson();
                                ApiResponse errorResponse = gson.fromJson(mJson, ApiResponse.class);

                                Toast.makeText(LoginActivity.this, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        }

                        try {
                            LoadingDialog.cancelLoading();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                        try {
                            LoadingDialog.cancelLoading();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        Toast.makeText(LoginActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }

            return null;
        }

    }

    private class forgotPassword extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... params) {
            try {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                HashMap<String, String> map = new HashMap<String, String>();

                map.put("Email", params[0]);

                Call<ApiResponse> apiResponseCall = apiInterface.forgotPassword(map);

                apiResponseCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        try {
                            LoadingDialog.cancelLoading();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        if (Global.IsNotNull(response.body())) {

                                if (Global.IsNotNull(response.body().getMessage())) {
                                    displayforgotPasswordMessage(response.body().getMessage(),response.body().isResponse());
                                }

                        } else if (Global.IsNotNull(response.errorBody())) {
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = null;
                            try {
                                mJson = parser.parse(response.errorBody().string());
                                Gson gson = new Gson();
                                ApiResponse errorResponse = gson.fromJson(mJson, ApiResponse.class);
                                Toast.makeText(LoginActivity.this, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        }

                        try {
                            LoadingDialog.cancelLoading();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                        try {
                            LoadingDialog.cancelLoading();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        Toast.makeText(LoginActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }

            return null;
        }

    }
}
