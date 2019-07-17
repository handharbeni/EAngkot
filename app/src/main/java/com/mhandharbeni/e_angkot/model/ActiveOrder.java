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

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
