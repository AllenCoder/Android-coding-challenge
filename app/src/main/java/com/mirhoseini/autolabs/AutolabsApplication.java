package com.mirhoseini.autolabs;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import com.mirhoseini.autolabs.weather.WeatherModule;
import com.mirhoseini.autolabs.weather.WeatherSubComponent;

/**
 * Created by Mohsen on 03/01/2017.
 */

public abstract class AutolabsApplication extends Application {
    private static ApplicationComponent component;
    private WeatherSubComponent weatherSubComponent;


    public static AutolabsApplication get(Context context) {
        return (AutolabsApplication) context.getApplicationContext();
    }

    public static ApplicationComponent getComponent() {
        return component;
    }

    protected AndroidModule getAndroidModule() {
        return new AndroidModule(this);
    }

    public WeatherSubComponent getWeatherSubComponent() {
        if (null == weatherSubComponent)
            createWeatherSubComponent();

        return weatherSubComponent;
    }

    public WeatherSubComponent createWeatherSubComponent() {
        weatherSubComponent = component.plus(new WeatherModule());
        return weatherSubComponent;
    }

    public void releaseWeatherSubComponent() {
        weatherSubComponent = null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);

        initApplication();

        component = DaggerApplicationComponent.builder()
                .androidModule(getAndroidModule())
                .build();
    }

    abstract void initApplication();
}
