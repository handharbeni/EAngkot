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
    String tujuan;

    public LocationDriver() {
    }

    public LocationDriver(String id, String latitude, String longitude, String token, String jurusan, boolean isLogin, String platNo, boolean isActive, String tujuan) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.token = token;
        this.jurusan = jurusan;
        this.isLogin = isLogin;
        this.platNo = platNo;
        this.isActive = isActive;
        this.tujuan = tujuan;
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

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public String getPlatNo() {
        return platNo;
    }

    public void setPlatNo(String platNo) {
        this.platNo = platNo;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getTujuan() {
        return tujuan;
    }

    public void setTujuan(String tujuan) {
        this.tujuan = tujuan;
    }
}
