package com.smileparser.medijunctions.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.smileparser.medijunctions.R;
import com.smileparser.medijunctions.databinding.PopupDisplaymessageBinding;

public class DisplayMessagePopup extends Dialog {

    PopupDisplaymessageBinding binding;
    Context mContext;
    String str = "";
    boolean sucess;

    public DisplayMessagePopup(@NonNull Context context) {
        super(context);
    }

    public DisplayMessagePopup(Context context,String message,boolean isSuccess) {
        super(context);
        this.mContext = context;
        this.str = message;
        this.sucess = isSuccess;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = PopupDisplaymessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setBackgroundDrawable( new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        binding.popupMessage.setText(str);

        if(sucess)
        {
            Glide.with(mContext).load(R.drawable.ic_check_true).into(binding.ivMessageSign);
        }
        else
        {
            Glide.with(mContext).load(R.drawable.ic_close).into(binding.ivMessageSign);
        }

        binding.btnMessagePopupOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
