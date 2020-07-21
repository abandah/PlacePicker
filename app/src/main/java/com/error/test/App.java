package com.error.test;

import android.app.Application;

import com.error.handler.UCEHandler;

/**
 * Created by Abandah on 7/21/2020.
 */
public class App extends com.error.handler.App {


    @Override
    protected boolean EnableErrorHandler() {
        return true;
    }

    @Override
    protected String ErrorHandlerLink() {
        return "";
    }
}
