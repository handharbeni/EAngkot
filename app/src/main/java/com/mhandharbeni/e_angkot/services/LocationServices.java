package com.mhandharbeni.e_angkot.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.RequiresApi;

import com.mhandharbeni.e_angkot.CoreApplication;
import com.mhandharbeni.e_angkot.MainActivity;
import com.mhandharbeni.e_angkot.R;
import com.mhandharbeni.e_angkot.model.LocationDriver;
import com.mhandharbeni.e_angkot.utils.Constant;

public class LocationServices extends Service {
    private final LocationServiceBinder binder = new LocationServiceBinder();
    private final String TAG = "BackgroundService";
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private NotificationManager notificationManager;

    private final int LOCATION_INTERVAL = 2000;
    private final int LOCATION_DISTANCE = 10;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private class LocationListener implements android.location.LocationListener {
        private Location lastLocation = null;
        private final String TAG = "LocationListener";
        private Location mLastLocation;

        public LocationListener(String provider) {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            mLastLocation = location;
            Constant.mLastLocation = mLastLocation;

            if (!CoreApplication.getPref().getString(Constant.ID_USER, "0").equalsIgnoreCase("0")) {
                String collection = CoreApplication.getPref().getString(Constant.MODE, "USER").equalsIgnoreCase("user") ? Constant.COLLECTION_TRACK_USER : Constant.COLLECTION_TRACK_DRIVER;
                if (CoreApplication.getPref().getString(Constant.MODE, "USER").equalsIgnoreCase("user")) {
                    com.mhandharbeni.e_angkot.model.Location location1 = new com.mhandharbeni.e_angkot.model.Location(
                            CoreApplication.getPref().getString(Constant.ID_USER, "0"),
                            String.valueOf(mLastLocation.getLatitude()),
                            String.valueOf(mLastLocation.getLongitude()),
                            true,
                            CoreApplication.getPref().getString(Constant.ID_TOKEN, "0")
                    );
                    CoreApplication.getFirebase().getDb().collection(collection).document(CoreApplication.getPref().getString(Constant.ID_USER, "0")).set(location1);
                } else {
                    LocationDriver locationDriver = new LocationDriver(
                            CoreApplication.getPref().getString(Constant.ID_USER, "0"),
                            String.valueOf(mLastLocation.getLatitude()),
                            String.valueOf(mLastLocation.getLongitude()),
                            CoreApplication.getPref().getString(Constant.ID_TOKEN, "0"),
                            CoreApplication.getPref().getString(Constant.ID_JURUSAN, "0"),
                            true,
                            CoreApplication.getPref().getString(Constant.PLAT_NO, "N/A"),
                            CoreApplication.getPref().getBoolean(Constant.DRIVER_ISACTIVE, false),
                            CoreApplication.getPref().getString(Constant.ID_TUJUAN, "0")
                    );
                    CoreApplication.getFirebase().getDb().collection(collection).document(CoreApplication.getPref().getString(Constant.ID_USER, "0")).set(locationDriver);
                }
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Notification notification = new Notification(R.drawable.ic_angkot, Constant.CHANNEL_TITLE,
                System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(Constant.APP_ID, getNotification());
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(Constant.APP_ID, getNotification());
        }
        startTracking();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(mLocationListener);
            } catch (Exception ex) {
            }
        }
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            }
        }
    }

    public void startTracking() {
        try {
            initializeLocationManager();
            mLocationListener = new LocationListener(LocationManager.GPS_PROVIDER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListener);

        } catch (Exception ex) {
        }

    }

    public void stopTracking() {
        this.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification getNotification() {

        NotificationChannel channel = new NotificationChannel(Constant.CHANNEL_ID, Constant.CHANNEL_TITLE, NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(getApplicationContext(), Constant.CHANNEL_ID).setAutoCancel(true);
        return builder.build();
    }


    public class LocationServiceBinder extends Binder {
        public LocationServices getService() {
            return LocationServices.this;
        }
    }

}
