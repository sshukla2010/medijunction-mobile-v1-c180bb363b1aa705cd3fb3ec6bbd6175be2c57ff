package com.smileparser.medijunctions.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.smileparser.medijunctions.ClinicHistoryNativeActivity;
import com.smileparser.medijunctions.ConsultationActivity;
import com.smileparser.medijunctions.R;
import com.smileparser.medijunctions.bean.ApiResponse;
import com.smileparser.medijunctions.interfaces.ApiInterface;
import com.smileparser.medijunctions.utils.ApiClient;
import com.smileparser.medijunctions.utils.Global;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddReportsService extends Service {

  String appointmentId;
  String loggedInUserId;
  int notifyID = 100;
  NotificationManager mNotificationManager;
  Notification.Builder mNotifBuilder;
  private static String TAG = "UPLOAD-BCKG-SRVC";
  ArrayList<String> filePaths;
  int count = 0;

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    Bundle p = intent.getExtras();
    appointmentId = p.getString("appointmentId");
    loggedInUserId = p.getString("loggedInUserId");
    filePaths = p.getStringArrayList("filePaths");
    count = 0;
    Log.i("UPLOAD_SERVICE",
        appointmentId + ",  loggedInUserId:" + loggedInUserId + ", filePaths: " + Arrays
            .toString(filePaths.toArray()));
    addFileToMemory();
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startid) {
    Bundle p = intent.getExtras();
    appointmentId = p.getString("appointmentId");
    filePaths = p.getStringArrayList("filePaths");
    loggedInUserId = p.getString("loggedInUserId");
    count = 0;
    Log.i("UPLOAD_SERVICE",
        appointmentId + ",  loggedInUserId:" + loggedInUserId + ", filePaths: " + Arrays
            .toString(filePaths.toArray()));
    addFileToMemory();
    return Service.START_NOT_STICKY;

  }

  /**
   *
   */
  void addFileToMemory() {
    ArrayList<File> files = new ArrayList<>();
    if (count < filePaths.size()) {
      updateNotif(0, count, filePaths.size());
      String filePath = filePaths.get(count);
      Log.i("UPLOAD_SERVICE",
          appointmentId + ",  loggedInUserId:" + loggedInUserId + ", filePath: " + filePath);
      final File file = new File(filePath);
      files.add(file);

    } else {
      Toast.makeText(AddReportsService.this,
          "Linked All Images to your Memory",
          Toast.LENGTH_SHORT).show();
      this.stopSelf();
    }
    uploadImage(loggedInUserId, appointmentId, files);
  }

  public static String getMimeType(String url) {
    String type = null;
    String extension = MimeTypeMap.getFileExtensionFromUrl(url);
    if (extension != null) {
      type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
    return type;
  }

  /**
   *
   */
  private void uploadImage(String loggedInUserId, String appointmentId, ArrayList<File> files) {
    Map<String,String> header = Global.generateHeader(AddReportsService.this);

    RequestBody requestFile = RequestBody
        .create(MediaType.parse("multipart/form-data"), files.get(count));
    // MultipartBody.Part is used to send also the actual filename
    MultipartBody.Part body = MultipartBody.Part
        .createFormData("file", files.get(count).getName(), requestFile);
    RequestBody appointmentIdBody = RequestBody
        .create(MediaType.parse("text/plain"), appointmentId);
    RequestBody loggedInUserIdBody = RequestBody
        .create(MediaType.parse("text/plain"), loggedInUserId);
    ApiClient.getClient().create(ApiInterface.class)
        .uploadDocument(header,body, appointmentIdBody, loggedInUserIdBody)
        .enqueue(new Callback<ApiResponse>() {
          @Override
          public void onResponse(Call<ApiResponse> call,
              Response<ApiResponse> response) {
            if (response.code() / 100 == 2) {
              Log.d(TAG, response.body().toString());
              updateProgress(100);
              count++;
//                        addFileToMemory();
            } else {
              Log.d(TAG, response.toString());
              Toast.makeText(AddReportsService.this,
                  "Unable to add image to memory memory at this time",
                  Toast.LENGTH_SHORT).show();
            }
          }

          @Override
          public void onFailure(Call<ApiResponse> call, Throwable t) {
            Toast.makeText(AddReportsService.this,
                "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
          }
        });
  }


  /**
   *
   */
  @SuppressLint("ResourceAsColor")
  public void updateNotif(int progress, int current, int total) {
    Intent showTaskIntent = new Intent(getApplicationContext(), ClinicHistoryNativeActivity.class);
    showTaskIntent.setAction(Intent.ACTION_MAIN);
    showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    PendingIntent contentIntent = PendingIntent.getActivity(
        getApplicationContext(),
        0,
        showTaskIntent,
        PendingIntent.FLAG_UPDATE_CURRENT);
// Sets an ID for the notification, so it can be updated.
    notifyID = 100;
    String CHANNEL_ID = "medi_junction_channel_01";// The id of the channel.
    CharSequence name = "MEDI_JUNC-Channel";// The user-visible name of the channel.
    int importance = NotificationManager.IMPORTANCE_HIGH;

// Create a notification and set the notification channel.
    Notification notification;
    mNotificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
      mNotifBuilder = new Notification.Builder(AddReportsService.this);

      mNotifBuilder.setContentTitle("Uploading Report")
          .setContentText("Report upload " + current + "/" + total)
          .setSmallIcon(R.drawable.ic_ourdoctor)
          .setWhen(System.currentTimeMillis())
          .setContentIntent(contentIntent)
          .setChannelId(CHANNEL_ID)
          .setColor(ContextCompat.getColor(this, R.color.colorAccent))
          .setProgress(100, progress, false)
          .setOnlyAlertOnce(true);
      notification = mNotifBuilder.build();
      mNotificationManager.createNotificationChannel(mChannel);
    } else {
      mNotifBuilder = new Notification.Builder(AddReportsService.this);

      mNotifBuilder.setContentTitle("Uploading Report")
          .setContentText("Report upload " + current + "/" + total)
          .setSmallIcon(R.drawable.ic_ourdoctor)
          .setWhen(System.currentTimeMillis())
          .setContentIntent(contentIntent)
          .setProgress(100, progress, false).setOnlyAlertOnce(true);
      notification = mNotifBuilder.build();
    }
//    notificationManager.notify(/* id */, notification);
    mNotificationManager.notify(notifyID, notification);
//    startForeground(notifyID, notification);
  }

  /**
   *
   */
  private void updateProgress(int progress) {
    int current = count + 1;
    if (current == filePaths.size()) {
      if (progress == 100) {
        mNotifBuilder.setContentTitle("Finished uploading all report");
        mNotifBuilder.setContentText("All document Uploaded");
        mNotifBuilder.setProgress(100, progress, false);
        mNotificationManager.notify(notifyID, mNotifBuilder.build());
      }
    } else {
      mNotifBuilder.setContentText("Document File upload " + current + "/" + filePaths.size());
      mNotifBuilder.setProgress(100, progress, false);
      mNotificationManager.notify(notifyID, mNotifBuilder.build());
    }
  }
}
