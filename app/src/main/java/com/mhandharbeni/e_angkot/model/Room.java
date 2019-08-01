package com.mhandharbeni.e_angkot.model;

import java.util.HashMap;

public class Room {
    String idDriver;
    String jurusan;
    int count;
    String platNo;
    HashMap<String, String> listUser;
    String tujuan;

    public Room() {
    }

    public Room(
            String idDriver,
            String jurusan,
            String platNo,
            int count,
            HashMap<String, String> listUser,
            String tujuan
    ) {
        this.idDriver = idDriver;
        this.jurusan = jurusan;
        this.platNo = platNo;
        this.count = count;
        this.listUser = listUser;
        this.tujuan = tujuan;
    }

    public String getIdDriver() {
        return idDriver;
    }

    public void setIdDriver(String idDriver) {
        this.idDriver = idDriver;
    }

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPlatNo() {
        return platNo;
    }

    public void setPlatNo(String platNo) {
        this.platNo = platNo;
    }

    public HashMap<String, String> getListUser() {
        return listUser;
    }

    public void setListUser(HashMap<String, String> listUser) {
        this.listUser = listUser;
    }

    public String getTujuan() {
        return tujuan;
    }

    public void setTujuan(String tujuan) {
        this.tujuan = tujuan;
    }
}
