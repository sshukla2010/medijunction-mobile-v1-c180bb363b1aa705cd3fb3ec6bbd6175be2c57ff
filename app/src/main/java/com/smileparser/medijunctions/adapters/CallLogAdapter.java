package com.smileparser.medijunctions.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.smileparser.medijunctions.R;
import com.smileparser.medijunctions.bean.CallLog;
import com.smileparser.medijunctions.databinding.AdapterCalllogBinding;
import com.smileparser.medijunctions.utils.GlideCircleTransformation;
import com.smileparser.medijunctions.utils.Global;

import java.util.List;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder> {


    Context context;
    List<CallLog> callLogList;

    public CallLogAdapter(Context context, List<CallLog> callLogList) {
        this.context = context;
        this.callLogList = callLogList;
    }


    @NonNull
    @Override
    public CallLogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_calllog, viewGroup, false);
        CallLogViewHolder viewHolder = new CallLogViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CallLogViewHolder holder, int i) {

        CallLog callLog = callLogList.get(i);

        if (Global.IsNotNull(callLog)) {

            try {


                if (Global.IsNotNull(callLog.getImagePath())) {
                    RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(true)
                            .placeholder(R.drawable.action_gray_button)
                            .error(R.drawable.action_gray_button)
                            .bitmapTransform(new GlideCircleTransformation(holder.itemView.getContext()));
                    Glide.with(holder.itemView.getContext())
                            .load(callLog.getImagePath())
                            .apply(requestOptions)
                            .into(holder.binding.ivContactImage);
                } else {
                   // holder.binding.ivContactImage.setImageResource(R.mipmap.icon_contacts);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (Global.IsNotNull(callLog.getCallerName())) {
                holder.binding.tvCallerName.setText(callLog.getCallerName());
            } else {
                holder.binding.tvCallerName.setVisibility(View.GONE);
            }

            if (Global.IsNotNull(callLog.getCallAt())) {
                String date = Global.getFormatedDate(callLog.getCallAt());
                if (Global.IsNotNull(date)) {
                    holder.binding.tvCallDate.setText(date);
                } else {
                    holder.binding.tvCallDate.setVisibility(View.GONE);
                }

                String time = Global.getFormatedTime(callLog.getCallAt());
                if (Global.IsNotNull(time)) {
                    holder.binding.tvCallTime.setText(time);
                } else {
                    holder.binding.tvCallTime.setVisibility(View.GONE);
                }

            } else {
                holder.binding.rlDatetime.setVisibility(View.GONE);
            }

            if (Global.IsNotNull(callLog.getPatientName())) {
                holder.binding.tvPatientName.setText("For" + callLog.getPatientName());
            } else {
                holder.binding.tvPatientName.setVisibility(View.GONE);
            }

        }

    }

    @Override
    public int getItemCount() {
        if (callLogList.size() > 0) {
            return callLogList.size();
        } else {
            return 0;
        }
    }

    public class CallLogViewHolder extends RecyclerView.ViewHolder {

        AdapterCalllogBinding binding;


        public CallLogViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = AdapterCalllogBinding.bind(itemView);
        }

    }
}
