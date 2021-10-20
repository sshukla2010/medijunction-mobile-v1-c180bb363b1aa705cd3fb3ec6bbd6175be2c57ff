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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import com.smileparser.medijunctions.interfaces.ApiInterface;
import com.smileparser.medijunctions.services.ItemService;
import com.smileparser.medijunctions.utils.ApiClient;
import com.smileparser.medijunctions.utils.Global;
import com.smileparser.medijunctions.utils.ServiceBase;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterPatientActivity extends AppCompatActivity implements View.OnClickListener {

    private ActionBar toolbar;

    ActivityRegisterPatientBinding binding;

    private static final int GET_FROM_CAMERA = 1;
    private static final int GET_FROM_GALLERY = 2;
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
    SharedPreferences mPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_register_patient);
        binding = ActivityRegisterPatientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = Global.getUserDetails(RegisterPatientActivity.this);
        try {

            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            if(Global.IsNotNull(user))
            {
                if (user.getUsertype().equals(getString(R.string.hint_officer))) {
                    actionBar.setTitle(getString(R.string.nav_RegisterPatient));
                } else {
                    actionBar.setTitle(getString(R.string.nav_RegisterAddFamilyMember));
                }
            }
            else
            {
                actionBar.setTitle(getString(R.string.nav_RegisterAddFamilyMember));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPrefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        healthConditionServiceBase = new ServiceBase<HealthCondition>(getApplicationContext());
        allergyServiceBase = new ServiceBase<Allergy>(getApplicationContext());

        allergyList = allergyServiceBase.GetAll(Allergy.class);
        healthConditionList = healthConditionServiceBase.GetAll(HealthCondition.class);

        binding.edtFirstname.setOnClickListener(this);
        binding.edtMiddlename.setOnClickListener(this);
        binding.edtLastname.setOnClickListener(this);
        binding.edtFatherhus.setOnClickListener(this);
        binding.edtMobile.setOnClickListener(this);
        binding.edtEmail.setOnClickListener(this);
        binding.edtAddress.setOnClickListener(this);
        binding.edtIdProof1.setOnClickListener(this);
        binding.edtIdProof2.setOnClickListener(this);
        binding.edtDob.setOnClickListener(this);
        binding.spnAllergy.setOnClickListener(this);
        binding.spnHealth.setOnClickListener(this);
        binding.edtAllergy.setOnClickListener(this);
        binding.edtHealthcondition.setOnClickListener(this);
        binding.edtFatherhus.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
        binding.btnSave.setOnClickListener(this);
        binding.profileImagebuttonImg.setOnClickListener(this);
        binding.fabbtnExtrafields.setOnClickListener(this);

        binding.edtRAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.edtRAge.isInEditMode()) {
                    if (Global.IsNotNull(s)) {
                        if (Global.IsNotNull(s.toString().trim())) {
                            if (Global.IsNotNull(Integer.parseInt(s.toString()) > 0)) {
                                String tempDate = "" + 01 + "/" + 01 + "/" + Global.getDOByear(Integer.parseInt(s.toString()));
                                binding.edtDob.setText(tempDate);
                            }
                        } else {
                            binding.edtDob.setText(null);
                        }
                    } else {
                        binding.edtDob.setText(null);
                    }
                }
            }
        });

        binding.radioGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //RadioButton radioButton = (RadioButton)findViewById(checkedId);
                //Toast.makeText(RegisterPatientActivity.this, "Selected: "+radioButton.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.radioFatherHus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);

                if (Global.IsNotNull(radioButton)) {
                    if (radioButton.getText().toString().equals(getString(R.string.hint_father))) {

                        binding.edtFatherhus.setHint(getString(R.string.hint_fathername));
                        if (binding.edtFatherhus.getVisibility() == View.GONE) {
                            binding.edtFatherhus.setVisibility(View.VISIBLE);
                        }
                    } else {
                        binding.edtFatherhus.setHint(getString(R.string.hint_husbandname));
                        if (binding.edtFatherhus.getVisibility() == View.GONE) {
                            binding.edtFatherhus.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        binding.codePicker.registerPhoneNumberTextView(binding.edtMobile);


        registerPatientServiceBase = new ServiceBase<RegisterPatient>(RegisterPatientActivity.this);
        if (Global.isInternetOn(RegisterPatientActivity.this)) {
            try {
                ItemService itemService = new ItemService(RegisterPatientActivity.this);
                itemService.UpdateAll();
                itemService.DeleteAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (Global.IsNotNull(user)) {
            if (user.getUsertype().equals(getString(R.string.hint_patient))) {
                if (Global.IsNotNull(user.getTotalPatient())) {
                    if (user.getTotalPatient() == 0) {
                        if (Global.IsNotNull(user.getFirstName())) {
                            binding.edtFirstname.setText(user.getFirstName());
                        }

                        if (Global.IsNotNull(user.getLastName())) {
                            binding.edtLastname.setText(user.getLastName());
                        }
                    }
                }

                if (Global.IsNotNull(user.getEmail())) {
                    binding.edtEmail.setText(user.getEmail());
                }

                if (Global.IsNotNull(user.getMobile())) {
                    binding.edtMobile.setText(user.getMobile());
                    binding.codePicker.setCountryForNameCode(user.getCountryCode());
                }
            }
        }

        binding.spnIdproof1.setSelection(0, false);
        binding.spnIdproof1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (binding.spnIdproof2.getSelectedItemPosition() > 0) {
                    if (binding.spnIdproof2.getSelectedItemPosition() == i) {
                        binding.spnIdproof1.setSelection(0, false);
                        Toast.makeText(RegisterPatientActivity.this, "Bothe ID can't be same please select diffrent ID.", Toast.LENGTH_SHORT).show();
                    }

                }

                if (i > 0) {
                    switch (i) {
                        case 1:
                            binding.edtIdProof1.setInputType(InputType.TYPE_CLASS_NUMBER);
                            break;
                        case 2:
                            binding.edtIdProof1.setInputType(InputType.TYPE_CLASS_TEXT);
                            break;
                        case 3:
                            binding.edtIdProof1.setInputType(InputType.TYPE_CLASS_TEXT);
                            break;
                        case 4:
                            binding.edtIdProof1.setInputType(InputType.TYPE_CLASS_TEXT);
                            break;
                        case 5:
                            binding.edtIdProof1.setInputType(InputType.TYPE_CLASS_TEXT);
                            break;
                        case 6:
                            binding.edtIdProof1.setInputType(InputType.TYPE_CLASS_TEXT);
                            break;
                    }

                } else {
                    binding.spnIdproof1.setSelection(0, false);
                    binding.edtIdProof1.setText(null);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spnIdproof2.setSelection(0, false);
        binding.spnIdproof2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (binding.spnIdproof1.getSelectedItemPosition() > 0) {
                    if (binding.spnIdproof1.getSelectedItemPosition() == i) {
                        binding.spnIdproof2.setSelection(0, false);
                        Toast.makeText(RegisterPatientActivity.this, "Bothe ID can't be same please select diffrent ID.", Toast.LENGTH_SHORT).show();
                    } else {
                        switch (i) {
                            case 1:
                                binding.edtIdProof2.setInputType(InputType.TYPE_CLASS_NUMBER);
                                break;
                            case 2:
                                binding.edtIdProof2.setInputType(InputType.TYPE_CLASS_TEXT);
                                break;
                            case 3:
                                binding.edtIdProof2.setInputType(InputType.TYPE_CLASS_TEXT);
                                break;
                            case 4:
                                binding.edtIdProof2.setInputType(InputType.TYPE_CLASS_TEXT);
                                break;
                            case 5:
                                binding.edtIdProof2.setInputType(InputType.TYPE_CLASS_TEXT);
                                break;
                            case 6:
                                binding.edtIdProof2.setInputType(InputType.TYPE_CLASS_TEXT);
                                break;
                        }
                    }
                } else {
                    binding.spnIdproof2.setSelection(0, false);
                    Toast.makeText(RegisterPatientActivity.this, "Please selct ID proof1 first.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edtFirstname:
                binding.edtFirstname.setError(null);
                break;
            case R.id.edtMiddlename:
                binding.edtMiddlename.setError(null);
                break;
            case R.id.edtLastname:
                binding.edtLastname.setError(null);
                break;
            case R.id.edtFatherhus:
                binding.edtFatherhus.setError(null);
                break;
            case R.id.edtMobile:
                binding.edtMobile.setError(null);
                break;
            case R.id.edtEmail:
                binding.edtEmail.setError(null);
                break;
            case R.id.edtAddress:
                binding.edtAddress.setError(null);
                break;
            case R.id.edtIdProof1:
                binding.edtIdProof1.setError(null);
                break;
            case R.id.edtAllergy:
                binding.edtAllergy.setError(null);
                break;
            case R.id.edtHealthcondition:
                binding.edtHealthcondition.setError(null);
                break;
            case R.id.btnSave:
                RegisterPatient(1);
                break;
            case R.id.edtDob:
                binding.edtDob.setError(null);
                prepareDatePickerDialog();
                datePickerDialog.show();
                break;
            case R.id.profileImagebuttonImg:
                startDialog();
                break;
            case R.id.spnAllergy:
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
            case R.id.spnHealth:
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
            case R.id.btnAdd:
                RegisterPatient(2);
                break;
            case R.id.fabbtnExtrafields:
                if (binding.layoutExtraFields.getVisibility() == View.VISIBLE) {
                    binding.fabbtnExtrafields.setImageResource(R.drawable.ic_add);
                    binding.layoutExtraFields.setVisibility(View.GONE);
                } else {
                    binding.fabbtnExtrafields.setImageResource(R.drawable.ic_minus);
                    binding.layoutExtraFields.setVisibility(View.VISIBLE);
                }
                break;

        }
    }

    public void RegisterPatient(int type) {
        if (validate()) {

            RegisterPatient registerPatient = new RegisterPatient();
            RadioButton radioButton = (RadioButton) binding.radioGender.findViewById(binding.radioGender.getCheckedRadioButtonId());
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
            registerPatient.setFirstName(binding.edtFirstname.getText().toString());
            if (!binding.edtMiddlename.getText().toString().isEmpty()) {
                registerPatient.setMiddleName(binding.edtMiddlename.getText().toString());
            }
            if (!binding.edtLastname.getText().toString().isEmpty()) {
                registerPatient.setLastName(binding.edtLastname.getText().toString());
            }
            if (!binding.edtMobile.getText().toString().isEmpty()) {
                registerPatient.setCountryCode(binding.codePicker.getSelectedCountryNameCode());
                registerPatient.setMobile(binding.edtMobile.getText().toString());//String.valueOf(codePicker.getPhoneNumber().getNationalNumber()));
            }
            if (!binding.edtEmail.getText().toString().isEmpty()) {
                registerPatient.setEmail(binding.edtEmail.getText().toString());
            }
            if (!binding.edtDob.getText().toString().isEmpty()) {
                String tempDate = binding.edtDob.getText().toString();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                // SimpleDateFormat finaldateFormat = new SimpleDateFormat("MM/dd/yyyy");
                try {
                    Date date = dateFormat.parse(tempDate);
                    String finalDate = dateFormat.format(date);
                    registerPatient.setDateOfBirth(finalDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //registerPatient.setDateOfBirth(edt_Dob.getText().toString());
            }
            if (!binding.edtAddress.getText().toString().isEmpty()) {
                registerPatient.setAddress(binding.edtAddress.getText().toString());
            }

            if (binding.edtAllergy.getVisibility() == View.VISIBLE) {
                registerPatient.setOtherAllergy(binding.edtAllergy.getText().toString());
            }
            if (binding.edtHealthcondition.getVisibility() == View.VISIBLE) {
                registerPatient.setOtherHealth(binding.edtHealthcondition.getText().toString());
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

            if (binding.edtFatherhus.getVisibility() == View.VISIBLE) {
                RadioButton radio = (RadioButton) binding.radioFatherHus.findViewById(binding.radioFatherHus.getCheckedRadioButtonId());
                if (radio != null) {
                    String fatherHus = radio.getText().toString();
                    if (fatherHus.equals(getString(R.string.hint_fathername))) {
                        registerPatient.setFatherName(binding.edtFatherhus.getText().toString());
                    } else {
                        registerPatient.setHusbandName(binding.edtFatherhus.getText().toString());
                    }
                }

            }

            if (!binding.edtIdProof1.getText().toString().isEmpty()) {
                registerPatient.setIdProofNo1(binding.edtIdProof1.getText().toString());
                registerPatient.setIdProofType1(binding.spnIdproof1.getSelectedItem().toString());
            }
            if (!binding.edtIdProof2.getText().toString().isEmpty()) {
                registerPatient.setIdProofType2(binding.spnIdproof2.getSelectedItem().toString());
                registerPatient.setIdProofNo2(binding.edtIdProof2.getText().toString());
            }
            registerPatient.setBloodGroup(binding.spnBloodgroup.getSelectedItem().toString());
            registerPatient.setInserted(false);
            registerPatient.setUserId(String.valueOf(user.getId()));

            if (type == 1) {
                LoadingDialog.showLoadingDialog(RegisterPatientActivity.this, "Uploading please wait...!!");
                if (Global.globalregisterPatientList.size() == 0) {
                    Global.globalregisterPatientList.add(registerPatient);
                } else {
                    Global.globalregisterPatientList.set(0, registerPatient);
                }
                if (Global.globalregisterPatientList.size() == 1) {
                    String uniqueId = Global.GenerateUniqueId(Global.getPinCode(RegisterPatientActivity.this));
                    Global.globalregisterPatientList.get(0).setPatientCode(uniqueId);
                } else {
                    int count = 0;
                    String uniqueId = Global.GenerateUniqueId(Global.getPinCode(RegisterPatientActivity.this));
                    for (RegisterPatient registerPatientData : Global.globalregisterPatientList) {
                        String uniqueIdWithAsscii = uniqueId + Global.getAsscii(count);
                        Global.globalregisterPatientList.get(count).setPatientCode(uniqueIdWithAsscii);
                        count++;
                    }
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
                    exitPopup();
                }
            } else {
                if (Global.globalregisterPatientList.size() == 0) {
                    Global.globalregisterPatientList.add(registerPatient);
                } else {
                    Global.globalregisterPatientList.set(0, registerPatient);
                }

                try {

                    Intent intent = new Intent(RegisterPatientActivity.this, RegisterPatientAddFamilyActivity.class);
                    Gson gson = new Gson();
                    String registerdata = gson.toJson(registerPatient);
                    intent.putExtra("registerPatient", registerdata);

                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
                    binding.edtRAge.setText("" + Global.getAge(year, Integer.parseInt(month), Integer.parseInt(day)));
                    String tempDate = "" + day + "/" + month + "/" + year;
                    binding.edtDob.setText(tempDate);
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
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(RegisterPatientActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(RegisterPatientActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) RegisterPatientActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_FOR_CAMERA);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public boolean checkPermissionsForGallery() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(RegisterPatientActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) RegisterPatientActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_FOR_GALLERY);
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

                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImage);
                    startActivityForResult(intent, GET_FROM_CAMERA);
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) RegisterPatientActivity.this, Manifest.permission.CAMERA)
                            || ActivityCompat.shouldShowRequestPermissionRationale((Activity) RegisterPatientActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RegisterPatientActivity.this);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("Camera Permission is necessary to click photo.\nStorage Permission is required to save the photo");
                        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) RegisterPatientActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_FOR_CAMERA);
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
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) RegisterPatientActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RegisterPatientActivity.this);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("Read Permission required to open gallery");
                        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) RegisterPatientActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_FOR_GALLERY);
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

                    Glide.with(this)
                            .load(resultUri.toString())
                            .apply(RequestOptions.circleCropTransform()
                                    .placeholder(R.drawable.ic_user)
                                    .error(R.drawable.ic_user))
                            .into(binding.profileImageviewimg);

                    isProfilePicSelected = true;

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

        binding.edtFirstname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final Matcher ms = ps.matcher(binding.edtFirstname.getText().toString());
                if (!ms.matches()) {
                    binding.edtFirstname.setError("Not valid");
                } else {
                    binding.edtFirstname.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.edtLastname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final Matcher ms = ps.matcher(binding.edtLastname.getText().toString());
                if (!ms.matches()) {
                    binding.edtLastname.setError("Not valid");
                } else {
                    binding.edtLastname.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.edtMiddlename.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final Matcher ms = ps.matcher(binding.edtMiddlename.getText().toString());
                if (!ms.matches()) {
                    binding.edtMiddlename.setError("Not valid");
                } else {
                    binding.edtMiddlename.setError(null);
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

        if (isProfilePicSelected) {
            try {
                BitmapDrawable drawable = (BitmapDrawable) binding.profileImageviewimg.getDrawable();
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
            return valid;
        }*/

        if (binding.edtFirstname.getText().toString().isEmpty()) {
            binding.edtFirstname.setError("This field is required");
            valid = false;
        }

        RadioButton radiofhus = (RadioButton) binding.radioFatherHus.findViewById(binding.radioFatherHus.getCheckedRadioButtonId());
        if (radiofhus != null) {
            if (binding.edtFatherhus.getText().toString().isEmpty()) {
                binding.edtFatherhus.setError("This field is required");
                binding.edtFatherhus.requestFocus();
                Toast.makeText(this, "Please enter father/husband name", Toast.LENGTH_SHORT).show();
                valid = false;
            }
        }

        RadioButton radioButton = (RadioButton) binding.radioGender.findViewById(binding.radioGender.getCheckedRadioButtonId());
        if (radioButton == null) {
            valid = false;
            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();
        }


        if (binding.edtDob.getText().toString().isEmpty()) {
            binding.edtDob.setError("This field is required");
            valid = false;
        }

        if (binding.edtRAge.getText().toString().isEmpty()) {
            binding.edtRAge.setError("This field is required");
            valid = false;
        }

        if (!binding.edtMobile.getText().toString().isEmpty()) {
            if (!binding.codePicker.isValid())//(edt_Mobile.getText().toString().length()>=10 && edt_Mobile.getText().toString().length()<=13))
            {
                binding.edtMobile.setError("Mobile Number is not valid");
                binding.edtMobile.requestFocus();
                valid = false;
            }
        }

        /*if(binding.edtMobile.getText().toString().isEmpty())
        {
            binding.edtMobile.setError("This field is required");
            binding.edtMobile.requestFocus();
            valid=false;
            return valid;
        }
        else {

            if (!binding.codePicker.isValid())//(edt_Mobile.getText().toString().length()>=10 && edt_Mobile.getText().toString().length()<=13))
            {
                binding.edtMobile.setError("Mobile Number is not valid");
                binding.edtMobile.requestFocus();
                valid = false;
                return valid;
            }
        }*/


        if (!binding.edtEmail.getText().toString().isEmpty()) {
            matchEmail = pattern.matcher(binding.edtEmail.getText().toString());
            if (!matchEmail.matches()) {
                binding.edtEmail.setError("Email is invalid");
                binding.edtEmail.requestFocus();
                valid = false;
            }
        }

        if (binding.edtAllergy.getVisibility() == View.VISIBLE) {
            if (binding.edtAllergy.getText().toString().isEmpty()) {
                binding.edtAllergy.setError("This field is required");
            /*    Toast.makeText(this, "Please enter allergy", Toast.LENGTH_SHORT).show();
                binding.edtAllergy.requestFocus();*/
                valid = false;
            }
        }

        if (binding.edtHealthcondition.getVisibility() == View.VISIBLE) {
            if (binding.edtHealthcondition.getText().toString().isEmpty()) {
                binding.edtHealthcondition.setError("This field is required");
             /*   Toast.makeText(this, "Please enter healthcondition", Toast.LENGTH_SHORT).show();
                binding.edtAllergy.requestFocus();*/
                valid = false;
            }
        }

        if (binding.spnIdproof1.getSelectedItemPosition() > 0) {
            if (binding.edtIdProof1.getText().toString().isEmpty()) {
                binding.edtIdProof1.setError("This field is required");
                binding.edtIdProof1.requestFocus();
                valid = false;
            } else {
                String text = binding.edtIdProof1.getText().toString();
                switch (binding.spnIdproof1.getSelectedItemPosition()) {
                    case 1:
                        Pattern aadharPattern = Pattern.compile("\\d{12}");
                        boolean isValidAadhar = aadharPattern.matcher(text).matches();
                        if (!isValidAadhar) {
                            binding.edtIdProof1.setError("Please enter valid aadhar number");
                            binding.edtIdProof1.requestFocus();
                            valid = false;
                        }
                        break;
                    case 2:
                        Pattern panpattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
                        boolean isValidPan = panpattern.matcher(text).matches();
                        if (!isValidPan) {
                            binding.edtIdProof1.setError("Please enter valid pan number");
                            binding.edtIdProof1.requestFocus();
                            valid = false;
                        }
                        break;
                    case 3:
                        if (text.length() != 16) {
                            binding.edtIdProof1.setError("Please enter valid DL number");
                            binding.edtIdProof1.requestFocus();
                            valid = false;
                        }
                        break;
                    case 4:
                        binding.edtIdProof1.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case 5:
                        Pattern passportpattern = Pattern.compile("^[A-Z]{1}-[0-9]{7}$");
                        boolean isValidPassport = passportpattern.matcher(text).matches();
                        if (!isValidPassport) {
                            binding.edtIdProof1.setError("Please enter valid passport number");
                            binding.edtIdProof1.requestFocus();
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

        if (binding.spnIdproof2.getSelectedItemPosition() > 0) {
            if (binding.edtIdProof2.getText().toString().isEmpty()) {
                binding.edtIdProof2.setError("This field is required");
                binding.edtIdProof2.requestFocus();
                valid = false;

            } else {
                String text = binding.edtIdProof2.getText().toString();
                switch (binding.spnIdproof2.getSelectedItemPosition()) {
                    case 1:
                        Pattern aadharPattern = Pattern.compile("\\d{12}");
                        boolean isValidAadhar = aadharPattern.matcher(text).matches();
                        if (!isValidAadhar) {
                            binding.edtIdProof2.setError("Please enter valid aadhar number");
                            binding.edtIdProof2.requestFocus();
                            valid = false;

                        }
                        break;
                    case 2:
                        Pattern panpattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
                        boolean isValidPan = panpattern.matcher(text).matches();
                        if (!isValidPan) {
                            binding.edtIdProof2.setError("Please enter valid pan number");
                            binding.edtIdProof2.requestFocus();
                            valid = false;

                        }
                        break;
                    case 3:
                        if (text.length() != 16) {
                            binding.edtIdProof2.setError("Please enter valid DL number");
                            binding.edtIdProof2.requestFocus();
                            valid = false;

                        }
                        break;
                    case 4:
                        binding.edtIdProof2.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case 5:
                        Pattern passportpattern = Pattern.compile("^[A-Z]{1}-[0-9]{7}$");
                        boolean isValidPassport = passportpattern.matcher(text).matches();
                        if (!isValidPassport) {
                            binding.edtIdProof2.setError("Please enter valid passport number");
                            binding.edtIdProof2.requestFocus();
                            valid = false;

                        }
                        break;
                }
            }
        }

        return valid;
    }

    private class registerPatientTask extends AsyncTask<Void, Void, Void> {

        List<RegisterPatient> registerPatient;

        public registerPatientTask(List<RegisterPatient> registerPatient) {

            this.registerPatient = registerPatient;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                ApiInterface apiUserInterface = ApiClient.getClient().create(ApiInterface.class);
                Map<String,String> header = Global.generateHeader(RegisterPatientActivity.this);

                Call<ApiResponse> apiResponseCall = apiUserInterface.registerPatientApi(header,registerPatient);

                apiResponseCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        //progressBar.setVisibility(View.GONE);

                        if (Global.IsNotNull(response.body())) {
                            if (response.body().isResponse()) {
                                LoadingDialog.cancelLoading();
                                Toast.makeText(RegisterPatientActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                Global.globalregisterPatientList.clear();
                                clearData();

                                exitPopup();

                            } else {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    LoadingDialog.cancelLoading();
                                    Toast.makeText(RegisterPatientActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
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

                                Toast.makeText(RegisterPatientActivity.this, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }


                        } else {
                            Toast.makeText(RegisterPatientActivity.this, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                        // progressBar.setVisibility(View.GONE);
                        LoadingDialog.cancelLoading();

                        Toast.makeText(RegisterPatientActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

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
            binding.edtFirstname.requestFocus();
            binding.edtEmail.setText("");
            binding.edtDob.setText("");
            binding.edtFirstname.setText("");
            binding.edtMiddlename.setText("");
            binding.edtLastname.setText("");
            binding.edtIdProof1.setText("");
            binding.edtIdProof2.setText("");
            binding.edtMobile.setText("");
            binding.edtAddress.setText("");
            ProfileImage = "";
            isProfilePicSelected = false;
            binding.spnIdproof1.setSelection(0, false);
            binding.spnIdproof2.setSelection(0, false);
            Glide.with(RegisterPatientActivity.this).load(R.drawable.ic_user).into(binding.profileImageviewimg);
            binding.radioGender.clearCheck();
            binding.radioFatherHus.clearCheck();
            binding.edtFatherhus.setText("");
            binding.edtFatherhus.setVisibility(View.GONE);
            binding.spnHealth.setText(null);
            binding.edtHealthcondition.setText("");
            binding.edtHealthcondition.setVisibility(View.GONE);
            binding.spnAllergy.setText(null);
            binding.edtAllergy.setText("");
            binding.edtAllergy.setVisibility(View.GONE);
            binding.alladdedMembers.setText("");
            binding.alladdedMembers.setVisibility(View.GONE);
            binding.spnBloodgroup.setSelection(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exitPopup() {
        user.setTotalPatient(user.getTotalPatient() + 1);
        final String userstring = Global.getJsonString(user);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString(getString(R.string.share_userdetails), userstring);
        prefsEditor.commit();
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Success")
                .setMessage("Do you want to register another patient ?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (Global.IsNotNull(user)) {
                            if (user.getUsertype().equals(getString(R.string.hint_patient))) {
                                if (Global.IsNotNull(user.getEmail())) {
                                    binding.edtEmail.setText(user.getEmail());
                                }

                                if (Global.IsNotNull(user.getMobile())) {
                                    binding.edtMobile.setText(user.getMobile());
                                    binding.codePicker.setCountryForNameCode(user.getCountryCode());
                                }
                            }
                        }

                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                        /*  if(Global.IsNotNull(user)) {
                         *//* if(user.getUsertype().equals(getString(R.string.hint_officer)))
                            {
                                onBackPressed();
                            }
                            else {
                                openAddCouponScreen();
                            }*//*
                        }*/
                    }

                })
                .show();
    }

    public void openAddCouponScreen() {
        Global.globalregisterPatientList = new ArrayList<>();
        Intent registerPatient = new Intent(RegisterPatientActivity.this, Dashboard.class);
        registerPatient.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(registerPatient);
        overridePendingTransition(0, 0);
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
        rv_dataList.setLayoutManager(new LinearLayoutManager(RegisterPatientActivity.this));
        RecyclerAllergyAdapter recyclerAllergyAdapter = new RecyclerAllergyAdapter(RegisterPatientActivity.this, allergyList);
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
                            binding.edtAllergy.setVisibility(View.VISIBLE);
                        } else {
                            binding.edtAllergy.setText("");
                            binding.edtAllergy.setVisibility(View.GONE);
                        }
                    }

                }

                selectedAllergy = sb1.toString();
                if (count == 0) {
                    binding.spnAllergy.setText(null);
                } else {
                    binding.spnAllergy.setText(selectedAllergy);
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
        rv_dataList.setLayoutManager(new LinearLayoutManager(RegisterPatientActivity.this));
        RecyclerHealthConditionAdapter recyclerAllergyAdapter = new RecyclerHealthConditionAdapter(RegisterPatientActivity.this, healthConditionList);
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
                            binding.edtHealthcondition.setVisibility(View.VISIBLE);
                        } else {
                            binding.edtHealthcondition.setText("");
                            binding.edtHealthcondition.setVisibility(View.GONE);
                        }
                    }

                }

                if (count == 0) {
                    binding.spnHealth.setText(null);
                } else {
                    selectedAllergy = sb1.toString();
                    binding.spnHealth.setText(selectedAllergy);
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


    @Override
    protected void onResume() {
        super.onResume();

        user = Global.getUserDetails(RegisterPatientActivity.this);
        if (user.getUsertype().equals(getString(R.string.hint_officer))) {
            binding.btnAdd.setVisibility(View.VISIBLE);
        } else {
            binding.btnAdd.setVisibility(View.GONE);
        }
        if (Global.globalregisterPatientList.size() > 0) {
            int count = 0;
            StringBuilder sb1 = new StringBuilder("");
            for (RegisterPatient registerPatient : Global.globalregisterPatientList) {
                if (count == 0) {
                    sb1.append(registerPatient.getFirstName());
                } else {
                    sb1.append("," + registerPatient.getFirstName());
                }
                count++;
            }


            if (Global.IsNotNull(user)) {
                if (user.getUsertype().equals(getString(R.string.hint_officer))) {
                    binding.alladdedMembers.setText(sb1.toString());
                    if (!binding.alladdedMembers.getText().toString().isEmpty()) {
                        binding.alladdedMembers.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.alladdedMembers.setVisibility(View.GONE);
                }
            }
        } else {
            binding.alladdedMembers.setText("");
            binding.alladdedMembers.setVisibility(View.GONE);
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
}
