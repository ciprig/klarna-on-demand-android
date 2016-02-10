package com.klarna.ondemand;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.*;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jockeyjs.Jockey;
import com.jockeyjs.JockeyHandler;
import com.jockeyjs.JockeyImpl;

import java.util.Map;

abstract class WebViewActivity extends Activity {

    private ProgressDialog progressDialog;
    private WebViewClient webViewClient;
    private Jockey jockey;
    private WebView webView;

    private static final String USER_READY_EVENT_IDENTIFIER = "userReady";
    private static final String USER_ERROR_EVENT_IDENTIFIER = "userError";
    public static final int RESULT_ERROR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);

        addSpinner();

        initializeActionBar();

        initializeWebView();

        registerJockeyEvents();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            setResult(homeButtonResultCode());
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        jockey.off(USER_READY_EVENT_IDENTIFIER);
        jockey.off(USER_ERROR_EVENT_IDENTIFIER);
        dismissProgressDialog();

        super.onDestroy();
    }

    protected abstract int homeButtonResultCode();

    protected abstract void handleUserReadyEvent(Map<Object, Object> payload);

    protected void handleUserErrorEvent() {
        setResult(RESULT_ERROR);
        finish();
    }

    protected WebView getWebView() {
        if (webView == null) {
            webView = (WebView) findViewById(R.id.webView);
        }

        return webView;
    }

    private void initializeWebView() {
        WebView webView = getWebView();

        webView.getSettings().setJavaScriptEnabled(true);
        webView.clearCache(true);
        webView.setWebViewClient(webViewClient = new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && WebViewActivity.this.isDestroyed()) ||
                    WebViewActivity.this.isFinishing()) {
                    return;
                }
                dismissProgressDialog();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                progressDialog.dismiss();

                setResult(RESULT_ERROR);
                finish();
            }
        });

        webView.getSettings().setSupportMultipleWindows(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg)
            {
                WebView.HitTestResult result = view.getHitTestResult();
                String data = result.getExtra();
                android.content.Context context = view.getContext();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                context.startActivity(browserIntent);
                return false;
            }
        });
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void addSpinner() {
        progressDialog = new ProgressDialog(WebViewActivity.this);
        progressDialog.setMessage(getString(R.string.LOADING_SPINNER));
        progressDialog.show();
    }

    private void initializeActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void registerJockeyEvents() {
        jockey = JockeyImpl.getDefault();
        jockey.configure(getWebView());
        jockey.setWebViewClient(webViewClient);

        jockey.on(USER_READY_EVENT_IDENTIFIER, new JockeyHandler() {
            @Override
            protected void doPerform(Map<Object, Object> payload) {
                handleUserReadyEvent(payload);
            }
        });

        jockey.on(USER_ERROR_EVENT_IDENTIFIER, new JockeyHandler() {
            @Override
            protected void doPerform(Map<Object, Object> payload) {
                handleUserErrorEvent();
            }
        });
    }
}
