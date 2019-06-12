package com.mhandharbeni.e_angkot.model;

public class Location {
    String id;
    String latitude;
    String longitude;
    String token;
    boolean isLogin;

    public Location() {
    }

    public Location(String id, String latitude, String longitude, boolean isLogin, String token) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isLogin = isLogin;
        this.token = token;
    }

    public Location(String id, String latitude, String longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(String id, boolean isLogin){
        this.id = id;
        this.isLogin = isLogin;
    }
}
