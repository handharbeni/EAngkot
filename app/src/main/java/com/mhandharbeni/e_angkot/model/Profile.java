package com.mhandharbeni.e_angkot.model;

import com.mhandharbeni.e_angkot.utils.Constant;

public class Profile {

    public String id;
    public String nama;
    public String alamat;
    public String imageProfile;
    public String nomorHp;
    public Constant.TypeUser typeUser;

    public Profile() {
    }

    public Profile(String id, String nama, String alamat, String imageProfile, String nomorHp, Constant.TypeUser typeUser) {
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
        this.imageProfile = imageProfile;
        this.nomorHp = nomorHp;
        this.typeUser = typeUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public String getNomorHp() {
        return nomorHp;
    }

    public void setNomorHp(String nomorHp) {
        this.nomorHp = nomorHp;
    }

    public Constant.TypeUser getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(Constant.TypeUser typeUser) {
        this.typeUser = typeUser;
    }
}
