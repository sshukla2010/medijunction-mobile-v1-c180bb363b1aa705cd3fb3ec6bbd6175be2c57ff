package com.smileparser.medijunctions.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smileparser.medijunctions.R;
import com.smileparser.medijunctions.bean.HealthCondition;
import com.smileparser.medijunctions.utils.Global;

import java.util.List;

public class RecyclerHealthConditionAdapter extends RecyclerView.Adapter<RecyclerHealthConditionAdapter.myViewHolder> {

    Context context;
    List<HealthCondition> healthConditionList;

    public RecyclerHealthConditionAdapter(Context context, List<HealthCondition> healthConditions) {
        this.context = context;
        this.healthConditionList = healthConditions;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item,viewGroup,false);
        myViewHolder viewHolder = new myViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, @SuppressLint("RecyclerView") final int i) {

        final HealthCondition healthCondition = healthConditionList.get(i);

        if(Global.IsNotNull(healthCondition))
        {
            if(Global.IsNotNull(healthCondition.getName())) {
                holder.tvname.setText(healthCondition.getName());
            }
            else
            {
                holder.tvname.setText("");
                holder.tvname.setVisibility(View.GONE);
            }

            holder.ch_allergy.setChecked(healthCondition.isSelected());

            holder.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(healthCondition.isSelected())
                    {
                        healthConditionList.get(i).setSelected(false);
                        holder.ch_allergy.setChecked(false);
                    }
                    else
                    {
                        healthConditionList.get(i).setSelected(true);
                        holder.ch_allergy.setChecked(true);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(Global.IsNotNull(healthConditionList))
        {
            if(healthConditionList.size() > 0) {
                return healthConditionList.size();
            }
            else
            {
                return 0;
            }
        }
        else
        {
            return 0;
        }

    }

    public class myViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvname;
        CheckBox ch_allergy;
        RelativeLayout mainLayout;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tvname = itemView.findViewById(R.id.name);
            ch_allergy = itemView.findViewById(R.id.ch_data);
            mainLayout = itemView.findViewById(R.id.item_info);
        }
    }
}
