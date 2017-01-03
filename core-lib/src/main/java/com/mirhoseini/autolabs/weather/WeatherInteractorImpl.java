package com.mirhoseini.autolabs.weather;

import com.mirhoseini.autolabs.service.WeatherApi;
import com.mirhoseini.autolabs.util.Constants;
import com.mirhoseini.autolabs.util.SchedulerProvider;

import org.openweathermap.model.WeatherCurrent;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;
import rx.Subscription;
import rx.subjects.ReplaySubject;

/**
 * Created by Mohsen on 03/01/2017.
 */

@Weather
public class WeatherInteractorImpl implements WeatherInteractor {
    private WeatherApi api;
    private SchedulerProvider scheduler;

    @Inject
    public WeatherInteractorImpl(WeatherApi api, SchedulerProvider scheduler) {
        this.api = api;
        this.scheduler = scheduler;
    }

    @Override
    public Observable<Response<WeatherCurrent>> loadWeather(String city) {
        return api.getWeather(city, Constants.API_KEY, Constants.WEATHER_UNITS)
                .subscribeOn(scheduler.backgroundThread());
    }

    @Override
    public void destroy() {
    }
}
