package com.mhandharbeni.e_angkot.model;

public class ActiveOrder {
    String idUser;
    String jurusan;
    boolean isActive;

    public ActiveOrder() {
    }

    public ActiveOrder(String idUser, String jurusan, boolean isActive) {
        this.idUser = idUser;
        this.jurusan = jurusan;
        this.isActive = isActive;
    }
}
