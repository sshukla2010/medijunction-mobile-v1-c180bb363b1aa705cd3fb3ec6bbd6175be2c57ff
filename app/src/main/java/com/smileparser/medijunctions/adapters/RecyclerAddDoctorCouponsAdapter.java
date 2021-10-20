package com.smileparser.medijunctions.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.smileparser.medijunctions.R;
import com.smileparser.medijunctions.bean.DoctorProfile;
import com.smileparser.medijunctions.utils.Global;

import java.util.List;

public class RecyclerAddDoctorCouponsAdapter{} /*extends RecyclerView.Adapter<RecyclerAddDoctorCouponsAdapter.MyViewHolder> {


    Context context;
    List<DoctorProfile> couponsList;
    RecyclerAddDoctorCouponsAdapterEvent recyclerAddDoctorCouponsAdapterEvent;

    public RecyclerAddDoctorCouponsAdapter(Context context, List<DoctorProfile> couponsList, RecyclerAddDoctorCouponsAdapterEvent recyclerAddDoctorCouponsAdapterEvent) {
        this.context = context;
        this.couponsList = couponsList;
        this.recyclerAddDoctorCouponsAdapterEvent = recyclerAddDoctorCouponsAdapterEvent;
    }


    public  interface RecyclerAddDoctorCouponsAdapterEvent
    {
        void selectedCoupon(int itemAdapterPosition, boolean select);
        void add(int itemAdapterPosition);
        void remove(int itemAdapterPosition);
        void showProfile(int itemAdapterPosition);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapterourdoctor,viewGroup,false);
        RecyclerAddDoctorCouponsAdapter.MyViewHolder viewHolder = new RecyclerAddDoctorCouponsAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        DoctorProfile coupon = couponsList.get(i);
        if(Global.IsNotNull(coupon))
        {
            if(Global.IsNotNull(coupon.getOrganizationName()))
            {
                holder.tvSectionHeading.setText(coupon.getOrganizationName());
                holder.tvSectionHeading.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.tvSectionHeading.setVisibility(View.GONE);
            }

            try
            {
                RequestOptions options = new RequestOptions()
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .placeholder(R.drawable.ic_usercolor)
                        .error(R.drawable.ic_usercolor);
                Glide.with(holder.itemView.getContext()).load(coupon.getImagePath())
                        .apply(options).into(holder.img_pic);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            if(Global.IsNotNull(coupon.getName())) {
                holder.tv_name.setText(coupon.getName());
            }
            else
            {
                holder.tv_name.setText("");
            }

            if(Global.IsNotNull(coupon.getDegree())) {
                holder.tv_deg.setText(coupon.getDegree());
            }
            else
            {
                holder.tv_deg.setText("");
            }

            if(Global.IsNotNull(coupon.getRate())) {
                String amount = String.valueOf((long)Math.round(Double.parseDouble(coupon.getRate())));
                holder.tv_rate.setText(amount);
            }
            else
            {
                holder.tv_rate.setText("");
            }

            if(Global.IsNotNull(coupon.getSpeciality())) {
                holder.tv_speciality.setText(coupon.getSpeciality());
            }
            else
            {
                holder.tv_speciality.setText("");
            }

            if(Global.IsNotNull(coupon.getLanguage())) {
                holder.tv_language.setText(coupon.getLanguage());
            }
            else
            {
                holder.tv_language.setText("");
            }

           *//* if(Global.IsNotNull(coupon.getValidTo())) {
                holder.tv_validto.setText(coupon.getValidTo());
            }*//*

            if (coupon.isSelect()) {
                holder.ch_coupon.setChecked(coupon.isSelect());
            } else {
                holder.ch_coupon.setChecked(coupon.isSelect());
            }

            if(Global.IsNotNull(coupon.getQty()))
            {
                if(coupon.getQty() > 1)
                {
                    holder.cp_minus.setEnabled(true);
                }
                else
                {
                    holder.cp_minus.setEnabled(false);
                }
                holder.tv_qty.setText(String.valueOf(coupon.getQty()));
            }




        }
    }

    @Override
    public int getItemCount() {
        if (couponsList.size() > 0) {
            return couponsList.size();
        } else {
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
        AppCompatCheckBox ch_coupon;
        TextView tv_name,tv_deg,tv_speciality,tv_rate,tv_language,tv_qty,tvSectionHeading;
        LinearLayout rl_qty;
        Button cp_minus,cp_plus,btn_profile;
        ImageView img_pic;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ch_coupon = itemView.findViewById(R.id.adp_od_checkbox);
            tv_name = itemView.findViewById(R.id.adp_od_name);
            tv_deg = itemView.findViewById(R.id.adp_od_degree);
            tv_rate = itemView.findViewById(R.id.adp_od_rate);
            tv_speciality = itemView.findViewById(R.id.adp_od_speciality);
            tv_language = itemView.findViewById(R.id.adp_od_language);
            tvSectionHeading = itemView.findViewById(R.id.tvSectionHeading);

            img_pic = itemView.findViewById(R.id.adp_od_img);
            rl_qty = itemView.findViewById(R.id.rl_qty);
            tv_qty = itemView.findViewById(R.id.tv_qty);
            cp_minus = itemView.findViewById(R.id.cp_minus);
            cp_plus = itemView.findViewById(R.id.cp_plus);
            btn_profile = itemView.findViewById(R.id.btn_viewProfile);

            cp_minus.setOnClickListener(this);
            cp_plus.setOnClickListener(this);
            btn_profile.setOnClickListener(this);
            ch_coupon.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            if (recyclerAddDoctorCouponsAdapterEvent != null) {

                try {
                    recyclerAddDoctorCouponsAdapterEvent.selectedCoupon(getAdapterPosition(),b);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onClick(View view) {

            switch (view.getId())
            {
                case R.id.cp_minus :
                    if (recyclerAddDoctorCouponsAdapterEvent != null) {

                        try {
                            recyclerAddDoctorCouponsAdapterEvent.remove(getAdapterPosition());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case R.id.cp_plus :
                    if (recyclerAddDoctorCouponsAdapterEvent != null) {

                        try {
                            recyclerAddDoctorCouponsAdapterEvent.add(getAdapterPosition());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case R.id.btn_viewProfile:
                    if (recyclerAddDoctorCouponsAdapterEvent != null) {

                        try {
                            recyclerAddDoctorCouponsAdapterEvent.showProfile(getAdapterPosition());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

            }
        }
    }
}*/
