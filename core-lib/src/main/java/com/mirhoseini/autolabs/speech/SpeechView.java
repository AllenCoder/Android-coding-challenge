package com.mirhoseini.autolabs.speech;

import java.util.ArrayList;

/**
 * Created by Mohsen on 16/01/2017.
 */

public interface SpeechView {

    void showSpeechProgress(boolean show);

    void setSpeechRecognized(ArrayList<String> cities);

    void setRmsChange(float rmsdB);

    void showMessage(String message);

}
