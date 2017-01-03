package com.mirhoseini.autolabs.weather;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by Mohsen on 03/01/2017.
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface Weather {
}
