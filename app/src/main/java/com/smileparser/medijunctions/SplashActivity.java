package com.smileparser.medijunctions;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.smileparser.medijunctions.bean.ApiResponse;
import com.smileparser.medijunctions.bean.CheckAppVeriosnResponse;
import com.smileparser.medijunctions.bean.CheckAppVersionRequest;
import com.smileparser.medijunctions.bean.User;
import com.smileparser.medijunctions.customviews.LoadingDialog;
import com.smileparser.medijunctions.dialogs.AppVersionConfirmDialog;
import com.smileparser.medijunctions.interfaces.ApiInterface;
import com.smileparser.medijunctions.utils.ApiClient;
import com.smileparser.medijunctions.utils.Constants;
import com.smileparser.medijunctions.utils.Global;
import com.smileparser.medijunctions.utils.SPreferenceManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    Context mContext;
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Global.getInstance(getApplicationContext()).onCreate();

        mContext = this;
        openNextScreen();
        /*if (Global.isInternetOn(mContext)) {
            checkAppVersion();
        }
        else
        {
            openNextScreen();
        }*/

       /* */
    }

    public void openNextScreen()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                User user = Global.getUserDetails(SplashActivity.this);
                if(Global.IsNotNull(user))
                {
                    Intent home = new Intent(SplashActivity.this, Dashboard.class);
                    startActivity(home);
                }
                else {
                    Intent login = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(login);
                }
                finish();

            }
        }, 2500);
    }

    private void checkAppVersion() {

        if (!Global.isInternetOn(mContext)) {
            return;
        }

        try {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            CheckAppVersionRequest checkAppVersionRequest = new CheckAppVersionRequest();
            checkAppVersionRequest.setAPKVersionCode(BuildConfig.VERSION_CODE);
            checkAppVersionRequest.setAPKVersionName(BuildConfig.VERSION_NAME);

            Call<ApiResponse> apiResponseCall = apiInterface.checkAPKVersionForUpdate(checkAppVersionRequest);

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

                        if (Global.IsNotNull(response.body().isResponse())) {
                            Gson gson = new Gson();
                            CheckAppVeriosnResponse checkAppVeriosnResponse = gson.fromJson(response.body().getResult(), CheckAppVeriosnResponse.class);
                            if(checkAppVeriosnResponse.isForceUpdate() && checkAppVeriosnResponse.isUpdate())
                            {
                                showAppVersion(checkAppVeriosnResponse.isForceUpdate(),checkAppVeriosnResponse.getUpdateDetails());
                            }
                            else
                            {
                                if(checkAppVeriosnResponse.isUpdate())
                                {
                                    showAppVersion(checkAppVeriosnResponse.isForceUpdate(),checkAppVeriosnResponse.getUpdateDetails());
                                }
                                else
                                {
                                    openNextScreen();
                                }
                            }

                        }

                    } else if (Global.IsNotNull(response.errorBody())) {
                        JsonParser parser = new JsonParser();
                        JsonElement mJson = null;
                        try {
                            mJson = parser.parse(response.errorBody().string());
                            Gson gson = new Gson();
                            ApiResponse errorResponse = gson.fromJson(mJson, ApiResponse.class);
                            Toast.makeText(SplashActivity.this, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch (Exception e) {
                            e.getMessage();
                        }
                        openNextScreen();
                    } else {
                        Toast.makeText(SplashActivity.this, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        openNextScreen();
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
                    Toast.makeText(SplashActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    openNextScreen();

                }
            });

        }
        catch (Exception e)
        {
            try {
                LoadingDialog.cancelLoading();
            }
            catch (Exception et)
            {
                et.printStackTrace();
            }
            e.printStackTrace();
            openNextScreen();

        }
    }

    private void showAppVersion(final boolean isForceUpdate, final String updateMessage) {
        if (!(SplashActivity.this).isFinishing()) {
            if (isForceUpdate) {
                showForceUpdate(updateMessage);
            } else {
                if (isUpdateAvailablePopUpDisplay()) {
                    showUpdateAvailable(updateMessage);
                }
                else
                {
                    openNextScreen();
                }
            }
        }
    }

    private void showUpdateAvailable(final String updateMessage) {
        showAppVersionDialog(getString(R.string.app_version_update_available), TextUtils.isEmpty(updateMessage) ? getString(R.string.app_version_force_update) : updateMessage, false, new AppVersionConfirmDialog.DialogCallbacks() {
            @Override
            public void onUpdateClicked() {
                clickToOpenPlayStore();
                savePopUpDisplayTime(SplashActivity.this);
            }

            @Override
            public void onCancelClicked() {
                savePopUpDisplayTime(SplashActivity.this);
                openNextScreen();
            }

            @Override
            public void onForceUpdateClicked() {

            }
        });
    }

    private void showForceUpdate(final String updateMessage) {
        showAppVersionDialog(getString(R.string.app_version_update_available), TextUtils.isEmpty(updateMessage) ? getString(R.string.app_version_force_update) : updateMessage, true, new AppVersionConfirmDialog.DialogCallbacks() {

            @Override
            public void onUpdateClicked() {

            }

            @Override
            public void onCancelClicked() {

            }

            @Override
            public void onForceUpdateClicked() {
                clickToOpenPlayStore();
            }
        });
    }

    public void showAppVersionDialog(String text, String subText, boolean isForceUpdate, AppVersionConfirmDialog.DialogCallbacks dialogCallbacks)
    {
        try {
            FragmentManager fm = getSupportFragmentManager();
            AppVersionConfirmDialog dialogFragment = new AppVersionConfirmDialog();
            dialogFragment.setCallbacks(dialogCallbacks);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.BUNDLE_KEYS.BUNDLE_DATA_1, text);
            bundle.putString(Constants.BUNDLE_KEYS.BUNDLE_DATA_2, subText);
            bundle.putBoolean(Constants.BUNDLE_KEYS.BUNDLE_DATA_3, isForceUpdate);
            dialogFragment.setArguments(bundle);
            dialogFragment.setCancelable(false);
            dialogFragment.show(fm, "");

        } catch (IllegalStateException e) {
            e.printStackTrace();
            openNextScreen();
        }
    }

    private void clickToOpenPlayStore() {
        try {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            Log.d(TAG,"clickToOpenPlayStore(): error :" + e.getMessage());
        }
    }

    private void savePopUpDisplayTime(final Context mContext) {
        SPreferenceManager.getInstance(mContext).setLongValue(SPreferenceManager.KEY_UPDATE_AVAILABLE_TIME, System.currentTimeMillis());
    }

    private boolean isUpdateAvailablePopUpDisplay() {
        long prefTime = SPreferenceManager.getInstance(SplashActivity.this).getLongValue(SPreferenceManager.KEY_UPDATE_AVAILABLE_TIME, 0l);
        return (System.currentTimeMillis() - prefTime > Constants.General.TIME_TO_CHECK_UPDATE);
    }
}
