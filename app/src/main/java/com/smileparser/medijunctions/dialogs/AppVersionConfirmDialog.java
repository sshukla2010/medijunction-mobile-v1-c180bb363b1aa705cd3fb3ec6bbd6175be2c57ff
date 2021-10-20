package com.smileparser.medijunctions.dialogs;

import android.os.Bundle;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.smileparser.medijunctions.R;
import com.smileparser.medijunctions.databinding.DialogAppVersionBinding;
import com.smileparser.medijunctions.utils.Constants;
import com.smileparser.medijunctions.utils.Global;

public class AppVersionConfirmDialog extends DialogFragment implements View.OnClickListener {

    private DialogAppVersionBinding binding;
    private DialogCallbacks mCallback;
    private boolean isForceUpdate = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAppVersionBinding.inflate(getLayoutInflater());
        initUI();
        return binding.getRoot();
    }

    private void initUI() {
        assert getArguments() != null;
        String title = getArguments().getString(Constants.BUNDLE_KEYS.BUNDLE_DATA_1);
        String subTitle = getArguments().getString(Constants.BUNDLE_KEYS.BUNDLE_DATA_2);
        isForceUpdate = getArguments().getBoolean(Constants.BUNDLE_KEYS.BUNDLE_DATA_3, false);

        binding.tvTitle.setText(title);
        binding.tvSubTitle.setText(subTitle);

        if (Global.IsNotNull(subTitle)) {
            binding.tvSubTitle.setVisibility(View.VISIBLE);
        } else {
            binding.tvSubTitle.setVisibility(View.GONE);
        }

        binding.tvForceUpdate.setVisibility(isForceUpdate ? View.VISIBLE : View.GONE);
        binding.linearUpdate.setVisibility(isForceUpdate ? View.GONE : View.VISIBLE);
        binding.imgClosePupup.setVisibility(isForceUpdate ? View.GONE : View.VISIBLE);

        binding.tvUpdate.setOnClickListener(this);
        binding.tvForceUpdate.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
        binding.imgClosePupup.setOnClickListener(this);
        binding.imgClosePupup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvUpdate) {
            mCallback.onUpdateClicked();
        } else if (view.getId() == R.id.tvCancel || view.getId() == R.id.imgClosePupup) {
            mCallback.onCancelClicked();
            dismiss();
        } else if (view.getId() == R.id.tvForceUpdate) {
            mCallback.onForceUpdateClicked();
        }

    }

    public void setCallbacks(DialogCallbacks dialogCallback) {
        mCallback = dialogCallback;
    }

    public interface DialogCallbacks {

        void onUpdateClicked();

        void onCancelClicked();

        void onForceUpdateClicked();

    }

}
