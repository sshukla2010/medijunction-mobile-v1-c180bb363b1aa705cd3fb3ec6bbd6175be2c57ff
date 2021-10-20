package com.smileparser.medijunctions.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smileparser.medijunctions.AshaConnectActivity;
import com.smileparser.medijunctions.ECGActivity;
import com.smileparser.medijunctions.R;
import com.smileparser.medijunctions.bean.SearchPatient;
import com.smileparser.medijunctions.bean.User;
import com.smileparser.medijunctions.utils.Global;

import java.util.ArrayList;
import java.util.List;

public class RecyclerSearchPatientAdapter extends RecyclerView.Adapter<RecyclerSearchPatientAdapter.myViewHolder> {

    Context context;
    List<SearchPatient> patientList;
    RecyclerSearchPatientAdapterEvent recyclerSearchPatientAdapterEvent;
    User user;

    public RecyclerSearchPatientAdapter(Context context, List<SearchPatient> patientList, RecyclerSearchPatientAdapterEvent recyclerSearchPatientAdapterEvent) {
        this.context = context;
        this.patientList = patientList;
        this.recyclerSearchPatientAdapterEvent = recyclerSearchPatientAdapterEvent;
        try {
            user = Global.getUserDetails(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface RecyclerSearchPatientAdapterEvent {
        void Goto(String id);

        void DeleteData(String id, int adapterPosition);
    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.searchpatient_list_row, viewGroup, false);
        final myViewHolder viewHolder = new myViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> listItems = new ArrayList<String>();
                listItems.add("Record ECG with Device");
                listItems.add("Record ECG with Cables");
                listItems.add("Record Vitals with Asha+");
                if (!user.getUsertype().equals(context.getString(R.string.hint_officer))) {
                    listItems.add("Edit");
                    listItems.add("Delete");
                }

                final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]); //{"Record ECG with Device", "Record ECG with Cables", "Edit", "Delete"};

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Pick an action for " + patientList.get(viewHolder.getAdapterPosition()).getName().split(" ")[0]);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                Intent ecgIntent = new Intent(context, ECGActivity.class);
                                ecgIntent.putExtra("patientName", patientList.get(viewHolder.getAdapterPosition()).getName());
                                ecgIntent.putExtra("ecgType", "device");
                                context.startActivity(ecgIntent);
                                break;
                            case 1:
                                ecgIntent = new Intent(context, ECGActivity.class);
                                ecgIntent.putExtra("patientName", patientList.get(viewHolder.getAdapterPosition()).getName());
                                ecgIntent.putExtra("ecgType", "switchsy");
                                context.startActivity(ecgIntent);
                                break;
                            case 2:
                                ecgIntent = new Intent(context, AshaConnectActivity.class);
                                ecgIntent.putExtra("patientid", patientList.get(viewHolder.getAdapterPosition()).getId());
                                ecgIntent.putExtra("ecgType", "switchsy");
                                context.startActivity(ecgIntent);
                                break;
                            case 3:
                                try {
                                    if (Global.IsNotNull(recyclerSearchPatientAdapterEvent)) {
                                        recyclerSearchPatientAdapterEvent.Goto(patientList.get(viewHolder.getAdapterPosition()).getId());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case 4:
                                try {
                                    if (Global.IsNotNull(recyclerSearchPatientAdapterEvent)) {
                                        recyclerSearchPatientAdapterEvent.DeleteData(patientList.get(viewHolder.getAdapterPosition()).getId(), viewHolder.getAdapterPosition());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;

                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
//        view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//            @Override
//            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                menu.add("Record ECG").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//
//                        Toast.makeText(context, patientList.get(viewHolder.getAdapterPosition()).getName(), Toast.LENGTH_SHORT).show();
//                        return true;
//                    }
//                });
//            }
//        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int i) {

        SearchPatient searchPatient = patientList.get(i);

        if (Global.IsNotNull(searchPatient)) {
            if (Global.IsNotNull(searchPatient.getName())) {
                holder.tvname.setText(searchPatient.getName());
            } else {
                holder.tvname.setText("");
                holder.tvname.setVisibility(View.GONE);
            }
            if (Global.IsNotNull(searchPatient.getEmail())) {
                holder.tvemail.setText(searchPatient.getEmail());
            } else {
                holder.tvemail.setText("");
                holder.tvemail.setVisibility(View.GONE);
            }

            if (Global.IsNotNull(searchPatient.getMobile())) {
                holder.tvmobile.setText(searchPatient.getMobile());
            } else {
                holder.tvmobile.setText("");
                holder.tvmobile.setVisibility(View.GONE);
            }
        }


    }

    @Override
    public int getItemCount() {
        if (Global.IsNotNull(patientList)) {
            if (patientList.size() > 0) {
                return patientList.size();
            } else {
                return 0;
            }
        } else {
            return 0;
        }

    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView tvname, tvemail, tvmobile;

        //        ImageButton img_button;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tvname = itemView.findViewById(R.id.spList_name);
            tvemail = itemView.findViewById(R.id.spList_email);
            tvmobile = itemView.findViewById(R.id.spList_mobile);
//            img_button = itemView.findViewById(R.id.delete_data);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (Global.IsNotNull(recyclerSearchPatientAdapterEvent)) {
                            recyclerSearchPatientAdapterEvent.Goto(patientList.get(getAdapterPosition()).getId());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

//            img_button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    try {
//                        if(Global.IsNotNull(recyclerSearchPatientAdapterEvent))
//                        {
//                            recyclerSearchPatientAdapterEvent.DeleteData(patientList.get(getAdapterPosition()).getId(),getAdapterPosition());
//                        }
//                    }
//                    catch (Exception e)
//                    {
//                        e.printStackTrace();
//                    }
//                }
//            });
        }
    }
}
