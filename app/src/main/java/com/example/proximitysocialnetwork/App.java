package com.example.proximitysocialnetwork;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_NEW_PERSON = "newPersonChannel";

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

    }
}
