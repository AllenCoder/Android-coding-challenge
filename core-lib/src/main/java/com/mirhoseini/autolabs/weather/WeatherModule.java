package com.mirhoseini.autolabs.weather;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Mohsen on 03/01/2017.
 */

@Module
public class WeatherModule {
    private WeatherView view;

    void bind(WeatherView view) {
        this.view = view;
    }

    void unbind() {
        this.view = null;
    }

    @Provides
    public WeatherView provideView() {
        return view;
    }

    @Weather
    @Provides
    public WeatherPresenter providePresenter(WeatherPresenterImpl presenter) {
        return presenter;
    }

    @Weather
    @Provides
    public WeatherInteractor provideInteractor(WeatherInteractorImpl interactor) {
        return interactor;
    }
}
