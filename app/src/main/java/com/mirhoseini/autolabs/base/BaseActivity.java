package com.mirhoseini.autolabs.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mirhoseini.autolabs.AutolabsApplication;

/**
 * Created by Mohsen on 03/01/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Because of Fragments should inject before super.onCreate
        injectDependencies(AutolabsApplication.get(this));

        super.onCreate(savedInstanceState);

        // can be used for general purpose in all Activities of Application
    }

    protected abstract void injectDependencies(AutolabsApplication application);

}
