package com.mirhoseini.autolabs.weather;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mirhoseini.autolabs.AutolabsApplication;
import com.mirhoseini.autolabs.BR;
import com.mirhoseini.autolabs.MainActivity;
import com.mirhoseini.autolabs.R;
import com.mirhoseini.autolabs.base.BaseFragment;

import org.openweathermap.model.WeatherCurrent;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by Mohsen on 03/01/2017.
 */

public class WeatherFragment extends BaseFragment implements WeatherView {
    private static final String ARG_CITY = "city";

    //injecting dependencies via Dagger
    @Inject
    Context context;
    @Inject
    WeatherPresenter presenter;

    //injecting views via ButterKnife
    @BindView(R.id.progress_container)
    ViewGroup progressContainer;
    @BindView(R.id.progress_message)
    TextView progressMessage;
    @BindView(R.id.error_container)
    ViewGroup errorContainer;
    @BindView(R.id.weather_container)
    ViewGroup weatherContainer;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private BehaviorSubject<String[]> citySubject = BehaviorSubject.create();

    private ViewDataBinding binding;
    private String[] cities;

    public WeatherFragment() {
        // Required empty public constructor
    }

    public static WeatherFragment newInstance(String city) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CITY, city);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String city = getArguments().getString(ARG_CITY);
            citySubject.onNext(new String[]{city});
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        ButterKnife.bind(this, view);

        binding = DataBindingUtil.bind(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        compositeSubscription.add(
                citySubject.subscribe(this::loadWeather)
        );
    }

    private void loadWeather(String[] cities) {
        this.cities = cities;

        presenter.loadWeather(cities);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        presenter.bind(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        presenter.unbind();

        if (null != compositeSubscription && !compositeSubscription.isUnsubscribed())
            compositeSubscription.unsubscribe();
    }

    @Override
    protected void injectDependencies(AutolabsApplication application) {
        application
                .getWeatherSubComponent()
                .inject(this);
    }

    @Override
    public void showToastMessage(String message) {
        Timber.d("Showing Toast Message: %s", message);

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateProgressMessage(String newMessage) {
        Timber.d("Showing Progress Message: %s", newMessage);

        progressMessage.setText(newMessage);
    }

    @Override
    public void showOfflineMessage(boolean isCritical) {
        Timber.d("Showing Offline Message");

        Snackbar.make(errorContainer, R.string.offline_message, Snackbar.LENGTH_LONG)
                .setAction(R.string.go_online, v -> {
                    startActivity(new Intent(
                            Settings.ACTION_WIFI_SETTINGS));
                })
                .setActionTextColor(Color.GREEN)
                .show();

        errorContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgress() {
        Timber.d("Showing Progress");

        progressContainer.setVisibility(View.VISIBLE);
        weatherContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);
    }

    @Override
    public void hideProgress() {
        Timber.d("Hiding Progress");

        progressContainer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setWeatherValues(WeatherCurrent weather) {
        Timber.d("Setting Weather: %s", weather.toString());

        progressContainer.setVisibility(View.GONE);
        weatherContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);

        // load weather data to view
        if (weather != null) {
            weatherContainer.setVisibility(View.VISIBLE);

            binding.setVariable(BR.weather, weather);
            binding.executePendingBindings();

//            Weather weather = weather.getWeather().get(0);
//
//            mNameTextView.setText(weather.getName());
//
//            mDescriptionTextView.setText(weather.getDescription());
//
//            mTempTextView.setText(weather.getMain().getTemp() + "°C");
//
//            mIconImageView.setImageResource(WeatherUtils.convertIconToResource(weather.getIcon()));
//
//            mWindSpeedTextView.setText(weather.getWind().getSpeed() + "m/s");
//
//            mTempHighTextView.setText(weather.getMain().getTempMax() + "°C");
//
//            mTempLowTextView.setText(weather.getMain().getTempMin() + "°C");
        } else {
            errorContainer.setVisibility(View.VISIBLE);
        }

        ((MainActivity) getActivity()).getSaveCitySubject().onNext(weather.getName());
    }

    @Override
    public void showConnectionError() {
        Timber.d("Showing Connection Error Message");

        errorContainer.setVisibility(View.VISIBLE);

        ((MainActivity) getActivity()).getNoInternetSubject().onNext(true);
    }

    @Override
    public void showRetryMessage(Throwable throwable) {
        Timber.d(throwable, "Showing Retry Message");

        errorContainer.setVisibility(View.VISIBLE);

        Snackbar.make(weatherContainer, R.string.retry_message, Snackbar.LENGTH_LONG)
                .setAction(R.string.load_retry, v -> loadWeather(cities))
                .setActionTextColor(Color.RED)
                .show();
    }

    public BehaviorSubject<String[]> getCitySubject() {
        return citySubject;
    }
}
