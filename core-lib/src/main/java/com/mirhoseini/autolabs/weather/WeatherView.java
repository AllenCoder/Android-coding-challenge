package com.mirhoseini.autolabs.weather;

import com.mirhoseini.autolabs.base.BaseView;

import org.openweathermap.model.WeatherCurrent;

/**
 * Created by Mohsen on 03/01/2017.
 */

public interface WeatherView extends BaseView {

    void updateProgressMessage(String newMessage);

    void showConnectionError();

    void setWeatherValues(WeatherCurrent WeatherCurrent);

}
