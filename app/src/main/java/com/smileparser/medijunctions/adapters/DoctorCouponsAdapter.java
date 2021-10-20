package com.smileparser.medijunctions.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.smileparser.medijunctions.R;
import com.smileparser.medijunctions.bean.DoctorProfile;
import com.smileparser.medijunctions.databinding.AdapterourdoctorBinding;
import com.smileparser.medijunctions.utils.Global;

import java.util.List;

public class DoctorCouponsAdapter extends RecyclerView.Adapter<DoctorCouponsAdapter.MyViewHolder> {


    Context context;
    List<DoctorProfile> couponsList;
    DoctorCouponsAdapterEvent doctorCouponsAdapterEvent;

    public DoctorCouponsAdapter(Context context, List<DoctorProfile> couponsList, DoctorCouponsAdapterEvent doctorCouponsAdapterEvent) {
        this.context = context;
        this.couponsList = couponsList;
        this.doctorCouponsAdapterEvent = doctorCouponsAdapterEvent;
    }


    public  interface DoctorCouponsAdapterEvent
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
        DoctorCouponsAdapter.MyViewHolder viewHolder = new DoctorCouponsAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        DoctorProfile coupon = couponsList.get(i);
        if(Global.IsNotNull(coupon))
        {
            if(Global.IsNotNull(coupon.getOrganizationName()))
            {
                holder.binding.adpOdtvSectionHeading.setText(coupon.getOrganizationName());
                holder.binding.adpOdtvSectionHeading.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.binding.adpOdtvSectionHeading.setVisibility(View.GONE);
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
                        .apply(options).into(holder.binding.adpOdimg);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            if(Global.IsNotNull(coupon.getName())) {
                holder.binding.adpOdName.setText(coupon.getName());
            }
            else
            {
                holder.binding.adpOdName.setText("");
            }

            if(Global.IsNotNull(coupon.getDegree())) {
                holder.binding.adpOdDegree.setText(coupon.getDegree());
            }
            else
            {
                holder.binding.adpOdDegree.setText("");
            }

            if(Global.IsNotNull(coupon.getRate())) {
                String amount = String.valueOf((long)Math.round(Double.parseDouble(coupon.getRate())));
                holder.binding.adpOdRate.setText(amount);
            }
            else
            {
                holder.binding.adpOdRate.setText("");
            }

            if(Global.IsNotNull(coupon.getSpeciality())) {
                holder.binding.adpOdSpeciality.setText(coupon.getSpeciality());
            }
            else
            {
                holder.binding.adpOdSpeciality.setText("");
            }

            if(Global.IsNotNull(coupon.getLanguage())) {
                holder.binding.adpOdLanguage.setText(coupon.getLanguage());
            }
            else
            {
                holder.binding.adpOdLanguage.setText("");
            }

           /* if(Global.IsNotNull(coupon.getValidTo())) {
                holder.tv_validto.setText(coupon.getValidTo());
            }*/

            if (coupon.isSelect()) {
                holder.binding.adpOdCheckbox.setChecked(coupon.isSelect());
            } else {
                holder.binding.adpOdCheckbox.setChecked(coupon.isSelect());
            }

            if(Global.IsNotNull(coupon.getQty()))
            {
                if(coupon.getQty() > 1)
                {
                    holder.binding.adpOdcpminus.setEnabled(true);
                }
                else
                {
                    holder.binding.adpOdcpminus.setEnabled(false);
                }
                holder.binding.adpOdtvqty.setText(String.valueOf(coupon.getQty()));
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
       AdapterourdoctorBinding binding;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = AdapterourdoctorBinding.bind(itemView);
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            if (doctorCouponsAdapterEvent != null) {

                try {
                    doctorCouponsAdapterEvent.selectedCoupon(getAdapterPosition(),b);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onClick(View view) {

            switch (view.getId())
            {
                case R.id.adpOdcpminus :
                    if (doctorCouponsAdapterEvent != null) {

                        try {
                            doctorCouponsAdapterEvent.remove(getAdapterPosition());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case R.id.adpOdcpplus:
                    if (doctorCouponsAdapterEvent != null) {

                        try {
                            doctorCouponsAdapterEvent.add(getAdapterPosition());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case R.id.adpOdbtnviewProfile:
                    if (doctorCouponsAdapterEvent != null) {

                        try {
                            doctorCouponsAdapterEvent.showProfile(getAdapterPosition());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

            }
        }
    }
}
