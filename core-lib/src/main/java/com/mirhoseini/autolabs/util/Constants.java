package com.mirhoseini.autolabs.util;

/**
 * Created by Mohsen on 03/01/2017.
 */

public class Constants {
    // all Constant values are here
    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static final String API_KEY = "ee498803643d25e7077f98d4d9849f5c";
    public static final String WEATHER_UNITS = "metric";

    public static final String KEY_LAST_CITY = "last_city";

    public static final String CITY_DEFAULT_VALUE = "Berlin";

    public static final int NETWORK_CONNECTION_TIMEOUT = 30; // 30 sec
    public static final long CACHE_SIZE = 10 * 1024 * 1024; // 10 MB
    public static final int CACHE_MAX_AGE = 2; // 2 min
    public static final int CACHE_MAX_STALE = 7; // 7 day
}
