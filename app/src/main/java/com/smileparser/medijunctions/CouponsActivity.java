package com.smileparser.medijunctions;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.smileparser.medijunctions.bean.ApiResponse;
import com.smileparser.medijunctions.bean.Coupons;
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

public class CouponsActivity  extends AppCompatActivity {

    WebView webView;
    TextView tv_nodata;
    private float m_downX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        try {

            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.nav_coupons));
        } catch (Exception e) {
            e.printStackTrace();
        }

        webView = findViewById(R.id.genericWebView);
        tv_nodata = findViewById(R.id.nodata);

        virtualLogin();
    }

    public void virtualLogin() {
        if (Global.isInternetOn(this)) {
            LoadingDialog.showLoadingDialog(CouponsActivity.this, "Please wait...");
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

                User user = Global.getUserDetails(CouponsActivity.this);
                Map<String,String> header = Global.generateHeader(CouponsActivity.this);
                HashMap<String, String> map = new HashMap<>();
                map.put("UserId", user.getId());
                map.put("TabFrom","mycoupon");
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

                                Toast.makeText(CouponsActivity.this, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }

                            LoadingDialog.cancelLoading();
                        } else {
                            LoadingDialog.cancelLoading();
                            Toast.makeText(CouponsActivity.this, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        }
                        //LoadingDialog.cancelLoading();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                        // progressBar.setVisibility(View.GONE);
                        LoadingDialog.cancelLoading();

                        Toast.makeText(CouponsActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }


            return null;
        }
    }

    private void initWebView() {
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LoadingDialog.showLoadingDialog(CouponsActivity.this, "Please wait...");
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
        Intent dashbord = new Intent(CouponsActivity.this, Dashboard.class);
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

}
