package com.mirhoseini.autolabs;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mirhoseini.appsettings.AppSettings;
import com.mirhoseini.autolabs.base.BaseActivity;
import com.mirhoseini.autolabs.speech.AppSpeechView;
import com.mirhoseini.autolabs.speech.SpeechProvider;
import com.mirhoseini.autolabs.util.Constants;
import com.mirhoseini.autolabs.weather.WeatherFragment;
import com.mirhoseini.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTouch;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by Mohsen on 03/01/2017.
 */

public class MainActivity extends BaseActivity implements AppSpeechView {

    public static final String TAG_CURRENT_FRAGMENT = "current_fragment";
    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 1;

    @Inject
    Context context;
    @Inject
    SpeechProvider speechProvider;

    //injecting views via ButterKnife
    @BindView(R.id.city)
    EditText city;
    @BindView(R.id.talk)
    ImageButton talk;
    @BindView(R.id.progress)
    ProgressBar progress;

    private WeatherFragment weatherFragment;
    private AlertDialog internetConnectionDialog;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private BehaviorSubject<Boolean> noInternetSubject = BehaviorSubject.create();
    private BehaviorSubject<String> saveCitySubject = BehaviorSubject.create();

    @OnEditorAction(R.id.city)
    public boolean onEditorAction(TextView textView, int action, KeyEvent keyEvent) {
        if (action == EditorInfo.IME_ACTION_GO || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            submit(textView);
        }
        return false;
    }

    @OnClick(R.id.go)
    public void submit(View view) {
        //hide keyboard for better UX
        Utils.hideKeyboard(context, city);

        weatherFragment.getCitySubject().onNext(new ArrayList<>(Arrays.asList(new String[]{city.getText().toString().trim()})));
    }

    @OnTouch(R.id.talk)
    public boolean onTalk(MotionEvent event) {
        //hide keyboard for better UX
        Utils.hideKeyboard(context, city);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                talk.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.red_A200));

                speechProvider.startSpeechRecognizer();
                break;
            case MotionEvent.ACTION_UP:
                talk.setColorFilter(Color.WHITE);

                speechProvider.stopSpeechRecognizer();
                break;
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // binding Views using ButterKnife
        ButterKnife.bind(this);

        loadLastLoadedCity(savedInstanceState);

        if (null == savedInstanceState) {
            createFragments();
            attachFragments();
        } else {
            findFragments();
            city.setText(savedInstanceState.getString(Constants.KEY_LAST_CITY));
        }

        Timber.d("Activity Created");
    }

    private void createFragments() {
        weatherFragment = WeatherFragment.newInstance(city.getText().toString().trim());
    }

    private void attachFragments() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.current_fragment, weatherFragment, TAG_CURRENT_FRAGMENT);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void findFragments() {
        weatherFragment = (WeatherFragment) getSupportFragmentManager().findFragmentByTag(TAG_CURRENT_FRAGMENT);
    }

    @Override
    protected void injectDependencies(AutolabsApplication application) {
        AutolabsApplication
                .getComponent()
                .inject(this);

        speechProvider.bind(this);
    }

    @Override
    protected void onResume() {
        Timber.d("Activity Resumed");
        super.onResume();

        compositeSubscription.addAll(
                noInternetSubject.subscribe(this::showConnectionError),
                saveCitySubject.subscribe(this::saveLastLoadedCity)
        );

        // dismiss no internet connection dialog in case of getting back from setting and connection fixed
        mayDismissInternetConnectionDialog();
    }

    private void mayDismissInternetConnectionDialog() {
        if (internetConnectionDialog != null)
            internetConnectionDialog.dismiss();
    }

    public void showConnectionError(boolean show) {
        Timber.d("Showing Connection Error Message");

        mayDismissInternetConnectionDialog();

        if (show) {
            internetConnectionDialog = Utils.showNoInternetConnectionDialog(this, false);
        }
    }

    // load user last successful city
    private String loadLastLoadedCity(Bundle savedInstanceState) {
        Timber.d("Loading Last City");

        String cityName;

        if (null == savedInstanceState) {
            cityName = AppSettings.getString(context, Constants.KEY_LAST_CITY, Constants.CITY_DEFAULT_VALUE);
        } else {
            cityName = savedInstanceState.getString(Constants.KEY_LAST_CITY);
        }

        city.setText(cityName);

        return cityName;
    }

    // save user last successful city
    private void saveLastLoadedCity(String cityName) {
        Timber.d("Saving Last City");

        city.setText(cityName);

        AppSettings.setValue(this, Constants.KEY_LAST_CITY, cityName);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Timber.d("Activity Saving Instance State");

        //save TimeSpan selected by user before data loaded and saved to SharedPreferences
        outState.putString(Constants.KEY_LAST_CITY, city.getText().toString().trim());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void showSpeechProgress(boolean show) {
        if (show) {
            progress.setVisibility(View.VISIBLE);
        } else {
            talk.setColorFilter(Color.WHITE);
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void setSpeechRecognized(ArrayList<String> cities) {
        String message = "Recognized and looking for: ";

        for (String city : cities) {
            message += "\n" + city;
        }

        showMessage(message);

        weatherFragment.getCitySubject().onNext(cities);
    }

    @Override
    public void setRmsChange(float rmsdB) {
        talk.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.red_A200) - (int) rmsdB * Color.RED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    talk.setImageResource(R.drawable.ic_mic);
                    talk.setColorFilter(Color.WHITE);

                    showMessage("Permission was granted");
                } else {
                    showMessage("Permission denied");

                    talk.setImageResource(R.drawable.ic_mic_off);
                }
                break;
            }
        }
    }

    @Override
    public void requestRecordAudioPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.RECORD_AUDIO)) {

            showMessage("Please give us the access");

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_RECORD_AUDIO);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_RECORD_AUDIO);
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public BehaviorSubject<Boolean> getNoInternetSubject() {
        return noInternetSubject;
    }

    public BehaviorSubject<String> getSaveCitySubject() {
        return saveCitySubject;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        speechProvider.unbind();
    }
}