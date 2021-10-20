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
import com.smileparser.medijunctions.bean.CommonObject;
import com.smileparser.medijunctions.utils.Global;

import java.util.List;

public class RecyclerCommonObjectAdapter extends RecyclerView.Adapter<RecyclerCommonObjectAdapter.myViewHolder> {

    Context context;
    List<CommonObject> commonObjectList;

    public RecyclerCommonObjectAdapter(Context context, List<CommonObject> commonObjectList) {
        this.context = context;
        this.commonObjectList = commonObjectList;
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

        final CommonObject commonObject = commonObjectList.get(i);

        if(Global.IsNotNull(commonObject))
        {
            if(Global.IsNotNull(commonObject.getName())) {
                holder.tvname.setText(commonObject.getName());
            }
            else
            {
                holder.tvname.setText("");
                holder.tvname.setVisibility(View.GONE);
            }

            holder.ch_select.setChecked(commonObject.isSelected());

            holder.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(commonObject.isSelected())
                    {
                        commonObjectList.get(i).setSelected(false);
                        holder.ch_select.setChecked(false);
                    }
                    else
                    {
                        commonObjectList.get(i).setSelected(true);
                        holder.ch_select.setChecked(true);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(Global.IsNotNull(commonObjectList))
        {
            if(commonObjectList.size() > 0) {
                return commonObjectList.size();
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
        CheckBox ch_select;
        RelativeLayout mainLayout;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tvname = itemView.findViewById(R.id.name);
            ch_select = itemView.findViewById(R.id.ch_data);
            mainLayout = itemView.findViewById(R.id.item_info);
        }
    }
}
