package com.smileparser.medijunctions.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;

import com.smileparser.medijunctions.R;
import com.smileparser.medijunctions.databinding.PopupForgotpasswordBinding;
import com.smileparser.medijunctions.utils.Global;

import org.apache.sanselan.formats.tiff.constants.TagInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPasswordDialog extends Dialog {

    Context mContext;

    PopupForgotpasswordBinding binding;
    private static final String TAG = "ForgotPasswordDialog: ";
    public String Error = "";

    public ForgotPasswordDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;

        binding = PopupForgotpasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setBackgroundDrawable( new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Error = mContext.getString(R.string.error_msg_require_field);

        String t1 = "New User? ";
        String t2 = "Sign Up";

        t1 = Global.getColoredSpanned(t1,"#000000");
        t2 = Global.getColoredSpanned(t2,"#008080");

        binding.edtpopupForgotEmail.requestFocus();

        if (Build.VERSION.SDK_INT >= 24) {
            binding.btnLoginSignup.setText(Html.fromHtml(t1 + t2, HtmlCompat.FROM_HTML_MODE_LEGACY)); // for 24 API  and more
        } else {
            binding.btnLoginSignup.setText(Html.fromHtml(t1 + t2)); // or for older API
        }

        binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.btnLoginSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSigupClick();
            }
        });

        binding.btnBacktologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.btnForgotSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validation())
                {
                    onSubmit(binding.edtpopupForgotEmail.getText().toString().trim());
                }
            }
        });

    }

    public void onSubmit(String value) {
        Log.d(TAG,"");
    }

    public void onSigupClick() {
        Log.d(TAG,"");
    }

    public boolean validation()
    {
        Matcher matchEmail;
        boolean isValid = true;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);


        if(!Global.IsNotNull(binding.edtpopupForgotEmail.getText().toString().trim()))
        {
            binding.edtpopupForgotEmail.setError(Error);
            isValid = false;
        }
        else
        {
            matchEmail = pattern.matcher(binding.edtpopupForgotEmail.getText().toString());
            if(!matchEmail.matches())
            {
                binding.edtpopupForgotEmail.setError("Email is invalid");
                isValid=false;
            }
        }
        return isValid;
    }
}
