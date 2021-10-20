package com.smileparser.medijunctions;

import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.smileparser.medijunctions.adapters.RecyclerMyCouponsAdapter;
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

public class MyCouponsActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coupons);
    }
}

/*public class MyCouponsActivity extends AppCompatActivity {

    RecyclerMyCouponsAdapter myCouponsAdapter = null;
    RecyclerView rvMyCoupons;
    List<Coupons> couponsList = null;
    TextView tv_total,tv_nodata;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coupons);

        try {

            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.nav_mycoupons));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        rvMyCoupons = findViewById(R.id.rv_mycouponslist);
        tv_total = findViewById(R.id.total_amount);
        tv_nodata = findViewById(R.id.nodata);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvMyCoupons.setLayoutManager(layoutManager);
        couponsList = new ArrayList<>();
        getCoupnList();

    }

    public void getCoupnList()
    {
        if (Global.isInternetOn(this)) {
            LoadingDialog.showLoadingDialog(MyCouponsActivity.this, "Please wait...");
            new MyCouponsActivity.getCoupons().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

                User user = Global.getUserDetails(MyCouponsActivity.this);

                HashMap<String,String> map = new HashMap<>();
                map.put("UserId",user.getId());

                Call<ApiResponse> apiResponseCall = apiInterface.myCouponList(map);

                apiResponseCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        //progressBar.setVisibility(View.GONE);

                        if (Global.IsNotNull(response.body())) {
                            if (response.body().isResponse()) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<List<Coupons>>() {
                                }.getType();
                                List<Coupons> CouponList = gson.fromJson(response.body().getResult(), type);

                                if (Global.IsNotNull(CouponList)) {

                                    couponsList = new ArrayList<>();
                                    couponsList.addAll(CouponList);

                                    if(couponsList.size() > 0) {
                                        rvMyCoupons.setVisibility(View.VISIBLE);
                                        tv_nodata.setVisibility(View.GONE);
                                        myCouponsAdapter = new RecyclerMyCouponsAdapter(MyCouponsActivity.this, couponsList);
                                        rvMyCoupons.setLayoutManager(new LinearLayoutManager(MyCouponsActivity.this));
                                        rvMyCoupons.setAdapter(myCouponsAdapter);
                                        LoadingDialog.cancelLoading();

                                        totalAmount();
                                    }
                                    else
                                    {

                                        tv_nodata.setVisibility(View.VISIBLE);
                                        rvMyCoupons.setVisibility(View.GONE);
                                        if(Global.IsNotNull(response.body().getMessage())) {
                                            tv_nodata.setText(response.body().getMessage());
                                        }
                                        else
                                        {
                                            tv_nodata.setText("You have no coupon yet!");
                                        }
                                        //Toast.makeText(MyCouponsActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            } else {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    LoadingDialog.cancelLoading();
                                    tv_nodata.setVisibility(View.VISIBLE);
                                    rvMyCoupons.setVisibility(View.GONE);
                                    if(Global.IsNotNull(response.body().getMessage())) {
                                        tv_nodata.setText(response.body().getMessage());
                                    }
                                    else
                                    {
                                        tv_nodata.setText("You have no coupon yet!");
                                    }
                                    //Toast.makeText(MyCouponsActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
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
                                rvMyCoupons.setVisibility(View.GONE);
                                if(Global.IsNotNull(errorResponse.getMessage())) {
                                    tv_nodata.setText(errorResponse.getMessage());
                                }
                                else
                                {
                                    tv_nodata.setText("You have no coupon yet!");
                                }
                                Toast.makeText(MyCouponsActivity.this, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }


                        } else {
                            tv_nodata.setVisibility(View.VISIBLE);
                            rvMyCoupons.setVisibility(View.GONE);
                            tv_nodata.setText(""+getString(R.string.somethingwrong));
                            Toast.makeText(MyCouponsActivity.this, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                        // progressBar.setVisibility(View.GONE);
                        LoadingDialog.cancelLoading();
                        tv_nodata.setVisibility(View.VISIBLE);
                        rvMyCoupons.setVisibility(View.GONE);
                        if(Global.IsNotNull(t.getMessage())) {
                            tv_nodata.setText(t.getMessage());
                        }
                        else
                        {
                            tv_nodata.setText("You have no coupon yet!");
                        }
                        Toast.makeText(MyCouponsActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }


            return null;
        }
    }

    public void totalAmount()
    {
        long totalamount = 0;
        for(Coupons coupon : couponsList)
        {
            if(coupon.getStatus().startsWith("N"))
            {
               totalamount += (long)Math.round(Double.parseDouble(coupon.getAmount()));
            }
        }
        if(totalamount > 0) {
            tv_total.setVisibility(View.VISIBLE);
            tv_total.setText("Total payment due is " + totalamount +
                    ". Call the above number after making payment to activate service.");
        }
        else
        {
            tv_total.setVisibility(View.GONE);
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

    public void viewPayTmDetails(View v){
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View inflatedView = layoutInflater.inflate(R.layout.paytm_detail_view, null,false);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        PopupWindow popWindow = new PopupWindow(inflatedView, size.x - 150,size.y - 300, true );
        popWindow.setAnimationStyle(R.anim.slide_up);
        popWindow.setOutsideTouchable(true);
        popWindow.showAtLocation(v, Gravity.BOTTOM, 0,50);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}*/
