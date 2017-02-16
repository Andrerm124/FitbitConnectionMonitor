package com.ljmu.andre.fitbitconnectionmonitor;

import android.app.Application;

import com.ljmu.andre.fitbitconnectionmonitor.Utils.TimberUtils;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class FCMApplication extends Application {
    public static final boolean DEBUG = true;
    public static final String MODULE_TAG = "FCM";

    @Override
    public void onCreate() {
        TimberUtils.plantAppropriateTree();

        Timber.d("Starting Application [BuildVariant: DEBUG]");

        super.onCreate();
    }
}
