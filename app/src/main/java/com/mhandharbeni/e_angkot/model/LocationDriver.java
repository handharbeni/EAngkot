package com.mhandharbeni.e_angkot.model;

public class LocationDriver {
    String id;
    String latitude;
    String longitude;
    String token;
    String jurusan;
    boolean isLogin;
    String platNo;
    boolean isActive;

    public LocationDriver() {
    }

    public LocationDriver(String id, String latitude, String longitude, String token, String jurusan, boolean isLogin, String platNo, boolean isActive) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.token = token;
        this.jurusan = jurusan;
        this.isLogin = isLogin;
        this.platNo = platNo;
        this.isActive = isActive;
    }
}
