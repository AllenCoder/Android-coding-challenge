package com.mirhoseini.autolabs.base;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.mirhoseini.autolabs.AutolabsApplication;

/**
 * Created by Mohsen on 03/01/2017.
 */

public abstract class BaseFragment extends Fragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        injectDependencies(AutolabsApplication.get(getContext()));

        // can be used for general purpose in all Fragments of Application
    }

    protected abstract void injectDependencies(AutolabsApplication application);

}
