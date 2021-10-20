package com.smileparser.medijunctions.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smileparser.medijunctions.R;
import com.smileparser.medijunctions.bean.Coupons;
import com.smileparser.medijunctions.utils.Global;

import java.util.List;

public class RecyclerMyCouponsAdapter extends RecyclerView.Adapter<RecyclerMyCouponsAdapter.MyViewHolder> {


    Context context;
    List<Coupons> couponsList;

    public RecyclerMyCouponsAdapter(Context context, List<Coupons> couponsList) {
        this.context = context;
        this.couponsList = couponsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adaptercoupon,viewGroup,false);
        RecyclerMyCouponsAdapter.MyViewHolder viewHolder = new RecyclerMyCouponsAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        Coupons coupon = couponsList.get(i);
        holder.ch_coupon.setVisibility(View.GONE);
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

            /*if(Global.IsNotNull(coupon.getValidTo())) {
                holder.tv_validto.setText(coupon.getValidTo());
            }*/

            if(Global.IsNotNull(coupon.getStatus())) {
                holder.tv_status.setText(coupon.getStatus());
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

    public class MyViewHolder extends RecyclerView.ViewHolder  {
        AppCompatCheckBox ch_coupon;
        TextView tv_name,tv_desc,tv_amount,tv_validfrom,tv_validto,tv_status;
        RelativeLayout statuslayout,rl_qty;

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
            rl_qty.setVisibility(View.GONE);

        }

    }
}

