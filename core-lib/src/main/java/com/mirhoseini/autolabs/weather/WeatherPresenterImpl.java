package com.mirhoseini.autolabs.weather;

import com.mirhoseini.autolabs.util.SchedulerProvider;
import com.mirhoseini.autolabs.util.StateManager;

import org.openweathermap.model.WeatherCurrent;

import java.net.URLDecoder;
import java.util.NoSuchElementException;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.subscriptions.Subscriptions;

/**
 * Created by Mohsen on 03/01/2017.
 */

@Weather
public class WeatherPresenterImpl implements WeatherPresenter {

    private WeatherInteractor interactor;
    private StateManager stateManager;
    private WeatherView view;
    private Subscription subscription = Subscriptions.empty();
    private SchedulerProvider scheduler;

    @Inject
    public WeatherPresenterImpl(StateManager stateManager, SchedulerProvider scheduler, WeatherInteractor interactor) {
        this.interactor = interactor;
        this.scheduler = scheduler;
        this.stateManager = stateManager;
    }

    @Override
    public void bind(WeatherView view) {
        this.view = view;
    }

    @Override
    public void unbind() {
        this.view = null;

        if (null != subscription && !subscription.isUnsubscribed())
            subscription.unsubscribe();
    }

    @Override
    public void loadWeather(String[] cities) {
        if (view != null) {
            view.showProgress();
            view.updateProgressMessage("Loading City Weather...");
        }

        subscription = Observable.from(cities)
                .concatMap(city -> interactor.loadWeather(city)
                        .filter(this::checkWeatherCurrentResponseValidation)
                        .onErrorResumeNext(Observable.empty()))
                .first()
                .map(weatherCurrentResponse -> weatherCurrentResponse.body())
                .observeOn(scheduler.mainThread())
                .subscribe(
                        weatherCurrent -> {
                            if (view != null) {
                                view.setWeatherValues(weatherCurrent);
                            }
                        },
                        throwable -> {
                            if (stateManager.isConnect()) {
                                if (view != null) {

                                    // TODO: 04/01/2017 handle exceptions using separate Exception classes
                                    if (throwable.getClass().equals(NoSuchElementException.class)) {
                                        view.showToastMessage("City not found!!!");
                                    } else {
                                        view.showRetryMessage(throwable);
                                    }
                                }
                            } else {
                                if (view != null) {
                                    view.showConnectionError();
                                }
                            }

                            if (view != null) {
                                view.hideProgress();
                            }
                        },
                        () -> {
                            if (view != null) {
                                view.hideProgress();
                            }
                        });
    }

    private boolean checkWeatherCurrentResponseValidation(Response<WeatherCurrent> weatherCurrentResponse) {
        boolean result = false;

        try {
            result = null != weatherCurrentResponse
                    && weatherCurrentResponse.isSuccessful()
                    && null != weatherCurrentResponse.body()
                    // Unfortunately OpenWeatherMap.org api answer any query even if it is not a city name!!! :/
                    // so, I temporarily solve the issue with checking if the answer is the same as request.
                    && URLDecoder.decode(weatherCurrentResponse.raw().request().url().toString().split("/?q=")[1].split("&")[0], "UTF-8").equalsIgnoreCase(weatherCurrentResponse.body().getName());
        } catch (Exception e) {
            Exceptions.propagate(e);
        }

        return result;

    }

}
