package com.smileparser.medijunctions;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.smileparser.medijunctions.bean.ApiResponse;
import com.smileparser.medijunctions.bean.User;
import com.smileparser.medijunctions.customviews.LoadingDialog;
import com.smileparser.medijunctions.interfaces.ApiInterface;
import com.smileparser.medijunctions.utils.ApiClient;
import com.smileparser.medijunctions.utils.Global;
import com.smileparser.medijunctions.utils.MediaUtility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsultationActivity extends AppCompatActivity {

    WebView webView;
    private float m_downX;
    private static final int REQUEST_FOR_CAMERA = 123;
    TextView tv_nodata;
    private static final int REQUEST_FILE_PICKER = 2;
    private ValueCallback<Uri[]> mFilePathCallback;
    private static final int GET_FROM_CAMERA = 0;
    private static final int GET_FROM_GALLERY = 1;
    private Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        try {

            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.nav_consultation));
        } catch (Exception e) {
            e.printStackTrace();
        }

        webView = findViewById(R.id.genericWebView);
        tv_nodata = findViewById(R.id.nodata);

        if(checkPermissionsForCamera()) {
            virtualLogin();
        }
    }

    public boolean checkPermissionsForCamera() {
        Context context;
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ConsultationActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(ConsultationActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(ConsultationActivity.this, Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(ConsultationActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(ConsultationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) ConsultationActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,Manifest.permission.MODIFY_AUDIO_SETTINGS,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_FOR_CAMERA);
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
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED )
                {
                    virtualLogin();
                }
                else
                {
                    new AlertDialog.Builder(this)
                            .setTitle("Warning")
                            .setMessage("Without permission we can't consulting you please allow the permissons you want to allow ?")
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    checkPermissionsForCamera();
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

    private void initWebView() {
        webView.setWebChromeClient(new MyWebChromeClient(this));
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                invalidateOptionsMenu();
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
                super.onReceivedClientCertRequest(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LoadingDialog.cancelLoading();
                invalidateOptionsMenu();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                LoadingDialog.cancelLoading();
                invalidateOptionsMenu();
            }
        });
        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getPointerCount() > 1) {
                    //Multi touch detected
                    return true;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        // save the x
                        m_downX = event.getX();
                    }
                    break;

                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP: {
                        // set x so that it doesn't move
                        event.setLocation(m_downX, event.getY());
                    }
                    break;
                }

                return false;
            }
        });
    }

    private class MyWebChromeClient extends WebChromeClient {
        Context context;

        public MyWebChromeClient(Context context) {
            super();
            this.context = context;

        }

        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams){
            mFilePathCallback = filePathCallback;
            openFileDialog();
            return true;
        }

        @Override
        public void onPermissionRequest(PermissionRequest request) {
            //super.onPermissionRequest(request);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    String[] resouces = request.getResources();
                    request.grant(resouces);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode==REQUEST_FILE_PICKER || requestCode == GET_FROM_CAMERA || requestCode == GET_FROM_GALLERY)
        {
            if(mFilePathCallback!=null)
            {
                Uri result = null;
                if(requestCode == GET_FROM_CAMERA)
                {
                   String path = getRealPathFromURI(selectedImage);
                    if(path != null) {
                        File finalFile = new File(path);
                        Uri uri = Uri.fromFile(finalFile);
                        mFilePathCallback.onReceiveValue(new Uri[]{uri});
                    }
                    else
                    {
                        mFilePathCallback.onReceiveValue(null);
                    }

                }
                else {
                    result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
                    if(result!=null)
                    {
                        String path = MediaUtility.getPath(ConsultationActivity.this, result);
                        Uri uri = Uri.fromFile(new File(path));
                        mFilePathCallback.onReceiveValue(new Uri[]{ uri });
                    }
                    else
                    {
                        mFilePathCallback.onReceiveValue(null);
                    }
                }

            }

            mFilePathCallback = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_home:
                return onHome();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    private boolean onHome() {
        Global.globalregisterPatientList = new ArrayList<>();
        Intent dashbord = new Intent(ConsultationActivity.this, Dashboard.class);
        dashbord.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dashbord);
        overridePendingTransition(0,0);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.action_bar_webview, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Override
    public void onBackPressed() {

        if(webView!= null && webView.canGoBack())
            webView.goBack();// if there is previous page open it
        else {
            onHome();
        }

    }

    public void virtualLogin() {
        if (Global.isInternetOn(this)) {
            LoadingDialog.showLoadingDialog(ConsultationActivity.this, "Please wait...");
            new getUrl().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            Toast.makeText(this, "" + getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }


    public void openFileDialog()
    {
        final CharSequence[] listItems = {"Take Photo", "Choose Image","Choose File"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add photo");
        builder.setItems(listItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (listItems[item].equals("Take Photo"))
                {

                        String fileName = "temp.jpg";
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, fileName);
                        selectedImage = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,selectedImage);
                        startActivityForResult(intent, GET_FROM_CAMERA);
                }
                else if (listItems[item].equals("Choose Image"))
                {
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, GET_FROM_GALLERY);
                }
                else if(listItems[item].equals("Choose File"))
                {
                    String[] mimeTypes = {"image/*","application/pdf"};

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                    startActivityForResult(Intent.createChooser(intent, "File Chooser"), REQUEST_FILE_PICKER);
                }
            }
        });
        builder.setNegativeButton("Cancel",null);
        builder.show();
    }

    private class getUrl extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                User user = Global.getUserDetails(ConsultationActivity.this);
                Map<String,String> header = Global.generateHeader(ConsultationActivity.this);
                HashMap<String, String> map = new HashMap<>();
                map.put("UserId", user.getId());
                Call<ApiResponse> apiResponseCall = apiInterface.virtualLogin(header,map);

                apiResponseCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        //progressBar.setVisibility(View.GONE);

                        if (Global.IsNotNull(response.body())) {
                            if (response.body().isResponse()) {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    //LoadingDialog.cancelLoading();
                                    tv_nodata.setVisibility(View.GONE);
                                    initWebView();

                                    webView.loadUrl(response.body().getResult().getAsString());

                                }
                                else
                                {
                                    tv_nodata.setVisibility(View.VISIBLE);
                                    if(Global.IsNotNull(response.body().getMessage())) {
                                        tv_nodata.setText(response.body().getMessage());
                                    }
                                    else
                                    {
                                        tv_nodata.setText("You have no coupon yet!");
                                    }
                                    LoadingDialog.cancelLoading();
                                }
                            } else {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    LoadingDialog.cancelLoading();
                                    tv_nodata.setVisibility(View.VISIBLE);
                                    if(Global.IsNotNull(response.body().getMessage())) {
                                        tv_nodata.setText(response.body().getMessage());
                                    }
                                    else
                                    {
                                        tv_nodata.setText("You have no coupon yet!");
                                    }
                                    //Toast.makeText(ConsultationActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                LoadingDialog.cancelLoading();
                            }
                        } else if (Global.IsNotNull(response.errorBody())) {
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = null;
                            try {
                                LoadingDialog.cancelLoading();
                                mJson = parser.parse(response.errorBody().string());
                                Gson gson = new Gson();
                                ApiResponse errorResponse = gson.fromJson(mJson, ApiResponse.class);

                                Toast.makeText(ConsultationActivity.this, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }

                            LoadingDialog.cancelLoading();
                        } else {
                            LoadingDialog.cancelLoading();
                            Toast.makeText(ConsultationActivity.this, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        }
                        //LoadingDialog.cancelLoading();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                        // progressBar.setVisibility(View.GONE);
                        LoadingDialog.cancelLoading();

                        Toast.makeText(ConsultationActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                LoadingDialog.cancelLoading();

                e.getMessage();
            }


            return null;
        }
    }






}
