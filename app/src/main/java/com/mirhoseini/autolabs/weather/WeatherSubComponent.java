package com.mirhoseini.autolabs.weather;

import dagger.Subcomponent;

/**
 * Created by Mohsen on 03/01/2017.
 */

@Weather
@Subcomponent(modules = {
        WeatherModule.class
})
public interface WeatherSubComponent {

    void inject(WeatherFragment fragment);

}
