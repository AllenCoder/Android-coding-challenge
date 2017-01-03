package com.mirhoseini.autolabs;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
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
import com.mirhoseini.autolabs.util.Constants;
import com.mirhoseini.autolabs.weather.WeatherFragment;
import com.mirhoseini.utils.Utils;

import java.util.ArrayList;

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

public class MainActivity extends BaseActivity {

    public static final String TAG_CURRENT_FRAGMENT = "current_fragment";
    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 1;

    @Inject
    Context context;

    //injecting views via ButterKnife
    @BindView(R.id.city)
    EditText city;
    @BindView(R.id.talk)
    ImageButton talk;
    @BindView(R.id.progress)
    ProgressBar progress;

    private WeatherFragment weatherFragment;
    private AlertDialog internetConnectionDialog;
    private SpeechRecognizer speechRecognizer;

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

        weatherFragment.getCitySubject().onNext(new String[]{city.getText().toString().trim()});
    }

    @OnTouch(R.id.talk)
    public boolean onTalk(MotionEvent event) {
        //hide keyboard for better UX
        Utils.hideKeyboard(context, city);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                talk.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.red_A200));

                startSpeechRecognizer();
                break;
            case MotionEvent.ACTION_UP:
                talk.setColorFilter(Color.WHITE);

                stopSpeechRecognizer();
                break;
        }
        return true;
    }

    private void stopSpeechRecognizer() {
        speechRecognizer.stopListening();
    }

    private void startSpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test");

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        speechRecognizer.startListening(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // binding Views using ButterKnife
        ButterKnife.bind(this);

        if (null == savedInstanceState) {
            loadLastLoadedCity();
            createFragments();
            attachFragments();
        } else {
            findFragments();
            city.setText(savedInstanceState.getString(Constants.KEY_LAST_CITY));
        }

        // TODO: 04/01/2017 Move SpeechRecognizer to another module for a cleaner code and inject using Dagger
        setupSpeechRecognizer();

        Timber.d("Activity Created");
    }

    private void setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            public void onReadyForSpeech(Bundle params) {
                Timber.d("onReadyForSpeech");
            }

            public void onBeginningOfSpeech() {
                Timber.d("onBeginningOfSpeech");

                progress.setVisibility(View.VISIBLE);
            }

            public void onRmsChanged(float rmsdB) {
                Timber.d("onRmsChanged:" + rmsdB);
                talk.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.red_A200) - (int) rmsdB * Color.RED);
            }

            public void onBufferReceived(byte[] buffer) {
                Timber.d("onBufferReceived");
            }

            public void onEndOfSpeech() {
                Timber.d("onEndOfSpeech");

                talk.setColorFilter(Color.WHITE);
            }

            public void onError(int error) {
                progress.setVisibility(View.GONE);

                switch (error) {
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        requestRecordAudioPermission();
                        break;

                    case SpeechRecognizer.ERROR_NETWORK:
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        Toast.makeText(context, "Speech network error", Toast.LENGTH_SHORT).show();
                        break;

                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        Toast.makeText(context, "Speech timeout", Toast.LENGTH_SHORT).show();
                        break;

                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        Toast.makeText(context, "Speech recognizer busy", Toast.LENGTH_SHORT).show();
                        break;

                    case SpeechRecognizer.ERROR_NO_MATCH:
                        Toast.makeText(context, "Speech no match", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        Toast.makeText(context, "Error!!!", Toast.LENGTH_SHORT).show();

                }
                Timber.d("error " + error);
            }

            public void onResults(Bundle results) {
                talk.setColorFilter(Color.WHITE);
                progress.setVisibility(View.GONE);

                Timber.d("onResults " + results);
                getSpeechPhrases(results);
            }

            public void onPartialResults(Bundle partialResults) {
                Timber.d("onPartialResults");
            }

            public void onEvent(int eventType, Bundle params) {
                Timber.d("onEvent " + eventType);
            }
        });
    }

    private void getSpeechPhrases(Bundle results) {
        ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        String message = "Recognized and looking for: ";
        String[] cities = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            Timber.d("result " + data.get(i));
            cities[i] = data.get(i).toString();

            message += "\n" + data.get(i).toString();
        }

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        weatherFragment.getCitySubject().onNext(cities);
    }

    private void requestRecordAudioPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.RECORD_AUDIO)) {

            Toast.makeText(MainActivity.this, "Please give us the access", Toast.LENGTH_SHORT).show();

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_RECORD_AUDIO);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_RECORD_AUDIO);
        }
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
        if (internetConnectionDialog != null)
            internetConnectionDialog.dismiss();
    }

    public void showConnectionError(boolean show) {
        Timber.d("Showing Connection Error Message");

        if (show) {
            if (internetConnectionDialog != null)
                internetConnectionDialog.dismiss();

            internetConnectionDialog = Utils.showNoInternetConnectionDialog(this, false);
        } else {
            if (internetConnectionDialog != null)
                internetConnectionDialog.dismiss();
        }
    }

    // load user last successful city
    private String loadLastLoadedCity() {
        Timber.d("Loading Last City");

        String cityName = AppSettings.getString(context, Constants.KEY_LAST_CITY, Constants.CITY_DEFAULT_VALUE);
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    talk.setImageResource(R.drawable.ic_mic);
                    talk.setColorFilter(Color.WHITE);

                    Toast.makeText(this, "Permission was granted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();

                    talk.setImageResource(R.drawable.ic_mic_off);
                }
                return;
            }
        }
    }

    public BehaviorSubject<Boolean> getNoInternetSubject() {
        return noInternetSubject;
    }

    public BehaviorSubject<String> getSaveCitySubject() {
        return saveCitySubject;
    }
}