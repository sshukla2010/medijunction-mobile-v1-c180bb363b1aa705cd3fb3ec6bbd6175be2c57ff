package com.smileparser.medijunctions;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.smileparser.medijunctions.adapters.RecyclerSearchPatientAdapter;
import com.smileparser.medijunctions.bean.ApiResponse;
import com.smileparser.medijunctions.bean.RegisterPatient;
import com.smileparser.medijunctions.bean.SearchPatient;
import com.smileparser.medijunctions.bean.User;
import com.smileparser.medijunctions.customviews.LoadingDialog;
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

public class RegisteredPatientSearchActivity extends AppCompatActivity {

    SearchView searchPatient;
    RecyclerView recyclerView;
    ApiInterface retrofitApiInterface;
    RecyclerSearchPatientAdapter recyclerSearchPatientAdapter;
    TextView tv_nodata;
    List<SearchPatient> searchPatientList;
    SharedPreferences mPrefs;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_patient_search);

        user = Global.getUserDetails(RegisteredPatientSearchActivity.this);
        try {

            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            if(Global.IsNotNull(user))
            {
                if (user.getUsertype().equals(getString(R.string.hint_officer))) {
                    actionBar.setTitle(getString(R.string.nav_RegisteredPatient));
                } else {
                    actionBar.setTitle(getString(R.string.nav_RegisteredFamilymembers));
                }
            }
            else
            {
                actionBar.setTitle(getString(R.string.nav_RegisterAddFamilyMember));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        mPrefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        searchPatient = findViewById(R.id.search_patient);
        recyclerView = findViewById(R.id.searchPatientList);
        tv_nodata = findViewById(R.id.nodata);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        searchPatientList = new ArrayList<>();
        searchPatient.setFocusable(false);


        searchPatient.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchData(newText);
                return false;
            }
        });
    }

    RecyclerSearchPatientAdapter.RecyclerSearchPatientAdapterEvent recyclerSearchPatientAdapterEvent = new RecyclerSearchPatientAdapter.RecyclerSearchPatientAdapterEvent() {
        @Override
        public void Goto(String id) {
            if (Global.isInternetOn(RegisteredPatientSearchActivity.this)) {
                LoadingDialog.showLoadingDialog(RegisteredPatientSearchActivity.this, "Please wait...");
                String[] ids = new String[1];
                ids[0] = id;
                new getRegisterPatientData().execute(ids);
            }
        }

        @Override
        public void DeleteData(final String Dbid,final int adapterposition) {

            AlertDialog.Builder builder = builder = new AlertDialog.Builder(RegisteredPatientSearchActivity.this);
            builder.setMessage("Are you sure you want to delete?")
                    .setTitle("Warning")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            if (Global.isInternetOn(RegisteredPatientSearchActivity.this)) {
                                LoadingDialog.showLoadingDialog(RegisteredPatientSearchActivity.this, "Please wait...");
                                String[] ids = new String[2];
                                ids[0] = String.valueOf(Dbid);
                                ids[1] = String.valueOf(Global.getUserDetails(RegisteredPatientSearchActivity.this).getId());
                                new deleteData().execute(ids);
                            }
                            else
                            {
                                /*ServiceBase<DeleteSync> deleteSyncServiceBase = new ServiceBase<DeleteSync>(RegisteredPatientSearchActivity.this);
                                DeleteSync deleteSync = new DeleteSync();
                                deleteSync.setDBId(Dbid);
                                deleteSync.setUserId(Global.getUserDetails().getId());
                                deleteSyncServiceBase.Insert(deleteSync,DeleteSync.class);*/
                                Toast.makeText(RegisteredPatientSearchActivity.this, "No Internet...", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
                            dialog.cancel();

                        }
                    });
            AlertDialog alert = builder.create();

            alert.show();


        }
    };



    private class getRegisterPatientData extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... params) {
            try {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                Map<String,String> header = Global.generateHeader(RegisteredPatientSearchActivity.this);
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("DBId", params[0]);

                Call<ApiResponse> apiResponseCall = apiInterface.getRegisteredEditPatient(header,map);

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
                                String s = gson.toJson(response.body().getResult());
                                RegisterPatient registerPatient = gson.fromJson(s, RegisterPatient.class);

                                if (Global.IsNotNull(registerPatient)) {

                                    Intent intent = new Intent(RegisteredPatientSearchActivity.this, RegisterPatientEditActivity.class);
                                    String registerdata = gson.toJson(registerPatient);
                                    intent.putExtra("registerPatient", registerdata);
                                    startActivity(intent);
                                }
                            } else {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    Toast.makeText(RegisteredPatientSearchActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (Global.IsNotNull(response.errorBody())) {
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = null;
                            try {
                                mJson = parser.parse(response.errorBody().string());
                                Gson gson = new Gson();
                                ApiResponse errorResponse = gson.fromJson(mJson, ApiResponse.class);

                                Toast.makeText(RegisteredPatientSearchActivity.this, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }

                        } else {
                            Toast.makeText(RegisteredPatientSearchActivity.this, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
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

                        Toast.makeText(RegisteredPatientSearchActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }

            return null;
        }

    }

    private class deleteData extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... params) {
            try {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                Map<String,String> header = Global.generateHeader(RegisteredPatientSearchActivity.this);
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("DBId", params[0]);
                //map.put("UserId", params[1]);


                Call<ApiResponse> apiResponseCall = apiInterface.deletePatient(header,map);

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
                                User user = Global.getUserDetails(RegisteredPatientSearchActivity.this);
                                if(Global.IsNotNull(user))
                                {
                                    if(user.getTotalPatient() > 0) {
                                        user.setTotalPatient(user.getTotalPatient() - 1);
                                        String userstring = Global.getJsonString(user);
                                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                        prefsEditor.putString(getString(R.string.share_userdetails), userstring);
                                        prefsEditor.commit();
                                    }
                                }

                                String query = String.valueOf(searchPatient.getQuery());
                                if(Global.IsNotNull(query))
                                {
                                    searchData(query);
                                }
                                else
                                {
                                    searchData("");
                                }
                                Toast.makeText(RegisteredPatientSearchActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();

                            } else {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    Toast.makeText(RegisteredPatientSearchActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (Global.IsNotNull(response.errorBody())) {
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = null;
                            try {
                                mJson = parser.parse(response.errorBody().string());
                                Gson gson = new Gson();
                                ApiResponse errorResponse = gson.fromJson(mJson, ApiResponse.class);

                                Toast.makeText(RegisteredPatientSearchActivity.this, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }

                        } else {
                            Toast.makeText(RegisteredPatientSearchActivity.this, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
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

                        Toast.makeText(RegisteredPatientSearchActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                try {
                    LoadingDialog.cancelLoading();
                }
                catch (Exception me)
                {
                    me.printStackTrace();
                }
                e.printStackTrace();
            }

            return null;
        }

    }

    public void searchData(String query)
    {
        if (Global.isInternetOn(RegisteredPatientSearchActivity.this))
        {


            retrofitApiInterface = ApiClient.getClient().create(ApiInterface.class);
            Map<String,String> header = Global.generateHeader(RegisteredPatientSearchActivity.this);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("UserId",user.getId());
            map.put("term", query);

            Call<ApiResponse> call = retrofitApiInterface.getRegisteredPatientList(header,map);
            try {

                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (Global.IsNotNull(response.body())) {
                            if (response.body().isResponse()) {

                                Gson gson = new Gson();
                                Type type = new TypeToken<List<SearchPatient>>() {
                                }.getType();
                                searchPatientList = gson.fromJson(response.body().getResult(), type);
                                // result = response.body()
                                if (Global.IsNotNull(searchPatientList)) {
                                    if(searchPatientList.size() > 0) {
                                        recyclerSearchPatientAdapter = new RecyclerSearchPatientAdapter(RegisteredPatientSearchActivity.this, searchPatientList, recyclerSearchPatientAdapterEvent);
                                        recyclerView.setAdapter(recyclerSearchPatientAdapter);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        tv_nodata.setVisibility(View.GONE);
                                    }
                                    else
                                    {
                                        recyclerSearchPatientAdapter = new RecyclerSearchPatientAdapter(RegisteredPatientSearchActivity.this, searchPatientList, recyclerSearchPatientAdapterEvent);
                                        recyclerView.setAdapter(recyclerSearchPatientAdapter);
                                        recyclerView.setVisibility(View.GONE);
                                        tv_nodata.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    searchPatientList = new ArrayList<>();
                                    recyclerSearchPatientAdapter = new RecyclerSearchPatientAdapter(RegisteredPatientSearchActivity.this, searchPatientList, recyclerSearchPatientAdapterEvent);
                                    recyclerView.setAdapter(recyclerSearchPatientAdapter);
                                    recyclerView.setVisibility(View.GONE);
                                    tv_nodata.setVisibility(View.VISIBLE);
                                }
                            } else {
                                searchPatientList = new ArrayList<>();
                                recyclerSearchPatientAdapter = new RecyclerSearchPatientAdapter(RegisteredPatientSearchActivity.this, searchPatientList, recyclerSearchPatientAdapterEvent);
                                recyclerView.setAdapter(recyclerSearchPatientAdapter);
                                recyclerView.setVisibility(View.GONE);
                                tv_nodata.setVisibility(View.VISIBLE);
                            }
                        }
                        else
                        {
                            tv_nodata.setVisibility(View.VISIBLE);
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
                        Log.d("LoginFailure", t.getMessage());
                        call.cancel();
                        try {
                            LoadingDialog.cancelLoading();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                try {
                    LoadingDialog.cancelLoading();
                }
                catch (Exception et)
                {
                    et.printStackTrace();
                }
                Log.d("RetrofitCall", e.getMessage());
            }
        }
        else
        {
            try {
                LoadingDialog.cancelLoading();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            Toast.makeText(RegisteredPatientSearchActivity.this, "No Internet Connection...", Toast.LENGTH_SHORT).show();
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
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Global.globalregisterPatientList = new ArrayList<>();
        try {
            LoadingDialog.showLoadingDialog(RegisteredPatientSearchActivity.this,"Please wait...");
            searchData("");
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
