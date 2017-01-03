package com.mirhoseini.autolabs;

import android.content.Context;

import com.mirhoseini.autolabs.util.AppConstants;
import com.mirhoseini.autolabs.util.AppSchedulerProvider;
import com.mirhoseini.autolabs.util.SchedulerProvider;
import com.mirhoseini.autolabs.util.StateManager;
import com.mirhoseini.autolabs.util.StateManagerImpl;

import java.io.File;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;

/**
 * Created by Mohsen on 03/01/2017.
 */

@Module
public class ApplicationModule {
    @Provides
    @Singleton
    @Named("isDebug")
    public boolean provideIsDebug() {
        return BuildConfig.DEBUG;
    }

    @Provides
    @Singleton
    @Named("networkTimeoutInSeconds")
    int provideNetworkTimeoutInSeconds() {
        return AppConstants.NETWORK_CONNECTION_TIMEOUT;
    }

    @Provides
    @Singleton
    public HttpUrl provideEndpoint() {
        return HttpUrl.parse(AppConstants.BASE_URL);
    }

    @Provides
    @Singleton
    public SchedulerProvider provideAppScheduler() {
        return new AppSchedulerProvider();
    }

    @Provides
    @Singleton
    @Named("cacheSize")
    long provideCacheSize() {
        return AppConstants.CACHE_SIZE;
    }

    @Provides
    @Singleton
    @Named("cacheMaxAge")
    int provideCacheMaxAgeMinutes() {
        return AppConstants.CACHE_MAX_AGE;
    }

    @Provides
    @Singleton
    @Named("cacheMaxStale")
    int provideCacheMaxStaleDays() {
        return AppConstants.CACHE_MAX_STALE;
    }

    @Provides
    @Singleton
    @Named("retryCount")
    public int provideApiRetryCount() {
        return AppConstants.API_RETRY_COUNT;
    }

    @Provides
    @Singleton
    @Named("cacheDir")
    File provideCacheDir(Context context) {
        return context.getCacheDir();
    }

    @Provides
    @Singleton
    public StateManager provideStateManager(StateManagerImpl stateManager) {
        return stateManager;
    }

}
