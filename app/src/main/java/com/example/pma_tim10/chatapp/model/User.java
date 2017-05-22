package com.example.pma_tim10.chatapp.model;

/**
 * Created by Dorian on 5/20/2017.
 */
public class User {
    private String email;
    private String password;
    private String name;
    private String surname;
    private String photoURL;
    private String aboutMe;

    public User() {

    }

    public User(String username, String password, String name, String surname, String photoURL, String aboutMe) {
        this.email = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.photoURL = photoURL;
        this.aboutMe = aboutMe;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String username) {
        this.email = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
