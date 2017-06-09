package com.example.pma_tim10.chatapp.model;

import android.graphics.Bitmap;

import com.google.firebase.database.Exclude;

import java.util.Objects;

/**
 * Created by Dorian on 5/20/2017.
 */
public class User {
    private String uid;
    private String email;
    private String name;
    private String surname;
    private String photoURL;
    private String aboutMe;
    private Boolean online;

    @Exclude
    private Bitmap userProfilePhoto;

    public User() {
    }

    public User(String uid, String email, String name, String surname, String photoURL, String aboutMe, Boolean online) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.photoURL = photoURL;
        this.aboutMe = aboutMe;
        this.online = online;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public Boolean isOnline() {
        return online == null ? false : online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    @Exclude
    public String getStatus(){
        return isOnline() == true ? "online" : "offline";
    }

    public Bitmap getUserProfilePhoto() {
        return userProfilePhoto;
    }

    public void setUserProfilePhoto(Bitmap userProfilePhoto) {
        this.userProfilePhoto = userProfilePhoto;
    }

    @Exclude
    public String getFullName() {
        return getName() + " " + getSurname();
    }

    @Override
    public boolean equals(Object user) {
        return this.getUid() == ((User)user).getUid();
    }
}
