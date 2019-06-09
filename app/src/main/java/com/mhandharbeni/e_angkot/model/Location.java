package com.mhandharbeni.e_angkot.model;

public class Location {
    String id;
    String latitude;
    String longitude;
    String token;
    public Location() {
    }

    public Location(String id, String latitude, String longitude, String token) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.token = token;
    }

    public Location(String id, String latitude, String longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
