package com.smileparser.medijunctions;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceFragmentCompat;

import com.agatsa.sanketlife.callbacks.PdfCallback;
import com.agatsa.sanketlife.callbacks.RegisterDeviceResponse;
import com.agatsa.sanketlife.callbacks.ResponseCallback;
import com.agatsa.sanketlife.callbacks.SaveEcgCallBack;
import com.agatsa.sanketlife.development.EcgConfig;
import com.agatsa.sanketlife.development.Errors;
import com.agatsa.sanketlife.development.InitiateEcg;
import com.agatsa.sanketlife.development.Success;
import com.agatsa.sanketlife.development.UserDetails;
import com.smileparser.medijunctions.customviews.LoadingDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class ECGActivity extends AppCompatActivity implements View.OnClickListener{
    private String client_Id = "5a3b4c16b4a56b000132f5d5ab2ad442b19940aeb48ce93faa9310dd";
    private String secret_Id = "5a3b4c16b4a56b000132f5d57ba4eda717764e17a666ad0358962c01";
    private Button connect_ecg_button;
    private Button take_ecg_button;
    private Button pdf_ecg_button;
    private InitiateEcg initiateEcg = null;
    private EcgConfig ecgConfigCurr = null; //deprecated
    private boolean filterEcg = true;
    private static final int REQUEST_FOR_LOCATION_WRITE = 10;
    private int MAX_CONNECTION_TRIES = 1;
    private int curr_connection_tries = 0;
    private String patientName = null;
    private String ecg_type = null;

    DialogInterface.OnClickListener dialogClickListener;
    private AlertDialog.Builder builder;
    private int curr_lead = 1;
    private int MAX_LEADS = 8;
    ArrayList<Integer> lead_order = new ArrayList<>(Arrays.asList(0, 1, 8, 2, 3, 4, 5, 6, 7));
    ArrayList<String> lead_labels = new ArrayList<>(Arrays.asList("Error", "L1", "L2", "V1", "V2", "V3", "V4", "V5", "V6"));
    ArrayList<Integer> lead_gifs = new ArrayList<>(Arrays.asList(-1, R.layout.ecg_l1,R.layout.ecg_l2, R.layout.ecg_v1, R.layout.ecg_v2, R.layout.ecg_v3,
                                                    R.layout.ecg_v4, R.layout.ecg_v5, R.layout.ecg_v6));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg);
        //        getSupportFragmentManager()
        //                .beginTransaction()
        //                .replace(R.id.settings, new SettingsFragment())
        //                .commit();
        try {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("ECG");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        patientName = intent.getStringExtra("patientName");
        ecg_type = intent.getStringExtra("ecgType");
        
        initiateEcg = new InitiateEcg();

        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        if (curr_lead == 1) ecgProcedure();
                        else recordECGSingleLeadSequence();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        builder = new AlertDialog.Builder(this);

        if(checkPermissions()) {
            if (ecg_type.equals("device")) updateAlertBox();
            else ecgProcedure();
        }

    }

    public boolean checkPermissions() {
        Context context;
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ){
                ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_FOR_LOCATION_WRITE);
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
            case REQUEST_FOR_LOCATION_WRITE:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED)
                {
                    updateAlertBox();
                }
                else
                {
                    new AlertDialog.Builder(this)
                            .setTitle("Warning")
                            .setMessage("Without permission we can't consulting you please allow the permissions you want to allow ?")
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    checkPermissions();
                                }

                            })
                            .setNegativeButton("no", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    onBackPressed();
                                }
                            })
                            .show();
                }
                break;
        }
    }

    private void updateAlertBox() {
        LayoutInflater factory = LayoutInflater.from(ECGActivity.this);
        final View view = factory.inflate(lead_gifs.get(curr_lead), null);
        builder.setView(view);
        builder.setMessage("Ready for Lead " + lead_labels.get(curr_lead) + "?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void ecgProcedure() {
//        copyPDF("/storage/emulated/0/DCIM/sanket/20-05-2020_12_14_45.pdf", "test test");
        connectDevice();
    }

    //TODO: fix crash on fallback to patient list
    private void connectDevice() {
        curr_connection_tries++;
        if (curr_connection_tries > MAX_CONNECTION_TRIES) {
//            Toast.makeText(this, "Cannot connect to the device. Please try again later", Toast.LENGTH_LONG).show();
            finish();
        }
        LoadingDialog.showLoadingDialog(ECGActivity.this, "Connecting...");
        initiateEcg.registerDevice(getApplicationContext(), client_Id, new RegisterMyDeviceResponse() {
            @Override
            public void onSuccess(String s) {
                LoadingDialog.cancelLoading();
//                Toast.makeText(ECGActivity.this, s, Toast.LENGTH_SHORT).show();
                if (ecg_type.equals("device")) recordECGSingleLeadSequence();
                else recordECGSwitchSy();
            }
            @Override
            public void onError(Errors errors){
                LoadingDialog.cancelLoading();

                Log.d("SANKET", String.valueOf(errors.getErrorCode()));
                Log.d("SANKET", errors.getErrorMsg());
                // TODO: check if error code corresponds to device already connected
                if (errors.getErrorCode() == 7) {
                    if (ecg_type.equals("device")) recordECGSingleLeadSequence();
                    else recordECGSwitchSy();
                } else {
                    Toast.makeText(ECGActivity.this, errors.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    connectDevice();
                }
            }
        });
    }



    private void recordECGSingleLeadSequence() {
        if (curr_lead > MAX_LEADS) return;
        initiateEcg.takeEcg(getApplicationContext(),secret_Id,lead_order.get(curr_lead),new ResponseCallback(){
            @Override
            public void onSuccess(Success success){
                if (curr_lead < MAX_LEADS) {
                    curr_lead++;
                    updateAlertBox();
                } else {
                    LoadingDialog.showLoadingDialog(ECGActivity.this, "Generating Report...");
                    makeECGReport(null);
                }
                Toast.makeText(ECGActivity.this, success.getSuccessMsg(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Errors errors){
                if (curr_lead < MAX_LEADS) {
                    updateAlertBox();
                }
                Toast.makeText(ECGActivity.this, errors.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void recordECGSingleLead(int lead) {
        initiateEcg.takeEcg(getApplicationContext(),secret_Id,lead,new ResponseCallback(){
            @Override
            public void onSuccess(Success success){
                Toast.makeText(ECGActivity.this, success.getSuccessMsg(), Toast.LENGTH_SHORT).show();
                initiateEcg.saveEcgData(ECGActivity.this, "newID", new SaveEcgCallBack() {
                    @Override
                    public void onSuccess(Success success, EcgConfig ecgConfig) {
                        makeECGReport(ecgConfig);
                        Toast.makeText(ECGActivity.this, success.getSuccessMsg(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Errors error) {
                        Toast.makeText(ECGActivity.this, error.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Errors errors){
                Toast.makeText(ECGActivity.this, errors.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void recordECGSwitchSy() {
        initiateEcg.takeEcgWithSwitchSy(getApplicationContext(),secret_Id,new ResponseCallback(){
            @Override
            public void onSuccess(Success success){
                Toast.makeText(ECGActivity.this, success.getSuccessMsg(), Toast.LENGTH_SHORT).show();
                initiateEcg.saveEcgData(ECGActivity.this, "newID", new SaveEcgCallBack() {
                    @Override
                    public void onSuccess(Success success, EcgConfig ecgConfig) {
                        LoadingDialog.showLoadingDialog(ECGActivity.this, "Generating Report...");
                        makeECGReport(null);
                        Toast.makeText(ECGActivity.this, success.getSuccessMsg(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Errors error) {
                        Toast.makeText(ECGActivity.this, error.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Errors errors){
                Toast.makeText(ECGActivity.this, errors.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void makeECGReport(EcgConfig ecgConfigCurr) {
        initiateEcg.saveEcgData(ECGActivity.this, "newID", new SaveEcgCallBack() {
            @Override
            public void onSuccess(Success success, EcgConfig ecgConfig) {
                initiateEcg.makeEcgReport(getApplicationContext(), new UserDetails(patientName, "0", "Gender"),
                        filterEcg,secret_Id, ecgConfig, new PdfCallback() {
                            @Override
                            public void onPdfAvailable(EcgConfig ecgConfig) {
                                LoadingDialog.cancelLoading();
                                Log.i("SANKET", ecgConfig.getFileUrl());
                                Toast.makeText(ECGActivity.this, "success pdf", Toast.LENGTH_SHORT).show();
                                copyPDF(ecgConfig.getFileUrl(), patientName);
                                openPDF(ecgConfig.getFileUrl());
                            }
                            @Override
                            public void onError(Errors errors) {
                                LoadingDialog.cancelLoading();
                                Log.i("SANKET", errors.getErrorMsg());
                                Toast.makeText(ECGActivity.this, errors.getErrorMsg(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onError(Errors error) {
                LoadingDialog.cancelLoading();
                Toast.makeText(ECGActivity.this, error.getErrorMsg(), Toast.LENGTH_SHORT).show();
                if (curr_lead < MAX_LEADS) {
                    updateAlertBox();
                }
            }
        });
    }

    private void copyPDF(String original_path, String patientName) {
        if (patientName == null || patientName.equals("")) return;
        File ecg_dir = new File(Environment.getDataDirectory()
                + "/ECG/");
        if (!ecg_dir.exists()) {
            ecg_dir.mkdir();
        }
        String[] dir_nodes = original_path.split("/");
        String orig_name = dir_nodes[dir_nodes.length-1];
        String new_path = Environment.getDataDirectory()
                + "/ECG/";

        try {
            File orig_ecg_file = new File(original_path);
            File new_ecg_file = new File(new_path, patientName + " " + orig_name);
            copyFile(orig_ecg_file, new_ecg_file);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    // TODO: open PDF from /ECG instead of /DCIM/sanket
    private void openPDF(String filepath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File myPDF = new File(filepath);
        Uri uri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", myPDF);
        Log.d("D", "openPDF: intent with uri: " + uri);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        this.startActivity(Intent.createChooser(intent, "Open with..."));
    }

    public static void copyFile(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.connect_ecg:
////                openPDF("/storage/emulated/0/DCIM/sanket/20-05-2020_12_14_45.pdf");
//            case R.id.take_ecg:
//            case R.id.pdf_ecg:
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    private class RegisterMyDeviceResponse implements RegisterDeviceResponse {
        @Override
        public void onSuccess(String s) {

        }

        @Override
        public void onError(Errors errors) {

        }
    }
}
