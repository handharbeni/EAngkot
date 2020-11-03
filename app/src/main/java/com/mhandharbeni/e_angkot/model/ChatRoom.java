package com.mhandharbeni.e_angkot.model;

public class ChatRoom {

    public String idUser;
    public String imageProfile;
    public String name;
    public String message;
    public String typeMessage;
    public String time;

    public ChatRoom() {
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTypeMessage() {
        return typeMessage;
    }

    public void setTypeMessage(String typeMessage) {
        this.typeMessage = typeMessage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "idUser='" + idUser + '\'' +
                ", imageProfile='" + imageProfile + '\'' +
                ", name='" + name + '\'' +
                ", message='" + message + '\'' +
                ", typeMessage='" + typeMessage + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
