package com.mirhoseini.autolabs;

import com.mirhoseini.autolabs.service.ApiModule;
import com.mirhoseini.autolabs.service.ClientModule;
import com.mirhoseini.autolabs.speech.SpeechModule;
import com.mirhoseini.autolabs.weather.WeatherModule;
import com.mirhoseini.autolabs.weather.WeatherSubComponent;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Mohsen on 03/01/2017.
 */

@Singleton
@Component(modules = {
        AndroidModule.class,
        ApplicationModule.class,
        ApiModule.class,
        ClientModule.class,
        SpeechModule.class,
})
interface ApplicationComponent {

    void inject(MainActivity mainActivity);

    WeatherSubComponent plus(WeatherModule module);

}