package com.smileparser.medijunctions;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.smileparser.medijunctions.adapters.RecyclerAddDoctorCouponsAdapter;
import com.smileparser.medijunctions.adapters.RecyclerCommonObjectAdapter;
import com.smileparser.medijunctions.bean.ApiResponse;
import com.smileparser.medijunctions.bean.CommonObject;
import com.smileparser.medijunctions.bean.DoctorProfile;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OurDoctorActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_our_doctor);
    }
}

/*
public class OurDoctorActivity extends AppCompatActivity implements View.OnClickListener {

    start Comment

    RecyclerView rvAddCoupons;
    List<CommonObject> specialityList;
    List<CommonObject> languageList;
    Button btn_searchDoctor,btn_addCoupons,tv_showhidefilter;
    TextView speciality_spinner, language_spinner,tv_nodata;
    List<DoctorProfile> couponsList = null;
    List<DoctorProfile> couponIdList = null;
    RecyclerAddDoctorCouponsAdapter addDoctorCouponsAdapter;
    LinearLayout filterLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_our_doctor);

        try {

            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.nav_ourdoctors));
        } catch (Exception e) {
            e.printStackTrace();
        }

        tv_showhidefilter = findViewById(R.id.tv_showhidefilter);
        filterLayout = findViewById(R.id.filterLayout);
        speciality_spinner = findViewById(R.id.spn_speciality);
        language_spinner = findViewById(R.id.spn_language);
        tv_nodata = findViewById(R.id.nodata);
        btn_searchDoctor = findViewById(R.id.searchDoctor);
        btn_addCoupons = findViewById(R.id.btn_add_doctor_coupons);
        rvAddCoupons = findViewById(R.id.rv_adddoctorcouponslist);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvAddCoupons.setLayoutManager(layoutManager);
        couponsList = new ArrayList<>();
        specialityList = new ArrayList<>();
        languageList = new ArrayList<>();

        LoadingDialog.showLoadingDialog(this,"Please wait...");
        try {
            getAllSpeciality();
            getAllLanguage();

            getDoctorList(new ArrayList<String>(),new ArrayList<String>());


            rvAddCoupons.setVisibility(View.VISIBLE);
            addDoctorCouponsAdapter = new RecyclerAddDoctorCouponsAdapter(OurDoctorActivity.this, couponsList, recyclerAddDoctorCouponsAdapterEvent);
            rvAddCoupons.setLayoutManager(new LinearLayoutManager(OurDoctorActivity.this));
            rvAddCoupons.setAdapter(addDoctorCouponsAdapter);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        tv_showhidefilter.setOnClickListener(this);
        speciality_spinner.setOnClickListener(this);
        language_spinner.setOnClickListener(this);
        btn_searchDoctor.setOnClickListener(this);
        btn_addCoupons.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.tv_showhidefilter:
                if(filterLayout.getVisibility() == View.VISIBLE)
                {
                    tv_showhidefilter.setText(getText(R.string.hint_showfilter));
                    filterLayout.setVisibility(View.GONE);
                }
                else
                {
                    tv_showhidefilter.setText(getText(R.string.hint_hidefilter));
                    filterLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.spn_speciality:
              showSpecialityPopup();
              break;
            case R.id.spn_language:
                showLanguagePopup();
                break;
            case R.id.searchDoctor:
                List<String> spList = new ArrayList<>();
                List<String> lgList = new ArrayList<>();
                for(CommonObject item : specialityList)
                {
                    if(item.isSelected())
                    {
                        spList.add(item.getId());
                    }
                }

                for(CommonObject item : languageList)
                {
                    if(item.isSelected())
                    {
                        lgList.add(item.getId());
                    }
                }

                getDoctorList(spList,lgList);
                break;
            case R.id.btn_add_doctor_coupons:
                couponIdList = new ArrayList<>();
                for(DoctorProfile item : couponsList)
                {
                    if(item.isSelect())
                    {
                        couponIdList.add(item);
                    }
                }

                if(couponIdList.size() > 0)
                {
                    addCoupons();
                }

                break;
        }
    }

    public void showProfilePopup(DoctorProfile doctorProfile)
    {
        LayoutInflater inflater = getLayoutInflater();
        View itemView = inflater.inflate(R.layout.popupprofile, null);
        TextView tv_name,tv_deg,tv_speciality,tv_rate,tv_language,tv_qty;
        Button btn_close;
        ImageView img_pic;

        tv_name = itemView.findViewById(R.id.pop_od_name);
        tv_deg = itemView.findViewById(R.id.pop_od_degree);
        tv_rate = itemView.findViewById(R.id.pop_od_rate);
        tv_speciality = itemView.findViewById(R.id.pop_od_speciality);
        tv_language = itemView.findViewById(R.id.pop_od_language);
        img_pic = itemView.findViewById(R.id.pop_od_img);
        btn_close = itemView.findViewById(R.id.btn_od_close);

        if(Global.IsNotNull(doctorProfile))
        {
            try
            {
                RequestOptions options = new RequestOptions()
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .placeholder(R.drawable.ic_usercolor)
                        .error(R.drawable.ic_usercolor);
                Glide.with(OurDoctorActivity.this).load(doctorProfile.getImagePath())
                        .apply(options).into(img_pic);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            if(Global.IsNotNull(doctorProfile.getName())) {
                tv_name.setText(doctorProfile.getName());
            }

            if(Global.IsNotNull(doctorProfile.getDegree())) {
                tv_deg.setText(doctorProfile.getDegree());
            }

            if(Global.IsNotNull(doctorProfile.getRate())) {
                String amount = String.valueOf((long)Math.round(Double.parseDouble(doctorProfile.getRate())));
                tv_rate.setText(amount);
            }

            if(Global.IsNotNull(doctorProfile.getSpeciality())) {
                tv_speciality.setText(doctorProfile.getSpeciality());
            }

            if(Global.IsNotNull(doctorProfile.getLanguage())) {
                tv_language.setText(doctorProfile.getLanguage());
            }
        }

        final AlertDialog.Builder alert = new AlertDialog.Builder(this,R.style.CustomDialogTheme);
        alert.setCancelable(false);
        alert.setView(itemView);

        final AlertDialog dialog = alert.create();
        dialog.show();

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    public void showSpecialityPopup() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_listview, null);
        RecyclerView rv_dataList = (RecyclerView) alertLayout.findViewById(R.id.rv_spinnerList);
        final Button btn_ok = (Button) alertLayout.findViewById(R.id.btn_ok);
        final Button btn_cancel = (Button) alertLayout.findViewById(R.id.btn_cancel);

        TextView tv_heading = alertLayout.findViewById(R.id.tv_heading);
        tv_heading.setText("Select "+ getString(R.string.hint_select_speciality));

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setView(alertLayout);

        final AlertDialog dialog = alert.create();
        dialog.show();
        rv_dataList.setLayoutManager(new LinearLayoutManager(OurDoctorActivity.this));
        RecyclerCommonObjectAdapter recyclerSpecialityAdapter = new RecyclerCommonObjectAdapter(OurDoctorActivity.this, specialityList);
        rv_dataList.setAdapter(recyclerSpecialityAdapter);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringBuilder sb1 = new StringBuilder("");
                String selectedItems = "";

                int count = 0;
                for (CommonObject item : specialityList) {
                    if (item.isSelected()) {
                        if (sb1.toString().isEmpty()) {
                            sb1.append(item.getName());
                        } else {
                            sb1.append("," + item.getName());
                        }
                        count++;
                    }

                }

                if (count == 0) {
                    speciality_spinner.setText(null);
                } else {
                    selectedItems = sb1.toString();
                    speciality_spinner.setText(selectedItems);
                }
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void showLanguagePopup() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_listview, null);
        RecyclerView rv_dataList = (RecyclerView) alertLayout.findViewById(R.id.rv_spinnerList);
        final Button btn_ok = (Button) alertLayout.findViewById(R.id.btn_ok);
        final Button btn_cancel = (Button) alertLayout.findViewById(R.id.btn_cancel);

        TextView tv_heading = alertLayout.findViewById(R.id.tv_heading);
        tv_heading.setText("Select Language");

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setView(alertLayout);

        final AlertDialog dialog = alert.create();
        dialog.show();
        rv_dataList.setLayoutManager(new LinearLayoutManager(OurDoctorActivity.this));
        RecyclerCommonObjectAdapter recyclerSpecialityAdapter = new RecyclerCommonObjectAdapter(OurDoctorActivity.this, languageList);
        rv_dataList.setAdapter(recyclerSpecialityAdapter);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringBuilder sb1 = new StringBuilder("");
                String selectedItems = "";

                int count = 0;
                for (CommonObject item : languageList) {
                    if (item.isSelected()) {
                        if (sb1.toString().isEmpty()) {
                            sb1.append(item.getName());
                        } else {
                            sb1.append("," + item.getName());
                        }
                        count++;
                    }

                }

                if (count == 0) {
                    language_spinner.setText(null);
                } else {
                    selectedItems = sb1.toString();
                    language_spinner.setText(selectedItems);
                }
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    public void getAllSpeciality() {
        try {
            if (Global.isInternetOn(OurDoctorActivity.this)) {
                try {

                    ApiInterface itemInterface = ApiClient.getClient().create(ApiInterface.class);
                    Call<ApiResponse> apiResponseCall = itemInterface.getSpecialityList();

                    apiResponseCall.enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                            //progressBar.setVisibility(View.GONE);

                            if (Global.IsNotNull(response.body())) {
                                if (response.body().isResponse()) {

                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<CommonObject>>() {
                                    }.getType();
                                    List<CommonObject> itemList = gson.fromJson(response.body().getResult(), type);
                                    speciality_spinner.setText(null);
                                    if (Global.IsNotNull(itemList)) {
                                        if (itemList.size() > 0) {
                                            specialityList = new ArrayList<>();
                                            specialityList.addAll(itemList);
                                        }
                                    }

                                }
                            } else if (Global.IsNotNull(response.errorBody())) {
                                JsonParser parser = new JsonParser();
                                JsonElement mJson = null;
                                specialityList = new ArrayList<>();
                                try {
                                    LoadingDialog.cancelLoading();
                                    mJson = parser.parse(response.errorBody().string());
                                    Gson gson = new Gson();
                                    ApiResponse errorResponse = gson.fromJson(mJson, ApiResponse.class);


                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                } catch (Exception e) {
                                    e.getMessage();
                                }


                            } else {

                                Log.d("log", "log");
                            }

                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                            specialityList = new ArrayList<>();
                        }
                    });
                } catch (Exception e) {
                    e.getMessage();
                    specialityList = new ArrayList<>();
                }

                LoadingDialog.cancelLoading();
            }
        } catch (Exception exp) {
            //if (Global.IsNotNull(exp) && Global.IsNotNull(exp.getMessage()))
                specialityList = new ArrayList<>();
            LoadingDialog.cancelLoading();
        }
    }

    public void getAllLanguage() {
        try {
            if (Global.isInternetOn(OurDoctorActivity.this)) {
                try {

                    ApiInterface itemInterface = ApiClient.getClient().create(ApiInterface.class);
                    Call<ApiResponse> apiResponseCall = itemInterface.getLanguageList();

                    apiResponseCall.enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                            //progressBar.setVisibility(View.GONE);

                            if (Global.IsNotNull(response.body())) {
                                if (response.body().isResponse()) {

                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<CommonObject>>() {
                                    }.getType();
                                    List<CommonObject> itemList = gson.fromJson(response.body().getResult(), type);
                                    language_spinner.setText(null);
                                    if (Global.IsNotNull(itemList)) {
                                        if (itemList.size() > 0) {
                                            languageList = new ArrayList<>();
                                            languageList.addAll(itemList);
                                        }
                                    }

                                }
                            } else if (Global.IsNotNull(response.errorBody())) {
                                JsonParser parser = new JsonParser();
                                JsonElement mJson = null;
                                languageList = new ArrayList<>();
                                try {
                                    LoadingDialog.cancelLoading();
                                    mJson = parser.parse(response.errorBody().string());
                                    Gson gson = new Gson();
                                    ApiResponse errorResponse = gson.fromJson(mJson, ApiResponse.class);


                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                } catch (Exception e) {
                                    e.getMessage();
                                }


                            } else {

                                Log.d("log", "log");
                            }

                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                            languageList = new ArrayList<>();
                        }
                    });
                } catch (Exception e) {
                    e.getMessage();
                    languageList = new ArrayList<>();
                }
                LoadingDialog.cancelLoading();
            }
        } catch (Exception exp) {
            //if (Global.IsNotNull(exp) && Global.IsNotNull(exp.getMessage()))
                //Log.e(AllergyDataService.class.getName(), exp.getMessage());
                languageList = new ArrayList<>();
            LoadingDialog.cancelLoading();
        }
    }

    public void getDoctorList(List<String> spList,List<String> lgList)
    {
        if (Global.isInternetOn(this)) {
            LoadingDialog.showLoadingDialog(OurDoctorActivity.this, "Please wait...");
            Object[] objectList = new Object[2];
            objectList[0]= spList;
            objectList[1] = lgList;
            new getDoctorList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,objectList);
        }
        else
        {
            Toast.makeText(this, ""+getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    private class getDoctorList extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            try {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                */
/*
                {

    "SpecialityIds": ["68CE952C-C02B-4AC0-9F4A-A4B3AD495F15","667C8673-CD5F-49EB-896A-F0509E3EC00F"], List<GUID>
    "LanguageIds": [] List<GUID>
}
                 *//*

                HashMap<String,Object> map = new HashMap<>();
              //  map.put("SpecialityIds",params[0]);
               // map.put("LanguageIds", params[1]);
                Call<ApiResponse> apiResponseCall = apiInterface.getActiveDoctorList(map);

                apiResponseCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        //progressBar.setVisibility(View.GONE);

                        if (Global.IsNotNull(response.body())) {
                            if (response.body().isResponse()) {
                                ////////Start
                                Gson gson = new Gson();
                                Type type = new TypeToken<List<DoctorProfile>>() {
                                }.getType();
                                List<DoctorProfile> VisitList = gson.fromJson(response.body().getResult(), type);

                                if (Global.IsNotNull(VisitList)) {

                                    couponsList = new ArrayList<>();
                                    couponsList.addAll(VisitList);

                                    if(couponsList.size() > 0) {
                                        tv_nodata.setVisibility(View.GONE);
                                        rvAddCoupons.setVisibility(View.VISIBLE);
                                        addDoctorCouponsAdapter = new RecyclerAddDoctorCouponsAdapter(OurDoctorActivity.this, couponsList, recyclerAddDoctorCouponsAdapterEvent);
                                        rvAddCoupons.setLayoutManager(new LinearLayoutManager(OurDoctorActivity.this));
                                        rvAddCoupons.setAdapter(addDoctorCouponsAdapter);
                                        showHideAddButton();
                                        LoadingDialog.cancelLoading();
                                    }
                                    else
                                    {
                                        tv_nodata.setVisibility(View.VISIBLE);
                                        rvAddCoupons.setVisibility(View.GONE);
                                        showHideAddButton();
                                        if(Global.IsNotNull(response.body().getMessage())) {
                                            tv_nodata.setText(response.body().getMessage());
                                        }
                                        else
                                        {
                                            tv_nodata.setText("No coupons found!");
                                        }
                                        //Toast.makeText(OurDoctorActivity.this, "No coupons Found", Toast.LENGTH_SHORT).show();
                                    }


                                }
                                ///////////////// over

                            } else {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    LoadingDialog.cancelLoading();
                                    //Toast.makeText(OurDoctorActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    showHideAddButton();
                                    tv_nodata.setVisibility(View.VISIBLE);
                                    rvAddCoupons.setVisibility(View.GONE);
                                    if(Global.IsNotNull(response.body().getMessage())) {
                                        tv_nodata.setText(response.body().getMessage());
                                    }
                                    else
                                    {
                                        tv_nodata.setText("No coupons found!");
                                    }
                                }
                                LoadingDialog.cancelLoading();
                            }
                        } else if (Global.IsNotNull(response.errorBody())) {
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = null;
                            try {
                                LoadingDialog.cancelLoading();
                                mJson = parser.parse(response.errorBody().string());
                                Gson gson = new Gson();
                                ApiResponse errorResponse = gson.fromJson(mJson, ApiResponse.class);
                                showHideAddButton();
                                tv_nodata.setVisibility(View.VISIBLE);
                                rvAddCoupons.setVisibility(View.GONE);
                                if(Global.IsNotNull(errorResponse.getMessage())) {
                                    tv_nodata.setText(errorResponse.getMessage());
                                }
                                else
                                {
                                    tv_nodata.setText("No coupons found!");
                                }
                                Toast.makeText(OurDoctorActivity.this, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }

                            LoadingDialog.cancelLoading();


                        } else {
                            tv_nodata.setVisibility(View.VISIBLE);
                            rvAddCoupons.setVisibility(View.GONE);
                            tv_nodata.setText(""+getString(R.string.somethingwrong));
                            Toast.makeText(OurDoctorActivity.this, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                        // progressBar.setVisibility(View.GONE);
                        LoadingDialog.cancelLoading();
                        showHideAddButton();
                        tv_nodata.setVisibility(View.VISIBLE);
                        rvAddCoupons.setVisibility(View.GONE);
                        if(Global.IsNotNull(t.getMessage())) {
                            tv_nodata.setText(t.getMessage());
                        }
                        else
                        {
                            tv_nodata.setText("No coupons found!");
                        }

                        Toast.makeText(OurDoctorActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                e.getMessage();
                LoadingDialog.cancelLoading();
            }


            return null;
        }
    }

    public void addCoupons()
    {
        if (Global.isInternetOn(this)) {
            LoadingDialog.showLoadingDialog(OurDoctorActivity.this, "Please wait...");
            new addCoupons().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else
        {
            Toast.makeText(this, ""+getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    private class addCoupons extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                User user = Global.getUserDetails(OurDoctorActivity.this);
                HashMap<String,Object> map = new HashMap<>();
                map.put("UserId",user.getId());
                map.put("CouponList",couponIdList);
                Call<ApiResponse> apiResponseCall = apiInterface.addDrCoupon(map);

                apiResponseCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        //progressBar.setVisibility(View.GONE);

                        if (Global.IsNotNull(response.body())) {
                            if (response.body().isResponse()) {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    LoadingDialog.cancelLoading();
                                    Toast.makeText(OurDoctorActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    getAllSpeciality();
                                    getAllLanguage();
                                    getDoctorList(new ArrayList<String>(),new ArrayList<String>());
                                    showHideAddButton();
                                }
                            } else {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    LoadingDialog.cancelLoading();
                                    Toast.makeText(OurDoctorActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
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

                                Toast.makeText(OurDoctorActivity.this, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }


                        } else {
                            Toast.makeText(OurDoctorActivity.this, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                        // progressBar.setVisibility(View.GONE);
                        LoadingDialog.cancelLoading();

                        Toast.makeText(OurDoctorActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                e.getMessage();
                LoadingDialog.cancelLoading();
            }


            return null;
        }
    }


    public void showHideAddButton()
    {
        int count = 0;
        if(Global.IsNotNull(couponsList)) {
            for (DoctorProfile item : couponsList) {
                if (item.isSelect()) {
                    count++;
                }
            }
        }
        if(count > 0)
        {
            btn_addCoupons.setVisibility(View.VISIBLE);
        }
        else
        {
            btn_addCoupons.setVisibility(View.GONE);
        }
    }

    public void showPromocodeDialog(final int adapterposition, final boolean isSelected) {
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.popup_description, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final EditText edt = (EditText) dialogView.findViewById(R.id.edt_popup_description);

        */
/*edt_qty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_qty.setError(null);
            }
        });

        if(type == 1)
        {
            edt.setVisibility(View.VISIBLE);
        }
        else
        {
            edt.setVisibility(View.GONE);
        }*//*


        dialogBuilder.setTitle("Details");
        //dialogBuilder.setMessage("Enter Promocode");
        dialogBuilder.setPositiveButton("Add",null);
        dialogBuilder.setNegativeButton("Cancel",null);

        final android.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button done = ((android.app.AlertDialog) alertDialog).getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                done.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something

                        //if(type == 1) {
                        if (Global.IsNotNull(edt.getText().toString())) {
                            String promocode = couponsList.get(adapterposition).getPromoCode();
                            if (promocode.equals(edt.getText().toString())) {
                                couponsList.get(adapterposition).setSelect(isSelected);
                                addDoctorCouponsAdapter.notifyItemChanged(adapterposition);
                                showHideAddButton();
                                alertDialog.dismiss();
                            } else {
                                edt.setError("Please enter valid promocode");
                            }
                        } else {
                            edt.setError("This field is require");
                        }
                        */
/*}
                        else
                        {
                            if(Global.IsNotNull(edt_qty.getText().toString()))
                            {
                                if(Integer.parseInt(edt_qty.getText().toString()) >= 1)
                                {
                                    couponsList.get(adapterposition).setSelect(isSelected);
                                    addCouponsAdapter.notifyItemChanged(adapterposition);
                                    showHideAddButton();
                                }
                            }
                        }
*//*

                        //Dismiss once everything is OK.
                    }
                });

                Button cancel = ((android.app.AlertDialog) alertDialog).getButton(android.app.AlertDialog.BUTTON_NEGATIVE);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        couponsList.get(adapterposition).setSelect(false);
                        addDoctorCouponsAdapter.notifyItemChanged(adapterposition);
                        showHideAddButton();
                        alertDialog.dismiss();
                    }
                });

            }
        });
        alertDialog.show();
    }


    RecyclerAddDoctorCouponsAdapter.RecyclerAddDoctorCouponsAdapterEvent recyclerAddDoctorCouponsAdapterEvent = new RecyclerAddDoctorCouponsAdapter.RecyclerAddDoctorCouponsAdapterEvent() {
        @Override
        public void selectedCoupon(int itemAdapterPosition, boolean select) {
            if(select) {
                if(Global.IsNotNull(couponsList.get(itemAdapterPosition).getPromoCode()) && !couponsList.get(itemAdapterPosition).isSelect())
                {
                    showPromocodeDialog(itemAdapterPosition,select);
                }
                else
                {
                    couponsList.get(itemAdapterPosition).setSelect(select);
                    addDoctorCouponsAdapter.notifyItemChanged(itemAdapterPosition);
                    showHideAddButton();
                }
            }
            else {
                couponsList.get(itemAdapterPosition).setSelect(select);
                addDoctorCouponsAdapter.notifyItemChanged(itemAdapterPosition);
                showHideAddButton();
            }


        }

        @Override
        public void add(int itemAdapterPosition) {

            if(Global.IsNotNull(couponsList.get(itemAdapterPosition).getQty()))
            {
                long qty = couponsList.get(itemAdapterPosition).getQty() + 1;
                couponsList.get(itemAdapterPosition).setQty(qty);
                addDoctorCouponsAdapter.notifyItemChanged(itemAdapterPosition);
            }
        }

        @Override
        public void remove(int itemAdapterPosition) {
            if(Global.IsNotNull(couponsList.get(itemAdapterPosition).getQty()))
            {
                if(couponsList.get(itemAdapterPosition).getQty() > 1) {
                    long qty = couponsList.get(itemAdapterPosition).getQty() - 1;
                    couponsList.get(itemAdapterPosition).setQty(qty);
                    addDoctorCouponsAdapter.notifyItemChanged(itemAdapterPosition);
                }
            }
        }

        @Override
        public void showProfile(int itemAdapterPosition) {
            showProfilePopup(couponsList.get(itemAdapterPosition));
        }
    };


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
        Intent dashbord = new Intent(OurDoctorActivity.this, Dashboard.class);
        dashbord.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dashbord);
        overridePendingTransition(0,0);
    }



}*/
