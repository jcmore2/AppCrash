package com.jcmore2.appcrashsample;

import android.app.Application;

import com.jcmore2.appcrash.AppCrash;

/**
 * Created by jcmore2 on 29/7/15.
 */
public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AppCrash.init(this);
    }
}
