package com.smileparser.medijunctions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.IntDef;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialDatePicker.Builder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smileparser.medijunctions.adapters.ClinicalHistoryCaseRecyclerViewAdapter;
import com.smileparser.medijunctions.adapters.ClinicalHistoryCaseRecyclerViewAdapter.ClinicHistoryCardClickListener;
import com.smileparser.medijunctions.bean.ApiResponse;
import com.smileparser.medijunctions.bean.CaseData;
import com.smileparser.medijunctions.bean.User;
import com.smileparser.medijunctions.customviews.LoadingDialog;
import com.smileparser.medijunctions.interfaces.ApiInterface;
import com.smileparser.medijunctions.services.AddReportsService;
import com.smileparser.medijunctions.utils.ApiClient;
import com.smileparser.medijunctions.utils.Global;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClinicHistoryNativeActivity extends AppCompatActivity implements
    ClinicHistoryCardClickListener {

  TextView tv_nodata;
  TextInputEditText tiet_dateRangeInput;
  TextInputEditText tiet_dateRangeToInput;
  RecyclerView clinicHistoryRecyclerView;
  DatePickerDialog datePickerDialog;
  ArrayList<Uri> imageUriList = new ArrayList<>();
  String setAppointmentId;
  User user;
  private Uri contentURI;
  public final int REQUEST_CODE_CAMERA_PERMISSION = 1, REQUEST_CODE_STORAGE_PERMISSION = 2;
  String READ_EXTERNAL_STORAGE_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE;

  String WRITE_EXTERNAL_STORAGE_PERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
  ClinicHistoryCardClickListener clinicHistoryCardClickListener;
  // for security permissions
  @DialogType
  private int mDialogType;
  private String mRequestPermissions = "We are requesting the camera and Gallery permission as it is absolutely necessary for the app to perform it\'s functionality.\nPlease select \"Grant Permission\" to try again and \"Cancel \" to exit the application.";
  private String mRequsetSettings = "You have rejected the camera and Gallery permission for the application. As it is absolutely necessary for the app to perform you need to enable it in the settings of your device.\nPlease select \"Go to settings\" to go to application settings in your device and \"Cancel \" to exit the application.";
  private String mGrantPermissions = "Grant Permissions";
  private String mCancel = "Cancel";
  private String mGoToSettings = "Go To Settings";
  private String mPermissionRejectWarning = "Cannot Proceed Without Permissions";

  // type of dialog opened
  @IntDef({DialogType.DIALOG_DENY, DialogType.DIALOG_NEVER_ASK})
  @Retention(RetentionPolicy.SOURCE)
  @interface DialogType {

    int DIALOG_DENY = 0, DIALOG_NEVER_ASK = 1;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_clinical_data_history);
    try {

      ActionBar actionBar = getSupportActionBar();
      actionBar.setHomeButtonEnabled(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setTitle(getString(R.string.nav_clinichistory));

    } catch (Exception e) {
      e.printStackTrace();
    }
    clinicHistoryRecyclerView = (RecyclerView) findViewById(R.id.clinic_history_recycler_view);
    String[] objectList = new String[3];
    objectList[0] = "spList";
    objectList[1] = "lgList";
    user = Global.getUserDetails(ClinicHistoryNativeActivity.this);
    new getClinicalHistory().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, objectList);
    Builder builder = MaterialDatePicker.Builder.datePicker();
    MaterialDatePicker picker = builder.build();
    tiet_dateRangeInput = findViewById(R.id.dateRangeText);
    tiet_dateRangeToInput = findViewById(R.id.dateRangeToText);
    tiet_dateRangeInput.setOnFocusChangeListener((v, focus) -> {
      if (focus) {
        prepareDatePickerDialog(tiet_dateRangeInput);
        datePickerDialog.show();
      }
    });
    tiet_dateRangeToInput.setOnFocusChangeListener((v, focus) -> {
      if (focus) {
        prepareDatePickerDialog(tiet_dateRangeToInput);
        datePickerDialog.show();
      }
    });
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
  public void onBackPressed() {
    super.onBackPressed();
  }

  private void populateClinicHistoryCard(ArrayList<CaseData> caseDataList) {
    ClinicalHistoryCaseRecyclerViewAdapter adapter = new ClinicalHistoryCaseRecyclerViewAdapter(
        this, caseDataList);
    clinicHistoryRecyclerView.setHasFixedSize(false);
    clinicHistoryRecyclerView.setAdapter(adapter);
    LinearLayoutManager llm = new LinearLayoutManager(this);
    llm.setOrientation(RecyclerView.VERTICAL);
    clinicHistoryRecyclerView.setLayoutManager(llm);
  }

  private void prepareDatePickerDialog(TextInputEditText tiet) {
    try {
      Calendar calendar = Calendar.getInstance();
      datePickerDialog = new DatePickerDialog(this, R.style.CustomDatePickerDialogTheme,
          new DatePickerDialog.OnDateSetListener() {
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
              String tempDate = "" + day + "-" + month + "-" + year;
              tiet.setText(tempDate);
              String[] objectList = new String[3];
              if ((!Strings.isNullOrEmpty(tiet_dateRangeInput.getText().toString())) && (!Strings
                  .isNullOrEmpty(tiet_dateRangeToInput.getText().toString()))) {
                objectList[0] = user.getId();

                objectList[1] = tiet_dateRangeInput.getText().toString();
                objectList[2] = tiet_dateRangeToInput.getText().toString();
                new getClinicalHistory()
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, objectList);
                LoadingDialog.showLoadingDialog(ClinicHistoryNativeActivity.this, "Loading ...");
              }

              datePickerDialog.dismiss();
            }
          }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
          calendar.get(Calendar.DAY_OF_MONTH));
    } catch (Exception e) {
      Log.d("RegistrationDatePicker", e.getMessage());
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    imageUriList = new ArrayList<>();
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_CANCELED) {
      return;
    }
    Log.i("MEDI-ACTIVITY", String.valueOf(data.getData()));
    ArrayList<String> filePaths = new ArrayList<>();
    if (data.getData() != null) {
      try {
        Uri uri = data.getData();
        imageUriList.add(uri);
        Log.i("MEDI-ACTIVITY1", String.valueOf(uri));
        String path = getPath(uri);
        if (path == null) {
          path = uri.getPath();
        }
        Log.i("MEDI-ACTIVITY1", String.valueOf(path) + "" + path);
        filePaths.add(path);
      } catch (Exception e) {
        Log.e("MEM-ACTIVITY1", Arrays.toString(e.getStackTrace()));
      }
    }
    if (data.getClipData() != null) {
      ClipData mClipData = data.getClipData();
      Toast
          .makeText(ClinicHistoryNativeActivity.this, mClipData.getItemCount() + " Images Selected",
              Toast.LENGTH_LONG).show();
      for (int i = 0; i < mClipData.getItemCount(); i++) {
        try {
          ClipData.Item item = mClipData.getItemAt(i);
          Uri uri = item.getUri();
          if (!imageUriList.contains(uri)) {
            imageUriList.add(uri);
          }
          Log.i("MEDI-ACTIVITY2", String.valueOf(uri));
          String path = getPath(uri);
          Log.i("MEDI-ACTIVITY2", String.valueOf(path) + "" + path);
          filePaths.add(path);
        } catch (Exception e) {
          Log.e("MEM-ACTIVITY2", Arrays.toString(e.getStackTrace()));
        }
      }

    }

//    ArrayList<String> filePaths = getFilePath(imageUriList);
    Log.d("UPLOAD_SERVICE",
        setAppointmentId + ",  loggedInUserId: " + user.getId() + ", filePaths: " + Arrays
            .toString(filePaths.toArray()));
    addFileToAppointment(user.getId(), filePaths);
  }

  private ArrayList<String> getFilePath(ArrayList<Uri> uriList) {
    ArrayList<String> filePaths = new ArrayList<>();
    for (Uri imageUri : imageUriList) {
      String[] filePathColumn = {MediaStore.Images.Media.DATA, MediaStore.Video.Media.DATA};
      android.database.Cursor cursor = getContentResolver()
          .query(imageUri, filePathColumn, null, null, null);
      if (cursor == null) {
        return null;
      }
      cursor.moveToFirst();
      int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
      String filePath = cursor.getString(columnIndex);
      filePaths.add(filePath);
      Log.d("MEDI_JUNCTION_DOCUMENT", filePath);
      cursor.close();
    }

    return filePaths;
  }

  @Override
  public void onAddDocumentClick(String appointmentId) {
    setAppointmentId = appointmentId;
    chooseDocumentFromGallary();
  }

  @Override
  public void onSaveClick(String appointmentId) {

  }

  @Override
  public void onReviewClick(String appointmentId) {
    SweetAlertDialog progressDialog = new SweetAlertDialog(ClinicHistoryNativeActivity.this,
        SweetAlertDialog.PROGRESS_TYPE)
        .setTitleText("Submitting")
        .setContentText("Submitting request for review consultation!");
    progressDialog.show();

    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
    Map<String,String> header = Global.generateHeader(ClinicHistoryNativeActivity.this);
    HashMap<String, String> map = new HashMap<>();

    map.put("AppointmentId", appointmentId);
    map.put("LoggedInUserId", user.getId());
    Call<ApiResponse> apiResponseCall = apiInterface.submitReconsultation(header,map);
    apiResponseCall.enqueue(new Callback<ApiResponse>() {
      @Override
      public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

        if (Global.IsNotNull(response.body())) {
          if (response.isSuccessful()) {
            progressDialog.dismissWithAnimation();
            new SweetAlertDialog(ClinicHistoryNativeActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Review Submitted!")
                .setContentText("Submitted request for review consultation!")
                .show();
          }
        }
      }

      @Override
      public void onFailure(Call<ApiResponse> call, Throwable t) {
        LoadingDialog.cancelLoading();
        progressDialog.dismissWithAnimation();
        new SweetAlertDialog(ClinicHistoryNativeActivity.this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Review Failed!")
            .setContentText("Failed request for review consultation!")
            .show();
        Toast
            .makeText(ClinicHistoryNativeActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT)
            .show();

      }
    });

  }

  @Override
  public void onCaseViewClick(String appointmentId) {
    Intent intent = new Intent(ClinicHistoryNativeActivity.this, WebViewActivity.class);
    intent.putExtra("appointmentId", appointmentId);
    startActivity(intent);
  }


  public void chooseDocumentFromGallary() {
    checkStoragePermissions(REQUEST_CODE_STORAGE_PERMISSION, ClinicHistoryNativeActivity.this);
  }

  @TargetApi(Build.VERSION_CODES.M)
  private void checkStoragePermissions(int permissionCode, Context context) {

    String[] PERMISSIONS = {READ_EXTERNAL_STORAGE_PERMISSION,
        WRITE_EXTERNAL_STORAGE_PERMISSION};
    if (!hasPermissions(context, PERMISSIONS)) {
      ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, permissionCode);
    } else {
      openGallery();
    }
  }

  private void openGallery() {
    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.setType("*/*");
    intent.addCategory(Intent.CATEGORY_OPENABLE);

    // special intent for Samsung file manager
    Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
    // if you want any file type, you can skip next line
    sIntent.putExtra("CONTENT_TYPE", "*/*");
    sIntent.addCategory(Intent.CATEGORY_DEFAULT);

    Intent chooserIntent;
    if (getPackageManager().resolveActivity(sIntent, 0) != null) {
      // it is device with Samsung file manager
      chooserIntent = Intent.createChooser(sIntent, "Open file");
      chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent});
    } else {
      chooserIntent = Intent.createChooser(intent, "Open file");
    }

    try {
      startActivityForResult(chooserIntent, 1);
    } catch (android.content.ActivityNotFoundException ex) {
      Toast.makeText(getApplicationContext(), "No suitable File Manager was found.",
          Toast.LENGTH_SHORT).show();
    }
  }

  private boolean hasPermissions(Context context, String... permissions) {
    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null
        && permissions != null) {
      for (String permission : permissions) {
        if (ActivityCompat.checkSelfPermission(context, permission)
            != PackageManager.PERMISSION_GRANTED) {
          return false;
        }
      }
    }
    return true;
  }

  void addFileToAppointment(String loggedInUserId, ArrayList<String> filePaths) {
    Log.d("UPLOAD_SERVICE",
        setAppointmentId + ",  loggedInUserId:" + loggedInUserId + ", filePaths: " + Arrays
            .toString(filePaths.toArray()));
    Intent addImageIntent = new Intent(ClinicHistoryNativeActivity.this, AddReportsService.class);
    addImageIntent.putExtra("appointmentId", setAppointmentId);
    addImageIntent.putExtra("loggedInUserId", loggedInUserId);
    addImageIntent.putStringArrayListExtra("filePaths", filePaths);
    startService(addImageIntent);
    setAppointmentId = null;
  }


  public String getPath(final Uri uri) {
    final boolean isKitKat = true;
    Uri contentUri = null;
    // DocumentProvider
    if (DocumentsContract.isDocumentUri(ClinicHistoryNativeActivity.this, uri)) {
      // ExternalStorageProvider
      if (isExternalStorageDocument(uri)) {
        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        final String type = split[0];
        if ("primary".equalsIgnoreCase(type)) {
          return Environment.getExternalStorageDirectory() + "/" + split[1];
        }
      }
      // DownloadsProvider
      else if (isDownloadsDocument(uri)) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          final String id;
          Cursor cursor = null;
          try {
            cursor = getContentResolver()
                .query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
              String fileName = cursor.getString(0);
              String path =
                  Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
              if (!TextUtils.isEmpty(path)) {
                return path;
              }
            }
          } finally {
            if (cursor != null) {
              cursor.close();
            }
          }
          id = DocumentsContract.getDocumentId(uri);
          if (!TextUtils.isEmpty(id)) {
            if (id.startsWith("raw:")) {
              return id.replaceFirst("raw:", "");
            }
            String[] contentUriPrefixesToTry = new String[]{
                "content://downloads/public_downloads",
                "content://downloads/my_downloads"
            };
            for (String contentUriPrefix : contentUriPrefixesToTry) {
              try {
                contentUri = ContentUris
                    .withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(id));

                         /*   final Uri contentUri = ContentUris.withAppendedId(
                                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));*/

                return getDataColumn(ClinicHistoryNativeActivity.this, contentUri, null, null);
              } catch (NumberFormatException e) {
                //In Android 8 and Android P the id is not a number
                return uri.getPath().replaceFirst("^/document/raw:", "").replaceFirst("^raw:", "");
              }
            }


          }

        } else {
          final String id = DocumentsContract.getDocumentId(uri);
          final boolean isOreo = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
          if (id.startsWith("raw:")) {
            return id.replaceFirst("raw:", "");
          }
          try {
            contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

          } catch (NumberFormatException e) {
            e.printStackTrace();
          }
          if (contentUri != null) {
            return getDataColumn(ClinicHistoryNativeActivity.this, contentUri, null, null);
          }
        }


      }
      // MediaProvider
      else if (isMediaDocument(uri)) {
        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        final String type = split[0];
        contentUri = null;
        if ("image".equals(type)) {
          contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if ("video".equals(type)) {
          contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if ("audio".equals(type)) {
          contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        final String selection = "_id=?";
        final String[] selectionArgs = new String[]{split[1]};
        return getDataColumn(ClinicHistoryNativeActivity.this, contentUri, selection,
            selectionArgs);
      }
    }
    // MediaStore (and general)
    else if ("content".equalsIgnoreCase(uri.getScheme())) {
      // Return the remote address
      if (isGooglePhotosUri(uri)) {
        return uri.getLastPathSegment();
      }
      return getDataColumn(ClinicHistoryNativeActivity.this, uri, null, null);
    }
    // File
    else if ("file".equalsIgnoreCase(uri.getScheme())) {
      return uri.getPath();
    }
    return null;
  }

  public static String getDataColumn(Context context, Uri uri, String selection,
      String[] selectionArgs) {
    Cursor cursor = null;
    final String column = "_data";
    final String[] projection = {column};
    try {
      cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
      if (cursor != null && cursor.moveToFirst()) {
        final int index = cursor.getColumnIndexOrThrow(column);
        return cursor.getString(index);
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return null;
  }

  public static boolean isExternalStorageDocument(Uri uri) {
    return "com.android.externalstorage.documents".equals(uri.getAuthority());
  }

  public static boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }

  public static boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
  }

  public static boolean isGooglePhotosUri(Uri uri) {
    return "com.google.android.apps.photos.content".equals(uri.getAuthority());
  }

  private class getClinicalHistory extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... params) {
      try {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Map<String,String> header = Global.generateHeader(ClinicHistoryNativeActivity.this);
        HashMap<String, String> map = new HashMap<>();

        map.put("UserId", params[0]);
        map.put("From", params[1]);
        map.put("To", params[2]);
        Call<ApiResponse> apiResponseCall = apiInterface.searchCases(header,map);

        apiResponseCall.enqueue(new Callback<ApiResponse>() {
          @Override
          public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

            if (Global.IsNotNull(response.body())) {
              if (response.body().isResponse()) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<CaseData>>() {
                }.getType();
                ArrayList<CaseData> visitList = gson.fromJson(response.body().getResult(), type);
                populateClinicHistoryCard(visitList);
              }
              LoadingDialog.cancelLoading();
            }
          }

          @Override
          public void onFailure(Call<ApiResponse> call, Throwable t) {
            LoadingDialog.cancelLoading();
            tv_nodata.setVisibility(View.VISIBLE);
            if (Global.IsNotNull(t.getMessage())) {
              tv_nodata.setText(t.getMessage());
            } else {
              tv_nodata.setText("No coupons found!");
            }

            Toast
                .makeText(ClinicHistoryNativeActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT)
                .show();

          }
        });
      } catch (Exception e) {
        e.getMessage();
        LoadingDialog.cancelLoading();
      }

      return null;
    }
  }
}


