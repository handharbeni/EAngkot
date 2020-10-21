package com.mhandharbeni.e_angkot.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;

public class Utils {
    public static String getDate(String timeMillis){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d/M/y H:m:s");
        return simpleDateFormat.format(Long.valueOf(timeMillis));
    }
}
