package com.mirhoseini.autolabs.util;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

/**
 * Created by Mohsen on 03/01/2017.
 */

public class CustomBindingAdapter {
    @BindingAdapter("bind:weatherIcon")
    public static void loadImage(ImageView imageView, String icon) {
        if (null != icon) {
            imageView.setImageResource(WeatherUtils.convertIconToResource(icon));
        }
    }
}
