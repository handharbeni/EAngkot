package com.mhandharbeni.e_angkot.utils;

import android.Manifest;
import android.location.Location;

public class Constant {
    public enum TypeUser{
        DRIVER,
        USER
    }
    public static Integer APP_ID = 12345678;
    public static String CHANNEL_ID = "ChannelGPS";
    public static String CHANNEL_TITLE = "Scanning Location";

    public static String API_MAPS = "AIzaSyDc0nBVJlzxmgVAFhk44SIfNnIWvGDwvh8";

    public static String[] listPermission = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET
    };

    public static Location mLastLocation;

    public static String PASSWORD_PREF = "NEWPASSWORD";
    public static String NAME_PREF = "E-ANGKOT";

    public static String MODE = "MODE";

    public static String ID_USER = "ID_USER";
    public static String ID_TOKEN = "ID_TOKEN";
    public static String ID_JURUSAN = "ID_JURUSAN";

    public static String COLLECTION_USER = "e_angkot_user";
    public static String COLLECTION_DRIVER = "e_angkot_driver";
    public static String COLLECTION_TRACK_USER = "e_angkot_track_user";
    public static String COLLECTION_TRACK_DRIVER = "e_angkot_track_driver";
    public static String COLLECTION_PROFILE = "e_angkot_profile";
    public static String COLLECTION_JURUSAN = "e_angkot_jurusan";


    public static String TOKEN = "TOKEN";
    public static String IS_LOGGIN = "ISLOGIN";
}
