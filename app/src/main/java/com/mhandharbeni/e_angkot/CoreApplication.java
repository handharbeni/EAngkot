package com.mhandharbeni.e_angkot;

import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;

public class CoreApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAnalytics.getInstance(this);
    }
}
