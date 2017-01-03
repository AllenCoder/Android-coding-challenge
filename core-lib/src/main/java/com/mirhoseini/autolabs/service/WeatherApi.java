package com.mirhoseini.autolabs.service;


import org.openweathermap.model.WeatherCurrent;

import retrofit2.Response;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Mohsen on 03/01/2017.
 */

public interface WeatherApi {

    // http://api.openweathermap.org/data/2.5/weather?q=Tehran&APPID=ee498803643d25e7077f98d4d9849f5c
    @GET("weather")
    Observable<Response<WeatherCurrent>> getWeather(@Query("q") String city, @Query("APPID") String apiKey, @Query("units") String units);

}
