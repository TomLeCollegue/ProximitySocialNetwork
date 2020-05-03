package com.example.proximitysocialnetwork;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.HashMap;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.example.proximitysocialnetwork.App.CHANNEL_ID_SERVICE;

public class NetworkService extends Service {
    private NotificationManagerCompat notificationManager;

    private static NetworkService instance = null;
    public static MainActivity instanceMainActivity = null;
    public static NetworkHelper net;
    SessionManager sessionManager;
    private String emailSession;


    public static boolean isInstanceCreated() {
        return instance != null;
    }

    public static boolean isInstanceMainActivityCreated() {
        return instanceMainActivity != null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        notificationManager = NotificationManagerCompat.from(this);
        sessionManager = new SessionManager(this);
        HashMap<String,String > user = sessionManager.getUserDetail();
        emailSession = user.get(sessionManager.EMAIL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_SERVICE)
                .setContentTitle("Proximity")
                .setContentText("Vous êtes Visible")
                .setSmallIcon(R.drawable.add_profil_img)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        // ******* setUp netNetworkHelper ****** //
        net = new NetworkHelper(this, emailSession);
        net.setCurrentNetworkService(this);
        net.SeachPeople();


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        net.StopAll();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    // ******* notif called for person nearby ***** //
    public void sendNotificationNewPerson(String newPerson){
        Intent activityIntentMain = new Intent(this,PersonDiscoveredActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntentMain, 0);
        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_NEW_PERSON)
                .setSmallIcon(R.drawable.add_profil_img)
                .setContentTitle("Une nouvelle personne decouverte !")
                .setContentText(newPerson + " est à proximité")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setColor(getResources().getColor(R.color.ColorPrincipale3))
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .build();
        notificationManager.notify(2, notification);
    }

    public void intentToDiscoveryAccount(){
        Intent intent = new Intent(this, PersonDiscoveredActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


    }
}