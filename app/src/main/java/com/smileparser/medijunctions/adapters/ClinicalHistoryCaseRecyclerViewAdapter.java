package com.smileparser.medijunctions.adapters;

import android.app.Activity;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.smileparser.medijunctions.R;
import com.smileparser.medijunctions.bean.CaseData;
import java.util.ArrayList;

public class ClinicalHistoryCaseRecyclerViewAdapter extends
    RecyclerView.Adapter<ClinicalHistoryCaseRecyclerViewAdapter.MyViewHolder> {

  public ArrayList<CaseData> myValues;
  public Activity activity;
  ClinicHistoryCardClickListener clinicHistoryCardClickListener;


  public ClinicalHistoryCaseRecyclerViewAdapter(Activity ctxt, ArrayList<CaseData> myValues) {
    this.myValues = myValues;
    this.activity = ctxt;
  }


  @Override
  public ClinicalHistoryCaseRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
      int viewType) {
    View listItem = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.clinical_data_view, parent, false);
    return new ClinicalHistoryCaseRecyclerViewAdapter.MyViewHolder(listItem);
  }

  public interface ClinicHistoryCardClickListener {

    void onAddDocumentClick(String appointmentId);

    void onSaveClick(String appointmentId);

    void onReviewClick(String appointmentId);

    void onCaseViewClick(String appointmentId);
  }

  @Override
  public void onBindViewHolder(ClinicalHistoryCaseRecyclerViewAdapter.MyViewHolder holder,
      int position) {
    try {
      clinicHistoryCardClickListener = (ClinicHistoryCardClickListener) activity;
    } catch (Exception e) {
      Log.e("InfantRecyclerViewAdptr", "Activity Must Implement infantVidCardInteractionListener");
    }
    holder.tv_caseNumber.setText(myValues.get(position).getCaseNo());
    holder.tv_caseNumber
        .setPaintFlags(holder.tv_caseNumber.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    holder.tv_caseNumber.setOnClickListener(view -> clinicHistoryCardClickListener
        .onCaseViewClick(myValues.get(position).getAppointmentId()));
    holder.tv_patientName.setText(myValues.get(position).getUserName());
    holder.tv_date.setText(myValues.get(position).getCreatedDate());
    holder.tv_totalFeeText.setText(String.format("%s", myValues.get(position).getFees()
        + myValues.get(position).getInjectionFees()
        - myValues.get(position).getDiscount()));
    holder.tv_drName.setText(myValues.get(position).getChampName());
    holder.tiet_consultingFee.setText(String.format("%s", myValues.get(position).getFees()));
    holder.tiet_consultingFee.setEnabled(false);
    holder.tiet_procedureFee
        .setText(String.format("%s", myValues.get(position).getInjectionFees()));
    holder.tiet_procedureFee.setEnabled(false);
    holder.tiet_discount.setText(String.format("%s", myValues.get(position).getDiscount()));
    holder.tiet_discount.setEnabled(false);
    holder.btn_save.setOnClickListener(
        v -> clinicHistoryCardClickListener.onSaveClick(myValues.get(position).getAppointmentId()));
    holder.btn_save.setEnabled(false);
    holder.btn_edit.setOnClickListener(
        v -> clinicHistoryCardClickListener
            .onAddDocumentClick(myValues.get(position).getAppointmentId()));

    holder.btn_review.setOnClickListener(
        v -> clinicHistoryCardClickListener
            .onReviewClick(myValues.get(position).getAppointmentId()));
  }


  @Override
  public int getItemCount() {

    return myValues.size();
  }

  public static class MyViewHolder extends RecyclerView.ViewHolder {

    private TextView tv_caseNumber;
    private TextView tv_patientName;
    private TextView tv_date;
    private TextView tv_totalFeeText;
    private TextView tv_drName;
    private TextInputEditText tiet_consultingFee;
    private TextInputEditText tiet_procedureFee;
    private TextInputEditText tiet_discount;
    private Button btn_save;
    private Button btn_edit;
    private Button btn_review;

    private RelativeLayout rlOverlay;

    public MyViewHolder(View itemView) {
      super(itemView);
      this.tv_caseNumber = itemView.findViewById(R.id.caseNumber);
      this.tv_patientName = itemView.findViewById(R.id.patientName);
      this.tv_date = itemView.findViewById(R.id.date);
      this.tv_drName = itemView.findViewById(R.id.drName);
      this.tv_totalFeeText = itemView.findViewById(R.id.totalFeeText);
      this.tiet_consultingFee = itemView.findViewById(R.id.consultingFee);
      this.tiet_procedureFee = itemView.findViewById(R.id.procedureFee);
      this.tiet_discount = itemView.findViewById(R.id.discount);
      this.btn_save = itemView.findViewById(R.id.saveButton);
      this.btn_edit = itemView.findViewById(R.id.editButton);
      this.btn_review = itemView.findViewById(R.id.reviewConsultaion);

    }
  }


}
