package com.mirhoseini.autolabs;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.mirhoseini.appsettings.AppSettings;
import com.mirhoseini.autolabs.service.WeatherApi;
import com.mirhoseini.autolabs.util.Constants;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openweathermap.model.Main;
import org.openweathermap.model.Weather;
import org.openweathermap.model.WeatherCurrent;

import java.util.ArrayList;

import javax.inject.Inject;

import okhttp3.Protocol;
import okhttp3.Request;
import retrofit2.Response;
import rx.Observable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.mirhoseini.autolabs.test.support.Matcher.childAtPosition;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by Mohsen on 03/01/2017.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    static final String TEST_CITY_NAME = "Tehran";
    static final String TEST_ICON = "01d";

    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<>(
            MainActivity.class,
            true,
            // false: do not launch the activity immediately
            false);

    @Inject
    WeatherApi api;

    Response<WeatherCurrent> expectedResult;

    @Before
    public void setUp() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        AutolibsTestApplication app = (AutolibsTestApplication) instrumentation.getTargetContext().getApplicationContext();
        ApplicationTestComponent component = (ApplicationTestComponent) AutolabsApplication.getComponent();

        // inject dagger
        component.inject(this);

        // reset last city local cache
        AppSettings.setValue(app, Constants.KEY_LAST_CITY, TEST_CITY_NAME);

        // Set up the stub we want to return in the mock
        ArrayList<Weather> weathers = new ArrayList<>();
        weathers.add(new Weather(1, "", "clean weather", TEST_ICON));

        WeatherCurrent weatherCurrent = new WeatherCurrent();
        weatherCurrent.setName(TEST_CITY_NAME);
        weatherCurrent.setMain(new Main(1d, 2d, 3, 4d, 5d, 6d, 7d));
        weatherCurrent.setWeather(weathers);
        expectedResult = Response.success(weatherCurrent, new okhttp3.Response.Builder().request(new Request.Builder().url("http://api.openweathermap.org/data/2.5/weather?q=" + TEST_CITY_NAME).build()).protocol(Protocol.HTTP_1_1).code(200).build());

        // Setup the mock
        when(api.getWeather(eq(TEST_CITY_NAME), any(String.class), any(String.class)))
                .thenReturn(Observable.just(expectedResult));
    }

    @Test
    public void shouldBeAbleToSearchForTestCity() {
        // Launch the activity
        mainActivity.launchActivity(new Intent());

        /*
         * This test is recorded using Android Studio "Record Espresso Test" tool
         */

        // enter test city name to search
        ViewInteraction city = onView(
                allOf(withId(R.id.city),
                        withParent(withId(R.id.search_container)),
                        isDisplayed()));
        city.perform(replaceText(TEST_CITY_NAME), closeSoftKeyboard());

        // press go button
        ViewInteraction go = onView(
                allOf(withId(R.id.go),
                        withParent(withId(R.id.search_container)),
                        isDisplayed()));
        go.perform(click());

        // check weather container is visible
        ViewInteraction currentWeather = onView(
                allOf(withId(R.id.current_fragment),
                        childAtPosition(
                                allOf(withId(R.id.weather),
                                        childAtPosition(
                                                IsInstanceOf.instanceOf(android.widget.ScrollView.class),
                                                0)),
                                0),
                        isDisplayed()));
        currentWeather.check(matches(isDisplayed()));

        // check displayed city name
        ViewInteraction name = onView(
                allOf(withId(R.id.name), withText(TEST_CITY_NAME),
                        isDisplayed()));
        name.check(matches(withText(TEST_CITY_NAME)));
    }

}

