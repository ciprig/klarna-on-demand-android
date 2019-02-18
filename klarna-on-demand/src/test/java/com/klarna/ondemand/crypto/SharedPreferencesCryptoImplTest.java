package com.klarna.ondemand.crypto;

import android.content.Context;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 18)
@PrepareForTest(CryptoBase.class)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*", "org.json.*" })
public class SharedPreferencesCryptoImplTest extends CryptoBaseTest {

    @Override
    protected int getTestSdkVersion() {
        return 17;
    }

    @Override
    protected CryptoBase getTestSubject(Context context) throws Exception {
        return new SharedPreferencesCryptoImpl(context);
    }

}