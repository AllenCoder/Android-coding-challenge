package com.mirhoseini.autolabs.test.support;

import android.support.annotation.StringRes;

import org.robolectric.RuntimeEnvironment;

/**
 * Created by Mohsen on 03/01/2017.
 */

public class ResourceLocator {

    public static String getString(@StringRes int id) {
        return RuntimeEnvironment.application.getString(id);
    }

}
