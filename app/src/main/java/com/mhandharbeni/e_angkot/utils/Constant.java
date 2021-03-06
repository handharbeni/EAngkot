package com.mhandharbeni.e_angkot.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

public class Constant {
    public enum TypeUser{
        DRIVER,
        USER
    }
    public static Integer APP_ID = 12345678;
    public static String CHANNEL_ID = "ChannelGPS";
    public static String CHANNEL_TITLE = "Scanning Location";

    public static String API_MAPS = "AIzaSyAr58RiTYuSLPHi4YxexeysNlspy4Yfk_s";
    public static String STORAGE_BUCKET = "gs://prototypeproject-1d503.appspot.com";
    public static String STORAGE_FOLDER = "E-ANGKOT";
//    public static String API_MAPS = "AIzaSyDc0nBVJlzxmgVAFhk44SIfNnIWvGDwvh8";

    public static String[] listPermission = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.FOREGROUND_SERVICE
    };

    public static Location mLastLocation;

    public static String PASSWORD_PREF = "NEWPASSWORD";
    public static String NAME_PREF = "E-ANGKOT";

    public static String MODE = "MODE";

    public static String ID_USER = "ID_USER";
    public static String NAMA_USER = "NAMA_USER";
    public static String IMAGE_USER = "IMAGE_USER";
    public static String ALAMAT_USER = "ALAMAT_USER";
    public static String PHONE_USER = "PHONE_USER";
    public static String TYPE_USER = "TYPE_USER";

    public static String PLAT_NO = "PLAT_NO";

    public static String ID_TOKEN = "ID_TOKEN";
    public static String ID_JURUSAN = "ID_JURUSAN";
    public static String ID_TUJUAN = "ID_TUJUAN";

    public static String COLLECTION_USER = "e_angkot_user";
    public static String COLLECTION_DRIVER = "e_angkot_driver";
    public static String COLLECTION_TRACK_USER = "e_angkot_track_user";
    public static String COLLECTION_TRACK_DRIVER = "e_angkot_track_driver";
    public static String COLLECTION_PROFILE = "e_angkot_profile";
    public static String COLLECTION_JURUSAN = "e_angkot_jurusan";
    public static String COLLECTION_ORDER = "e_angkot_order";
    public static String COLLECTION_ROOM = "e_angkot_room";
    public static String COLLECTION_RATING_DRIVER = "e_angkot_rating_driver";
    public static String COLLECTION_RATING_ANGKOT = "e_angkot_rating_angkot";
    public static String COLLECTION_TRAVEL_HISTORY = "e_angkot_travel_history";
    public static String COLLECTION_TERMINAL = "e_angkot_terminal";

    public static String TOKEN = "TOKEN";
    public static String IS_LOGGIN = "ISLOGIN";

    public static String DRIVER_ISACTIVE = "DRIVER_ISACTIVE";

    public static String MY_LATITUDE = "MY_LATITUDE";
    public static String MY_LONGITUDE = "MY_LONGITUDE";

    public static String MY_OLD_LATITUDE = "MY_OLD_LATITUDE";
    public static String MY_OLD_LONGITUDE = "MY_OLD_LONGITUDE";

    public static String STATE_ORDER = "STATE_ORDER";

    public static String ACTIVE_ORDER_JURUSAN = "ACTIVE_ORDER_JURUSAN";
    public static String ACTIVE_ORDER_TUJUAN = "ACTIVE_ORDER_TUJUAN";
    public static String ACTIVE_ORDER_COUNT = "ACTIVE_ORDER_COUNT";
    public static String ACTIVE_ORDER_IDDRIVER = "ACTIVE_ORDER_IDDRIVER";
    public static String ACTIVE_ORDER_PLATNO = "ACTIVE_ORDER_PLATNO";

    public static String CURRENT_USERNAME = "";
    public static String CURRENT_PASSWORD = "";

    public static void displayPromptForEnablingGPS(
            final Activity activity)
    {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Enable either GPS or any other location"
                + " service to find current location.  Click OK to go to"
                + " location services settings to let you do so.";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        (d, id) -> {
                            activity.startActivity(new Intent(action));
                            d.dismiss();
                        })
                .setNegativeButton("Cancel",
                        (d, id) -> d.cancel());
        builder.create().show();
    }

    public static void displayNoInternet(Activity activity){
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Kami Melihat Anda Offline, Keluar";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        (d, id) -> {
                            d.dismiss();
                            activity.finish();
                        });
        builder.create().show();
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
