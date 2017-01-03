package com.mirhoseini.autolabs.weather;

import com.mirhoseini.autolabs.service.WeatherApi;
import com.mirhoseini.autolabs.util.SchedulerProvider;

import org.junit.Before;
import org.junit.Test;
import org.openweathermap.model.WeatherCurrent;

import java.util.Collections;

import okhttp3.Protocol;
import okhttp3.Request;
import retrofit2.Response;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Mohsen on 03/01/2017.
 */

public class WeatherInteractorTest {
    static final String TEST_CITY_NAME = "Berlin";
    private WeatherApi api;

    private WeatherInteractor interactor;
    private SchedulerProvider scheduler;

    private Response<WeatherCurrent> expectedResult;

    @Before
    public void setup() {
        api = mock(WeatherApi.class);
        scheduler = mock(SchedulerProvider.class);

        WeatherCurrent weatherCurrent = new WeatherCurrent();
        weatherCurrent.setName(TEST_CITY_NAME);
        weatherCurrent.setDt(0L);
        expectedResult = Response.success(weatherCurrent, new okhttp3.Response.Builder().request(new Request.Builder().url("http://api.openweathermap.org/data/2.5/weather?q=" + TEST_CITY_NAME).build()).protocol(Protocol.HTTP_1_1).code(200).build());

        // mock scheduler to run immediately
        when(scheduler.mainThread())
                .thenReturn(Schedulers.immediate());
        when(scheduler.backgroundThread())
                .thenReturn(Schedulers.immediate());

        // mock api result with expected result
        when(api.getWeather(eq(TEST_CITY_NAME), any(String.class), any(String.class)))
                .thenReturn(Observable.just(expectedResult));

        // create a real new interactor using mocked api and scheduler
        interactor = new WeatherInteractorImpl(api, scheduler);
    }

    @Test
    public void testHitsMemoryCache() {
        // must load data from Network, cause Memory and disk cache are null
        TestSubscriber<Response<WeatherCurrent>> testSubscriberFirst = new TestSubscriber<>();
        interactor.loadWeather(TEST_CITY_NAME).subscribe(testSubscriberFirst);
        testSubscriberFirst.assertNoErrors();
        testSubscriberFirst.assertReceivedOnNext(Collections.singletonList(expectedResult));
    }

}