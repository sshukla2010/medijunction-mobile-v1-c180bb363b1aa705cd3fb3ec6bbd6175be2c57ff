package com.smileparser.medijunctions;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.webkit.PermissionRequest;
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
import com.smileparser.medijunctions.bean.User;
import com.smileparser.medijunctions.customviews.LoadingDialog;
import com.smileparser.medijunctions.interfaces.ApiInterface;
import com.smileparser.medijunctions.utils.ApiClient;
import com.smileparser.medijunctions.utils.Global;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebViewActivity extends AppCompatActivity {

  WebView webView;
  private float m_downX;
  TextView tv_nodata;
  String appointmentId;
  private boolean loggedIn = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_web_view);
    try {

      ActionBar actionBar = getSupportActionBar();
      assert actionBar != null;
      actionBar.setHomeButtonEnabled(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setTitle(getString(R.string.nav_casedetail));

    } catch (Exception e) {
      e.printStackTrace();
    }
    Intent intent = getIntent();
    appointmentId = intent.getExtras().getString("appointmentId");
    webView = findViewById(R.id.genericWebView);
    tv_nodata = findViewById(R.id.nodata);
    virtualLogin();

  }
  @Override
  public void onBackPressed() {
    super.onBackPressed();
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
        if (!loggedIn) {
          webView.loadUrl(Global.siteurl + "Champ/MemberDetails/CaseDetail/" + appointmentId);
          loggedIn = true;
        }
      }

      @Override
      public void onReceivedError(WebView view, WebResourceRequest request,
          WebResourceError error) {
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
    webView.setOnTouchListener((v, event) -> {

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
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  public void virtualLogin() {
    if (Global.isInternetOn(this)) {
      LoadingDialog.showLoadingDialog(WebViewActivity.this, "Please wait...");
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

        User user = Global.getUserDetails(WebViewActivity.this);
        Map<String,String> header = Global.generateHeader(WebViewActivity.this);
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
                } else {
                  tv_nodata.setVisibility(View.VISIBLE);
                  if (Global.IsNotNull(response.body().getMessage())) {
                    tv_nodata.setText(response.body().getMessage());
                  } else {
                    tv_nodata.setText("Error in API");
                  }
                  LoadingDialog.cancelLoading();
                }
              } else {
                if (Global.IsNotNull(response.body().getMessage())) {
                  LoadingDialog.cancelLoading();
                  tv_nodata.setVisibility(View.VISIBLE);
                  if (Global.IsNotNull(response.body().getMessage())) {
                    tv_nodata.setText(response.body().getMessage());
                  } else {
                    tv_nodata.setText("Error in API");
                  }
                }
                LoadingDialog.cancelLoading();
              }
            } else if (Global.IsNotNull(response.errorBody())) {
              JsonParser parser = new JsonParser();
              JsonElement mJson;
              try {
                LoadingDialog.cancelLoading();
                mJson = parser.parse(response.errorBody().string());
                Gson gson = new Gson();
                ApiResponse errorResponse = gson.fromJson(mJson, ApiResponse.class);

                Toast.makeText(WebViewActivity.this, "" + errorResponse.getMessage(),
                    Toast.LENGTH_SHORT).show();
              } catch (IOException ex) {
                ex.printStackTrace();
              } catch (Exception e) {
                e.getMessage();
              }

              LoadingDialog.cancelLoading();
            } else {
              LoadingDialog.cancelLoading();
              Toast.makeText(WebViewActivity.this, "" + getString(R.string.somethingwrong),
                  Toast.LENGTH_SHORT).show();
            }
          }

          @Override
          public void onFailure(Call<ApiResponse> call, Throwable t) {
            LoadingDialog.cancelLoading();
            Toast.makeText(WebViewActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
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
