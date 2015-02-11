package com.klarna.ondemand;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.tester.android.view.TestMenuItem;
import org.robolectric.util.ActivityController;

import java.util.Collections;
import java.util.HashMap;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
@PrepareForTest({ Context.class, UrlHelper.class })
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*" })
public class RegistrationActivityTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private ActivityController<RegistrationActivity> registrationActivityController;
    private RegistrationActivity registrationActivity;

    @Before
    public void beforeEach() {
        mockStatic(Context.class);
        when(Context.getApiKey()).thenReturn("test_skadoo");

        mockStatic(UrlHelper.class);
        when(UrlHelper.registrationUrl((android.content.Context)anyObject())).thenReturn("my_url");

        registrationActivityController = Robolectric.buildActivity(RegistrationActivity.class).create();
        registrationActivity = spy(registrationActivityController.get());
    }

    @Test
    public void handleUserReadyEvent_ShouldPutRegistrationResultInExtraAndCallFinishWithResultOk() {
        final HashMap<Object, Object> userDetails = new HashMap<Object, Object>() {{
            put("firstName", "Tom");
        }};

        registrationActivity.handleUserReadyEvent(new HashMap<Object, Object>() {{
            put("userToken", "my_token");
            put("phone", "my_phoneNumber");
            put("userDetails", userDetails);
        }});

        Intent expectedIntent = new Intent();
        expectedIntent.putExtra(RegistrationActivity.EXTRA_USER_TOKEN, "my_token");


        expectedIntent.putExtra(RegistrationActivity.EXTRA_REGISTRATION_RESULT,
                new RegistrationResult("my_token", "my_phoneNumber", userDetails));

        verify(registrationActivity).setResult(eq(Activity.RESULT_OK), eq(expectedIntent));
        verify(registrationActivity).finish();
    }

    @Test
    public void handleUserReadyEvent_ShouldPutEmptyUserDetailsInTheRegistrationResult_WhenPaylodDoesNotContainUserDetails() {
        registrationActivity.handleUserReadyEvent(new HashMap<Object, Object>() {{
            put("userToken", "my_token");
            put("phone", "my_phoneNumber");
        }});

        Intent expectedIntent = new Intent();
        expectedIntent.putExtra(RegistrationActivity.EXTRA_USER_TOKEN, "my_token");

        expectedIntent.putExtra(RegistrationActivity.EXTRA_REGISTRATION_RESULT,
                new RegistrationResult("my_token", "my_phoneNumber", Collections.emptyMap()));

        verify(registrationActivity).setResult(eq(Activity.RESULT_OK), eq(expectedIntent));
        verify(registrationActivity).finish();
    }

    @Test
    public void handleUserErrorEvent_ShouldCallFinishWithResultError() {
        registrationActivity.handleUserErrorEvent();

        verify(registrationActivity).setResult(RegistrationActivity.RESULT_ERROR);
        verify(registrationActivity).finish();
    }

    @Test
    public void homeButtonPress_ShouldCallFinishWithResultCanceled() {
        MenuItem item = new TestMenuItem() {
            public int getItemId() {
                return android.R.id.home;
            }
        };
        registrationActivity.onOptionsItemSelected(item);

        verify(registrationActivity).setResult(RegistrationActivity.RESULT_CANCELED);
        verify(registrationActivity).finish();
    }

    @Test
    public void backButtonPress_ShouldCallShowDismissAlert() {
        registrationActivity.onBackPressed();

        verify(registrationActivity).showDismissAlert();
    }
}
