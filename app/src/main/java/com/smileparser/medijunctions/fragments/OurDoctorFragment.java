package com.smileparser.medijunctions.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.smileparser.medijunctions.R;
import com.smileparser.medijunctions.adapters.DoctorCouponsAdapter;
import com.smileparser.medijunctions.bean.ApiResponse;
import com.smileparser.medijunctions.bean.DoctorProfile;
import com.smileparser.medijunctions.customviews.LoadingDialog;
import com.smileparser.medijunctions.databinding.FragmentOurdoctorsBinding;
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

public class OurDoctorFragment extends Fragment {

    public static final String TAG = "OurDoctorFragment";

    FragmentOurdoctorsBinding binding;
    DoctorCouponsAdapter adapter = null;
    List<DoctorProfile> couponsList = null;
    List<DoctorProfile> couponIdList = null;
    Context mContext;

    public OurDoctorFragment(Context context) {
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOurdoctorsBinding.inflate(getLayoutInflater());
        initView();
        return binding.getRoot();
    }

    private void initView() {

        if(couponsList == null) {
            couponsList = new ArrayList<>();
            couponIdList = new ArrayList<>();
        }

        adapter = new DoctorCouponsAdapter(mContext, couponsList, recyclerAddDoctorCouponsAdapterEvent);
        binding.rvOurDoctorlist.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvOurDoctorlist.setAdapter(adapter);
        if(couponsList.size() == 0) {
            getDoctorList();
        }
        else
        {
            binding.tvDoctorNodata.setVisibility(View.GONE);
            binding.rvOurDoctorlist.setVisibility(View.VISIBLE);
        }

        binding.ourdoctorswipeRefreshLayout.setOnRefreshListener(() -> {
            if (Global.isInternetOn(mContext)) {
                getDoctorList();
            } else {
                if (binding.ourdoctorswipeRefreshLayout.isRefreshing())
                    binding.ourdoctorswipeRefreshLayout.setRefreshing(false);
            }
        });
    }




    public void getDoctorList()
    {
        if (Global.isInternetOn(mContext)) {
            LoadingDialog.showLoadingDialog(mContext, "Please wait...");

            new getDoctorList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else
        {
            binding.ourdoctorswipeRefreshLayout.setRefreshing(false);
            Toast.makeText(mContext, ""+getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    private class getDoctorList extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            try {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                Map<String,String> header = Global.generateHeader(mContext);
                HashMap<String,Object> map = new HashMap<>();


                Call<ApiResponse> apiResponseCall = apiInterface.getActiveDoctorList(header,map);

                apiResponseCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (binding.ourdoctorswipeRefreshLayout.isRefreshing()) {
                            binding.ourdoctorswipeRefreshLayout.setRefreshing(false);
                        }
                        if (Global.IsNotNull(response.body())) {
                            if (response.body().isResponse()) {
                                ////////Start
                                Gson gson = new Gson();
                                Type type = new TypeToken<List<DoctorProfile>>() {
                                }.getType();
                                List<DoctorProfile> VisitList = gson.fromJson(response.body().getResult(), type);

                                if (Global.IsNotNull(VisitList)) {

                                    couponsList.clear();
                                    couponsList.addAll(VisitList);

                                    if(couponsList.size() > 0) {
                                        binding.tvDoctorNodata.setVisibility(View.GONE);
                                         binding.rvOurDoctorlist.setVisibility(View.VISIBLE);
                                         adapter.notifyDataSetChanged();
                                        //showHideAddButton();
                                        LoadingDialog.cancelLoading();
                                    }
                                    else
                                    {
                                        binding.tvDoctorNodata.setVisibility(View.VISIBLE);
                                         binding.rvOurDoctorlist.setVisibility(View.GONE);
                                        //showHideAddButton();
                                        if(Global.IsNotNull(response.body().getMessage())) {
                                            binding.tvDoctorNodata.setText(response.body().getMessage());
                                        }
                                        else
                                        {
                                            binding.tvDoctorNodata.setText("No coupons found!");
                                        }
                                        //Toast.makeText(mContext, "No coupons Found", Toast.LENGTH_SHORT).show();
                                    }


                                }
                                ///////////////// over

                            } else {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    LoadingDialog.cancelLoading();
                                    //Toast.makeText(mContext, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    //showHideAddButton();
                                    binding.tvDoctorNodata.setVisibility(View.VISIBLE);
                                     binding.rvOurDoctorlist.setVisibility(View.GONE);
                                    if(Global.IsNotNull(response.body().getMessage())) {
                                        binding.tvDoctorNodata.setText(response.body().getMessage());
                                    }
                                    else
                                    {
                                        binding.tvDoctorNodata.setText("No coupons found!");
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
                                //showHideAddButton();
                                binding.tvDoctorNodata.setVisibility(View.VISIBLE);
                                 binding.rvOurDoctorlist.setVisibility(View.GONE);
                                if(Global.IsNotNull(errorResponse.getMessage())) {
                                    binding.tvDoctorNodata.setText(errorResponse.getMessage());
                                }
                                else
                                {
                                    binding.tvDoctorNodata.setText("No coupons found!");
                                }
                                Toast.makeText(mContext, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }

                            LoadingDialog.cancelLoading();


                        } else {
                            binding.tvDoctorNodata.setVisibility(View.VISIBLE);
                             binding.rvOurDoctorlist.setVisibility(View.GONE);
                            binding.tvDoctorNodata.setText(""+getString(R.string.somethingwrong));
                            Toast.makeText(mContext, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                        // progressBar.setVisibility(View.GONE);
                        if (binding.ourdoctorswipeRefreshLayout.isRefreshing()) {
                            binding.ourdoctorswipeRefreshLayout.setRefreshing(false);
                        }
                        LoadingDialog.cancelLoading();
                        //showHideAddButton();
                        binding.tvDoctorNodata.setVisibility(View.VISIBLE);
                         binding.rvOurDoctorlist.setVisibility(View.GONE);
                        if(Global.IsNotNull(t.getMessage())) {
                            binding.tvDoctorNodata.setText(t.getMessage());
                        }
                        else
                        {
                            binding.tvDoctorNodata.setText("No coupons found!");
                        }

                        Toast.makeText(mContext, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                e.getMessage();
                if (binding.ourdoctorswipeRefreshLayout.isRefreshing()) {
                    binding.ourdoctorswipeRefreshLayout.setRefreshing(false);
                }
                LoadingDialog.cancelLoading();
            }


            return null;
        }
    }


    DoctorCouponsAdapter.DoctorCouponsAdapterEvent recyclerAddDoctorCouponsAdapterEvent = new DoctorCouponsAdapter.DoctorCouponsAdapterEvent() {
        @Override
        public void selectedCoupon(int itemAdapterPosition, boolean select) {
            couponsList.get(itemAdapterPosition).setSelect(select);
            adapter.notifyItemChanged(itemAdapterPosition);
            /*  if(select) {
                if(Global.IsNotNull(couponsList.get(itemAdapterPosition).getPromoCode()) && !couponsList.get(itemAdapterPosition).isSelect())
                {
                    showPromocodeDialog(itemAdapterPosition,select);
                }
                else
                {
                    couponsList.get(itemAdapterPosition).setSelect(select);
                    adapter.notifyItemChanged(itemAdapterPosition);
                    showHideAddButton();
                }
            }
            else {
                couponsList.get(itemAdapterPosition).setSelect(select);
                adapter.notifyItemChanged(itemAdapterPosition);
                showHideAddButton();
            }*/


        }

        @Override
        public void add(int itemAdapterPosition) {

            if(Global.IsNotNull(couponsList.get(itemAdapterPosition).getQty()))
            {
                long qty = couponsList.get(itemAdapterPosition).getQty() + 1;
                couponsList.get(itemAdapterPosition).setQty(qty);
                adapter.notifyItemChanged(itemAdapterPosition);
            }
        }

        @Override
        public void remove(int itemAdapterPosition) {
            if(Global.IsNotNull(couponsList.get(itemAdapterPosition).getQty()))
            {
                if(couponsList.get(itemAdapterPosition).getQty() > 1) {
                    long qty = couponsList.get(itemAdapterPosition).getQty() - 1;
                    couponsList.get(itemAdapterPosition).setQty(qty);
                    adapter.notifyItemChanged(itemAdapterPosition);
                }
            }
        }

        @Override
        public void showProfile(int itemAdapterPosition) {
            showProfilePopup(couponsList.get(itemAdapterPosition));
        }
    };

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
                Glide.with(mContext).load(doctorProfile.getImagePath())
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

        final AlertDialog.Builder alert = new AlertDialog.Builder(mContext,R.style.CustomDialogTheme);
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


}
