package com.mirhoseini.autolabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Mohsen on 03/01/2017.
 */

@Module
public class AndroidModule {
    private AutolabsApplication autolabsApplication;

    public AndroidModule(AutolabsApplication autolabsApplication) {
        this.autolabsApplication = autolabsApplication;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return autolabsApplication.getApplicationContext();
    }

    @Provides
    @Singleton
    public Resources provideResources() {
        return autolabsApplication.getResources();
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(autolabsApplication);
    }

}
