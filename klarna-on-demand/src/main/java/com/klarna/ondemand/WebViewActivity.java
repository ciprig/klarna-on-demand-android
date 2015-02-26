package com.klarna.ondemand;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

        getWebView().loadUrl(getUrl());
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

        super.onDestroy();
    }

    protected abstract int homeButtonResultCode();

    protected abstract String getUrl();

    protected abstract void handleUserReadyEvent(Map<Object, Object> payload);

    protected void handleUserErrorEvent() {
        setResult(RESULT_ERROR);
        finish();
    }

    protected WebView getWebView() {
        if (webView == null) {
            WebView.setWebContentsDebuggingEnabled(true);
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
              Log.d("Sauce", "Everything is awesome!");
              progressDialog.dismiss();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
              Log.d("Sauce", "Something bad happened! " + errorCode);
              Log.d("Sauce", description);

              progressDialog.dismiss();

              setResult(RESULT_ERROR);
              finish();
            }
        });
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
