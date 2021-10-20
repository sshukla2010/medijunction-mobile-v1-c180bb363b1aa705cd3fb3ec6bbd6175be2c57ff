package com.smileparser.medijunctions.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.smileparser.medijunctions.AddCouponsActivity;
import com.smileparser.medijunctions.BookConsultationActivity;
import com.smileparser.medijunctions.R;
import com.smileparser.medijunctions.adapters.CouponsAdapter;
import com.smileparser.medijunctions.adapters.RecyclerAddCouponsAdapter;
import com.smileparser.medijunctions.bean.ApiResponse;
import com.smileparser.medijunctions.bean.Coupons;
import com.smileparser.medijunctions.customviews.LoadingDialog;
import com.smileparser.medijunctions.databinding.FragmentCouponsBinding;
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

public class CouponsFragment extends Fragment {

    public static final String TAG = "CouponsFragment";
    FragmentCouponsBinding binding;
    CouponsAdapter couponsAdapter = null;
    List<Coupons> couponsList = null;
    List<Coupons> couponIdList = null;
    Context mContext;

    public CouponsFragment(Context context) {
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCouponsBinding.inflate(getLayoutInflater());
        initView();
        return binding.getRoot();
    }

    private void initView() {

        if(couponsList == null) {
            couponsList = new ArrayList<>();
            couponIdList = new ArrayList<>();
        }
        couponsAdapter = new CouponsAdapter(mContext, couponsList,couponsAdapterEvent);
        binding.rvCouponslist.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvCouponslist.setAdapter(couponsAdapter);

        if(couponsList.size() == 0) {
            getCoupnList();
        }
        else
        {
            binding.tvCouponsNodata.setVisibility(View.GONE);
            binding.rvCouponslist.setVisibility(View.VISIBLE);
        }

        binding.couponswipeRefreshLayout.setOnRefreshListener(() -> {
            if (Global.isInternetOn(mContext)) {
                getCoupnList();
            } else {
                if (binding.couponswipeRefreshLayout.isRefreshing()) {
                    binding.couponswipeRefreshLayout.setRefreshing(false);
                }
            }
        });

    }

    public void getCoupnList()
    {
        if (Global.isInternetOn(mContext)) {
            LoadingDialog.showLoadingDialog(mContext, "Please wait...");
            new getCoupons().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else
        {
            Toast.makeText(mContext, ""+getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    private class getCoupons extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                Map<String,String> header = Global.generateHeader(mContext);
                HashMap<String,Object> map = new HashMap<>();
                map.put("UserId", BookConsultationActivity.user.getId());

                Call<ApiResponse> apiResponseCall = apiInterface.getCouponsList(header,map);

                apiResponseCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        //progressBar.setVisibility(View.GONE);
                        if (binding.couponswipeRefreshLayout.isRefreshing()) {
                            binding.couponswipeRefreshLayout.setRefreshing(false);
                        }

                        if (Global.IsNotNull(response.body())) {
                            if (response.body().isResponse()) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<List<Coupons>>() {
                                }.getType();
                                List<Coupons> VisitList = gson.fromJson(response.body().getResult(), type);

                                if (Global.IsNotNull(VisitList)) {

                                    couponsList.clear();
                                    couponsList.addAll(VisitList);

                                    if(couponsList.size() > 0) {
                                        binding.tvCouponsNodata.setVisibility(View.GONE);
                                        binding.rvCouponslist.setVisibility(View.VISIBLE);
                                        couponsAdapter.notifyDataSetChanged();
                                        //showHideAddButton();
                                        LoadingDialog.cancelLoading();
                                    }
                                    else
                                    {
                                        binding.tvCouponsNodata.setVisibility(View.VISIBLE);
                                         binding.rvCouponslist.setVisibility(View.GONE);
                                        //showHideAddButton();
                                        if(Global.IsNotNull(response.body().getMessage())) {
                                            binding.tvCouponsNodata.setText(response.body().getMessage());
                                        }
                                        else
                                        {
                                            binding.tvCouponsNodata.setText("No coupons found!");
                                        }
                                        //Toast.makeText(mContext, "No coupons Found", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            } else {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    LoadingDialog.cancelLoading();
                                    //Toast.makeText(mContext, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    binding.tvCouponsNodata.setVisibility(View.VISIBLE);
                                     binding.rvCouponslist.setVisibility(View.GONE);
                                    //showHideAddButton();
                                    if(Global.IsNotNull(response.body().getMessage())) {
                                        binding.tvCouponsNodata.setText(response.body().getMessage());
                                    }
                                    else
                                    {
                                        binding.tvCouponsNodata.setText("No coupons found!");
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

                                binding.tvCouponsNodata.setVisibility(View.VISIBLE);
                                 binding.rvCouponslist.setVisibility(View.GONE);
                                //showHideAddButton();
                                if(Global.IsNotNull(errorResponse.getMessage())) {
                                    binding.tvCouponsNodata.setText(errorResponse.getMessage());
                                }
                                else
                                {
                                    binding.tvCouponsNodata.setText("No coupons found!");
                                }
                                Toast.makeText(mContext, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }


                        } else {
                            binding.tvCouponsNodata.setVisibility(View.VISIBLE);
                             binding.rvCouponslist.setVisibility(View.GONE);
                            //showHideAddButton();
                            binding.tvCouponsNodata.setText(""+getString(R.string.somethingwrong));
                            Toast.makeText(mContext, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        if (binding.couponswipeRefreshLayout.isRefreshing()) {
                            binding.couponswipeRefreshLayout.setRefreshing(false);
                        }
                        // progressBar.setVisibility(View.GONE);
                        LoadingDialog.cancelLoading();
                        binding.tvCouponsNodata.setVisibility(View.VISIBLE);
                         binding.rvCouponslist.setVisibility(View.GONE);
                        //showHideAddButton();
                        if(Global.IsNotNull(t.getMessage())) {
                            binding.tvCouponsNodata.setText(t.getMessage());
                        }
                        else
                        {
                            binding.tvCouponsNodata.setText("No coupons found!");
                        }

                        Toast.makeText(mContext, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                e.getMessage();
                if (binding.couponswipeRefreshLayout.isRefreshing()) {
                    binding.couponswipeRefreshLayout.setRefreshing(false);
                }
                LoadingDialog.cancelLoading();
            }


            return null;
        }
    }

    CouponsAdapter.CouponsAdapterEvent couponsAdapterEvent = new CouponsAdapter.CouponsAdapterEvent() {
        @Override
        public void selectedCoupon(int itemAdapterPosition, boolean select) {
            couponsList.get(itemAdapterPosition).setSelect(select);
            couponsAdapter.notifyItemChanged(itemAdapterPosition);
           /* if(select) {
                if(Global.IsNotNull(couponsList.get(itemAdapterPosition).getPromoCode()) && !couponsList.get(itemAdapterPosition).isSelect())
                {
                    showPromocodeDialog(itemAdapterPosition,select);
                }
                else
                {
                    couponsList.get(itemAdapterPosition).setSelect(select);
                    couponsAdapter.notifyItemChanged(itemAdapterPosition);
                   // showHideAddButton();
                }
            }
            else {
                couponsList.get(itemAdapterPosition).setSelect(select);
                couponsAdapter.notifyItemChanged(itemAdapterPosition);
              //  showHideAddButton();
            }*/


        }

        @Override
        public void add(int itemAdapterPosition) {

            if(Global.IsNotNull(couponsList.get(itemAdapterPosition).getQty()))
            {
                long qty = couponsList.get(itemAdapterPosition).getQty() + 1;
                couponsList.get(itemAdapterPosition).setQty(qty);
                couponsAdapter.notifyItemChanged(itemAdapterPosition);
            }
        }

        @Override
        public void remove(int itemAdapterPosition) {
            if(Global.IsNotNull(couponsList.get(itemAdapterPosition).getQty()))
            {
                if(couponsList.get(itemAdapterPosition).getQty() > 1) {
                    long qty = couponsList.get(itemAdapterPosition).getQty() - 1;
                    couponsList.get(itemAdapterPosition).setQty(qty);
                    couponsAdapter.notifyItemChanged(itemAdapterPosition);
                }
            }
        }
    };
}
