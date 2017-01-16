package com.mirhoseini.autolabs.speech;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Mohsen on 16/01/2017.
 */

@Module
public class SpeechModule {

    @Provides
    SpeechProvider provideSpeechProvider(SpeechProviderImpl speechProvider) {
        return speechProvider;
    }

}
