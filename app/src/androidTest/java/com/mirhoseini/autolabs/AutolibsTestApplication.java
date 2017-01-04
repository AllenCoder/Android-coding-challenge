package com.mirhoseini.autolabs;

import android.support.annotation.NonNull;

import com.mirhoseini.autolabs.service.ApiTestModule;

/**
 * Created by Mohsen on 03/01/2017.
 */

public class AutolibsTestApplication extends AutolabsApplicationImpl {

    @Override
    public ApplicationTestComponent createComponent() {
        return DaggerApplicationTestComponent
                .builder()
                .androidModule(getAndroidModule())
                // replace Api Module with Mocked one
                .apiModule(getMockedApiModule())
                .build();
    }

    @NonNull
    private ApiTestModule getMockedApiModule() {
        return new ApiTestModule();
    }

}
