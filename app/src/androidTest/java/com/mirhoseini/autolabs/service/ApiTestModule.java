package com.mirhoseini.autolabs.service;

import retrofit2.Retrofit;

import static org.mockito.Mockito.mock;

/**
 * Created by Mohsen on 03/01/2017.
 */

public class ApiTestModule extends ApiModule {

    @Override
    public WeatherApi provideWeatherApiService(Retrofit retrofit) {
        // replace Api with Mocked one
        return mock(WeatherApi.class);
    }

}
