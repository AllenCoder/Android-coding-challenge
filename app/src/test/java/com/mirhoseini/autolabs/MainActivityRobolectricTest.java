package com.mirhoseini.autolabs;


import com.mirhoseini.autolabs.test.support.ShadowSnackbar;
import com.mirhoseini.autolabs.weather.WeatherFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.mirhoseini.autolabs.test.support.Assert.assertAlertDialogIsShown;
import static com.mirhoseini.autolabs.test.support.Assert.assertSnackbarIsShown;
import static org.junit.Assert.assertNotNull;


/**
 * Created by Mohsen on 03/01/2017.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, shadows = {ShadowSnackbar.class})
public class MainActivityRobolectricTest {

    private MainActivity activity;
    private WeatherFragment fragment;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.setupActivity(MainActivity.class);
        fragment = (WeatherFragment) activity.getSupportFragmentManager().findFragmentByTag(MainActivity.TAG_CURRENT_FRAGMENT);

        assertNotNull(activity);
    }

    @Test
    public void testShowOfflineMessage() throws Exception {
        activity.getNoInternetSubject().onNext(true);

        assertAlertDialogIsShown(R.string.utils__no_connection_title, R.string.utils__no_connection);
    }

    @Test
    public void testShowErrorMessage() throws Exception {
        fragment.showRetryMessage(new Throwable());

        assertSnackbarIsShown(R.string.retry_message);
    }
}
