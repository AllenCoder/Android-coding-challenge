package com.mirhoseini.autolabs.weather;


import com.mirhoseini.autolabs.base.BaseInteractor;

import org.openweathermap.model.WeatherCurrent;

import retrofit2.Response;
import rx.Observable;

/**
 * Created by Mohsen on 03/01/2017.
 */

public interface WeatherInteractor extends BaseInteractor {

    Observable<Response<WeatherCurrent>> loadWeather(String city);

}
