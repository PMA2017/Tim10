package com.example.pma_tim10.chatapp.model;

import android.graphics.Bitmap;

import com.google.firebase.database.Exclude;

/**
 * Created by Dorian on 5/20/2017.
 */
public class User {
    private String uid;
    private String email;
    private String fullName;
    private String photoURL;
    private String aboutMe;
    private Boolean online;
    private String fcmtoken;

    @Exclude
    private Bitmap userProfilePhoto;

    public User() {
    }

    public User(String uid,String fullName, String email, String photoURL, String aboutMe, Boolean online, String fcmtoken) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.photoURL = photoURL;
        this.aboutMe = aboutMe;
        this.online = online;
        this.fcmtoken = fcmtoken;
    }

    public String getFcmtoken() {
        return fcmtoken;
    }

    public void setFcmtoken(String fcmtoken) {
        this.fcmtoken = fcmtoken;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public boolean equals(Object user) {
        return this.getUid() == ((User)user).getUid();
    }
}
