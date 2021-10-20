package com.smileparser.medijunctions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.smileparser.medijunctions.bean.ApiResponse;
import com.smileparser.medijunctions.bean.User;
import com.smileparser.medijunctions.customviews.LoadingDialog;
import com.smileparser.medijunctions.interfaces.ApiInterface;
import com.smileparser.medijunctions.utils.ApiClient;
import com.smileparser.medijunctions.utils.Global;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClinicHistoryActivity extends AppCompatActivity {

    WebView webView;
    private float m_downX;
    private static final int REQUEST_FOR_DISK = 1;
    TextView tv_nodata;
    private ValueCallback<Uri[]> mFilePathCallback;

    private Uri selectedImage;
    private boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        try {

            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.nav_clinichistory));

        } catch (Exception e) {
            e.printStackTrace();
        }

        webView = findViewById(R.id.genericWebView);
        tv_nodata = findViewById(R.id.nodata);
        if (checkPermissionsForDisk())
            virtualLogin();

    }

    public boolean checkPermissionsForDisk() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ClinicHistoryActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions((Activity) ClinicHistoryActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_FOR_DISK);
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
            case REQUEST_FOR_DISK:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    virtualLogin();
                }
                else
                {
                    new AlertDialog.Builder(this)
                            .setTitle("Warning")
                            .setMessage("Please grant storage permissions to download reports")
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    checkPermissionsForDisk();
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
                LoadingDialog.showLoadingDialog(ClinicHistoryActivity.this, "Please wait...");
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
                if (!loggedIn) {
                    webView.loadUrl(Global.siteurl + "DispensaryChamp/CaseHistory");
                    loggedIn = true;
                }
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
        webView.setInitialScale(1);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.getSettings().setUserAgentString("Android");
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

        @Override
        public void onPermissionRequest(PermissionRequest request) {

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
        finish();
        Global.globalregisterPatientList = new ArrayList<>();
        Intent dashbord = new Intent(ClinicHistoryActivity.this, Dashboard.class);
        dashbord.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dashbord);
        overridePendingTransition(0,0);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_webview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {

        if(webView!= null && webView.canGoBack())
            webView.goBack();
        else {
            onHome();
        }

    }

    public void virtualLogin() {
        if (Global.isInternetOn(this)) {
            LoadingDialog.showLoadingDialog(ClinicHistoryActivity.this, "Please wait...");
            new getUrl().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            Toast.makeText(this, "" + getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    private class getUrl extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                User user = Global.getUserDetails(ClinicHistoryActivity.this);
                Map<String,String> header = Global.generateHeader(ClinicHistoryActivity.this);
                HashMap<String, String> map = new HashMap<>();
                map.put("UserId", user.getId());
                Log.d("USER ID", user.getId());
                Call<ApiResponse> apiResponseCall = apiInterface.caseHistory(header,map);

                apiResponseCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        if (Global.IsNotNull(response.body())) {
                            if (response.body().isResponse()) {
                                if (Global.IsNotNull(response.body().getMessage())) {
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
                                        tv_nodata.setText("Error in API");
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
                                        tv_nodata.setText("Error in API");
                                    }
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

                                Toast.makeText(ClinicHistoryActivity.this, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }

                            LoadingDialog.cancelLoading();
                        } else {
                            LoadingDialog.cancelLoading();
                            Toast.makeText(ClinicHistoryActivity.this, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        LoadingDialog.cancelLoading();
                        Toast.makeText(ClinicHistoryActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }


            return null;
        }
    }


}
