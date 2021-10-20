package com.smileparser.medijunctions;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.smileparser.medijunctions.adapters.RecyclerAddCouponsAdapter;
import com.smileparser.medijunctions.bean.ApiResponse;
import com.smileparser.medijunctions.bean.Coupons;
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


public class AddCouponsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coupons);

    }
}

/*public class AddCouponsActivity extends AppCompatActivity {

    RecyclerAddCouponsAdapter addCouponsAdapter = null;
    RecyclerView rvAddCoupons;
    List<Coupons> couponsList = null;
    Button btn_addCoupons,btn_organization;
    List<Coupons> couponIdList = null;
    TextView tv_nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coupons);

        try {

            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.nav_addcoupons));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        rvAddCoupons = findViewById(R.id.rv_addcouponslist);
        btn_addCoupons = findViewById(R.id.btn_add_coupons);
        btn_organization = findViewById(R.id.btn_organization);
        tv_nodata = findViewById(R.id.nodata);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvAddCoupons.setLayoutManager(layoutManager);
        couponsList = new ArrayList<>();
        couponIdList = new ArrayList<>();
        getCoupnList();

        showHideAddButton();

        btn_addCoupons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                couponIdList = new ArrayList<>();
                for(Coupons coupons : couponsList)
                {
                    if(coupons.isSelect())
                    {
                        couponIdList.add(coupons);
                    }
                }

                if(couponIdList.size() > 0)
                {
                    addCoupons();
                }
            }
        });

        btn_organization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ourdoctor = new Intent(AddCouponsActivity.this, OurDoctorActivity.class);
                ourdoctor.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(ourdoctor);
                overridePendingTransition(0,0);
            }
        });
    }

    public void addCoupons()
    {
        if (Global.isInternetOn(this)) {
            LoadingDialog.showLoadingDialog(AddCouponsActivity.this, "Please wait...");
            new addCoupons().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else
        {
            Toast.makeText(this, ""+getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }


    public void getCoupnList()
    {
        if (Global.isInternetOn(this)) {
            LoadingDialog.showLoadingDialog(AddCouponsActivity.this, "Please wait...");
            new getCoupons().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else
        {
            Toast.makeText(this, ""+getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    private class getCoupons extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                Call<ApiResponse> apiResponseCall = apiInterface.getCouponList();

                apiResponseCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        //progressBar.setVisibility(View.GONE);

                        if (Global.IsNotNull(response.body())) {
                            if (response.body().isResponse()) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<List<Coupons>>() {
                                }.getType();
                                List<Coupons> VisitList = gson.fromJson(response.body().getResult(), type);

                                if (Global.IsNotNull(VisitList)) {

                                    couponsList = new ArrayList<>();
                                    couponsList.addAll(VisitList);

                                    if(couponsList.size() > 0) {
                                        tv_nodata.setVisibility(View.GONE);
                                        rvAddCoupons.setVisibility(View.VISIBLE);
                                        addCouponsAdapter = new RecyclerAddCouponsAdapter(AddCouponsActivity.this, couponsList, recyclerAddCouponsAdapterEvent);
                                        rvAddCoupons.setLayoutManager(new LinearLayoutManager(AddCouponsActivity.this));
                                        rvAddCoupons.setAdapter(addCouponsAdapter);
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
                                        //Toast.makeText(AddCouponsActivity.this, "No coupons Found", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            } else {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    LoadingDialog.cancelLoading();
                                    //Toast.makeText(AddCouponsActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
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

                                tv_nodata.setVisibility(View.VISIBLE);
                                rvAddCoupons.setVisibility(View.GONE);
                                showHideAddButton();
                                if(Global.IsNotNull(errorResponse.getMessage())) {
                                    tv_nodata.setText(errorResponse.getMessage());
                                }
                                else
                                {
                                    tv_nodata.setText("No coupons found!");
                                }
                                Toast.makeText(AddCouponsActivity.this, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }


                        } else {
                            tv_nodata.setVisibility(View.VISIBLE);
                            rvAddCoupons.setVisibility(View.GONE);
                            showHideAddButton();
                            tv_nodata.setText(""+getString(R.string.somethingwrong));
                            Toast.makeText(AddCouponsActivity.this, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                        // progressBar.setVisibility(View.GONE);
                        LoadingDialog.cancelLoading();
                        tv_nodata.setVisibility(View.VISIBLE);
                        rvAddCoupons.setVisibility(View.GONE);
                        showHideAddButton();
                        if(Global.IsNotNull(t.getMessage())) {
                            tv_nodata.setText(t.getMessage());
                        }
                        else
                        {
                            tv_nodata.setText("No coupons found!");
                        }

                        Toast.makeText(AddCouponsActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }


            return null;
        }
    }


    private class addCoupons extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

               User  user = Global.getUserDetails(AddCouponsActivity.this);
               HashMap<String,Object> map = new HashMap<>();
               map.put("UserId",user.getId());
               map.put("CouponList",couponIdList);
                Call<ApiResponse> apiResponseCall = apiInterface.addCoupon(map);

                apiResponseCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        //progressBar.setVisibility(View.GONE);

                        if (Global.IsNotNull(response.body())) {
                            if (response.body().isResponse()) {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    LoadingDialog.cancelLoading();
                                    Toast.makeText(AddCouponsActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();

                                    getCoupnList();
                                }
                            } else {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    LoadingDialog.cancelLoading();
                                    Toast.makeText(AddCouponsActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
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

                                Toast.makeText(AddCouponsActivity.this, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }


                        } else {
                            Toast.makeText(AddCouponsActivity.this, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                        // progressBar.setVisibility(View.GONE);
                        LoadingDialog.cancelLoading();

                        Toast.makeText(AddCouponsActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }


            return null;
        }
    }


    RecyclerAddCouponsAdapter.RecyclerAddCouponsAdapterEvent recyclerAddCouponsAdapterEvent = new RecyclerAddCouponsAdapter.RecyclerAddCouponsAdapterEvent() {
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
                    addCouponsAdapter.notifyItemChanged(itemAdapterPosition);
                    showHideAddButton();
                }
            }
            else {
                couponsList.get(itemAdapterPosition).setSelect(select);
                addCouponsAdapter.notifyItemChanged(itemAdapterPosition);
                showHideAddButton();
            }


        }

        @Override
        public void add(int itemAdapterPosition) {

            if(Global.IsNotNull(couponsList.get(itemAdapterPosition).getQty()))
            {
                long qty = couponsList.get(itemAdapterPosition).getQty() + 1;
                couponsList.get(itemAdapterPosition).setQty(qty);
                addCouponsAdapter.notifyItemChanged(itemAdapterPosition);
            }
        }

        @Override
        public void remove(int itemAdapterPosition) {
            if(Global.IsNotNull(couponsList.get(itemAdapterPosition).getQty()))
            {
                if(couponsList.get(itemAdapterPosition).getQty() > 1) {
                    long qty = couponsList.get(itemAdapterPosition).getQty() - 1;
                    couponsList.get(itemAdapterPosition).setQty(qty);
                    addCouponsAdapter.notifyItemChanged(itemAdapterPosition);
                }
            }
        }
    };

    public void showHideAddButton()
    {
        int count = 0;
        if(Global.IsNotNull(couponsList)) {
            for (Coupons coupon : couponsList) {
                if (coupon.isSelect()) {
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
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.popup_description, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final EditText edt = (EditText) dialogView.findViewById(R.id.edt_popup_description);

        *//*edt_qty.setOnClickListener(new View.OnClickListener() {
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

      final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button done = ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                done.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something

                        //if(type == 1) {
                            if (Global.IsNotNull(edt.getText().toString())) {
                                String promocode = couponsList.get(adapterposition).getPromoCode();
                                if (promocode.equals(edt.getText().toString())) {
                                    couponsList.get(adapterposition).setSelect(isSelected);
                                    addCouponsAdapter.notifyItemChanged(adapterposition);
                                    showHideAddButton();
                                    alertDialog.dismiss();
                                } else {
                                    edt.setError("Please enter valid promocode");
                                }
                            } else {
                                edt.setError("This field is require");
                            }
                        *//*}
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

                Button cancel = ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_NEGATIVE);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        couponsList.get(adapterposition).setSelect(false);
                        addCouponsAdapter.notifyDataSetChanged();
                        showHideAddButton();
                        alertDialog.dismiss();
                    }
                });

            }
        });
        alertDialog.show();
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
        Intent dashbord = new Intent(AddCouponsActivity.this, Dashboard.class);
        dashbord.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dashbord);
        overridePendingTransition(0,0);
    }
}*/
