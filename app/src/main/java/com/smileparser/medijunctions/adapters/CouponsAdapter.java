package com.smileparser.medijunctions.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.smileparser.medijunctions.R;
import com.smileparser.medijunctions.bean.Coupons;
import com.smileparser.medijunctions.databinding.AdaptercouponBinding;
import com.smileparser.medijunctions.utils.Global;

import java.util.List;

public class CouponsAdapter extends RecyclerView.Adapter<CouponsAdapter.MyViewHolder> {


    Context context;
    List<Coupons> couponsList;
    CouponsAdapterEvent couponsAdapterEvent;

    public CouponsAdapter(Context context, List<Coupons> couponsList, CouponsAdapterEvent couponsAdapterEvent) {
        this.context = context;
        this.couponsList = couponsList;
        this.couponsAdapterEvent = couponsAdapterEvent;
    }


    public  interface CouponsAdapterEvent
    {
        void selectedCoupon(int itemAdapterPosition,boolean select);
        void add(int itemAdapterPosition);
        void remove(int itemAdapterPosition);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adaptercoupon,viewGroup,false);
        CouponsAdapter.MyViewHolder viewHolder = new CouponsAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        Coupons coupon = couponsList.get(i);
        if(Global.IsNotNull(coupon))
        {
            if(Global.IsNotNull(coupon.getName())) {
                holder.binding.adpCouponName.setText(coupon.getName());
            }

            if(Global.IsNotNull(coupon.getCouponDescription())) {
                holder.binding.adpCouponDescription.setText(coupon.getCouponDescription());
            }

            if(Global.IsNotNull(coupon.getRate())) {
                String amount = String.valueOf((long)Math.round(Double.parseDouble(coupon.getRate())));
                holder.binding.adpCouponAmount.setText(amount);
            }

            if(Global.IsNotNull(coupon.getValidity())) {
                 holder.binding.adpCouponValidfrom.setText(coupon.getValidity());
            }

           /* if(Global.IsNotNull(coupon.getValidTo())) {
                holder.tv_validto.setText(coupon.getValidTo());
            }*/

            if (coupon.isSelect()) {
                holder.binding.adpCouponCheckbox.setChecked(coupon.isSelect());
            } else {
                holder.binding.adpCouponCheckbox.setChecked(coupon.isSelect());
            }

            if(Global.IsNotNull(coupon.getQty()))
            {
                if(coupon.getQty() > 1)
                {
                    holder.binding.adpcpMinus.setEnabled(true);
                }
                else
                {
                    holder.binding.adpcpMinus.setEnabled(false);
                }
                holder.binding.adpCouponTvQty.setText(String.valueOf(coupon.getQty()));
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

        AdaptercouponBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = AdaptercouponBinding.bind(itemView);
            binding.linStatuslayout.setVisibility(View.GONE);
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            if (couponsAdapterEvent != null) {

                try {
                    couponsAdapterEvent.selectedCoupon(getAdapterPosition(),b);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onClick(View view) {

            switch (view.getId())
            {
                case R.id.adpcpMinus:
                    if (couponsAdapterEvent != null) {

                        try {
                            couponsAdapterEvent.remove(getAdapterPosition());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case R.id.adp_Coupon_plus:
                    if (couponsAdapterEvent != null) {

                        try {
                            couponsAdapterEvent.add(getAdapterPosition());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

            }
        }
    }
}
