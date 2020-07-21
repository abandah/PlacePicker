package com.error.handler;

import android.Manifest;
import android.app.Application;


/**
 * Created by Abandah on 7/1/2020.
 */
public abstract class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if(EnableErrorHandler()) {
            new UCEHandler.Builder(this)
                    .setTrackActivitiesEnabled(false)
                    .setLink(ErrorHandlerLink())
                    .build();
        }

    }

    protected abstract boolean EnableErrorHandler();
    protected abstract String ErrorHandlerLink();

}