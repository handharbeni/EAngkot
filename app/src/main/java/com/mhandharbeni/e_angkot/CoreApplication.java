package com.mhandharbeni.e_angkot;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.mhandharbeni.e_angkot.utils.Constant;
import com.mhandharbeni.e_angkot.utils.ToolsFirebase;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import io.fabric.sdk.android.Fabric;

public class CoreApplication extends Application {

    private static EncryptedPreferences encryptedPreferences;
    private static ToolsFirebase toolsFirebase;
    private static FirebaseStorage storage;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        storage = FirebaseStorage.getInstance(Constant.STORAGE_BUCKET);
        FirebaseAnalytics.getInstance(this);
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        Stetho.initializeWithDefaults(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Constant.TOKEN = task.getResult().getToken();
            }
        });
        encryptedPreferences = new EncryptedPreferences.Builder(this)
                .withEncryptionPassword(Constant.PASSWORD_PREF)
                .withPreferenceName(Constant.NAME_PREF)
                .withSaveAsSingleton(true)
                .build();
        toolsFirebase = new ToolsFirebase(getApplicationContext());
    }

    public static EncryptedPreferences getPref() {
        return encryptedPreferences;
    }

    public static ToolsFirebase getFirebase() {
        return toolsFirebase;
    }

    public static FirebaseStorage getStorage() {
        if (storage == null){
            storage = FirebaseStorage.getInstance(Constant.STORAGE_BUCKET);
        }
        return storage;
    }
}
