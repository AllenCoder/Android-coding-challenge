package com.mirhoseini.autolabs.util;

import rx.Scheduler;

/**
 * Created by Mohsen on 03/01/2017.
 */

public interface SchedulerProvider {

    Scheduler mainThread();

    Scheduler backgroundThread();

}
