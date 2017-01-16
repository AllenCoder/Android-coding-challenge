package com.mirhoseini.autolabs.speech;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by Mohsen on 16/01/2017.
 */

public class SpeechProviderImpl implements SpeechProvider, RecognitionListener {

    private final SpeechRecognizer speechRecognizer;
    AppSpeechView view;
    private Context context;

    @Inject
    public SpeechProviderImpl(Context context) {
        this.context = context;
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(this);
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Timber.d("onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Timber.d("onBeginningOfSpeech");

        if (null != view)
            view.showSpeechProgress(true);
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Timber.d("onRmsChanged: %s", rmsdB);

        if (null != view)
            view.setRmsChange(rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Timber.d("onBufferReceived");
    }

    @Override
    public void onEndOfSpeech() {
        Timber.d("onEndOfSpeech");
    }

    @Override
    public void onError(int error) {
        if (null != view) {
            view.showSpeechProgress(false);

            switch (error) {
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    if (null != view)
                        view.requestRecordAudioPermission();
                    break;

                case SpeechRecognizer.ERROR_NETWORK:
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    view.showMessage("Speech network error");
                    break;

                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    view.showMessage("Speech timeout");
                    break;

                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    view.showMessage("Speech recognizer busy");
                    break;

                case SpeechRecognizer.ERROR_NO_MATCH:
                    view.showMessage("Speech no match");
                    break;

                default:
                    view.showMessage("Error!!!");

            }
        }
        Timber.d("error %s", error);
    }

    @Override
    public void onResults(Bundle results) {
        if (null != view)
            view.showSpeechProgress(false);

        Timber.d("onResults %s", results);
        getSpeechPhrases(results);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Timber.d("onPartialResults");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Timber.d("onEvent %s", eventType);
    }

    @Override
    public void bind(SpeechView view) {
        this.view = (AppSpeechView) view;
    }

    @Override
    public void unbind() {
        this.view = null;
    }

    @Override
    public void stopSpeechRecognizer() {
        speechRecognizer.stopListening();
    }

    @Override
    public void startSpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test");

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        speechRecognizer.startListening(intent);
    }

    private void getSpeechPhrases(Bundle results) {
        ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        if (null != data && null != view) {
            view.setSpeechRecognized(data);
        }
    }

}
