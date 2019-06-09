package com.mhandharbeni.e_angkot;

import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.mhandharbeni.e_angkot.utils.Constant;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;

public class CoreApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAnalytics.getInstance(this);

        new EncryptedPreferences.Builder(this)
                .withEncryptionPassword(Constant.PASSWORD_PREF)
                .withPreferenceName(Constant.NAME_PREF)
                .withSaveAsSingleton(true)
                .build();
    }
}
