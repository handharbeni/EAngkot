package com.mhandharbeni.e_angkot.utils;

import android.Manifest;
import android.location.Location;

public class Constant {
    public static Integer APP_ID = 12345678;
    public static String CHANNEL_ID = "ChannelGPS";
    public static String CHANNEL_TITLE = "Scanning Location";


    public static String[] listPermission = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET
    };

    public static Location mLastLocation;

    public static String PASSWORD_PREF = "NEWPASSWORD";
    public static String NAME_PREF = "E-ANGKOT";

    public static String MODE = "MODE";

    public static String COLLECTION_USER = "e_angkot_user";
    public static String COLLECTION_DRIVER = "e_angkot_driver";

}
