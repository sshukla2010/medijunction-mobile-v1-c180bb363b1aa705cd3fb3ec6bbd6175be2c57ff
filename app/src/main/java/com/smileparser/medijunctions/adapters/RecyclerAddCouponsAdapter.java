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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smileparser.medijunctions.R;
import com.smileparser.medijunctions.bean.Coupons;
import com.smileparser.medijunctions.utils.Global;

import java.util.List;

public class RecyclerAddCouponsAdapter extends RecyclerView.Adapter<RecyclerAddCouponsAdapter.MyViewHolder> {


    Context context;
    List<Coupons> couponsList;
    RecyclerAddCouponsAdapterEvent recyclerAddCouponsAdapterEvent;

    public RecyclerAddCouponsAdapter(Context context, List<Coupons> couponsList,RecyclerAddCouponsAdapterEvent recyclerAddCouponsAdapterEvent) {
        this.context = context;
        this.couponsList = couponsList;
        this.recyclerAddCouponsAdapterEvent = recyclerAddCouponsAdapterEvent;
    }


    public  interface RecyclerAddCouponsAdapterEvent
    {
        void selectedCoupon(int itemAdapterPosition,boolean select);
        void add(int itemAdapterPosition);
        void remove(int itemAdapterPosition);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adaptercoupon,viewGroup,false);
        RecyclerAddCouponsAdapter.MyViewHolder viewHolder = new RecyclerAddCouponsAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        Coupons coupon = couponsList.get(i);
        if(Global.IsNotNull(coupon))
        {
            if(Global.IsNotNull(coupon.getName())) {
                holder.tv_name.setText(coupon.getName());
            }

            if(Global.IsNotNull(coupon.getCouponDescription())) {
                holder.tv_desc.setText(coupon.getCouponDescription());
            }

            if(Global.IsNotNull(coupon.getRate())) {
                String amount = String.valueOf((long)Math.round(Double.parseDouble(coupon.getRate())));
                holder.tv_amount.setText(amount);
            }

            if(Global.IsNotNull(coupon.getValidity())) {
                holder.tv_validfrom.setText(coupon.getValidity());
            }

           /* if(Global.IsNotNull(coupon.getValidTo())) {
                holder.tv_validto.setText(coupon.getValidTo());
            }*/

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
        TextView tv_name,tv_desc,tv_amount,tv_validfrom,tv_validto,tv_status,tv_qty;
        RelativeLayout statuslayout,rl_qty;
        Button cp_minus,cp_plus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            ch_coupon = itemView.findViewById(R.id.adp_coupon_checkbox);
            tv_name = itemView.findViewById(R.id.adp_coupon_name);
            tv_desc = itemView.findViewById(R.id.adp_coupon_description);
            tv_amount = itemView.findViewById(R.id.adp_coupon_amount);
            tv_validfrom = itemView.findViewById(R.id.adp_coupon_validfrom);
            tv_validto = itemView.findViewById(R.id.adp_coupon_validto);
            tv_status = itemView.findViewById(R.id.adp_coupon_status);
            statuslayout = itemView.findViewById(R.id.lin_statuslayout);
            rl_qty = itemView.findViewById(R.id.rl_qty);
            tv_qty = itemView.findViewById(R.id.adp_Coupon_tv_qty);
            cp_minus = itemView.findViewById(R.id.adpcpMinus);
            cp_plus = itemView.findViewById(R.id.adp_Coupon_plus);
            cp_minus.setOnClickListener(this);
            cp_plus.setOnClickListener(this);
            ch_coupon.setOnCheckedChangeListener(this);
            statuslayout.setVisibility(View.GONE);
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            if (recyclerAddCouponsAdapterEvent != null) {

                try {
                    recyclerAddCouponsAdapterEvent.selectedCoupon(getAdapterPosition(),b);
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
                    if (recyclerAddCouponsAdapterEvent != null) {

                        try {
                            recyclerAddCouponsAdapterEvent.remove(getAdapterPosition());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case R.id.adp_Coupon_plus:
                    if (recyclerAddCouponsAdapterEvent != null) {

                        try {
                            recyclerAddCouponsAdapterEvent.add(getAdapterPosition());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

            }
        }
    }
}
