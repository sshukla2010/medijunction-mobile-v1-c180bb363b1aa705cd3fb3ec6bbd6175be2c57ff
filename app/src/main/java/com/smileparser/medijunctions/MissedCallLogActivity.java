package com.smileparser.medijunctions;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.smileparser.medijunctions.adapters.CallLogAdapter;
import com.smileparser.medijunctions.adapters.CouponsAdapter;
import com.smileparser.medijunctions.bean.ApiResponse;
import com.smileparser.medijunctions.bean.CallLog;
import com.smileparser.medijunctions.bean.CallLog;
import com.smileparser.medijunctions.bean.User;
import com.smileparser.medijunctions.customviews.LoadingDialog;
import com.smileparser.medijunctions.databinding.ActivityMissedCallLogBinding;
import com.smileparser.medijunctions.databinding.ActivityRegisterPatientBinding;
import com.smileparser.medijunctions.fragments.CouponsFragment;
import com.smileparser.medijunctions.interfaces.ApiInterface;
import com.smileparser.medijunctions.utils.ApiClient;
import com.smileparser.medijunctions.utils.Global;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MissedCallLogActivity extends AppCompatActivity {

    ActivityMissedCallLogBinding binding;
    CallLogAdapter callLogAdapter = null;
    List<CallLog> callLogList = null;
    public User user;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMissedCallLogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.nav_missedCallsLog));

            user = Global.getUserDetails(MissedCallLogActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        initUI();
    }

    private void initUI() {

        if(callLogList == null) {
            callLogList = new ArrayList<>();
        }
        callLogAdapter = new CallLogAdapter(MissedCallLogActivity.this, callLogList);
        binding.rvMissedcall.setLayoutManager(new LinearLayoutManager(MissedCallLogActivity.this));
        binding.rvMissedcall.setAdapter(callLogAdapter);

        if(callLogList.size() == 0) {
            getCallLogList();
        }
        else
        {
            binding.tvCallLogNodata.setVisibility(View.GONE);
            binding.rvMissedcall.setVisibility(View.VISIBLE);
        }

        binding.callLogswipeRefreshLayout.setOnRefreshListener(() -> {
            if (Global.isInternetOn(MissedCallLogActivity.this)) {
                getCallLogList();
            } else {
                if (binding.callLogswipeRefreshLayout.isRefreshing()) {
                    binding.callLogswipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void getCallLogList() {
        if (Global.isInternetOn(MissedCallLogActivity.this)) {
            LoadingDialog.showLoadingDialog(MissedCallLogActivity.this, "Please wait...");
            new getCallLogs().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else
        {
            Toast.makeText(MissedCallLogActivity.this, ""+getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }


    private class getCallLogs extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                Map<String,String> header = Global.generateHeader(MissedCallLogActivity.this);
                HashMap<String,String> map = new HashMap<>();
                map.put("UserId", user.getId());

                Call<ApiResponse> apiResponseCall = apiInterface.getMissedCallLog(header,map);

                apiResponseCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        //progressBar.setVisibility(View.GONE);
                        if (binding.callLogswipeRefreshLayout.isRefreshing()) {
                            binding.callLogswipeRefreshLayout.setRefreshing(false);
                        }

                        if (Global.IsNotNull(response.body())) {
                            if (response.body().isResponse()) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<List<CallLog>>() {
                                }.getType();
                                List<CallLog> VisitList = gson.fromJson(response.body().getResult(), type);

                                if (Global.IsNotNull(VisitList)) {

                                    callLogList.clear();
                                    callLogList.addAll(VisitList);

                                    if(callLogList.size() > 0) {
                                        binding.tvCallLogNodata.setVisibility(View.GONE);
                                        binding.rvMissedcall.setVisibility(View.VISIBLE);
                                        callLogAdapter.notifyDataSetChanged();
                                        //showHideAddButton();
                                        LoadingDialog.cancelLoading();
                                    }
                                    else
                                    {
                                        binding.tvCallLogNodata.setVisibility(View.VISIBLE);
                                        binding.rvMissedcall.setVisibility(View.GONE);
                                        //showHideAddButton();
                                        if(Global.IsNotNull(response.body().getMessage())) {
                                            binding.tvCallLogNodata.setText(response.body().getMessage());
                                        }
                                        else
                                        {
                                            binding.tvCallLogNodata.setText("No call logs!");
                                        }
                                        //Toast.makeText(MissedCallLogActivity.this, "No coupons Found", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            } else {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    LoadingDialog.cancelLoading();
                                    //Toast.makeText(MissedCallLogActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    binding.tvCallLogNodata.setVisibility(View.VISIBLE);
                                    binding.rvMissedcall.setVisibility(View.GONE);
                                    //showHideAddButton();
                                    if(Global.IsNotNull(response.body().getMessage())) {
                                        binding.tvCallLogNodata.setText(response.body().getMessage());
                                    }
                                    else
                                    {
                                        binding.tvCallLogNodata.setText("No call logs!");
                                    }
                                }
                            }
                        } else if (Global.IsNotNull(response.errorBody())) {
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = null;
                            try {
                                LoadingDialog.cancelLoading();
                                mJson = parser.parse(response.errorBody().string());
                                Gson gson = new Gson();
                                ApiResponse errorResponse = gson.fromJson(mJson, ApiResponse.class);

                                binding.tvCallLogNodata.setVisibility(View.VISIBLE);
                                binding.rvMissedcall.setVisibility(View.GONE);
                                //showHideAddButton();
                                if(Global.IsNotNull(errorResponse.getMessage())) {
                                    binding.tvCallLogNodata.setText(errorResponse.getMessage());
                                }
                                else
                                {
                                    binding.tvCallLogNodata.setText("No call logs!");
                                }
                                Toast.makeText(MissedCallLogActivity.this, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }


                        } else {
                            binding.tvCallLogNodata.setVisibility(View.VISIBLE);
                            binding.rvMissedcall.setVisibility(View.GONE);
                            //showHideAddButton();
                            binding.tvCallLogNodata.setText(""+getString(R.string.somethingwrong));
                            Toast.makeText(MissedCallLogActivity.this, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        if (binding.callLogswipeRefreshLayout.isRefreshing()) {
                            binding.callLogswipeRefreshLayout.setRefreshing(false);
                        }
                        // progressBar.setVisibility(View.GONE);
                        LoadingDialog.cancelLoading();
                        binding.tvCallLogNodata.setVisibility(View.VISIBLE);
                        binding.rvMissedcall.setVisibility(View.GONE);
                        //showHideAddButton();
                        if(Global.IsNotNull(t.getMessage())) {
                            binding.tvCallLogNodata.setText(t.getMessage());
                        }
                        else
                        {
                            binding.tvCallLogNodata.setText("No call logs!");
                        }

                        Toast.makeText(MissedCallLogActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                e.getMessage();
                if (binding.callLogswipeRefreshLayout.isRefreshing()) {
                    binding.callLogswipeRefreshLayout.setRefreshing(false);
                }
                LoadingDialog.cancelLoading();
                try {
                    if (callLogList.size() == 0) {
                        binding.tvCallLogNodata.setVisibility(View.VISIBLE);
                        binding.rvMissedcall.setVisibility(View.GONE);
                        binding.tvCallLogNodata.setText("No call logs!");

                    }

                }
                catch (Exception exception)
                {
                    exception.printStackTrace();
                }
            }


            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                // ProjectsActivity is my 'home' activity
                onBackPressed();
                return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Global.globalregisterPatientList = new ArrayList<>();
        Intent dashbord = new Intent(MissedCallLogActivity.this, Dashboard.class);
        dashbord.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dashbord);
        overridePendingTransition(0,0);
    }
}