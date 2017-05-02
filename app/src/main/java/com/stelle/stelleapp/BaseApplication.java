package com.stelle.stelleapp;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by Sarthak on 02-05-2017
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
