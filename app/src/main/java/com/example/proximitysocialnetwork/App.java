package com.example.proximitysocialnetwork;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import java.util.ArrayList;

public class App extends Application {

    public static final String CHANNEL_NEW_PERSON = "newPersonChannel";
    public static final String CHANNEL_ID_SERVICE = "serviceNetwork";

    public static ArrayList<Profil> profilsDiscovered = new ArrayList<>();
    public static ArrayList<String> profilsDiscoveredOffLine = new ArrayList<>();

    public ArrayList<Profil> getProfilsDiscovered() {
        return profilsDiscovered;
    }

    public static void removeFirstProfilDiscovered() {
        profilsDiscovered.remove(0);
    }

    public static void clearProfilsDiscoveredOffline(){
        profilsDiscoveredOffLine.clear();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel newPerson = new NotificationChannel(
                    CHANNEL_NEW_PERSON,
                    "New Person nearby",
                    NotificationManager.IMPORTANCE_HIGH
            );
            newPerson.setDescription("Nouvelle personn notif");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(newPerson);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID_SERVICE,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }

    }

}
