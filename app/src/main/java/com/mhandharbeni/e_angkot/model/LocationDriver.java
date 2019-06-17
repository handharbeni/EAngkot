package com.mhandharbeni.e_angkot.model;

public class LocationDriver {
    String id;
    String latitude;
    String longitude;
    String token;
    String jurusan;
    boolean isLogin;

    public LocationDriver() {
    }

    public LocationDriver(String id, String latitude, String longitude, String token, String jurusan, boolean isLogin) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.token = token;
        this.jurusan = jurusan;
        this.isLogin = isLogin;
    }
}
