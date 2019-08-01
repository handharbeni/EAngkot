package com.mhandharbeni.e_angkot.model;

public class ActiveOrder {
    String idUser;
    String jurusan;
    boolean isActive;
    String tujuan;

    public ActiveOrder() {
    }

    public ActiveOrder(String idUser, String jurusan, boolean isActive, String tujuan) {
        this.idUser = idUser;
        this.jurusan = jurusan;
        this.isActive = isActive;
        this.tujuan = tujuan;
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

    public String getTujuan() {
        return tujuan;
    }

    public void setTujuan(String tujuan) {
        this.tujuan = tujuan;
    }
}
