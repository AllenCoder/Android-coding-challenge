package com.mirhoseini.autolabs;

import timber.log.Timber;

/**
 * Created by Mohsen on 03/01/2017.
 */

public class AutolabsApplicationImpl extends AutolabsApplication {

    @Override
    void initApplication() {
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected String createStackElementTag(StackTraceElement element) {
                //adding line number to logs
                return super.createStackElementTag(element) + ":" + element.getLineNumber();
            }
        });
    }
}
