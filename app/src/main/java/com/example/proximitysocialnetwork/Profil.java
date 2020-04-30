package com.example.proximitysocialnetwork;

import android.media.Image;
import android.net.Uri;



import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Profil implements Serializable {

    private String name;
    private String email;
    private String birthDate;
    private String profileImage;
    private String password;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage){ this.profileImage = profileImage;}
}
