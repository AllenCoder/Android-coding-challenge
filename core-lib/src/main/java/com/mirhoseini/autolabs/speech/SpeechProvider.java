package com.mirhoseini.autolabs.speech;

/**
 * Created by Mohsen on 16/01/2017.
 */

public interface SpeechProvider {

    void bind(SpeechView view);

    void unbind();

    void stopSpeechRecognizer();

    void startSpeechRecognizer();

}
