package com.mhandharbeni.e_angkot.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mhandharbeni.e_angkot.utils.Constant;

import static com.mhandharbeni.e_angkot.utils.BaseActivity.TAG;

public class FirebaseServices extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            if (remoteMessage.getNotification() != null) {
            }
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "onNewToken: " + token);
        Constant.TOKEN = token;
    }
}
