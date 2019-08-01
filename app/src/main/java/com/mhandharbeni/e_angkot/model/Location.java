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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }
}
