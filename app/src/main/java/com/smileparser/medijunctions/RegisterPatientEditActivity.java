package com.smileparser.medijunctions;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.rilixtech.CountryCodePicker;
import com.smileparser.medijunctions.adapters.RecyclerAllergyAdapter;
import com.smileparser.medijunctions.adapters.RecyclerHealthConditionAdapter;
import com.smileparser.medijunctions.bean.Allergy;
import com.smileparser.medijunctions.bean.ApiResponse;
import com.smileparser.medijunctions.bean.HealthCondition;
import com.smileparser.medijunctions.bean.RegisterPatient;
import com.smileparser.medijunctions.bean.SerializedList;
import com.smileparser.medijunctions.bean.User;
import com.smileparser.medijunctions.customviews.LoadingDialog;
import com.smileparser.medijunctions.databinding.ActivityRegisterPatientBinding;
import com.smileparser.medijunctions.databinding.ActivityRegisterPatientEditBinding;
import com.smileparser.medijunctions.interfaces.ApiInterface;
import com.smileparser.medijunctions.utils.ApiClient;
import com.smileparser.medijunctions.utils.Global;
import com.smileparser.medijunctions.utils.ServiceBase;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterPatientEditActivity extends AppCompatActivity implements View.OnClickListener {

    private ActionBar toolbar;

    ActivityRegisterPatientEditBinding binding;
    private static final int GET_FROM_CAMERA = 0;
    private static final int GET_FROM_GALLERY = 1;
    private static final int REQUEST_FOR_CAMERA = 123;
    private static final int REQUEST_FOR_GALLERY = 200;
    private Uri selectedImage;
    private DatePickerDialog datePickerDialog;
    String path;
    String ProfileImage = "";
    ServiceBase<RegisterPatient> registerPatientServiceBase;
    User user;
    boolean isProfilePicSelected = false;
    ServiceBase<Allergy> allergyServiceBase;
    ServiceBase<HealthCondition> healthConditionServiceBase;
    List<Allergy> allergyList;
    List<HealthCondition> healthConditionList;
    RegisterPatient registerPatient;
    boolean isEmailVerified;
    boolean isMobileVerifired;
    String tmpPassword = "";
    public String Error = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterPatientEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // setContentView(R.layout.activity_register_patient_edit);
        /*ViewGroup content = (ViewGroup) findViewById(R.id.sidenav_frame_container);
        getLayoutInflater().inflate(R.layout.activity_register_patient_edit, content, true);*/


        //toolbar=getSupportActionBar();

        try {

            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Edit Registered Patient Data");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            healthConditionServiceBase = new ServiceBase<HealthCondition>(getApplicationContext());
            allergyServiceBase = new ServiceBase<Allergy>(getApplicationContext());

            allergyList = allergyServiceBase.GetAll(Allergy.class);
            healthConditionList = healthConditionServiceBase.GetAll(HealthCondition.class);
        } catch (Exception e) {
            e.getMessage();
        }
        Error = getApplicationContext().getString(R.string.error_msg_require_field);
        binding.editFirstname.setOnClickListener(this);
        binding.editMiddlename.setOnClickListener(this);
        binding.editLastname.setOnClickListener(this);
        binding.editFatherHus.setOnClickListener(this);
        binding.editMobile.setOnClickListener(this);
        binding.editEmail.setOnClickListener(this);
        binding.editAddress.setOnClickListener(this);
        binding.editIdProof1.setOnClickListener(this);
        binding.editIdProof2.setOnClickListener(this);
        binding.editDob.setOnClickListener(this);
        binding.editSpnAllergy.setOnClickListener(this);
        binding.editSpnHealth.setOnClickListener(this);
        binding.editAllergy.setOnClickListener(this);
        binding.editHealthcondition.setOnClickListener(this);
        binding.editFatherHus.setOnClickListener(this);

        binding.btnEditSave.setOnClickListener(this);
        binding.btnEditAdd.setOnClickListener(this);
        binding.btnEditCamera.setOnClickListener(this);
        binding.edtbtnVerifyEmail.setOnClickListener(this);
        binding.edtbtnSendEmailotp.setOnClickListener(this);
        binding.edtbtnVerifyMobile.setOnClickListener(this);
        binding.edtbtnSendMobileotp.setOnClickListener(this);

        binding.editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(Global.IsNotNull(s.toString().trim()))
                {
                    if(registerPatient.getEmail().equals(s.toString()))
                    {
                        isEmailVerified = true;
                    }
                    else
                    {
                        isEmailVerified = false;

                        if (Global.IsNotNull(s.toString().trim())) {
                            String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
                            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
                            Matcher matchEmail = pattern.matcher(binding.editEmail.getText().toString());
                            if (matchEmail.matches()) {
                                binding.edtlayoutVeryfyEmail.setVisibility(View.VISIBLE);
                            } else {
                                binding.edtlayoutVeryfyEmail.setVisibility(View.GONE);

                            }
                            binding.edtbtnVerifyEmail.setVisibility(View.VISIBLE);
                            binding.edtregEmailOtp.setVisibility(View.GONE);
                            binding.edtregEmailOtp.setText(null);
                            binding.edtbtnSendEmailotp.setVisibility(View.GONE);
                            tmpPassword = "";
                            isEmailVerified = false;
                        }
                    }
                }
            }
        });

        binding.editMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(registerPatient.getMobile().equals(s.toString()))
                {
                    isMobileVerifired = true;
                }
                else
                {
                    if (Global.IsNotNull(s.toString().trim())) {
                        if (binding.editCodePicker.isValid()) {
                            binding.edtlayoutVeryfyMobile.setVisibility(View.VISIBLE);
                        } else {
                            binding.edtlayoutVeryfyMobile.setVisibility(View.GONE);
                        }
                        binding.edtbtnVerifyMobile.setVisibility(View.VISIBLE);
                        binding.edtrgMobileOtp.setVisibility(View.GONE);
                        binding.edtrgMobileOtp.setText(null);
                        binding.edtbtnSendMobileotp.setVisibility(View.GONE);

                    }
                    tmpPassword = "";
                    isMobileVerifired = false;
                }
            }
        });

        binding.editAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.editAge.isInEditMode()) {
                    if (Global.IsNotNull(s)) {
                        if (Global.IsNotNull(s.toString().trim())) {
                            if (Global.IsNotNull(Integer.parseInt(s.toString()) > 0)) {
                                String tempDate = "" + 01 + "/" + 01 + "/" + Global.getDOByear(Integer.parseInt(s.toString()));
                                binding.editDob.setText(tempDate);
                            }
                        } else {
                            binding.editDob.setText(null);
                        }
                    } else {
                        binding.editDob.setText(null);
                    }
                }

            }
        });


        binding.eRadioFatherHus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);

                if (Global.IsNotNull(radioButton)) {
                    if (radioButton.getText().toString().equals(getString(R.string.hint_father))) {

                        binding.editFatherHus.setHint(getString(R.string.hint_fathername));
                        if (binding.editFatherHus.getVisibility() == View.GONE) {
                            binding.editFatherHus.setVisibility(View.VISIBLE);
                        }
                    } else {
                        binding.editFatherHus.setHint(getString(R.string.hint_husbandname));
                        if (binding.editFatherHus.getVisibility() == View.GONE) {
                            binding.editFatherHus.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        binding.editCodePicker.registerPhoneNumberTextView(binding.editMobile);

        registerPatientServiceBase = new ServiceBase<RegisterPatient>(RegisterPatientEditActivity.this);
        user = Global.getUserDetails(RegisterPatientEditActivity.this);

        binding.editSpnIdproofType1.setSelection(0, false);
        binding.editSpnIdproofType1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (binding.editSpnIdproofType2.getSelectedItemPosition() > 0) {
                    if (binding.editSpnIdproofType2.getSelectedItemPosition() == i) {
                        binding.editSpnIdproofType1.setSelection(0, false);
                        Toast.makeText(RegisterPatientEditActivity.this, "Bothe ID can't be same please select diffrent ID.", Toast.LENGTH_SHORT).show();
                    }

                }

                if (i > 0) {
                    switch (i) {
                        case 1:
                            binding.editIdProof1.setInputType(InputType.TYPE_CLASS_NUMBER);
                            break;
                        case 2:
                            binding.editIdProof1.setInputType(InputType.TYPE_CLASS_TEXT);
                            break;
                        case 3:
                            binding.editIdProof1.setInputType(InputType.TYPE_CLASS_TEXT);
                            break;
                        case 4:
                            binding.editIdProof1.setInputType(InputType.TYPE_CLASS_TEXT);
                            break;
                        case 5:
                            binding.editIdProof1.setInputType(InputType.TYPE_CLASS_TEXT);
                            break;
                        case 6:
                            binding.editIdProof1.setInputType(InputType.TYPE_CLASS_TEXT);
                            break;
                    }

                } else {
                    binding.editSpnIdproofType1.setSelection(0, false);
                    binding.editIdProof1.setText(null);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.editSpnIdproofType2.setSelection(0, false);
        binding.editSpnIdproofType2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (binding.editSpnIdproofType1.getSelectedItemPosition() > 0) {
                    if (binding.editSpnIdproofType1.getSelectedItemPosition() == i) {
                        binding.editSpnIdproofType2.setSelection(0, false);
                        Toast.makeText(RegisterPatientEditActivity.this, "Bothe ID can't be same please select diffrent ID.", Toast.LENGTH_SHORT).show();
                    } else {
                        switch (i) {
                            case 1:
                                binding.editIdProof2.setInputType(InputType.TYPE_CLASS_NUMBER);
                                break;
                            case 2:
                                binding.editIdProof2.setInputType(InputType.TYPE_CLASS_TEXT);
                                break;
                            case 3:
                                binding.editIdProof2.setInputType(InputType.TYPE_CLASS_TEXT);
                                break;
                            case 4:
                                binding.editIdProof2.setInputType(InputType.TYPE_CLASS_TEXT);
                                break;
                            case 5:
                                binding.editIdProof2.setInputType(InputType.TYPE_CLASS_TEXT);
                                break;
                            case 6:
                                binding.editIdProof2.setInputType(InputType.TYPE_CLASS_TEXT);
                                break;
                        }
                    }
                } else {
                    binding.editSpnIdproofType2.setSelection(0, false);
                    Toast.makeText(RegisterPatientEditActivity.this, "Please selct ID proof1 first.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Intent intent = getIntent();
        Gson gson = new Gson();
        registerPatient = gson.fromJson(intent.getStringExtra("registerPatient"), RegisterPatient.class);
        if (Global.IsNotNull(registerPatient)) {
            setEditData();
        }

        // textListeners();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editFirstname:
                binding.editFirstname.setError(null);
                break;
            case R.id.editMiddlename:
                binding.editMiddlename.setError(null);
                break;
            case R.id.editLastname:
                binding.editLastname.setError(null);
                break;
            case R.id.editFatherHus:
                binding.editFatherHus.setError(null);
                break;
            case R.id.editMobile:
                binding.editMobile.setError(null);
                break;
            case R.id.editEmail:
                binding.editEmail.setError(null);
                break;
            case R.id.editAddress:
                binding.editAddress.setError(null);
                break;
            case R.id.editIdProof1:
                binding.editIdProof1.setError(null);
                break;
            case R.id.editAllergy:
                binding.editAllergy.setError(null);
                break;
            case R.id.editHealthcondition:
                binding.editHealthcondition.setError(null);
                break;
            case R.id.btnEditSave:
                RegisterPatient(1);
                break;
            case R.id.editDob:
                binding.editDob.setError(null);
                prepareDatePickerDialog();
                datePickerDialog.show();
                break;
            case R.id.btnEditCamera:
                startDialog();
                break;
            case R.id.editSpnAllergy:
                if (Global.IsNotNull(allergyList)) {
                    if (allergyList.size() > 0) {
                        showAllergyPopup();
                    } else {
                        Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.editSpnHealth:
                if (Global.IsNotNull(healthConditionList)) {
                    if (healthConditionList.size() > 0) {
                        showHealthPopup();
                    } else {
                        Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btnEditAdd:
                RegisterPatient(2);
                break;
            case R.id.edtbtn_verify_email:
                if (Global.IsNotNull(binding.editEmail.getText().toString().trim())) {
                    sendEmailOtp(binding.editEmail.getText().toString().trim());
                }
                break;
            case R.id.edtbtn_send_emailotp:
                if (Global.IsNotNull(binding.edtregEmailOtp.getText().toString().trim())) {
                    verifyEmailOtp(binding.editEmail.getText().toString().trim(), binding.edtregEmailOtp.getText().toString().trim());
                } else {
                    binding.edtregEmailOtp.setError(Error);
                }
                break;

            case R.id.edtbtn_verify_mobile:
                if (Global.IsNotNull(binding.editMobile.getText().toString().trim())) {
                    senMobileOtp(binding.editMobile.getText().toString().trim());
                }
                break;
            case R.id.btn_send_mobileotp:
                if (Global.IsNotNull(binding.edtrgMobileOtp.getText().toString().trim())) {
                    verifyMobileOtp(binding.editMobile.getText().toString().trim(), binding.edtrgMobileOtp.getText().toString().trim());
                } else {
                    binding.edtrgMobileOtp.setError(Error);
                }
                break;
                
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        user = Global.getUserDetails(RegisterPatientEditActivity.this);
        if (Global.IsNotNull(user)) {
            if (user.getUsertype().equals(getString(R.string.hint_officer))) {
                binding.btnEditAdd.setVisibility(View.VISIBLE);
            } else {
                binding.btnEditAdd.setVisibility(View.GONE);
            }
        }

        if (Global.IsNotNull(registerPatient)) {
            StringBuilder sb1 = new StringBuilder("");
            if (Global.IsNotNull(registerPatient.getFamilyList())) {
                sb1.append(registerPatient.getFamilyList());
            }
            if (sb1.toString().isEmpty()) {
                if (Global.globalregisterPatientList.size() > 0) {

                    int count = 0;
                    for (RegisterPatient tempregisterPatient : Global.globalregisterPatientList) {

                        if (sb1.toString().isEmpty()) {
                            sb1.append(tempregisterPatient.getFirstName());
                        } else {
                            sb1.append("," + tempregisterPatient.getFirstName());
                        }
                        count++;
                    }

                }
            } else {
                if (Global.globalregisterPatientList.size() > 1) {

                    int count = 0;
                    for (RegisterPatient tempregisterPatient : Global.globalregisterPatientList) {
                        if (sb1.toString().isEmpty()) {
                            sb1.append(tempregisterPatient.getFirstName());
                        } else {
                            if (count != 0) {
                                sb1.append("," + tempregisterPatient.getFirstName());
                            }
                        }
                        count++;
                    }


                }
            }

            if (Global.IsNotNull(user)) {
                if (user.getUsertype().equals(getString(R.string.hint_officer))) {
                    binding.txtAddedMembers.setText(sb1.toString());
                    if (!binding.txtAddedMembers.getText().toString().isEmpty()) {
                        binding.txtAddedMembers.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.txtAddedMembers.setVisibility(View.GONE);
                }
            }

        } else {
            binding.txtAddedMembers.setText("");
            binding.txtAddedMembers.setVisibility(View.GONE);
        }

    }

    public void RegisterPatient(int type) {
        if (validate()) {

            if (!isEmailOrMobileVarifired()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("You have to verify at least one of them Email OR Mobile");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            } else {
            /////////////////////
            RadioButton radioButton = (RadioButton) binding.eRadioGender.findViewById(binding.eRadioGender.getCheckedRadioButtonId());
            if (radioButton != null) {
                if (radioButton.getText().toString().equals(getString(R.string.hint_male))) {
                    registerPatient.setGender("Male");
                } else if (radioButton.getText().toString().equals(getString(R.string.hint_female))) {
                    registerPatient.setGender("Female");
                } else {
                    registerPatient.setGender("Other");
                }

            }
            if (!ProfileImage.isEmpty()) {
                registerPatient.setProfileImage(ProfileImage);
            }
            registerPatient.setFirstName(binding.editFirstname.getText().toString());
            if (!binding.editMiddlename.getText().toString().isEmpty()) {
                registerPatient.setMiddleName(binding.editMiddlename.getText().toString());
            }
            if (!binding.editLastname.getText().toString().isEmpty()) {
                registerPatient.setLastName(binding.editLastname.getText().toString());
            }
            registerPatient.setMobile(String.valueOf(binding.editCodePicker.getPhoneNumber().getNationalNumber()));
            if (!binding.editEmail.getText().toString().isEmpty()) {
                registerPatient.setEmail(binding.editEmail.getText().toString());
            }
            if (!binding.editDob.getText().toString().isEmpty()) {
                String tempDate = binding.editDob.getText().toString();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                // SimpleDateFormat finaldateFormat = new SimpleDateFormat("MM/dd/yyyy");
                try {
                    Date date = dateFormat.parse(tempDate);
                    String finalDate = dateFormat.format(date);
                    registerPatient.setDateOfBirth(finalDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat finaldateFormat = new SimpleDateFormat("MM/dd/yyyy");
                try {
                    Date date = dateFormat.parse(tempDate);
                    String finalDate = finaldateFormat.format(date);
                    registerPatient.setDateOfBirth(finalDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/
                //registerPatient.setDateOfBirth(edt_Dob.getText().toString());
            }
            if (!binding.editAddress.getText().toString().isEmpty()) {
                registerPatient.setAddress(binding.editAddress.getText().toString());
            }

            if (binding.editAllergy.getVisibility() == View.VISIBLE) {
                registerPatient.setOtherAllergy(binding.editAllergy.getText().toString());
            }
            if (binding.editHealthcondition.getVisibility() == View.VISIBLE) {
                registerPatient.setOtherHealth(binding.editHealthcondition.getText().toString());
            }
            SerializedList<Allergy> allergies = new SerializedList<>();
            for (Allergy allergy : allergyList) {
                if (allergy.isSelected()) {
                    allergies.add(allergy);
                }
            }
            registerPatient.setAllergies(allergies);

            SerializedList<HealthCondition> healthConditions = new SerializedList<>();
            for (HealthCondition healthCondition : healthConditionList) {
                if (healthCondition.isSelected()) {
                    healthConditions.add(healthCondition);
                }
            }
            registerPatient.setHealthConditions(healthConditions);

            if (binding.editFatherHus.getVisibility() == View.VISIBLE) {
                RadioButton radio = (RadioButton) binding.eRadioFatherHus.findViewById(binding.eRadioFatherHus.getCheckedRadioButtonId());
                if (radio != null) {
                    String fatherHus = radio.getText().toString();
                    if (fatherHus.equals(getString(R.string.hint_fathername))) {
                        registerPatient.setFatherName(binding.editFatherHus.getText().toString());
                        registerPatient.setHusbandName("");
                    } else {
                        registerPatient.setFatherName("");
                        registerPatient.setHusbandName(binding.editFatherHus.getText().toString());
                    }
                }

            }

            registerPatient.setIdProofNo1(binding.editIdProof1.getText().toString());
            registerPatient.setIdProofType1(binding.editSpnIdproofType1.getSelectedItem().toString());
            if (!binding.editIdProof2.getText().toString().isEmpty()) {
                registerPatient.setIdProofType2(binding.editSpnIdproofType2.getSelectedItem().toString());
                registerPatient.setIdProofNo2(binding.editIdProof2.getText().toString());
            }
            registerPatient.setBloodGroup(binding.editSpnBloodGroup.getSelectedItem().toString());
            registerPatient.setInserted(false);
            registerPatient.setUserId(String.valueOf(user.getId()));

            if (type == 1) {
                LoadingDialog.showLoadingDialog(RegisterPatientEditActivity.this, "Uploading please wait...!!");
                if (Global.globalregisterPatientList.size() == 0) {
                    Global.globalregisterPatientList.add(registerPatient);
                } else {
                    Global.globalregisterPatientList.set(0, registerPatient);
                }

                String uniqueId = registerPatient.getPatientCode();
                if (uniqueId.length() > 14) {
                    uniqueId = uniqueId.substring(0, 14);
                }
                int unicount = 0;
                /*for(int i = 1;i<Global.globalregisterPatientList.size();i++)
                {
                    String uniqueIdWithAascii = uniqueId + Global.getAsscii(unicount);
                    Global.globalregisterPatientList.get(i).setPatientCode(uniqueIdWithAascii);
                    unicount++;
                }*/
                int familycount = registerPatient.getFamilyCount();
                for (RegisterPatient registerPatientData : Global.globalregisterPatientList) {
                    if (unicount != 0) {
                        String uniqueIdWithAascii = uniqueId + Global.getAsscii(familycount);
                        registerPatientData.setPatientCode(uniqueIdWithAascii);
                        familycount++;
                    }
                    unicount++;
                }

                if (Global.isInternetOn(this)) {

                    new registerPatientTask(Global.globalregisterPatientList).execute();
                } else {

                    for (RegisterPatient registerPatientData : Global.globalregisterPatientList) {
                        registerPatientServiceBase.Insert(registerPatientData, RegisterPatient.class);
                    }
                    clearData();
                    Toast.makeText(this, "Data Inserted Successfully...", Toast.LENGTH_SHORT).show();
                    LoadingDialog.cancelLoading();
                }
            } else {
                if (Global.globalregisterPatientList.size() == 0) {
                    Global.globalregisterPatientList.add(registerPatient);
                } else {
                    Global.globalregisterPatientList.set(0, registerPatient);
                }

                try {

                    Intent intent = new Intent(RegisterPatientEditActivity.this, RegisterPatientAddFamilyActivity.class);
                    Gson gson = new Gson();
                    String registerdata = gson.toJson(registerPatient);
                    intent.putExtra("registerPatient", registerdata);

                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            ///////////////////////////////
        }
        }

    }


    private void prepareDatePickerDialog() {
        try {
            Calendar calendar = Calendar.getInstance();
            datePickerDialog = new DatePickerDialog(this, R.style.CustomDatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    String month = "";
                    String day = "";
                    if ((monthOfYear + 1) < 10) {

                        month = "0" + (monthOfYear + 1);
                    } else {
                        month = "" + (monthOfYear + 1);
                    }
                    if (dayOfMonth < 10) {

                        day = "0" + dayOfMonth;
                    } else {
                        day = "" + dayOfMonth;
                    }
                    binding.editAge.setText("" + Global.getAge(year, Integer.parseInt(month), Integer.parseInt(day)));
                    String tempDate = "" + day + "/" + month + "/" + year;
                    binding.editDob.setText(tempDate);
                    datePickerDialog.dismiss();
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        } catch (Exception e) {
            Log.d("RegistrationDatePicker", e.getMessage());
        }
    }

    /*Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&*/
    public void startDialog() {
        final CharSequence[] listItems = {"Take Photo", "Choose from Library"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add photo");
        builder.setItems(listItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (listItems[item].equals("Take Photo")) {
                    boolean result = checkPermissionsForCamera();
                    if (result) {
                        String fileName = "temp.jpg";
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, fileName);
                        selectedImage = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImage);
                        startActivityForResult(intent, GET_FROM_CAMERA);
                    }
                } else if (listItems[item].equals("Choose from Library")) {
                    boolean result = checkPermissionsForGallery();
                    if (result) {
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, GET_FROM_GALLERY);
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public boolean checkPermissionsForCamera() {
        Context context;
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(RegisterPatientEditActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(RegisterPatientEditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) RegisterPatientEditActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_FOR_CAMERA);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public boolean checkPermissionsForGallery() {
        Context context;
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(RegisterPatientEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) RegisterPatientEditActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_FOR_GALLERY);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FOR_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    String fileName = "temp.jpg";
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, fileName);
                    selectedImage = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImage);
                    startActivityForResult(intent, GET_FROM_CAMERA);
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) RegisterPatientEditActivity.this, Manifest.permission.CAMERA)
                            || ActivityCompat.shouldShowRequestPermissionRationale((Activity) RegisterPatientEditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RegisterPatientEditActivity.this);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("Camera Permission is necessary to click photo.\nStorage Permission is required to save the photo");
                        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) RegisterPatientEditActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_FOR_CAMERA);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();
                    }
                }
                break;
            case REQUEST_FOR_GALLERY:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, GET_FROM_GALLERY);
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) RegisterPatientEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RegisterPatientEditActivity.this);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("Read Permission required to open gallery");
                        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) RegisterPatientEditActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_FOR_GALLERY);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GET_FROM_CAMERA:
                if (resultCode == RESULT_OK) {
                    path = getRealPathFromURI(selectedImage);
                    cropImage(selectedImage);
                    // Log.d("pathurl"," "+path);
                    /*Glide.with(this)
                            .load(path)
                            .apply(RequestOptions.circleCropTransform())
                            .into(editprofileImage);*/
                }
                break;
            case GET_FROM_GALLERY:
                if (resultCode == Activity.RESULT_OK) {
                    selectedImage = data.getData();
                    Bitmap bitmap = null;
                    path = getRealPathFromURI(selectedImage);
                    cropImage(selectedImage);
                }
                break;
            case Crop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    final Uri resultUri = Crop.getOutput(data);

                    RequestOptions options = new RequestOptions()
                            .circleCrop()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .placeholder(R.drawable.ic_user)
                            .error(R.drawable.ic_user);

                    Glide.with(this)
                            .load(resultUri.toString())
                            .apply(options)
                            .into(binding.imgEditprofile);

                    isProfilePicSelected = true;
                    /*Bitmap bmp = decodeUriAsBitmap(resultUri);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    ProfileImage = encodeTobase64(bmp);*/
                }
                break;
        }
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.e("LOOK", imageEncoded);
        return imageEncoded;
    }

    private void cropImage(Uri sourceUri) {
        try {
            Uri destinationUri = Uri.fromFile(new File(getCacheDir(), queryName(getContentResolver(), sourceUri)));
            Crop.of(sourceUri, destinationUri).asSquare().start(this);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private static String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor = resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    private String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
            Cursor cursor = loader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String result = cursor.getString(column_index);
            cursor.close();
            return result;
        } catch (Exception e) {
            return null;
        }
    }


    public void textListeners() {

        final Pattern ps = Pattern.compile("^[a-zA-Z ]+$");

        binding.editFirstname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final Matcher ms = ps.matcher(binding.editFirstname.getText().toString());
                if (!ms.matches()) {
                    binding.editFirstname.setError("Not valid");
                } else {
                    binding.editFirstname.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.editLastname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final Matcher ms = ps.matcher(binding.editLastname.getText().toString());
                if (!ms.matches()) {
                    binding.editLastname.setError("Not valid");
                } else {
                    binding.editLastname.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.editMiddlename.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final Matcher ms = ps.matcher(binding.editMiddlename.getText().toString());
                if (!ms.matches()) {
                    binding.editMiddlename.setError("Not valid");
                } else {
                    binding.editMiddlename.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public boolean validate() {
        Matcher matchEmail, matchFirstname, matchMiddleName, matchLastname;
        boolean valid = true;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);

        Pattern ps = Pattern.compile("^[a-zA-Z ]+$");
        matchFirstname = ps.matcher(binding.editFirstname.getText().toString());
        matchMiddleName = ps.matcher(binding.editMiddlename.getText().toString());
        matchLastname = ps.matcher(binding.editLastname.getText().toString());
        if (isProfilePicSelected) {
            try {
                BitmapDrawable drawable = (BitmapDrawable) binding.imgEditprofile.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] image = stream.toByteArray();

                String img_str = Base64.encodeToString(image, 0);
                ProfileImage = img_str;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

      /*  if(!isProfilePicSelected)
        {
            valid = false;
            Toast.makeText(this, "Please add photo", Toast.LENGTH_SHORT).show();

        }*/

        if (binding.editFirstname.getText().toString().isEmpty()) {
            binding.editFirstname.setError("This field is required");
            valid = false;
        }

        RadioButton radiofhus = (RadioButton) binding.eRadioFatherHus.findViewById(binding.eRadioFatherHus.getCheckedRadioButtonId());
        if (radiofhus != null) {
            if (binding.editFatherHus.getText().toString().isEmpty()) {
                binding.editFatherHus.setError("This field is required");
            /*    binding.editFatherHus.requestFocus();
                Toast.makeText(this, "Please enter father/husband name", Toast.LENGTH_SHORT).show();*/
                valid = false;
            }
        }

        RadioButton radioButton = (RadioButton) binding.eRadioGender.findViewById(binding.eRadioGender.getCheckedRadioButtonId());
        if (radioButton == null) {
            valid = false;
            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();

        }


        if (binding.editDob.getText().toString().isEmpty()) {
            binding.editDob.setError("This field is required");
            // Toast.makeText(this, "Please Select Date of birth", Toast.LENGTH_SHORT).show();
            valid = false;
            // binding.editDob.requestFocus();
        }

        if (!binding.editMobile.getText().toString().isEmpty()) {
            if (!binding.editCodePicker.isValid())//(edt_Mobile.getText().toString().length()>=10 && edt_Mobile.getText().toString().length()<=13))
            {
                binding.editMobile.setError("Mobile Number is not valid");
                //binding.editMobile.requestFocus();
                valid = false;
            }
        }


        if (!binding.editEmail.getText().toString().isEmpty()) {
            matchEmail = pattern.matcher(binding.editEmail.getText().toString());
            if (!matchEmail.matches()) {
                binding.editEmail.setError("Email is invalid");
                //binding.editEmail.requestFocus();
                valid = false;
            }
        }

        if (binding.editAllergy.getVisibility() == View.VISIBLE) {
            if (binding.editAllergy.getText().toString().isEmpty()) {
                binding.editAllergy.setError("This field is required");
            /*    Toast.makeText(this, "Please enter allergy", Toast.LENGTH_SHORT).show();
                binding.editAllergy.requestFocus();*/
                valid = false;
            }
        }

        if (binding.editHealthcondition.getVisibility() == View.VISIBLE) {
            if (binding.editHealthcondition.getText().toString().isEmpty()) {
                binding.editHealthcondition.setError("This field is required");
              /*  Toast.makeText(this, "Please enter healthcondition", Toast.LENGTH_SHORT).show();
                binding.editAllergy.requestFocus();*/
                valid = false;
            }
        }

        if (binding.editSpnIdproofType1.getSelectedItemPosition() > 0) {
            if (binding.editIdProof1.getText().toString().isEmpty()) {
                binding.editIdProof1.setError("This field is required");
                //binding.editIdProof1.requestFocus();
                valid = false;
            } else {
                String text = binding.editIdProof1.getText().toString();
                switch (binding.editSpnIdproofType1.getSelectedItemPosition()) {
                    case 1:
                        Pattern aadharPattern = Pattern.compile("\\d{12}");
                        boolean isValidAadhar = aadharPattern.matcher(text).matches();
                        if (!isValidAadhar) {
                            binding.editIdProof1.setError("Please enter valid aadhar number");
                            // binding.editIdProof1.requestFocus();
                            valid = false;

                        }
                        break;
                    case 2:
                        Pattern panpattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
                        boolean isValidPan = panpattern.matcher(text).matches();
                        if (!isValidPan) {
                            binding.editIdProof1.setError("Please enter valid pan number");
                            // binding.editIdProof1.requestFocus();
                            valid = false;

                        }
                        break;
                    case 3:
                        if (text.length() != 16) {
                            binding.editIdProof1.setError("Please enter valid DL number");
                            //binding.editIdProof1.requestFocus();
                            valid = false;

                        }
                        break;
                    case 4:
                        binding.editIdProof1.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case 5:
                        Pattern passportpattern = Pattern.compile("^[A-Z]{1}-[0-9]{7}$");
                        boolean isValidPassport = passportpattern.matcher(text).matches();
                        if (!isValidPassport) {
                            binding.editIdProof1.setError("Please enter valid passport number");
                            //binding.editIdProof1.requestFocus();
                            valid = false;

                        }
                        break;
                }
            }
        }
       /* else
        {
            valid = false;
            Toast.makeText(this, "Please Select ID Proof 1", Toast.LENGTH_SHORT).show();
        }*/

        if (binding.editSpnIdproofType2.getSelectedItemPosition() > 0) {
            if (binding.editIdProof2.getText().toString().isEmpty()) {
                binding.editIdProof2.setError("This field is required");
                //binding.editIdProof2.requestFocus();
                valid = false;

            } else {
                String text = binding.editIdProof2.getText().toString();
                switch (binding.editSpnIdproofType2.getSelectedItemPosition()) {
                    case 1:
                        Pattern aadharPattern = Pattern.compile("\\d{12}");
                        boolean isValidAadhar = aadharPattern.matcher(text).matches();
                        if (!isValidAadhar) {
                            binding.editIdProof2.setError("Please enter valid aadhar number");
                            //binding.editIdProof2.requestFocus();
                            valid = false;

                        }
                        break;
                    case 2:
                        Pattern panpattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
                        boolean isValidPan = panpattern.matcher(text).matches();
                        if (!isValidPan) {
                            binding.editIdProof2.setError("Please enter valid pan number");
                            // binding.editIdProof2.requestFocus();
                            valid = false;

                        }
                        break;
                    case 3:
                        if (text.length() != 16) {
                            binding.editIdProof2.setError("Please enter valid DL number");
                            //binding.editIdProof2.requestFocus();
                            valid = false;
                        }
                        break;
                    case 4:
                        binding.editIdProof2.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case 5:
                        Pattern passportpattern = Pattern.compile("^[A-Z]{1}-[0-9]{7}$");
                        boolean isValidPassport = passportpattern.matcher(text).matches();
                        if (!isValidPassport) {
                            binding.editIdProof2.setError("Please enter valid passport number");
                            //binding.editIdProof2.requestFocus();
                            valid = false;
                        }
                        break;
                }
            }
        }


        return valid;
    }

    /*public boolean validate() {
        Matcher matchEmail,matchFirstname,matchMiddleName,matchLastname;
        boolean valid = true;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);

        Pattern ps = Pattern.compile("^[a-zA-Z ]+$");
        matchFirstname = ps.matcher(edt_Firstname.getText().toString());
        matchMiddleName = ps.matcher(edt_Middlename.getText().toString());
        matchLastname=ps.matcher(edt_Lastname.getText().toString());

        try {
            BitmapDrawable drawable = (BitmapDrawable) img_profileImage.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image=stream.toByteArray();

            String img_str = Base64.encodeToString(image, 0);
            ProfileImage = img_str;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        if(!edt_Email.getText().toString().isEmpty())
        {
            //edt_Email.setError("This field is required");
            //valid=false;
            matchEmail = pattern.matcher(edt_Email.getText().toString());
            if(!matchEmail.matches())
            {
                edt_Email.setError("Email is invalid");
                valid=false;
            }
        }
        *//*if(!matchFirstname.matches())
        {
            edt_Firstname.setError("Invalid Name");
            valid=false;
        }*//*
        if(edt_Firstname.getText().toString().isEmpty())
        {
            edt_Firstname.setError("This field is required");
            valid=false;
        }
      *//*  if(!matchLastname.matches())
        {
            edt_Lastname.setError("Invalid Name");
            valid=false;

        }
        if(!matchMiddleName.matches())
        {
            edt_Middlename.setError("Invalid Name");
            valid=false;

        }

        if(edt_Middlename.getText().toString().isEmpty())
        {
            edt_Middlename.setError("This field is required");
            valid=false;
        }
        *//*
     *//* if(edt_Lastname.getText().toString().isEmpty())
        {
            edt_Lastname.setError("This field is required");
            valid=false;
        }*//*
        if(!(edt_Mobile.getText().toString().length()>=10 && edt_Mobile.getText().toString().length()<=13))
        {
            edt_Mobile.setError("Mobile Number is not valid");
            valid=false;
        }
        if(edt_Mobile.getText().toString().isEmpty())
        {
            edt_Mobile.setError("This field is required");
            valid=false;
        }

        RadioButton radioButton = (RadioButton) radioGender.findViewById(radioGender.getCheckedRadioButtonId());
        if (radioButton == null) {
            valid = false;
            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();
        }

        *//*if(edt_Address.getText().toString().isEmpty())
        {
            edt_Address.setError("This field is required");
            valid=false;
        }*//*

        if(edt_Dob.getText().toString().isEmpty())
        {
            edt_Dob.setError("This field is required");
            valid=false;
        }

        if(!isProfilePicSelected)
        {
            valid = false;
            Toast.makeText(this, "Please add photo", Toast.LENGTH_SHORT).show();
        }

        if(spn_idproofType1.getSelectedItemPosition() > 0) {
            if (edt_idProof1.getText().toString().isEmpty()) {
                edt_idProof1.setError("This field is required");
                valid = false;
            }
            else {
                String text = edt_idProof1.getText().toString();
                switch (spn_idproofType1.getSelectedItemPosition())
                {
                    case 1:
                        Pattern aadharPattern = Pattern.compile("\\d{12}");
                        boolean isValidAadhar = aadharPattern.matcher(text).matches();
                        if(!isValidAadhar)
                        {
                            valid = false;
                            edt_idProof1.setError("Please enter valid aadhar number");
                        }
                        break;
                    case 2:
                        Pattern panpattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
                        boolean isValidPan = panpattern.matcher(text).matches();
                        if(!isValidPan)
                        {
                            valid = false;
                            edt_idProof1.setError("Please enter valid pan number");
                        }
                        break;
                    case 3:
                        if(text.length() != 16)
                        {
                            valid = false;
                            edt_idProof1.setError("Please enter valid DL number");
                        }
                        break;
                    case 4:
                        edt_idProof1.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case 5:
                        Pattern passportpattern = Pattern.compile("^[A-Z]{1}-[0-9]{7}$");
                        boolean isValidPassport = passportpattern.matcher(text).matches();
                        if(!isValidPassport)
                        {
                            valid = false;
                            edt_idProof1.setError("Please enter valid passport number");
                        }
                        break;
                }
            }
        }
        else
        {
            valid = false;
            Toast.makeText(this, "Please Select ID Proof 1", Toast.LENGTH_SHORT).show();
        }

        if(spn_idproofType2.getSelectedItemPosition() > 0)
        {
            if (edt_idProof2.getText().toString().isEmpty()) {
                edt_idProof2.setError("This field is required");
                valid = false;
            }
            else {
                String text = edt_idProof2.getText().toString();
                switch (spn_idproofType2.getSelectedItemPosition())
                {
                    case 1:
                        Pattern aadharPattern = Pattern.compile("\\d{12}");
                        boolean isValidAadhar = aadharPattern.matcher(text).matches();
                        if(!isValidAadhar)
                        {
                            valid = false;
                            edt_idProof2.setError("Please enter valid aadhar number");
                        }
                        break;
                    case 2:
                        Pattern panpattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
                        boolean isValidPan = panpattern.matcher(text).matches();
                        if(!isValidPan)
                        {
                            valid = false;
                            edt_idProof2.setError("Please enter valid pan number");
                        }
                        break;
                    case 3:
                        if(text.length() != 16)
                        {
                            valid = false;
                            edt_idProof2.setError("Please enter valid DL number");
                        }
                        break;
                    case 4:
                        edt_idProof2.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case 5:
                        Pattern passportpattern = Pattern.compile("^[A-Z]{1}-[0-9]{7}$");
                        boolean isValidPassport = passportpattern.matcher(text).matches();
                        if(!isValidPassport)
                        {
                            valid = false;
                            edt_idProof2.setError("Please enter valid passport number");
                        }
                        break;
                }
            }
        }

        if(edt_allergy.getVisibility() == View.VISIBLE)
        {
            if(edt_allergy.getText().toString().isEmpty())
            {
                edt_allergy.setError("This field is required");
                valid=false;
            }
        }

        if(edt_healthcondition.getVisibility() == View.VISIBLE)
        {
            if(edt_healthcondition.getText().toString().isEmpty())
            {
                edt_healthcondition.setError("This field is required");
                valid=false;
            }
        }

        RadioButton radiofhus = (RadioButton) radioFatherHus.findViewById(radioFatherHus.getCheckedRadioButtonId());
        if (radiofhus != null) {
            if(edtFatherHus.getText().toString().isEmpty())
            {
                edtFatherHus.setError("This field is required");
                valid=false;
            }
        }

        return valid;
    }*/

    private class registerPatientTask extends AsyncTask<Void, Void, Void> {

        List<RegisterPatient> registerPatient;

        public registerPatientTask(List<RegisterPatient> registerPatient) {

            this.registerPatient = registerPatient;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                ApiInterface apiUserInterface = ApiClient.getClient().create(ApiInterface.class);
                Map<String,String> header = Global.generateHeader(RegisterPatientEditActivity.this);

                Call<ApiResponse> apiResponseCall = apiUserInterface.registerPatientApi(header,registerPatient);

                apiResponseCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        //progressBar.setVisibility(View.GONE);

                        if (Global.IsNotNull(response.body())) {
                            if (response.body().isResponse()) {
                                LoadingDialog.cancelLoading();
                                Toast.makeText(RegisterPatientEditActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                Global.globalregisterPatientList.clear();
                                clearData();
                                finish();

                            } else {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    LoadingDialog.cancelLoading();
                                    Toast.makeText(RegisterPatientEditActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (Global.IsNotNull(response.errorBody())) {
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = null;
                            try {
                                LoadingDialog.cancelLoading();
                                mJson = parser.parse(response.errorBody().string());
                                Gson gson = new Gson();
                                ApiResponse errorResponse = gson.fromJson(mJson, ApiResponse.class);

                                Toast.makeText(RegisterPatientEditActivity.this, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }


                        } else {
                            Toast.makeText(RegisterPatientEditActivity.this, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                        // progressBar.setVisibility(View.GONE);
                        LoadingDialog.cancelLoading();

                        Toast.makeText(RegisterPatientEditActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }


            return null;

        }

    }

    public void clearData() {
        try {
            Global.globalregisterPatientList = new ArrayList<>();
            binding.editEmail.setText("");
            binding.editDob.setText("");
            binding.editFirstname.setText("");
            binding.editMiddlename.setText("");
            binding.editLastname.setText("");
            binding.editIdProof1.setText("");
            binding.editIdProof2.setText("");
            binding.editMobile.setText("");
            binding.editAddress.setText("");
            ProfileImage = "";
            isProfilePicSelected = false;
            binding.editSpnIdproofType1.setSelection(0, false);
            binding.editSpnIdproofType2.setSelection(0, false);
            Glide.with(RegisterPatientEditActivity.this).load(R.drawable.ic_user).into(binding.imgEditprofile);
            binding.eRadioGender.clearCheck();
            binding.eRadioFatherHus.clearCheck();
            binding.editFatherHus.setText("");
            binding.editFatherHus.setVisibility(View.GONE);
            binding.editSpnHealth.setText(null);
            binding.editHealthcondition.setText("");
            binding.editHealthcondition.setVisibility(View.GONE);
            binding.editSpnAllergy.setText(null);
            binding.editAllergy.setText("");
            binding.editAllergy.setVisibility(View.GONE);
            binding.editSpnBloodGroup.setSelection(0);
            binding.txtAddedMembers.setText("");
            binding.txtAddedMembers.setVisibility(View.GONE);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showAllergyPopup() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_listview, null);
        RecyclerView rv_dataList = (RecyclerView) alertLayout.findViewById(R.id.rv_spinnerList);
        final Button btn_ok = (Button) alertLayout.findViewById(R.id.btn_ok);
        final Button btn_cancel = (Button) alertLayout.findViewById(R.id.btn_cancel);

        TextView tv_heading = alertLayout.findViewById(R.id.tv_heading);
        tv_heading.setText(getString(R.string.hint_spn_allergy));

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setView(alertLayout);

        final AlertDialog dialog = alert.create();
        dialog.show();
        rv_dataList.setLayoutManager(new LinearLayoutManager(RegisterPatientEditActivity.this));
        RecyclerAllergyAdapter recyclerAllergyAdapter = new RecyclerAllergyAdapter(RegisterPatientEditActivity.this, allergyList);
        rv_dataList.setAdapter(recyclerAllergyAdapter);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringBuilder sb1 = new StringBuilder("");
                String selectedAllergy = "";
                int count = 0;
                for (Allergy allergy : allergyList) {
                    if (allergy.isSelected()) {
                        if (sb1.toString().isEmpty()) {
                            sb1.append(allergy.getName());
                        } else {
                            sb1.append("," + allergy.getName());
                        }
                        count++;
                    }

                    if (allergy.getName().contains("Other")) {
                        if (allergy.isSelected()) {
                            binding.editAllergy.setVisibility(View.VISIBLE);
                        } else {
                            binding.editAllergy.setText("");
                            binding.editAllergy.setVisibility(View.GONE);
                        }
                    }

                }

                selectedAllergy = sb1.toString();
                if (count == 0) {
                    binding.editSpnAllergy.setText(null);
                } else {
                    binding.editSpnAllergy.setText(selectedAllergy);
                }

                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void showHealthPopup() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_listview, null);
        RecyclerView rv_dataList = (RecyclerView) alertLayout.findViewById(R.id.rv_spinnerList);
        final Button btn_ok = (Button) alertLayout.findViewById(R.id.btn_ok);
        final Button btn_cancel = (Button) alertLayout.findViewById(R.id.btn_cancel);

        TextView tv_heading = alertLayout.findViewById(R.id.tv_heading);
        tv_heading.setText(getString(R.string.hint_health));

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setView(alertLayout);

        final AlertDialog dialog = alert.create();
        dialog.show();
        rv_dataList.setLayoutManager(new LinearLayoutManager(RegisterPatientEditActivity.this));
        RecyclerHealthConditionAdapter recyclerAllergyAdapter = new RecyclerHealthConditionAdapter(RegisterPatientEditActivity.this, healthConditionList);
        rv_dataList.setAdapter(recyclerAllergyAdapter);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringBuilder sb1 = new StringBuilder("");
                String selectedAllergy = "";

                int count = 0;
                for (HealthCondition healthCondition : healthConditionList) {
                    if (healthCondition.isSelected()) {
                        if (sb1.toString().isEmpty()) {
                            sb1.append(healthCondition.getName());
                        } else {
                            sb1.append("," + healthCondition.getName());
                        }
                        count++;
                    }

                    if (healthCondition.getName().contains("Other")) {
                        if (healthCondition.isSelected()) {
                            binding.editHealthcondition.setVisibility(View.VISIBLE);
                        } else {
                            binding.editHealthcondition.setText("");
                            binding.editHealthcondition.setVisibility(View.GONE);
                        }
                    }

                }

                if (count == 0) {
                    binding.editSpnHealth.setText(null);
                } else {
                    selectedAllergy = sb1.toString();
                    binding.editSpnHealth.setText(selectedAllergy);
                }
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void setEditData() {

        if (Global.IsNotNull(registerPatient)) {
            if (Global.IsNotNull(registerPatient.getProfileImage())) {
                //Bitmap bitmap = StringToBitMap(registerPatient.getProfileImage());
                try {
                    //if (Global.IsNotNull(bitmap)) {
                    RequestOptions options = new RequestOptions()
                            .circleCrop()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .placeholder(R.drawable.ic_user)
                            .error(R.drawable.ic_user);
                    Glide.with(RegisterPatientEditActivity.this).load(registerPatient.getProfileImage())
                            .apply(options).into(binding.imgEditprofile);
                    isProfilePicSelected = true;
                    //}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (Global.IsNotNull(registerPatient.getFirstName())) {
                binding.editFirstname.setText(registerPatient.getFirstName());
            }

            if (Global.IsNotNull(registerPatient.getMiddleName())) {
                binding.editMiddlename.setText(registerPatient.getMiddleName());
            }

            if (Global.IsNotNull(registerPatient.getLastName())) {
                binding.editLastname.setText(registerPatient.getLastName());
            }

            if (Global.IsNotNull(registerPatient.getFatherName()) || Global.IsNotNull(registerPatient.getHusbandName())) {
                binding.editFatherHus.setVisibility(View.VISIBLE);
                if (Global.IsNotNull(registerPatient.getFatherName())) {
                    binding.editFatherHus.setText(registerPatient.getFatherName());
                    binding.eRadioFather.setChecked(true);
                } else if (Global.IsNotNull(registerPatient.getHusbandName())) {
                    binding.editFatherHus.setText(registerPatient.getHusbandName());
                    binding.eRadioHusband.setChecked(true);
                }
            }

            if (Global.IsNotNull(registerPatient.getGender())) {
                if (registerPatient.getGender().equals("Male")) {
                    binding.eRadioMale.setChecked(true);
                } else if (registerPatient.getGender().equals("Female")) {
                    binding.eRadioFemale.setChecked(true);
                } else {
                    binding.eRadioOther.setChecked(true);
                }
            }

            if (Global.IsNotNull(registerPatient.getDateOfBirth())) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                // SimpleDateFormat finaldateFormat = new SimpleDateFormat("MM/dd/yyyy");
                try {
                    Date date = dateFormat.parse(registerPatient.getDateOfBirth());
                    String finalDate = dateFormat.format(date);
                    binding.editDob.setText(finalDate);
                    int day = Integer.parseInt(DateFormat.format("dd", date).toString());
                    int month = Integer.parseInt(DateFormat.format("MM", date).toString());
                    int year = Integer.parseInt(DateFormat.format("yyyy", date).toString());
                    binding.editAge.setText("" + Global.getAge(year, month, day));
                } catch (Exception e) {
                    e.printStackTrace();
                }


                /*SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat finaldateFormat = new SimpleDateFormat("MM/dd/yyyy");
                try {
                    Date date = finaldateFormat.parse(registerPatient.getDateOfBirth());
                    String finalDate = dateFormat.format(date);
                    edt_Dob.setText(finalDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/
            }

            if (Global.IsNotNull(registerPatient.getAddress())) {
                binding.editAddress.setText(registerPatient.getAddress());
            }

            if (Global.IsNotNull(registerPatient.getMobile())) {
                //codePicker.setFullNumber(registerPatient.getMobile());
                binding.editMobile.setText(registerPatient.getMobile());
            }
            if (Global.IsNotNull(registerPatient.getCountryCode())) {
                binding.editCodePicker.setCountryForNameCode(registerPatient.getCountryCode());
            }
            if (Global.IsNotNull(registerPatient.getEmail())) {
                binding.editEmail.setText(registerPatient.getEmail());
            }

            if (Global.IsNotNull(registerPatient.getBloodGroup())) {
                String[] bloodgroup = getResources().getStringArray(R.array.bloodgrouparray);
                for (int i = 0; i < bloodgroup.length; i++) {
                    if (bloodgroup[i].equals(registerPatient.getBloodGroup())) {
                        binding.editSpnBloodGroup.setSelection(i);
                    }
                }
            }

            if (Global.IsNotNull(registerPatient.getAllergies())) {
                if (registerPatient.getAllergies().size() > 0) {
                    for (int i = 0; i < registerPatient.getAllergies().size(); i++) {
                        for (int j = 0; j < allergyList.size(); j++) {
                            if (allergyList.get(j).getId() == registerPatient.getAllergies().get(i).getId()) {
                                allergyList.get(j).setSelected(true);
                            }
                        }
                    }
                }

                StringBuilder sb1 = new StringBuilder("");
                String selectedAllergy = "";
                int count = 0;
                for (Allergy allergy : allergyList) {
                    if (allergy.isSelected()) {
                        if (sb1.toString().isEmpty()) {
                            sb1.append(allergy.getName());
                        } else {
                            sb1.append("," + allergy.getName());
                        }
                        count++;
                    }

                    if (allergy.getName().contains("Other")) {
                        if (allergy.isSelected()) {
                            binding.editAllergy.setVisibility(View.VISIBLE);
                            if (Global.IsNotNull(registerPatient.getOtherAllergy())) {
                                binding.editAllergy.setText(registerPatient.getOtherAllergy());
                            }
                        } else {
                            binding.editAllergy.setText("");
                            binding.editAllergy.setVisibility(View.GONE);
                        }
                    }

                }

                selectedAllergy = sb1.toString();
                if (count == 0) {
                    binding.editSpnAllergy.setText(null);
                } else {
                    binding.editSpnAllergy.setText(selectedAllergy);
                }
            }


            if (Global.IsNotNull(registerPatient.getHealthConditions())) {
                if (registerPatient.getHealthConditions().size() > 0) {
                    for (int i = 0; i < registerPatient.getHealthConditions().size(); i++) {
                        for (int j = 0; j < healthConditionList.size(); j++) {
                            if (healthConditionList.get(j).getId() == registerPatient.getHealthConditions().get(i).getId()) {
                                healthConditionList.get(j).setSelected(true);
                            }
                        }
                    }
                }

                StringBuilder sb1 = new StringBuilder("");
                String selectedAllergy = "";

                int count = 0;
                for (HealthCondition healthCondition : healthConditionList) {
                    if (healthCondition.isSelected()) {
                        if (sb1.toString().isEmpty()) {
                            sb1.append(healthCondition.getName());
                        } else {
                            sb1.append("," + healthCondition.getName());
                        }
                        count++;
                    }


                    if (healthCondition.getName().contains("Other")) {
                        if (healthCondition.isSelected()) {
                            binding.editHealthcondition.setVisibility(View.VISIBLE);
                            if (Global.IsNotNull(registerPatient.getOtherHealth())) {
                                binding.editHealthcondition.setText(registerPatient.getOtherHealth());
                            }
                        } else {
                            binding.editHealthcondition.setText("");
                            binding.editHealthcondition.setVisibility(View.GONE);
                        }
                    }


                }

                if (count == 0) {
                    binding.editSpnHealth.setText(null);
                } else {
                    selectedAllergy = sb1.toString();
                    binding.editSpnHealth.setText(selectedAllergy);
                }

            }


            if (Global.IsNotNull(registerPatient.getIdProofNo1())) {
                if (Global.IsNotNull(registerPatient.getIdProofType1())) {
                    String[] idproofs = getResources().getStringArray(R.array.idproofarray);

                    for (int i = 0; i < idproofs.length; i++) {
                        if (idproofs[i].equals(registerPatient.getIdProofType1())) {
                            binding.editSpnIdproofType1.setSelection(i, false);
                        }
                    }
                }

                binding.editIdProof1.setText(registerPatient.getIdProofNo1());
            }

            if (Global.IsNotNull(registerPatient.getIdProofNo2())) {
                if (Global.IsNotNull(registerPatient.getIdProofType2())) {
                    String[] idproofs = getResources().getStringArray(R.array.idproofarray);

                    for (int i = 0; i < idproofs.length; i++) {
                        if (idproofs[i].equals(registerPatient.getIdProofType2())) {
                            binding.editSpnIdproofType2.setSelection(i, false);
                        }
                    }
                }

                binding.editIdProof2.setText(registerPatient.getIdProofNo2());
            }


            //////////end////////////////
        }
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                // ProjectsActivity is my 'home' activity
                onBackPressed();
                return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void sendEmailOtp(String email) {
        if (Global.isInternetOn(this)) {
            LoadingDialog.showLoadingDialog(RegisterPatientEditActivity.this, "Please wait...");
            try {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("Email", email);

                Call<ApiResponse> apiResponseCall = apiInterface.sendEmailOTP(map);

                apiResponseCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        try {
                            LoadingDialog.cancelLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (Global.IsNotNull(response.body())) {
                            if (response.body().isResponse()) {
                                Toast.makeText(RegisterPatientEditActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                tmpPassword = response.body().getResult().getAsJsonObject().get("EmailOTP").getAsString();
                                binding.edtbtnVerifyEmail.setVisibility(View.GONE);
                                binding.edtregEmailOtp.setVisibility(View.VISIBLE);
                                binding.edtregEmailOtp.requestFocus();
                                binding.edtbtnSendEmailotp.setVisibility(View.VISIBLE);
                            } else {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    Toast.makeText(RegisterPatientEditActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (Global.IsNotNull(response.errorBody())) {
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = null;
                            try {
                                mJson = parser.parse(response.errorBody().string());
                                Gson gson = new Gson();
                                ApiResponse errorResponse = gson.fromJson(mJson, ApiResponse.class);

                                Toast.makeText(RegisterPatientEditActivity.this, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }

                        } else {
                            Toast.makeText(RegisterPatientEditActivity.this, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        }

                        try {
                            LoadingDialog.cancelLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                        try {
                            LoadingDialog.cancelLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(RegisterPatientEditActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                try {
                    LoadingDialog.cancelLoading();
                } catch (Exception em) {
                    em.printStackTrace();
                }
                e.getMessage();
            }

        } else {
            Toast.makeText(this, "" + getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    private void verifyEmailOtp(String email, String otp) {

        if(Global.IsNotNull(tmpPassword))
        {
            String otpstr = md5(otp);
            if(Global.IsNotNull(otpstr))
            {
                if(otpstr.equals(tmpPassword))
                {

                    binding.edtlayoutVeryfyEmail.setVisibility(View.GONE);
                    binding.edtbtnVerifyEmail.setVisibility(View.VISIBLE);
                    binding.edtregEmailOtp.setVisibility(View.GONE);
                    binding.edtbtnSendEmailotp.setVisibility(View.GONE);
                    showEmailOrMobileDialog("Email");
                    isEmailVerified = true;
                    tmpPassword = "";
                }
                else
                {
                    Toast.makeText(this,"OTP not matched!", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this, getString(R.string.somethingwrong) + " try again", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
        }


    }

    private void showEmailOrMobileDialog(String data) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your " + data + " has been verified sucessfully.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private void senMobileOtp(String mobile) {
        if (Global.isInternetOn(this)) {
            LoadingDialog.showLoadingDialog(RegisterPatientEditActivity.this, "Please wait...");

            try {

                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("Mobile", mobile);

                Call<ApiResponse> apiResponseCall = apiInterface.sendMobileOTP(map);

                apiResponseCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        try {
                            LoadingDialog.cancelLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (Global.IsNotNull(response.body())) {
                            if (response.body().isResponse()) {
                                Toast.makeText(RegisterPatientEditActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                tmpPassword = response.body().getResult().getAsJsonObject().get("MobileOTP").getAsString();
                                binding.edtbtnVerifyMobile.setVisibility(View.GONE);
                                binding.edtrgMobileOtp.setVisibility(View.VISIBLE);
                                binding.edtrgMobileOtp.requestFocus();
                                binding.edtbtnSendMobileotp.setVisibility(View.VISIBLE);
                            } else {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    Toast.makeText(RegisterPatientEditActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (Global.IsNotNull(response.errorBody())) {
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = null;
                            try {
                                mJson = parser.parse(response.errorBody().string());
                                Gson gson = new Gson();
                                ApiResponse errorResponse = gson.fromJson(mJson, ApiResponse.class);

                                Toast.makeText(RegisterPatientEditActivity.this, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }

                        } else {
                            Toast.makeText(RegisterPatientEditActivity.this, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        }

                        try {
                            LoadingDialog.cancelLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                        try {
                            LoadingDialog.cancelLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(RegisterPatientEditActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                try {
                    LoadingDialog.cancelLoading();
                } catch (Exception em) {
                    em.printStackTrace();
                }
                e.getMessage();
            }
        } else {
            Toast.makeText(this, "" + getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    private void verifyMobileOtp(String mobile, String otp) {

        if(Global.IsNotNull(tmpPassword))
        {
            String otpstr = md5(otp);
            if(Global.IsNotNull(otpstr))
            {
                if(otpstr.equals(tmpPassword))
                {
                    binding.edtlayoutVeryfyMobile.setVisibility(View.GONE);
                    binding.edtbtnVerifyMobile.setVisibility(View.VISIBLE);
                    binding.edtrgMobileOtp.setVisibility(View.GONE);
                    binding.edtrgMobileOtp.setVisibility(View.GONE);
                    showEmailOrMobileDialog("Mobile");
                    isMobileVerifired = true;
                    tmpPassword = "";
                }
                else
                {
                    Toast.makeText(this,"OTP not matched!", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this, getString(R.string.somethingwrong) + " try again", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isEmailOrMobileVarifired() {
        if (isEmailVerified) {
            return true;
        } else if (isMobileVerifired) {
            return true;
        } else {
            return false;
        }
    }

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
