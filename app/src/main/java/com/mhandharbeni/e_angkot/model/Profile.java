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
}
