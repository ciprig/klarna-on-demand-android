package com.klarna.ondemand;

import com.jockeyjs.Jockey;
import com.jockeyjs.JockeyImpl;

import junit.framework.Assert;

import java.util.Map;

/**
 * Responsible for showing and modifying a user's payment methods.
 */
public class PreferencesActivity extends WebViewActivity {

    /**
     * Token that uniquely identifies the user at Klarna.
     * This extra item is required.
     */
    public static final String EXTRA_USER_TOKEN = "userToken";

    @Override
    protected String getUrl() {
        String token = getIntent().getStringExtra(EXTRA_USER_TOKEN);
        if(token == null) {
            throw new IllegalStateException("EXTRA_USER_TOKEN is not set for preferences activity");
        }
        return UrlHelper.preferencesUrl(token);
    }

    @Override
    protected void handleUserReadyEvent(Map<Object, Object> payload) {
        getWebView().loadUrl(getUrl());
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected int homeButtonResultCode() {
        return RESULT_OK;
    }
}
